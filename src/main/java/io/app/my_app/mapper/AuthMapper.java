package io.app.my_app.mapper;

import io.app.my_app.model.User;
import io.app.my_app.model.dtos.auth.AuthenticationResponse;
import io.app.my_app.model.dtos.auth.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "fullName", source = "fullName", qualifiedByName = "normalize")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalize")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "normalize")
    User toUser(RegisterRequest request);

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    AuthenticationResponse toResponse(String accessToken, String refreshToken);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
