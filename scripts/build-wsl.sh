#!/bin/bash

# Run Maven build with environment variables from .env/dev.env
# Usage: ./scripts/build-wsl.sh

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MICROSERVICE_ID=""
CREATE_NEW=false

usage() {
    echo "Usage: $0 [--id <microservice-id> | --create]"
    echo ""
    echo "  --id <id>   Update binary for an existing microservice with the given ID"
    echo "  --create    Create a new microservice"
    exit 1
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --id)
            MICROSERVICE_ID="$2"
            shift 2
            ;;
        --create)
            CREATE_NEW=true
            shift
            ;;
        *)
            usage
            ;;
    esac
done


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

shopt -s nullglob
ZIP_FILES=( "$PROJECT_ROOT/target"/*.zip )
shopt -u nullglob

if [ ${#ZIP_FILES[@]} -eq 1 ]; then
    ZIP_FILE="${ZIP_FILES[0]}"
    echo "=== Using zip artifact: $ZIP_FILE ==="
elif [ ${#ZIP_FILES[@]} -eq 0 ]; then
    echo "=== No zip file found under $PROJECT_ROOT/target ==="
    exit 1
else
    echo "=== Multiple zip files found under $PROJECT_ROOT/target; expected exactly one ==="
    printf ' - %s\n' "${ZIP_FILES[@]}"
    exit 1
fi

echo "=== Deploying to Cumulocity..."
if [[ "$CREATE_NEW" == true ]]; then
    c8y microservices create --file "$ZIP_FILE"
else
    c8y microservices createBinary --id "$MICROSERVICE_ID" --file "$ZIP_FILE"
fi

echo "==> Done."

