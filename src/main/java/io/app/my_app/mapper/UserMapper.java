package io.app.my_app.mapper;

import io.app.my_app.model.User;
import io.app.my_app.model.dtos.user.UserCreateRequest;
import io.app.my_app.model.dtos.user.UserResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "fullName", source = "fullName", qualifiedByName = "normalize")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalize")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "normalize")
    User toEntity(UserCreateRequest request);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
