# E2E compatibility testing against the Cumulocity CD test tenant

Status: Concept (draft)
Owner: Alexander Pester
Date: 2026-11-02

## 1. Goal

The Service Request Management microservice runs on customer tenants that follow the Cumulocity CD (continuous delivery) release train. The platform version on those tenants changes without a release of this microservice. We want to find incompatibilities (API changes, changed permissions, hosting changes, changed alarm or inventory behavior) before customers do.

Targets:

1. Run an automated E2E test suite against the existing test tenant once a week, and on demand.
2. Test the microservice as it really runs: deployed and subscribed on the tenant, called through `/service/service-request-mgmt/...`.
3. If the run fails, create a GitHub issue that contains the tenant component versions, the failed tests, and a link to the run.
4. Show what changed on the tenant since the last successful run, because a version change is the most likely cause of a new failure.

Out of scope: load and performance tests, UI (plugin) tests, tests against tenants other than the test tenant.

## 2. Current state

| Item | Today |
|------|-------|
| Test tenant | Already exists. Used by `maven.yml` and `maven_artifact.yml` through the secrets `TEST_URL`, `TEST_BOOTSTRAP_TENANT`, `TEST_BOOTSTRAP_USER`, `TEST_BOOTSTRAP_PASSWORD`. |
| API doc generation | `mvn package` starts the microservice locally (`spring-boot-maven-plugin` start/stop, profile `dev`) with the bootstrap credentials and pulls `/v3/api-docs` into `docs/openapi.json`. |
| Deployment to a tenant | `maven_artifact.yml` deploys the S package with `go-c8y-cli` (`C8Y_HOST`, `C8Y_USER`, `C8Y_PASSWORD`) on every push to `develop` and `main`. |
| Tests | Unit tests for the `service/c8y` layer and a `contextLoads` test. No test calls the REST API against a real tenant. |
| Platform SDK | Java SDK `c8y.version` 2025.81.0 (from `pom.xml`). |

Point to confirm: are the tenant behind `TEST_URL` and the tenant behind `C8Y_HOST` the same? This concept assumes that they are. If they are not, use the `C8Y_HOST` tenant, because the microservice is already deployed there.

## 3. Approach

### 3.1 Where the microservice runs during the test

| Option | How | Covers | Misses |
|--------|-----|--------|--------|
| A. Local in the GitHub runner | Same as the API doc generation: start the JAR with bootstrap credentials, tests call `localhost:8080`. | Java SDK against tenant REST APIs, alarm, inventory, event, and binary behavior. | Hosting: manifest (`apiVersion 2`), required roles, health probes, subscription, proxying through `/service/...`, `openApiSpec` exposure. |
| B. Deployed on the tenant (recommended) | Build the package, deploy it with `go-c8y-cli`, wait until healthy, tests call `{baseUrl}/service/service-request-mgmt/...`. | Everything in A plus the hosting layer, which is exactly what changes with CD releases. | Takes longer (image upload and startup, about 3 to 5 minutes). |

Recommendation: use option B for the weekly run. Option A stays useful for local debugging, because the same test suite can run against `localhost` by changing one property.

### 3.2 Which version of the microservice is tested

The weekly run uses a matrix with two refs:

- `main`: the latest release, which is what customers run.
- `develop`: the upcoming release, so that a problem is found before it ships.

Both entries deploy to the same tenant, so they run one after the other (see section 6, concurrency). If runtime matters, start with `main` only.

### 3.3 Overall flow

```
schedule (weekly) / workflow_dispatch
  │
  ├─ 1. Collect tenant component versions  ──► tenant-versions.json / .md (artifact)
  ├─ 2. Compare with versions of the last green run ──► "changed since last green run"
  ├─ 3. Build package (mvn package -DskipTests)
  ├─ 4. Deploy + subscribe microservice (go-c8y-cli), wait for /health = UP
  ├─ 5. Run E2E suite (mvn -Pe2e, JUnit 5 + REST Assured)
  ├─ 6. Cleanup test data (always)
  ├─ 7. Upload reports + versions as artifacts (always)
  └─ 8. On failure: create or update GitHub issue
        On success after a failure: comment on and close the open issue
```

