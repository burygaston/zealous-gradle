// Jest setup file
require('@testing-library/jest-dom');

// Mock fetch globally
global.fetch = jest.fn();

// Mock console methods to reduce noise in tests
global.console = {
  ...console,
  error: jest.fn(),
  warn: jest.fn(),
};

// Setup DOM
beforeEach(() => {
  // Reset fetch mock
  fetch.mockClear();

  // Clear document body
  document.body.innerHTML = '';
});
