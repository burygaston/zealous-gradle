package com.taskmanager.mapper;

import com.taskmanager.dto.UserDTO;
import com.taskmanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between User entity and UserDTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts User entity to UserDTO.
     *
     * @param user the user entity
     * @return the user DTO
     */
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserDTO toDTO(User user);

    /**
     * Converts UserDTO to User entity.
     *
     * @param userDTO the user DTO
     * @return the user entity
     */
    User toEntity(UserDTO userDTO);
}