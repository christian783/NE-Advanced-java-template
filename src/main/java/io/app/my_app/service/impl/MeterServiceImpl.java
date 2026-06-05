package io.app.my_app.service.impl;

import io.app.my_app.exception.BadRequestException;
import io.app.my_app.exception.DuplicateRecordException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.MeterMapper;
import io.app.my_app.model.Customer;
import io.app.my_app.model.Meter;
import io.app.my_app.model.dtos.meter.MeterFilter;
import io.app.my_app.model.dtos.meter.MeterRequest;
import io.app.my_app.model.dtos.meter.MeterResponse;
import io.app.my_app.model.enums.UtilityStatus;
import io.app.my_app.repository.MeterRepository;
import io.app.my_app.service.CustomerService;
import io.app.my_app.service.MeterService;
import io.app.my_app.specification.MeterSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final CustomerService customerService;
    private final MeterMapper meterMapper;
    private final MeterSpec meterSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<MeterResponse> findAll(MeterFilter filter, Pageable pageable) {
        return meterRepository.findAll(meterSpec.hasFilters(filter), pageable)
                .map(meterMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MeterResponse findById(UUID id) {
        return meterMapper.toResponse(findMeter(id));
    }

    @Override
    @Transactional
    public MeterResponse create(MeterRequest request) {
        String meterNumber = meterMapper.normalize(request.getMeterNumber());
        if (meterRepository.existsByMeterNumber(meterNumber)) {
            throw new DuplicateRecordException("exceptions.meter.numberAlreadyExists", meterNumber);
        }
        Customer customer = customerService.findCustomer(request.getCustomerId());
        if (customer.getStatus() != UtilityStatus.ACTIVE) {
            throw new BadRequestException("exceptions.customer.inactive");
        }

        Meter meter = meterMapper.toEntity(request);
        meter.setCustomer(customer);
        return meterMapper.toResponse(meterRepository.save(meter));
    }

    @Override
    @Transactional
    public MeterResponse update(UUID id, MeterRequest request) {
        Meter meter = findMeter(id);
        String meterNumber = meterMapper.normalize(request.getMeterNumber());
        if (meterRepository.existsByMeterNumberAndIdNot(meterNumber, id)) {
            throw new DuplicateRecordException("exceptions.meter.numberAlreadyExists", meterNumber);
        }
        Customer customer = customerService.findCustomer(request.getCustomerId());
        if (customer.getStatus() != UtilityStatus.ACTIVE) {
            throw new BadRequestException("exceptions.customer.inactive");
        }

        meterMapper.updateEntity(request, meter);
        meter.setCustomer(customer);
        return meterMapper.toResponse(meterRepository.save(meter));
    }

    @Override
    public Meter findMeter(UUID id) {
        return meterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.meter.notFound", id));
    }

    @Override
    public Meter findActiveMeter(UUID id) {
        return meterRepository.findByIdAndStatus(id, UtilityStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.meter.activeNotFound", id));
    }
}
