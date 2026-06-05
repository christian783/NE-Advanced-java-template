package io.app.my_app.service.impl;

import io.app.my_app.exception.BadRequestException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.TariffMapper;
import io.app.my_app.model.Tariff;
import io.app.my_app.model.TariffTier;
import io.app.my_app.model.dtos.tariff.*;
import io.app.my_app.model.enums.TariffType;
import io.app.my_app.repository.TariffRepository;
import io.app.my_app.service.TariffService;
import io.app.my_app.specification.TariffSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;
    private final TariffSpec tariffSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<TariffResponse> findAll(TariffFilter filter, Pageable pageable) {
        return tariffRepository.findAll(tariffSpec.hasFilters(filter), pageable)
                .map(tariffMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TariffResponse findById(UUID id) {
        return tariffMapper.toResponse(findTariff(id));
    }

    @Override
    @Transactional
    public TariffResponse configure(TariffRequest request) {
        LocalDate nextCycle = YearMonth.now().plusMonths(1).atDay(1);
        if (request.getEffectiveFrom().isBefore(nextCycle)) {
            throw new BadRequestException("exceptions.tariff.futureCycleRequired", nextCycle);
        }
        if (request.getTariffType() == TariffType.FLAT && request.getUnitPrice() == null) {
            throw new BadRequestException("exceptions.tariff.unitPriceRequired");
        }
        if (request.getTariffType() == TariffType.TIERED && (request.getTiers() == null || request.getTiers().isEmpty())) {
            throw new BadRequestException("exceptions.tariff.tiersRequired");
        }

        int nextVersion = tariffRepository.findTopByMeterTypeAndActiveTrueOrderByTariffVersionDesc(request.getMeterType())
                .map(Tariff::getTariffVersion)
                .orElse(0) + 1;

        Tariff tariff = tariffMapper.toEntity(request);
        tariff.setFixedCharge(defaultZero(tariff.getFixedCharge()));
        tariff.setVatRate(defaultZero(tariff.getVatRate()));
        tariff.setPenaltyRate(defaultZero(tariff.getPenaltyRate()));
        tariff.setTariffVersion(nextVersion);
        tariff.setActive(true);

        if (request.getTiers() != null) {
            request.getTiers().forEach(tierRequest -> {
                TariffTier tier = tariffMapper.toTierEntity(tierRequest);
                tier.setTariff(tariff);
                tariff.getTiers().add(tier);
            });
        }

        return tariffMapper.toResponse(tariffRepository.save(tariff));
    }

    @Override
    public Tariff findApplicableTariff(io.app.my_app.model.enums.MeterType meterType, LocalDate billingCycleStart) {
        return tariffRepository
                .findTopByMeterTypeAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDescTariffVersionDesc(meterType, billingCycleStart)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.tariff.notFound", meterType));
    }

    @Override
    public Tariff findTariff(UUID id) {
        return tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.tariff.notFound", id));
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