## 4. Test suite

### 4.1 Technology

- JUnit 5 + REST Assured + Awaitility (for asynchronous checks), in the same repository.
- Test classes end with `E2E` (for example `ServiceRequestLifecycleE2E.java`) and live in `src/test/java/.../mgmt/e2e/`.
- A Maven profile `e2e` adds `maven-failsafe-plugin` with `<include>**/*E2E.java</include>`. The normal `mvn package` never runs these tests, so PR builds are unchanged.
- Configuration through environment variables only:

| Variable | Meaning |
|----------|---------|
| `E2E_BASE_URL` | Tenant URL, for example `https://e2e.example.cumulocity.com` |
| `E2E_SERVICE_PATH` | `/service/service-request-mgmt` (option B) or empty with `E2E_BASE_URL=http://localhost:8080` (option A) |
| `E2E_TENANT`, `E2E_USER`, `E2E_PASSWORD` | Test user. Cumulocity basic auth uses `tenant/user:password`. |
| `E2E_RUN_ID` | `github.run_id`, written to every object the tests create |

The tests call two APIs: the microservice API (the system under test) and the Cumulocity core API (to create devices and alarms and to check side effects, for example that an alarm received the service request reference).

### 4.2 Test data isolation

The tenant is shared with the API doc generation and with the deployment from `maven_artifact.yml`. Rules:

- Every managed object, alarm, and event that a test creates gets the fragment `e2e_test: { runId: <E2E_RUN_ID> }` and a name or text prefix `e2e-`.
- Each test class creates its own test device in `@BeforeAll` and deletes it with `?cascade=true` in `@AfterAll`.
- A separate cleanup step (`if: always()`) deletes everything with the fragment `e2e_test` that is older than 24 hours. This removes leftovers from aborted runs.
- Status and priority configurations that the tests create use IDs with the prefix `e2e-` so that existing tenant configuration is not changed. If a test must change global configuration, it saves the original and restores it.

### 4.3 Scenarios

Grouped by risk. P1 runs in every weekly run. P2 can be added in phase 2.

| ID | Prio | Scenario | Endpoints | What it detects |
|----|------|----------|-----------|-----------------|
| S01 | P1 | Microservice is subscribed, `/health` returns `UP`, microservice settings are readable | `/service/service-request-mgmt/health`, `/application/currentApplication/settings` | Hosting, manifest, subscription, role changes |
| S02 | P1 | Status configuration CRUD | `/api/service/request/status` | Tenant options and inventory storage |
| S03 | P1 | Priority configuration CRUD | `/api/service/request/priority` | Same as S02 |
| S04 | P1 | Service request lifecycle: create for device, get, list with filters and paging, update, delete | `/api/service/request`, `/api/service/request/{id}` | Inventory and event queries, paging, query language |
| S05 | P1 | Service request with alarm reference: create alarm, create service request for it, link alarm, check alarm fragments and device `sr_ActiveStatus` | `/api/service/request`, `/{id}/alarm`, `/device/sr_ActiveStatus/refresh` | Alarm API behavior, alarm update semantics, fragment handling |
| S06 | P1 | Comments CRUD | `/{id}/comment`, `/comment/{commentId}` | Event API, user lookups (`ROLE_USER_MANAGEMENT_READ`) |
| S07 | P1 | External adapter API: list for sync, update status and active flag | `/api/adapter/service/request/...` | The contract that FSM and ITS integrations depend on |
| S08 | P2 | Attachments on service request and comment: upload, download, compare bytes | `/{id}/attachment`, `/comment/{commentId}/attachment` | Binary API, multipart handling through the proxy |
| S09 | P2 | Context configuration CRUD and apply to alarm | `/api/context/config`, `/apply/alarm/{alarmId}` | Alarm and measurement queries |
| S10 | P2 | Event linking | `/{id}/event` | Event API |
| S11 | P2 | OpenAPI contract: `GET /service/service-request-mgmt/v3/api-docs` matches `docs/openapi.json` of the same ref | `/v3/api-docs` | Proxy changes, unintended API drift |

