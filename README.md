# Classifieds API POC — sahibinden.com

## Project Requirements & Explanations (EN)

### Purpose

Develop a **POC API** where users can create listings, publish/unpublish them, and manage the listing lifecycle. After the POC demo, the goal is to use feedback to turn it into a full project.

### Scope (Functional)

* **Listing Title**: Must start with a letter (Turkish characters allowed) or a digit; **min 10, max 50** characters. If any word from `Badwords.txt` appears, the submission is rejected.
* **Listing Detail**: **min 20, max 200** characters; special characters are allowed.
* **Category**: `Real Estate | Automotive | Shopping | Other` (no new category is expected).

### Lifecycle Rules

* Initial state: `Shopping` → **Active**, all other categories → **Pending Approval**.
* If the same **category + title + description** combination is submitted, the listing is marked **Duplicate** and its **status cannot be updated**.
* `Pending Approval` → **Active** (assume there is no "rejected" path).
* The user can set a listing in `Active` or `Pending Approval` to **Deactivated**.

### Assumptions

* In this POC, the user who creates and approves a listing may be the same; assume an auth mechanism exists.
* Only the **status** will be updated (no updates for title/detail/category).
* A `Deactivated` → `Active` re-activation scenario is **out of scope** for the POC demo.

### API Expectations

* Listing creation
* Status changes such as activation/deactivation
* **Dashboard**: counts of all listings by status (e.g., `Active: 151, Deactivated: 71 …`)
* (**BONUS**) A list of **all status changes over time** for a listing

### Tests

* **Unit Tests** (coverage level at the developer’s discretion)

### Bonuses

* Swagger/Postman documentation
* Log every request that takes **more than 5 ms**
* Containerize the application (Docker)
* **Integration Tests**

### Constraints

* **Java 11**, Maven 3.6+, Spring Boot 2+
* **In-memory DB**
* Apart from the specified endpoint(s), API design is up to the developer
* Timeline: **5 business days** after the repository invitation
* Development flow: open a **PR** from `feature/code-case` branched from `main`

### Recommendations

* Complete core features first; postpone or skip bonuses
* Follow OOP principles, modular design, and API standards
* Feel free to add any libraries you need

### FAQ

* **I can’t push**: Read/write access is granted via the invitation. Check **SSH Key** settings in your Git profile; if the problem persists, contact the repository owner.

## Pull Request (What My PR Includes)

### Scope

This PR bootstraps the whole POC from an empty repo and implements the functional scope described above: input rules (title/detail/category), lifecycle rules, required endpoints, and selected bonuses (**Swagger, slow-request logging, status history**).

### Domain Model & Lifecycle

* **Entities & Enums**

  * `Classified` with validation, duplicate fingerprint, timestamps, and JPA indexes.
  * `Status` = `PENDING_APPROVAL | ACTIVE | DEACTIVATED | DUPLICATE`.
  * `Category` = `AUTOMOTIVE | REAL_ESTATE | SHOPPING | OTHER`.
  * `StatusLog` persists full status-change history (**bonus**).
