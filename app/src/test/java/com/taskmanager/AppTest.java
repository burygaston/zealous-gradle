package com.taskmanager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    App classUnderTest = new App();

    @Test
    void appHasAGreeting() {
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
        assertEquals("Wrong Greeting", classUnderTest.getGreeting());
    }
}
