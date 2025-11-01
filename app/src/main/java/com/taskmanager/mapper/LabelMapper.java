package com.taskmanager.mapper;

import com.taskmanager.dto.LabelDTO;
import com.taskmanager.model.Label;
import org.mapstruct.Mapper;

import java.util.Set;

/**
 * Mapper for converting between Label entity and LabelDTO.
 */
@Mapper(componentModel = "spring")
public interface LabelMapper {

    /**
     * Converts Label entity to LabelDTO.
     *
     * @param label the label entity
     * @return the label DTO
     */
    LabelDTO toDTO(Label label);

    /**
     * Converts LabelDTO to Label entity.
     *
     * @param labelDTO the label DTO
     * @return the label entity
     */
    Label toEntity(LabelDTO labelDTO);

    /**
     * Converts a set of Label entities to a set of LabelDTOs.
     *
     * @param labels the label entities
     * @return the label DTOs
     */
    Set<LabelDTO> toDTOSet(Set<Label> labels);

    /**
     * Converts a set of LabelDTOs to a set of Label entities.
     *
     * @param labelDTOs the label DTOs
     * @return the label entities
     */
    Set<Label> toEntitySet(Set<LabelDTO> labelDTOs);
}