#!/bin/bash

# Run Maven build with environment variables from .env/dev.env
# Usage: ./scripts/build-wsl.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load and EXPORT environment variables (stripping Windows line endings)
set -a
source <(tr -d '\r' < "$PROJECT_ROOT/.env/dev.env")
set +a

# Display loaded variables (without password for security)
echo ""
echo "=== Environment Variables Loaded ==="
echo "C8Y_BOOTSTRAP_TENANT: $C8Y_BOOTSTRAP_TENANT"
echo "C8Y_BASEURL: $C8Y_BASEURL"
echo "C8Y_BOOTSTRAP_USER: $C8Y_BOOTSTRAP_USER"
echo "C8Y_MICROSERVICE_ISOLATION: $C8Y_MICROSERVICE_ISOLATION"
echo "==================================="
echo ""

echo "=== Starting Build ==="
cd "$PROJECT_ROOT" && mvn clean install

if [ $? -eq 0 ]; then
    echo "=== Build completed successfully ==="
else
    echo "=== Build failed ==="
    exit 1
fi

