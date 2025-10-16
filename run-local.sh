#!/bin/bash

echo "=== Task Manager - Local H2 Setup ==="
echo ""
echo "This script will run the application with an in-memory H2 database."
echo "No Docker or MySQL required!"
echo ""

# Clean previous builds
echo "[1/3] Cleaning previous builds..."
./gradlew clean

# Build the application
echo ""
echo "[2/3] Building application..."
./gradlew build -x test

# Run with H2 profile
echo ""
echo "[3/3] Starting application with H2..."
echo ""
echo "Application will be available at: http://localhost:8080"
echo "Login credentials: demo / password"
echo "H2 Console: http://localhost:8080/h2-console"
echo ""
echo "Press Ctrl+C to stop"
echo ""

./gradlew bootRun --args='--spring.profiles.active=h2'
