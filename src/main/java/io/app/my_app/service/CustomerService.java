package io.app.my_app.service;

import io.app.my_app.model.Customer;
import io.app.my_app.model.dtos.customer.CustomerFilter;
import io.app.my_app.model.dtos.customer.CustomerRequest;
import io.app.my_app.model.dtos.customer.CustomerResponse;
import io.app.my_app.model.enums.UtilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Page<CustomerResponse> findAll(CustomerFilter filter, Pageable pageable);

    CustomerResponse findById(UUID id);

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(UUID id, CustomerRequest request);

    CustomerResponse updateStatus(UUID id, UtilityStatus status);

    Customer findCustomer(UUID id);
}
