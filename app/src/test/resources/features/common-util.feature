Feature: Common Utility Functions
  As a developer
  I want to test common utility functions
  So that I can ensure they work correctly

  Scenario: Get application name
    Given the application is running
    When I request the application name
    Then the application name should be "Hello from Task Manager"

  Scenario: Call new method V3
    Given the application is running
    When I call the new V3 method
    Then the method should execute successfully

  Scenario: Call new method V5
    Given the application is running
    When I call the new V5 method
    Then the method should execute successfully
