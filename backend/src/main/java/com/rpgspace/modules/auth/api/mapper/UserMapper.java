package com.rpgspace.modules.auth.api.mapper;

import com.rpgspace.modules.auth.api.dto.UserResponse;
import com.rpgspace.modules.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