Not covered on purpose: the scheduled jobs in `ServiceRequestAlarmValidationService` (10-minute initial delay). Waiting for them makes runs slow and flaky. If needed, set `validation.job.scheduled.delay.millis` to a low value through a microservice setting for the E2E deployment, or expose a trigger endpoint for tests only.

### 4.4 Handling asynchronous behavior

Cumulocity processes some updates asynchronously (for example alarm updates and real-time notifications). Tests poll with Awaitility (timeout 60 seconds, poll every 2 seconds) instead of using fixed sleeps.

## 5. Tenant component versions

### 5.1 What is collected

| Component | Source |
|-----------|--------|
| Platform (backend) version | `GET /tenant/system/options/system/version` → `value` |
| Tenant ID and domain | `GET /tenant/currentTenant` → `name`, `domainName` |
| Web applications (Cockpit, Device Management, Administration, and others) | `GET /application/applicationsByTenant/{tenantId}?pageSize=2000` → `name`, `type`, `manifest.version` |
| Microservices on the tenant, including `service-request-mgmt` | Same call, filter `type == MICROSERVICE` |
| Microservice under test | Git ref, commit SHA, `pom.xml` version |
| Java SDK used by the build | `c8y.version` from `pom.xml` |

The test user needs `ROLE_OPTION_MANAGEMENT_READ` and `ROLE_APPLICATION_MANAGEMENT_READ` for these calls. Verify on the test tenant that `manifest.version` is filled for all application types. For microservices with several versions, fall back to `GET /application/applications/{id}/versions`.

### 5.2 Script `scripts/e2e/collect-tenant-versions.sh`

```bash
#!/usr/bin/env bash
# Writes tenant-versions.json and tenant-versions.md to the current directory.
set -euo pipefail
AUTH="${E2E_TENANT}/${E2E_USER}:${E2E_PASSWORD}"
api() { curl -fsS -u "$AUTH" -H 'Accept: application/json' "${E2E_BASE_URL}$1"; }

PLATFORM=$(api /tenant/system/options/system/version | jq -r '.value')
TENANT=$(api /tenant/currentTenant)
TENANT_ID=$(jq -r '.name' <<<"$TENANT")
APPS=$(api "/application/applicationsByTenant/${TENANT_ID}?pageSize=2000" \
  | jq '[.applications[] | {name, type, version: (.manifest.version // "n/a")}] | sort_by(.type, .name)')

jq -n --arg platform "$PLATFORM" --argjson tenant "$TENANT" --argjson apps "$APPS" \
  --arg sdk "$(mvn -q help:evaluate -Dexpression=c8y.version -DforceStdout)" \
  --arg msVersion "$(mvn -q help:evaluate -Dexpression=project.version -DforceStdout)" \
  --arg ref "${GITHUB_REF_NAME:-local}" --arg sha "${GITHUB_SHA:-local}" \
  '{platform: $platform, tenantId: $tenant.name, domain: $tenant.domainName,
    microserviceUnderTest: {ref: $ref, sha: $sha, version: $msVersion, javaSdk: $sdk},
    applications: $apps}' > tenant-versions.json

{
  echo "| Component | Type | Version |"
  echo "|-----------|------|---------|"
  echo "| Platform | BACKEND | $PLATFORM |"
  jq -r '.applications[] | "| \(.name) | \(.type) | \(.version) |"' tenant-versions.json
} > tenant-versions.md
```

### 5.3 Change detection

The workflow uploads `tenant-versions.json` as an artifact in every run. Before the tests start, it downloads the file from the last successful run of the same workflow (`gh run list --workflow e2e-weekly.yml --status success -L 1` and `gh run download`) and creates a diff table with `jq`. The diff goes into the job summary and, on failure, into the issue. A failure that comes with a changed platform version points to the platform. A failure without any tenant change points to the microservice or to test data.

## 6. GitHub Actions workflow

File: `.github/workflows/e2e-weekly.yml`

