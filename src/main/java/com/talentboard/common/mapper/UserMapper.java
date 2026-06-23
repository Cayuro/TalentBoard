package com.talentboard.common.mapper;

import com.talentboard.user.dto.UserRegistrationRequest;
import com.talentboard.user.dto.UserResponse;
import com.talentboard.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRegistrationRequest request);
}
