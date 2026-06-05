package io.app.my_app.mapper;

import io.app.my_app.model.MeterReading;
import io.app.my_app.model.dtos.reading.MeterReadingRequest;
import io.app.my_app.model.dtos.reading.MeterReadingResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterReadingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meter", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "readingMonth", ignore = true)
    @Mapping(target = "readingYear", ignore = true)
    @Mapping(target = "consumption", ignore = true)
    MeterReading toEntity(MeterReadingRequest request);

    @Mapping(target = "meterId", source = "meter.id")
    @Mapping(target = "meterNumber", source = "meter.meterNumber")
    @Mapping(target = "meterType", source = "meter.meterType")
    MeterReadingResponse toResponse(MeterReading reading);
}