```yaml
name: E2E compatibility (CD tenant)

on:
  schedule:
    - cron: '0 5 * * 1'        # Monday 05:00 UTC
  workflow_dispatch:
    inputs:
      ref:
        description: 'Branch or tag to test'
        default: 'main'

permissions:
  contents: read
  issues: write
  actions: read                  # to download artifacts of earlier runs

concurrency:
  group: c8y-test-tenant         # use the same group in maven.yml and maven_artifact.yml
  cancel-in-progress: false

jobs:
  e2e:
    runs-on: ubuntu-latest
    timeout-minutes: 45
    strategy:
      fail-fast: false
      max-parallel: 1            # one deployment on the tenant at a time
      matrix:
        ref: ${{ github.event_name == 'workflow_dispatch' && fromJSON(format('["{0}"]', inputs.ref)) || fromJSON('["main","develop"]') }}
    env:
      E2E_BASE_URL: ${{ secrets.TEST_URL }}
      E2E_TENANT:   ${{ secrets.E2E_TENANT }}
      E2E_USER:     ${{ secrets.E2E_USER }}
      E2E_PASSWORD: ${{ secrets.E2E_PASSWORD }}
      E2E_SERVICE_PATH: /service/service-request-mgmt
      E2E_RUN_ID: ${{ github.run_id }}-${{ matrix.ref }}
      C8Y_HOST:     ${{ secrets.TEST_URL }}
      C8Y_TENANT:   ${{ secrets.E2E_TENANT }}
      C8Y_USER:     ${{ secrets.E2E_USER }}
      C8Y_PASSWORD: ${{ secrets.E2E_PASSWORD }}
      GH_TOKEN: ${{ github.token }}
    steps:
      - uses: actions/checkout@v4
        with:
          ref: ${{ matrix.ref }}
      # Scripts always come from the default branch, so that old release branches can be tested.
      - uses: actions/checkout@v4
        with:
          path: e2e-tooling
          sparse-checkout: scripts/e2e

      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin', cache: maven }
      - uses: reubenmiller/setup-go-c8y-cli@main

      - name: Collect tenant versions
        run: e2e-tooling/scripts/e2e/collect-tenant-versions.sh
      - name: Diff against last green run
        run: e2e-tooling/scripts/e2e/diff-tenant-versions.sh >> "$GITHUB_STEP_SUMMARY"

      - name: Build package
        run: mvn -B package -DskipTests -Dspringdoc.skip=true

      - name: Deploy and wait for health
        run: |
          c8y microservices create --file target/*.zip --name service-request-mgmt --key service-request-mgmt
          for i in $(seq 1 40); do
            c8y api GET /service/service-request-mgmt/health --silentStatusCodes 404,502,503 \
              | jq -e '.status == "UP"' && exit 0
            sleep 15
          done
          echo "::error::Microservice not healthy after 10 minutes" && exit 1

      - name: Run E2E tests
        run: mvn -B -Pe2e test-compile failsafe:integration-test failsafe:verify

      - name: Cleanup test data
        if: always()
        run: e2e-tooling/scripts/e2e/cleanup.sh

      - name: Upload reports and versions
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: e2e-${{ matrix.ref }}
          path: |
            tenant-versions.json
            tenant-versions.md
            target/failsafe-reports/

      - name: Create or update issue
        if: failure() && github.event_name == 'schedule'
        run: e2e-tooling/scripts/e2e/report-failure.sh "${{ matrix.ref }}"

      - name: Close issue after recovery
        if: success() && github.event_name == 'schedule'
        run: e2e-tooling/scripts/e2e/close-recovered.sh "${{ matrix.ref }}"
```

Notes:

- Scheduled workflows run only from the default branch (`develop` in this repository). GitHub disables them in public repositories after 60 days without repository activity. Check the workflow status after quiet periods.
- `-Dspringdoc.skip=true` skips the local start for the API doc generation. Check that this property disables the plugin; if not, add a property to the `springdoc-openapi-maven-plugin` configuration.
- The deployment replaces the microservice that `maven_artifact.yml` deployed from `develop`. After a run of `main`, the tenant runs the release version until the next push to `develop`. This is acceptable for a test tenant. If not, redeploy `develop` at the end.

