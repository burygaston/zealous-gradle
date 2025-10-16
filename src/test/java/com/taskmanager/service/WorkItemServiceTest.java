package com.taskmanager.service;

import com.taskmanager.dto.WorkItemDTO;
import com.taskmanager.mapper.WorkItemMapper;
import com.taskmanager.model.User;
import com.taskmanager.model.WorkItem;
import com.taskmanager.model.WorkItemStatus;
import com.taskmanager.repository.LabelRepository;
import com.taskmanager.repository.WorkItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WorkItemService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Work Item Service Tests")
class WorkItemServiceTest {

    @Mock
    private WorkItemRepository workItemRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private WorkItemMapper workItemMapper;

    @InjectMocks
    private WorkItemService workItemService;

    private User testUser;
    private WorkItem testWorkItem;
    private WorkItemDTO testWorkItemDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testWorkItem = new WorkItem();
        testWorkItem.setId(1L);
        testWorkItem.setTitle("Test Task");
        testWorkItem.setDescription("Test Description");
        testWorkItem.setStatus(WorkItemStatus.TODO);
        testWorkItem.setDeadline(LocalDateTime.now().plusDays(1));
        testWorkItem.setPriority(1);
        testWorkItem.setUser(testUser);
        testWorkItem.setLabels(new HashSet<>());

        testWorkItemDTO = new WorkItemDTO();
        testWorkItemDTO.setId(1L);
        testWorkItemDTO.setTitle("Test Task");
        testWorkItemDTO.setDescription("Test Description");
        testWorkItemDTO.setStatus(WorkItemStatus.TODO);
        testWorkItemDTO.setDeadline(LocalDateTime.now().plusDays(1));
        testWorkItemDTO.setPriority(1);
        testWorkItemDTO.setLabels(new HashSet<>());
    }

    @Test
    @DisplayName("Should get all work items for user")
    void shouldGetAllWorkItems() {
        List<WorkItem> workItems = Arrays.asList(testWorkItem);
        List<WorkItemDTO> workItemDTOs = Arrays.asList(testWorkItemDTO);

        when(workItemRepository.findByUserOrderByDeadlineAsc(testUser)).thenReturn(workItems);
        when(workItemMapper.toDTOList(workItems)).thenReturn(workItemDTOs);

        List<WorkItemDTO> result = workItemService.getAllWorkItems(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        verify(workItemRepository).findByUserOrderByDeadlineAsc(testUser);
    }

    @Test
    @DisplayName("Should get work items by status")
    void shouldGetWorkItemsByStatus() {
        List<WorkItem> workItems = Arrays.asList(testWorkItem);
        List<WorkItemDTO> workItemDTOs = Arrays.asList(testWorkItemDTO);

        when(workItemRepository.findByUserAndStatus(testUser, WorkItemStatus.TODO)).thenReturn(workItems);
        when(workItemMapper.toDTOList(workItems)).thenReturn(workItemDTOs);

        List<WorkItemDTO> result = workItemService.getWorkItemsByStatus(testUser, WorkItemStatus.TODO);

        assertThat(result).hasSize(1);
        verify(workItemRepository).findByUserAndStatus(testUser, WorkItemStatus.TODO);
    }

    @Test
    @DisplayName("Should get overdue work items")
    void shouldGetOverdueWorkItems() {
        WorkItem overdueItem = new WorkItem();
        overdueItem.setDeadline(LocalDateTime.now().minusDays(1));
        overdueItem.setStatus(WorkItemStatus.TODO);

        List<WorkItem> workItems = Arrays.asList(overdueItem);
        List<WorkItemDTO> workItemDTOs = Arrays.asList(testWorkItemDTO);

        when(workItemRepository.findOverdueWorkItems(eq(testUser), any(LocalDateTime.class), anyList()))
                .thenReturn(workItems);
        when(workItemMapper.toDTOList(workItems)).thenReturn(workItemDTOs);

        List<WorkItemDTO> result = workItemService.getOverdueWorkItems(testUser);

        assertThat(result).hasSize(1);
        verify(workItemRepository).findOverdueWorkItems(eq(testUser), any(LocalDateTime.class), anyList());
    }

    @Test
    @DisplayName("Should get work item by ID")
    void shouldGetWorkItemById() {
        when(workItemRepository.findById(1L)).thenReturn(Optional.of(testWorkItem));
        when(workItemMapper.toDTO(testWorkItem)).thenReturn(testWorkItemDTO);

        WorkItemDTO result = workItemService.getWorkItemById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(workItemRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when work item not found")
    void shouldThrowExceptionWhenNotFound() {
        when(workItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workItemService.getWorkItemById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Work item not found");
    }

    @Test
    @DisplayName("Should create work item")
    void shouldCreateWorkItem() {
        when(workItemMapper.toEntity(testWorkItemDTO)).thenReturn(testWorkItem);
        when(workItemRepository.save(any(WorkItem.class))).thenReturn(testWorkItem);
        when(workItemMapper.toDTO(testWorkItem)).thenReturn(testWorkItemDTO);

        WorkItemDTO result = workItemService.createWorkItem(testWorkItemDTO, testUser);

        assertThat(result).isNotNull();
        verify(workItemRepository).save(any(WorkItem.class));
    }

    @Test
    @DisplayName("Should update work item")
    void shouldUpdateWorkItem() {
        WorkItemDTO updateDTO = new WorkItemDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setStatus(WorkItemStatus.IN_PROGRESS);
        updateDTO.setDeadline(LocalDateTime.now().plusDays(2));
        updateDTO.setPriority(2);
        updateDTO.setLabels(new HashSet<>());

        when(workItemRepository.findById(1L)).thenReturn(Optional.of(testWorkItem));
        when(workItemRepository.save(any(WorkItem.class))).thenReturn(testWorkItem);
        when(workItemMapper.toDTO(testWorkItem)).thenReturn(updateDTO);

        WorkItemDTO result = workItemService.updateWorkItem(1L, updateDTO, testUser);

        assertThat(result).isNotNull();
        verify(workItemRepository).save(any(WorkItem.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-owned work item")
    void shouldThrowExceptionWhenUpdatingNonOwnedWorkItem() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(workItemRepository.findById(1L)).thenReturn(Optional.of(testWorkItem));

        assertThatThrownBy(() -> workItemService.updateWorkItem(1L, testWorkItemDTO, differentUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to user");
    }

    @Test
    @DisplayName("Should delete work item")
    void shouldDeleteWorkItem() {
        when(workItemRepository.findById(1L)).thenReturn(Optional.of(testWorkItem));
        doNothing().when(workItemRepository).delete(testWorkItem);

        workItemService.deleteWorkItem(1L, testUser);

        verify(workItemRepository).delete(testWorkItem);
    }

    @Test
    @DisplayName("Should count work items by status")
    void shouldCountByStatus() {
        when(workItemRepository.countByUserAndStatus(testUser, WorkItemStatus.TODO)).thenReturn(5L);

        long count = workItemService.countByStatus(testUser, WorkItemStatus.TODO);

        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should count total work items")
    void shouldCountTotal() {
        when(workItemRepository.countByUser(testUser)).thenReturn(10L);

        long count = workItemService.countTotal(testUser);

        assertThat(count).isEqualTo(10L);
    }

    @Test
    @Tag("flaky")
    @DisplayName("Flaky test that randomly fails 30% of the time")
    void flakyTestForBuildkiteDetection() {
        // This test randomly fails to demonstrate Buildkite's flaky test detection
        double random = Math.random();
        assertThat(random).isGreaterThan(0.3);
    }
}