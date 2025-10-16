package com.taskmanager.mapper;

import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.model.WorkItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for converting between WorkItem entity and WorkItemDTO.
 */
@Mapper(componentModel = "spring", uses = {LabelMapper.class})
public interface WorkItemMapper {

    /**
     * Converts WorkItem entity to WorkItemDTO.
     *
     * @param workItem the work item entity
     * @return the work item DTO
     */
    @Mapping(target = "overdue", expression = "java(workItem.isOverdue())")
    WorkItemDTO toDTO(WorkItem workItem);

    /**
     * Converts WorkItemDTO to WorkItem entity.
     *
     * @param workItemDTO the work item DTO
     * @return the work item entity
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "overdue", ignore = true)
    WorkItem toEntity(WorkItemDTO workItemDTO);

    /**
     * Converts a list of WorkItem entities to a list of WorkItemDTOs.
     *
     * @param workItems the work item entities
     * @return the work item DTOs
     */
    List<WorkItemDTO> toDTOList(List<WorkItem> workItems);
}