## 7. GitHub issue on failure

### 7.1 Duplicate handling

The script searches for an open issue with the labels `e2e-failure` and `ref:<ref>`:

- No open issue: create one.
- Open issue exists: add a comment with the new run. Do not create a second issue for the same ongoing problem.
- Next successful run: comment "Passed again in run X with platform version Y" and close the issue.

Failure types get separate labels so that tenant problems do not look like code bugs:

| Label | Condition |
|-------|-----------|
| `e2e-failure` + `infra` | Version collection, deployment, or health check failed (tenant unreachable, credentials expired, quota) |
| `e2e-failure` + `compatibility` | E2E tests ran and at least one failed |

### 7.2 Issue content

Title: `E2E failure on <ref> against platform <platform version> (<date>)`

```markdown
## Summary
Weekly E2E run failed for `main` (1.6.2, commit abc1234).
Run: https://github.com/Cumulocity-IoT/cumulocity-microservice-service-request-mgmt/actions/runs/<id>

## Failed tests
| Test | Message |
|------|---------|
| ServiceRequestAlarmE2E.linkAlarm | expected status 200 but was 403 |

## Tenant changes since last green run (<date>)
| Component | Before | Now |
|-----------|--------|-----|
| Platform | 1023.4.0 | 1023.7.1 |
| cockpit | 1023.4.0 | 1023.7.1 |

## Tenant component versions
<contents of tenant-versions.md>

## Microservice under test
Version 1.6.2, Java SDK 2025.81.0, ref `main`
```

The failed tests come from `target/failsafe-reports/*.xml` (parse with `xmllint` or `yq`, or use an action like `dorny/test-reporter` and link its check run). Truncate long messages and stack traces to stay below the issue body limit of 65,536 characters. The full reports are in the run artifacts.

Assignment: assign the issue to the code owners or the team that maintains the repository, and add it to the project board if one exists.

## 8. Secrets and access

| Secret | Purpose | Status |
|--------|---------|--------|
| `TEST_URL` | Tenant URL | Exists |
| `E2E_TENANT`, `E2E_USER`, `E2E_PASSWORD` | Dedicated technical user for E2E | New |
| `GITHUB_TOKEN` | Issues, artifact download | Built in, permissions set in the workflow |

The dedicated E2E user needs the roles from `cumulocity.json` (`requiredRoles`), the roles to use the microservice API, `ROLE_APPLICATION_MANAGEMENT_ADMIN` (deploy), `ROLE_OPTION_MANAGEMENT_READ` (platform version), and user read access. Use a separate user instead of the bootstrap user, so that a rotated password does not break the API doc generation and the E2E run at the same time. Option: store the secrets in a GitHub environment `c8y-test-tenant` to limit which workflows can read them.

## 9. Rollout plan

| Phase | Content | Result |
|-------|---------|--------|
| 1 | Maven profile `e2e`, test base class (config, auth, test device, cleanup), scenarios S01 to S04, `collect-tenant-versions.sh`, workflow with `workflow_dispatch` only | Manual runs work end to end |
| 2 | Scenarios S05 to S07, issue creation and closing, version diff, weekly schedule, concurrency group in all three workflows | Weekly automated compatibility check |
| 3 | Scenarios S08 to S11, matrix with `main` and `develop` | Full coverage |
| 4 (optional) | Daily lightweight job that only reads the platform version and starts the E2E workflow when the version changes (`gh workflow run`) | Failures are found within a day of a tenant update instead of within a week |

## 10. Open points

1. Confirm that `TEST_URL` and `C8Y_HOST` point to the same tenant, and which CD channel the tenant follows.
2. Confirm that `/tenant/system/options/system/version` and `manifest.version` from `applicationsByTenant` return the expected values on the test tenant.
3. Decide whether `develop` is part of the weekly matrix from the start or only `main`.
4. Define who receives the issues (assignee, team, project board).
5. Decide whether the scheduled alarm validation job (`ServiceRequestAlarmValidationService`) needs an E2E test, and if yes, how to trigger it without a 10-minute wait.
