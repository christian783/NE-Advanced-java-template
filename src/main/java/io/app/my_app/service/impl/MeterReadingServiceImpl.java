package io.app.my_app.service.impl;

import io.app.my_app.exception.BadRequestException;
import io.app.my_app.exception.EntityNotFoundException;
import io.app.my_app.mapper.MeterReadingMapper;
import io.app.my_app.model.Meter;
import io.app.my_app.model.MeterReading;
import io.app.my_app.model.User;
import io.app.my_app.model.dtos.reading.MeterReadingFilter;
import io.app.my_app.model.dtos.reading.MeterReadingRequest;
import io.app.my_app.model.dtos.reading.MeterReadingResponse;
import io.app.my_app.model.enums.UtilityStatus;
import io.app.my_app.repository.MeterReadingRepository;
import io.app.my_app.service.MeterReadingService;
import io.app.my_app.service.MeterService;
import io.app.my_app.specification.MeterReadingSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterService meterService;
    private final MeterReadingMapper meterReadingMapper;
    private final MeterReadingSpec meterReadingSpec;

    @Override
    @Transactional(readOnly = true)
    public Page<MeterReadingResponse> findAll(MeterReadingFilter filter, Pageable pageable) {
        return meterReadingRepository.findAll(meterReadingSpec.hasFilters(filter), pageable)
                .map(meterReadingMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MeterReadingResponse findById(UUID id) {
        return meterReadingMapper.toResponse(findReading(id));
    }

    @Transactional
    public MeterReadingResponse capture(MeterReadingRequest request) {
        Meter meter = meterService.findActiveMeter(request.getMeterId());
        if (meter.getCustomer().getStatus() != UtilityStatus.ACTIVE) {
            throw new BadRequestException("exceptions.customer.inactive");
        }
        if (request.getCurrentReading().compareTo(request.getPreviousReading()) <= 0) {
            throw new BadRequestException("exceptions.reading.currentMustExceedPrevious");
        }

        int month = request.getReadingDate().getMonthValue();
        int year = request.getReadingDate().getYear();
        if (meterReadingRepository.existsByMeterIdAndReadingMonthAndReadingYear(meter.getId(), month, year)) {
            throw new BadRequestException("exceptions.reading.duplicateMonthlyReading", meter.getMeterNumber(), month, year);
        }

        BigDecimal consumption = request.getCurrentReading().subtract(request.getPreviousReading());
        MeterReading reading = meterReadingMapper.toEntity(request);
        reading.setMeter(meter);
        reading.setOperator(currentUserOrNull());
        reading.setReadingMonth(month);
        reading.setReadingYear(year);
        reading.setConsumption(consumption);
        return meterReadingMapper.toResponse(meterReadingRepository.save(reading));
    }

    @Override
    public MeterReading findReading(UUID id) {
        return meterReadingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exceptions.reading.notFound", id));
    }

    private User currentUserOrNull() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return null;
        }
        return user;
    }
}
