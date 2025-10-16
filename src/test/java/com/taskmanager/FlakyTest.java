package com.taskmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Intentionally flaky tests to demonstrate Buildkite Test Suite flaky test detection.
 * These tests have a 25-30% failure rate to simulate real-world flakiness.
 */
public class FlakyTest {

    private static final Random random = new Random();
    private static final double FLAKY_PROBABILITY = 0.275; // 27.5% failure rate

    @Test
    @DisplayName("Flaky Test 1: Random assertion")
    public void flakyTest1() {
        if (random.nextDouble() < FLAKY_PROBABILITY) {
            fail("Flaky test failed randomly");
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Flaky Test 2: Timing sensitive")
    public void flakyTest2() {
        long timestamp = System.currentTimeMillis();
        if (timestamp % 4 == 0) { // ~25% failure rate
            fail("Flaky test failed due to timing");
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Flaky Test 3: Random number comparison")
    public void flakyTest3() {
        int value = random.nextInt(100);
        if (value < 27) { // ~27% failure rate
            fail("Random value was too low: " + value);
        }
        assertTrue(value >= 0);
    }

    @Test
    @DisplayName("Flaky Test 4: Probabilistic assertion")
    public void flakyTest4() {
        if (random.nextDouble() < FLAKY_PROBABILITY) {
            assertEquals(1, 2, "Flaky assertion failed");
        }
        assertNotNull(this);
    }

    @Test
    @DisplayName("Flaky Test 5: Thread timing")
    public void flakyTest5() throws InterruptedException {
        Thread.sleep(random.nextInt(10));
        if (System.nanoTime() % 4 == 0) { // ~25% failure rate
            fail("Thread timing caused failure");
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Flaky Test 6: Random boolean")
    public void flakyTest6() {
        boolean shouldFail = random.nextInt(100) < 28; // ~28% failure rate
        if (shouldFail) {
            fail("Random boolean caused failure");
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Flaky Test 7: List size check")
    public void flakyTest7() {
        int size = random.nextInt(100);
        if (size < 26) { // ~26% failure rate
            fail("List size check failed: " + size);
        }
        assertTrue(size >= 0);
    }

    @Test
    @DisplayName("Flaky Test 8: State dependent")
    public void flakyTest8() {
        if (random.nextDouble() < FLAKY_PROBABILITY) {
            assertFalse(true, "State check failed");
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Flaky Test 9: Concurrent operation")
    public void flakyTest9() {
        int value = random.nextInt(4);
        if (value == 0) { // ~25% failure rate
            fail("Concurrent operation failed");
        }
        assertTrue(value >= 0);
    }

    @Test
    @DisplayName("Flaky Test 10: Resource availability")
    public void flakyTest10() {
        if (System.currentTimeMillis() % 4 == 1) { // ~25% failure rate
            fail("Resource not available");
        }
        assertTrue(true);
    }
}
