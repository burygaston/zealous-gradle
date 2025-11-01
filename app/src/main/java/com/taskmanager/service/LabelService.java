package com.taskmanager.service;

import com.taskmanager.dto.LabelDTO;
import com.taskmanager.mapper.LabelMapper;
import com.taskmanager.model.Label;
import com.taskmanager.model.User;
import com.taskmanager.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing labels.
 */
@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    /**
     * Retrieves all labels for a user.
     *
     * @param user the user
     * @return list of label DTOs
     */
    @Transactional(readOnly = true)
    public List<LabelDTO> getAllLabels(User user) {
        return labelRepository.findByUser(user)
                .stream()
                .map(labelMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds a label by ID.
     *
     * @param id the label ID
     * @return the label DTO
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public LabelDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label not found: " + id));
        return labelMapper.toDTO(label);
    }

    /**
     * Creates a new label.
     *
     * @param labelDTO the label DTO
     * @param user     the user
     * @return the created label DTO
     * @throws IllegalArgumentException if label name already exists for user
     */
    @Transactional
    public LabelDTO createLabel(LabelDTO labelDTO, User user) {
        if (labelRepository.existsByUserAndName(user, labelDTO.getName())) {
            throw new IllegalArgumentException("Label with name already exists: " + labelDTO.getName());
        }

        Label label = labelMapper.toEntity(labelDTO);
        label.setUser(user);
        Label saved = labelRepository.save(label);
        return labelMapper.toDTO(saved);
    }

    /**
     * Updates an existing label.
     *
     * @param id       the label ID
     * @param labelDTO the updated label DTO
     * @param user     the user
     * @return the updated label DTO
     * @throws IllegalArgumentException if not found or user mismatch
     */
    @Transactional
    public LabelDTO updateLabel(Long id, LabelDTO labelDTO, User user) {
        Label existing = labelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label not found: " + id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Label does not belong to user");
        }

        existing.setName(labelDTO.getName());
        existing.setColor(labelDTO.getColor());
        existing.setIcon(labelDTO.getIcon());

        Label updated = labelRepository.save(existing);
        return labelMapper.toDTO(updated);
    }

    /**
     * Deletes a label.
     *
     * @param id   the label ID
     * @param user the user
     * @throws IllegalArgumentException if not found or user mismatch
     */
    @Transactional
    public void deleteLabel(Long id, User user) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label not found: " + id));

        if (!label.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Label does not belong to user");
        }

        labelRepository.delete(label);
    }
}