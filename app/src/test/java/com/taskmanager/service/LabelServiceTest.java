package com.taskmanager.service;

import com.taskmanager.dto.LabelDTO;
import com.taskmanager.mapper.LabelMapper;
import com.taskmanager.model.Label;
import com.taskmanager.model.User;
import com.taskmanager.repository.LabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LabelService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Label Service Tests")
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelService labelService;

    private User testUser;
    private Label testLabel;
    private LabelDTO testLabelDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testLabel = new Label();
        testLabel.setId(1L);
        testLabel.setName("Urgent");
        testLabel.setColor("#FF0000");
        testLabel.setIcon("🔥");
        testLabel.setUser(testUser);

        testLabelDTO = new LabelDTO();
        testLabelDTO.setId(1L);
        testLabelDTO.setName("Urgent");
        testLabelDTO.setColor("#FF0000");
        testLabelDTO.setIcon("🔥");
    }

    @Test
    @DisplayName("Should get all labels for user")
    void shouldGetAllLabels() {
        List<Label> labels = Arrays.asList(testLabel);

        when(labelRepository.findByUser(testUser)).thenReturn(labels);
        when(labelMapper.toDTO(testLabel)).thenReturn(testLabelDTO);

        List<LabelDTO> result = labelService.getAllLabels(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Urgent");
        verify(labelRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("Should get label by ID")
    void shouldGetLabelById() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelMapper.toDTO(testLabel)).thenReturn(testLabelDTO);

        LabelDTO result = labelService.getLabelById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Urgent");
        verify(labelRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when label not found")
    void shouldThrowExceptionWhenNotFound() {
        when(labelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.getLabelById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Label not found");
    }

    @Test
    @DisplayName("Should create label")
    void shouldCreateLabel() {
        when(labelRepository.existsByUserAndName(testUser, "Urgent")).thenReturn(false);
        when(labelMapper.toEntity(testLabelDTO)).thenReturn(testLabel);
        when(labelRepository.save(any(Label.class))).thenReturn(testLabel);
        when(labelMapper.toDTO(testLabel)).thenReturn(testLabelDTO);

        LabelDTO result = labelService.createLabel(testLabelDTO, testUser);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Urgent");
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate label")
    void shouldThrowExceptionForDuplicateLabel() {
        when(labelRepository.existsByUserAndName(testUser, "Urgent")).thenReturn(true);

        assertThatThrownBy(() -> labelService.createLabel(testLabelDTO, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Label with name already exists");
    }

    @Test
    @DisplayName("Should update label")
    void shouldUpdateLabel() {
        LabelDTO updateDTO = new LabelDTO();
        updateDTO.setName("Updated");
        updateDTO.setColor("#00FF00");
        updateDTO.setIcon("✅");

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        when(labelRepository.save(any(Label.class))).thenReturn(testLabel);
        when(labelMapper.toDTO(testLabel)).thenReturn(updateDTO);

        LabelDTO result = labelService.updateLabel(1L, updateDTO, testUser);

        assertThat(result).isNotNull();
        verify(labelRepository).save(any(Label.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-owned label")
    void shouldThrowExceptionWhenUpdatingNonOwnedLabel() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));

        assertThatThrownBy(() -> labelService.updateLabel(1L, testLabelDTO, differentUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to user");
    }

    @Test
    @DisplayName("Should delete label")
    void shouldDeleteLabel() {
        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));
        doNothing().when(labelRepository).delete(testLabel);

        labelService.deleteLabel(1L, testUser);

        verify(labelRepository).delete(testLabel);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-owned label")
    void shouldThrowExceptionWhenDeletingNonOwnedLabel() {
        User differentUser = new User();
        differentUser.setId(2L);

        when(labelRepository.findById(1L)).thenReturn(Optional.of(testLabel));

        assertThatThrownBy(() -> labelService.deleteLabel(1L, differentUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to user");
    }
}