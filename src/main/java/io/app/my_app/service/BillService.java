package io.app.my_app.service;

import io.app.my_app.model.Bill;
import io.app.my_app.model.dtos.billing.BillFilter;
import io.app.my_app.model.dtos.billing.BillGenerateRequest;
import io.app.my_app.model.dtos.billing.BillResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BillService {
    Page<BillResponse> findAll(BillFilter filter, Pageable pageable);

    BillResponse findById(UUID id);

    BillResponse generate(BillGenerateRequest request);

    BillResponse approve(UUID id);

    Bill findBillByReference(String reference);

    Bill save(Bill bill);
}
