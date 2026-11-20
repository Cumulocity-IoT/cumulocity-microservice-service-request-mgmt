#!/usr/bin/env zsh

# Run Maven build with environment variables from .env/dev.env
# Usage: ./scripts/build-zsh.sh [--id <microservice-id> | --create]
#        Without options, the script only builds and does not deploy.

SCRIPT_NAME="$0"
SCRIPT_DIR="${0:A:h}"
PROJECT_ROOT="${SCRIPT_DIR:h}"
MICROSERVICE_ID=""
CREATE_NEW=false

usage() {
    echo "Usage: $SCRIPT_NAME [--id <microservice-id> | --create]"
    echo ""
    echo "  (no option) Build only, do not deploy"
    echo "  --id <id>   Build and update binary for an existing microservice with the given ID"
    echo "  --create    Build and create a new microservice"
    exit 1
}

while (( $# > 0 )); do
    case "$1" in
        --id)
            [[ -n "$2" ]] || usage
            MICROSERVICE_ID="$2"
            shift 2
            ;;
        --create)
            CREATE_NEW=true
            shift
            ;;
        -h|--help|*)
            usage
            ;;
    esac
done

if [[ "$CREATE_NEW" == true && -n "$MICROSERVICE_ID" ]]; then
    echo "=== Use either --id or --create, not both ==="
    usage
fi

DEPLOY=false
if [[ "$CREATE_NEW" == true || -n "$MICROSERVICE_ID" ]]; then
    DEPLOY=true
fi

ENV_FILE="$PROJECT_ROOT/.env/dev.env"
if [[ ! -f "$ENV_FILE" ]]; then
    echo "=== Environment file not found: $ENV_FILE ==="
    exit 1
fi

# Load and EXPORT environment variables (stripping Windows line endings)
set -a
source <(tr -d '\r' < "$ENV_FILE")
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

# The Cumulocity packaging plugin talks to Docker via DOCKER_HOST or /var/run/docker.sock
# and ignores Docker contexts. With Colima (or similar) the socket lives elsewhere, so
# derive DOCKER_HOST from the active Docker context when it is not set.
if [[ -z "$DOCKER_HOST" && ! -S /var/run/docker.sock ]] && (( $+commands[docker] )); then
    DOCKER_HOST="$(docker context inspect --format '{{.Endpoints.docker.Host}}' 2>/dev/null)"
    if [[ -n "$DOCKER_HOST" ]]; then
        export DOCKER_HOST
        echo "=== Using DOCKER_HOST from active Docker context: $DOCKER_HOST ==="
    else
        unset DOCKER_HOST
    fi
fi

echo "=== Starting Build ==="
if cd "$PROJECT_ROOT" && mvn clean install; then
    echo "=== Build completed successfully ==="
else
    echo "=== Build failed ==="
    exit 1
fi

if [[ "$DEPLOY" != true ]]; then
    echo "==> Build only, skipping deployment. Done."
    exit 0
fi

# (N) = nullglob for this pattern only
ZIP_FILES=( "$PROJECT_ROOT"/target/*.zip(N) )

if (( ${#ZIP_FILES} == 1 )); then
    ZIP_FILE="${ZIP_FILES[1]}"
    echo "=== Using zip artifact: $ZIP_FILE ==="
elif (( ${#ZIP_FILES} == 0 )); then
    echo "=== No zip file found under $PROJECT_ROOT/target ==="
    exit 1
else
    echo "=== Multiple zip files found under $PROJECT_ROOT/target; expected exactly one ==="
    printf ' - %s\n' "${ZIP_FILES[@]}"
    exit 1
fi

echo "=== Deploying to Cumulocity..."
if [[ "$CREATE_NEW" == true ]]; then
    c8y microservices create --file "$ZIP_FILE" || exit 1
else
    c8y microservices createBinary --id "$MICROSERVICE_ID" --file "$ZIP_FILE" || exit 1
fi

echo "==> Done."
