package com.taskmanager.cucumber;

import com.taskmanager.common.CommonUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Common Utility feature tests.
 */
public class CommonUtilSteps {

    private String appName;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        // Application context is already loaded by CucumberSpringConfiguration
        assertNotNull(CommonUtil.class);
    }

    @When("I request the application name")
    public void iRequestTheApplicationName() {
        appName = CommonUtil.getAppName();
    }

    @Then("the application name should be {string}")
    public void theApplicationNameShouldBe(String expectedName) {
        assertEquals(expectedName, appName);
    }

    @When("I call the new V3 method")
    public void iCallTheNewV3Method() {
        System.setOut(new PrintStream(outputStream));
        CommonUtil.newMethod();
        System.setOut(originalOut);
    }

    @Then("the method should execute successfully")
    public void theMethodShouldExecuteSuccessfully() {
        String output = outputStream.toString();
        assertTrue(output.contains("This is a new V3 method!") || output.contains("This is a new V5 method!"));
        outputStream.reset();
    }

    @When("I call the new V5 method")
    public void iCallTheNewV5Method() {
        System.setOut(new PrintStream(outputStream));
        CommonUtil.newMethodV5();
        System.setOut(originalOut);
    }
}