* **Defaults & Lifecycle Rules**

  * On create: **ACTIVE** for `SHOPPING`, otherwise \*\*PENDING\_APPROVAL\` for first-time posts.
  * Central **state machine** enforces transitions (e.g., `PENDING_APPROVAL → ACTIVE/DEACTIVATED`, `ACTIVE → DEACTIVATED`, no exits from `DUPLICATE`); illegal transitions → `400`.
* **Duplicate Detection**

  * `DuplicateKey` builds a normalized Base64url **SHA‑256** fingerprint from `(category, title, detail)`.
  * On create, if a matching fingerprint exists in the same category, the new record is marked **DUPLICATE** and logged.

### Validation & Content Policy

* **Field Rules**

  * **Title**: starts with letter/digit, **10–50** chars.
  * **Detail**: **20–200** chars.
  * **Category**: required enum (string).
* **Bad words (bonus)**

  * `@NoBadWords` + `BadWordDetector` loads a word list and rejects payloads containing forbidden terms **at DTO validation time**.

### Services & Use‑cases

* **ClassifiedService** — creates ads, marks duplicates, logs initial status, and updates status via the state machine.
* **StatusLogService** — persists and fetches ordered history for the read API.
* **ClassifiedsStatisticsService** — aggregates counts by status for the dashboard.

### REST API

* **Resource Base**: `/api/v1/classifieds`

  * `POST /api/v1/classifieds` → `201 Created` with `Location` header.
  * `GET /api/v1/classifieds/{id}` → `200 OK` with DTO.
  * `PUT /api/v1/classifieds/{id}/status` with `StatusUpdateDto` → state‑machine enforced update.
  * `GET /api/v1/classifieds/{id}/history` → chronological status changes (**bonus**).
* **Dashboard**

  * `GET /dashboard/classifieds/statistics` → counts per status.

### API Documentation (bonus)

* Springdoc/OpenAPI configuration + light controller annotations. Swagger UI available at the default Springdoc path when running.

### Observability (bonus)

* **Slow request logging**: Servlet filter logs any request exceeding **5 ms** (method, URI, pattern, status, UA, IP) under a dedicated `slow-requests` logger.

## Endpoints & Sample Payloads

> Base path: `/api/v1/classifieds`

**Create**

```http
POST /api/v1/classifieds
Content-Type: application/json

{
  "title": "iPhone 13 Pro Max 128GB",
  "detail": "Excellent condition, single owner, box & charger included",
  "category": "SHOPPING"
}
```

→ `201 Created` with `Location: /api/v1/classifieds/{id}`.

**Get by id**

```http
GET /api/v1/classifieds/{id}
```

→ `200 OK` with `ClassifiedDto`.

**Update status**

```http
PUT /api/v1/classifieds/{id}/status
Content-Type: application/json

{ "status": "ACTIVE" }
```

→ `200 OK` or `400 Bad Request` if transition is illegal.

**Status history (bonus)**

```http
GET /api/v1/classifieds/{id}/history
```

→ `200 OK` list of `{status, createdAt}` items.

**Dashboard**

```http
GET /dashboard/classifieds/statistics
```

→ `200 OK` like `{ "active": 151, "deactivated": 71, "duplicate": 3, "pending": 12 }`.



## Tests (Unit + Integration)

### Coverage Summary

* **Controller (integration)**

  * `ClassifiedControllerIntegrationTest` — create, get, update status (valid/invalid), history ordering.
  * `DashboardControllerIntegrationTest` — statistics endpoint response schema.
* **Domain & Entity**

  * `StatusMachineTest` — allowed/illegal transitions.
  * `ClassifiedTest` — validation rules, default status logic.
* **Mapper**

  * `CustomClassifiedMapperTest` — DTO ↔ entity mapping fidelity.
* **Utility**

  * `DuplicateKeyTest` — fingerprint stability and normalization.
  * `TextNormalizerTest` — normalization behavior.
* **Validation**

  * `BadWordDetectorTest` — detection accuracy.
  * `NoBadWordsTest` — DTO-level annotation behavior.
* **Bootstrapping**

  * `CodecaseApplicationTests` — Spring context loads in test profile.

### How to Run

```bash
mvn test
mvn -Dtest=ClassifiedControllerIntegrationTest test
mvn -Dtest=ClassifiedControllerIntegrationTest#creates_classified_and_sets_Location_header test
```

## Run Locally

### Prerequisites

* Java 11, Maven 3.6+

### Build & Run (Maven)

```bash
mvn clean package -DskipTests
java -jar target/*.jar
```

### Run with Docker

```bash
# Build the image
docker build -t sahibinden-api .

# Start the container on port 8080
docker run --rm -p 8080:8080 --name sahibinden-api sahibinden-api
```

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

## Notes on Design Decisions

* **Two-layer validation**: DTO-level + entity-level for integrity.
* **Deterministic duplicate detection**: normalized hash avoids fuzzy matching cost.
* **Explicit state machine**: centralizes lifecycle control and guards invalid transitions.

© sahibinden.com — POC
