package io.app.my_app.service.impl;

import io.app.my_app.exception.DuplicateRecordException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.CustomerMapper;
import io.app.my_app.model.Customer;
import io.app.my_app.model.dtos.customer.CustomerFilter;
import io.app.my_app.model.dtos.customer.CustomerRequest;
import io.app.my_app.model.dtos.customer.CustomerResponse;
import io.app.my_app.repository.CustomerRepository;
import io.app.my_app.specification.CustomerSpec;
import io.app.my_app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerSpec customerSpec;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CustomerResponse> findAll(CustomerFilter filter, org.springframework.data.domain.Pageable pageable) {
        return customerRepository.findAll(customerSpec.hasFilters(filter), pageable).map(customerMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return customerMapper.toResponse(findCustomer(id));
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        String nationalId = customerMapper.normalize(request.getNationalId());
        if (customerRepository.existsByNationalId(nationalId)) {
            throw new DuplicateRecordException("exceptions.customer.nationalIdAlreadyExists", nationalId);
        }

        Customer customer = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = findCustomer(id);
        String nationalId = customerMapper.normalize(request.getNationalId());
        if (customerRepository.existsByNationalIdAndIdNot(nationalId, id)) {
            throw new DuplicateRecordException("exceptions.customer.nationalIdAlreadyExists", nationalId);
        }

        customerMapper.updateEntity(request, customer);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse updateStatus(UUID id, io.app.my_app.model.enums.UtilityStatus status) {
        Customer customer = findCustomer(id);
        customer.setStatus(status);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public Customer findCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.customer.notFound", id));
    }
}
