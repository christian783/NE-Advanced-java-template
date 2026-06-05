package io.app.my_app.mapper;

import io.app.my_app.model.Tariff;
import io.app.my_app.model.TariffTier;
import io.app.my_app.model.dtos.tariff.*;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TariffMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tariffVersion", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "tiers", ignore = true)
    Tariff toEntity(TariffRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tariff", ignore = true)
    TariffTier toTierEntity(TariffTierRequest request);

    TariffResponse toResponse(Tariff tariff);

    TariffTierResponse toTierResponse(TariffTier tier);
}
