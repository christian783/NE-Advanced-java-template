package io.app.my_app.service.impl;

import io.app.my_app.exception.BadRequestException;
import io.app.my_app.mapper.PaymentMapper;
import io.app.my_app.model.Bill;
import io.app.my_app.model.Payment;
import io.app.my_app.model.dtos.payment.PaymentFilter;
import io.app.my_app.model.dtos.payment.PaymentRequest;
import io.app.my_app.model.dtos.payment.PaymentResponse;
import io.app.my_app.model.enums.BillStatus;
import io.app.my_app.repository.PaymentRepository;
import io.app.my_app.service.BillService;
import io.app.my_app.service.NotificationService;
import io.app.my_app.service.PaymentService;
import io.app.my_app.specification.PaymentSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillService billService;
    private final NotificationService notificationService;
    private final PaymentMapper paymentMapper;
    private final PaymentSpec paymentSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(PaymentFilter filter, Pageable pageable) {
        return paymentRepository.findAll(paymentSpec.hasFilters(filter), pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional
    public PaymentResponse record(PaymentRequest request) {
        if (request.getReferenceNumber() != null && !request.getReferenceNumber().isBlank()
                && paymentRepository.existsByReferenceNumber(request.getReferenceNumber().trim())) {
            throw new BadRequestException("exceptions.payment.referenceAlreadyExists", request.getReferenceNumber());
        }

        Bill bill = billService.findBillByReference(request.getBillReference().trim());
        if (bill.getStatus() == BillStatus.PENDING_APPROVAL) {
            throw new BadRequestException("exceptions.payment.billNotApproved");
        }
        if (bill.getStatus() == BillStatus.PAID) {
            throw new BadRequestException("exceptions.payment.billAlreadyPaid");
        }
        if (request.getAmountPaid().compareTo(bill.getOutstandingBalance()) > 0) {
            throw new BadRequestException("exceptions.payment.exceedsOutstanding");
        }

        BigDecimal balanceBefore = bill.getOutstandingBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmountPaid());
        bill.setAmountPaid(bill.getAmountPaid().add(request.getAmountPaid()));
        bill.setOutstandingBalance(balanceAfter);
        bill.setStatus(balanceAfter.compareTo(BigDecimal.ZERO) == 0 ? BillStatus.PAID : BillStatus.PARTIAL);
        billService.save(bill);

        Payment payment = paymentMapper.toEntity(request);
        payment.setBill(bill);
        payment.setCustomer(bill.getCustomer());
        payment.setBalanceBefore(balanceBefore);
        payment.setBalanceAfter(balanceAfter);

        Payment saved = paymentRepository.save(payment);
        if (bill.getStatus() == BillStatus.PAID) {
            notificationService.createPaymentReceivedNotification(saved);
        }
        return paymentMapper.toResponse(saved);
    }
}
