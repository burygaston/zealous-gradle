package com.taskmanager.repository;

import com.taskmanager.model.Label;
import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Label entity operations.
 */
@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    /**
     * Finds all labels for a specific user.
     *
     * @param user the user whose labels to retrieve
     * @return list of labels
     */
    List<Label> findByUser(User user);

    /**
     * Finds a label by user and name.
     *
     * @param user the user
     * @param name the label name
     * @return an Optional containing the label if found
     */
    Optional<Label> findByUserAndName(User user, String name);

    /**
     * Checks if a label exists for a user with the given name.
     *
     * @param user the user
     * @param name the label name
     * @return true if the label exists
     */
    boolean existsByUserAndName(User user, String name);
}