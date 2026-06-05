package io.app.my_app.service.impl;

import io.app.my_app.exception.BadRequestException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.BillMapper;
import io.app.my_app.model.*;
import io.app.my_app.model.dtos.billing.BillFilter;
import io.app.my_app.model.dtos.billing.BillGenerateRequest;
import io.app.my_app.model.dtos.billing.BillResponse;
import io.app.my_app.model.enums.BillStatus;
import io.app.my_app.model.enums.TariffType;
import io.app.my_app.model.enums.UtilityStatus;
import io.app.my_app.repository.BillRepository;
import io.app.my_app.service.BillService;
import io.app.my_app.service.MeterReadingService;
import io.app.my_app.service.NotificationService;
import io.app.my_app.service.TariffService;
import io.app.my_app.specification.BillSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final MeterReadingService meterReadingService;
    private final TariffService tariffService;
    private final NotificationService notificationService;
    private final BillMapper billMapper;
    private final BillSpec billSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<BillResponse> findAll(BillFilter filter, Pageable pageable) {
        return billRepository.findAll(billSpec.hasFilters(filter), pageable)
                .map(billMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse findById(UUID id) {
        return billMapper.toResponse(findBill(id));
    }

    @Override
    @Transactional
    public BillResponse generate(BillGenerateRequest request) {
        MeterReading reading = meterReadingService.findReading(request.getMeterReadingId());
        Meter meter = reading.getMeter();
        Customer customer = meter.getCustomer();
        if (customer.getStatus() != UtilityStatus.ACTIVE) {
            throw new BadRequestException("exceptions.customer.inactive");
        }
        if (meter.getStatus() != UtilityStatus.ACTIVE) {
            throw new BadRequestException("exceptions.meter.inactive");
        }
        if (billRepository.existsByMeterIdAndBillMonthAndBillYear(meter.getId(), reading.getReadingMonth(), reading.getReadingYear())) {
            throw new BadRequestException("exceptions.bill.duplicateMonthlyBill", meter.getMeterNumber(), reading.getReadingMonth(), reading.getReadingYear());
        }

        LocalDate billingCycleStart = LocalDate.of(reading.getReadingYear(), reading.getReadingMonth(), 1);
        Tariff tariff = tariffService.findApplicableTariff(meter.getMeterType(), billingCycleStart);
        BigDecimal unitCharge = calculateUnitCharge(tariff, reading.getConsumption());
        BigDecimal fixedCharge = tariff.getFixedCharge();
        BigDecimal subtotal = unitCharge.add(fixedCharge);
        BigDecimal vatAmount = subtotal.multiply(normalizeRate(tariff.getVatRate())).setScale(4, RoundingMode.HALF_UP);
        BigDecimal penaltyAmount = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(vatAmount).add(penaltyAmount).setScale(4, RoundingMode.HALF_UP);

        Bill bill = Bill.builder()
                .reference(buildReference(meter, reading))
                .customer(customer)
                .meter(meter)
                .meterReading(reading)
                .tariff(tariff)
                .billMonth(reading.getReadingMonth())
                .billYear(reading.getReadingYear())
                .consumptionUnits(reading.getConsumption())
                .unitCharge(unitCharge)
                .fixedCharge(fixedCharge)
                .vatAmount(vatAmount)
                .penaltyAmount(penaltyAmount)
                .totalAmount(total)
                .amountPaid(BigDecimal.ZERO)
                .outstandingBalance(total)
                .status(BillStatus.PENDING_APPROVAL)
                .dueDate(request.getDueDate())
                .build();

        Bill saved = billRepository.save(bill);
        notificationService.createBillGeneratedNotification(saved);
        return billMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BillResponse approve(UUID id) {
        Bill bill = findBill(id);
        if (bill.getStatus() != BillStatus.PENDING_APPROVAL) {
            throw new BadRequestException("exceptions.bill.notPendingApproval");
        }
        bill.setStatus(BillStatus.APPROVED);
        bill.setApprovedBy(currentUserOrNull());
        bill.setApprovedAt(LocalDateTime.now());
        return billMapper.toResponse(billRepository.save(bill));
    }

    @Override
    public Bill findBillByReference(String reference) {
        return billRepository.findByReference(reference)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.bill.notFound", reference));
    }

    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    private Bill findBill(UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.bill.notFound", id));
    }

    private BigDecimal calculateUnitCharge(Tariff tariff, BigDecimal consumption) {
        if (tariff.getTariffType() == TariffType.FLAT) {
            return consumption.multiply(tariff.getUnitPrice()).setScale(4, RoundingMode.HALF_UP);
        }

        BigDecimal total = BigDecimal.ZERO;
        List<TariffTier> tiers = tariff.getTiers().stream()
                .sorted(Comparator.comparing(TariffTier::getMinUnits))
                .toList();
        for (TariffTier tier : tiers) {
            BigDecimal maxUnits = tier.getMaxUnits() == null ? consumption : tier.getMaxUnits();
            if (consumption.compareTo(tier.getMinUnits()) <= 0) {
                continue;
            }
            BigDecimal billableUnits = consumption.min(maxUnits).subtract(tier.getMinUnits());
            if (billableUnits.compareTo(BigDecimal.ZERO) > 0) {
                total = total.add(billableUnits.multiply(tier.getUnitPrice()));
            }
        }
        return total.setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeRate(BigDecimal rate) {
        if (rate == null) {
            return BigDecimal.ZERO;
        }
        return rate.compareTo(BigDecimal.ONE) > 0
                ? rate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                : rate;
    }

    private String buildReference(Meter meter, MeterReading reading) {
        return meter.getMeterNumber() + "-" + reading.getReadingYear() + String.format("%02d", reading.getReadingMonth());
    }

    private User currentUserOrNull() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return null;
        }
        return user;
    }
}
