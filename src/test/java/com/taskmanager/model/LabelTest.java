package com.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Label model.
 */
class LabelTest {

    private Label label;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        label = new Label();
        label.setId(1L);
        label.setName("Bug");
        label.setColor("#FF0000");
        label.setUser(user);
    }

    @Test
    @DisplayName("Should have valid ID")
    void shouldHaveValidId() {
        assertThat(label.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should have valid name")
    void shouldHaveValidName() {
        assertThat(label.getName()).isEqualTo("Bug");
    }

    @Test
    @DisplayName("Should have valid color")
    void shouldHaveValidColor() {
        assertThat(label.getColor()).isEqualTo("#FF0000");
    }

    @Test
    @DisplayName("Should have valid user association")
    void shouldHaveValidUserAssociation() {
        assertThat(label.getUser()).isEqualTo(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF"})
    @DisplayName("Should accept valid color codes")
    void shouldAcceptValidColorCodes(String color) {
        label.setColor(color);
        assertThat(label.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("Should allow name update")
    void shouldAllowNameUpdate() {
        label.setName("Feature");
        assertThat(label.getName()).isEqualTo("Feature");
    }

    @Test
    @DisplayName("Should allow color update")
    void shouldAllowColorUpdate() {
        label.setColor("#00FF00");
        assertThat(label.getColor()).isEqualTo("#00FF00");
    }

    @Test
    @DisplayName("Should handle priority label name")
    void shouldHandlePriorityLabelName() {
        label.setName("Priority");
        assertThat(label.getName()).isEqualTo("Priority");
    }

    @Test
    @DisplayName("Should handle red color")
    void shouldHandleRedColor() {
        label.setColor("#FF0000");
        assertThat(label.getColor()).isEqualTo("#FF0000");
    }

    @Test
    @DisplayName("Should handle green color")
    void shouldHandleGreenColor() {
        label.setColor("#00FF00");
        assertThat(label.getColor()).isEqualTo("#00FF00");
    }

    @Test
    @DisplayName("Should handle blue color")
    void shouldHandleBlueColor() {
        label.setColor("#0000FF");
        assertThat(label.getColor()).isEqualTo("#0000FF");
    }

    @Test
    @DisplayName("Should handle black color")
    void shouldHandleBlackColor() {
        label.setColor("#000000");
        assertThat(label.getColor()).isEqualTo("#000000");
    }

    @Test
    @DisplayName("Should handle white color")
    void shouldHandleWhiteColor() {
        label.setColor("#FFFFFF");
        assertThat(label.getColor()).isEqualTo("#FFFFFF");
    }

    @Test
    @DisplayName("Should handle bug label name")
    void shouldHandleBugLabelName() {
        label.setName("Bug");
        assertThat(label.getName()).isEqualTo("Bug");
    }

    @Test
    @DisplayName("Should handle feature label name")
    void shouldHandleFeatureLabelName() {
        label.setName("Feature");
        assertThat(label.getName()).isEqualTo("Feature");
    }

    @Test
    @DisplayName("Should handle enhancement label name")
    void shouldHandleEnhancementLabelName() {
        label.setName("Enhancement");
        assertThat(label.getName()).isEqualTo("Enhancement");
    }

    @Test
    @DisplayName("Should handle documentation label name")
    void shouldHandleDocumentationLabelName() {
        label.setName("Documentation");
        assertThat(label.getName()).isEqualTo("Documentation");
    }

    @Test
    @DisplayName("Should handle urgent label name")
    void shouldHandleUrgentLabelName() {
        label.setName("Urgent");
        assertThat(label.getName()).isEqualTo("Urgent");
    }
}
