# G-Scores API Documentation

Base URL for local development:

```text
http://localhost:8080
```

Generated OpenAPI documentation:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`
- Use the JSON or YAML URL in Swagger UI, Redoc, Postman, Insomnia, or any OpenAPI client.

All endpoints are read-only and return JSON.

## Error Format

Validation and lookup failures return the same shape:

```json
{
  "timestamp": "2026-07-19T10:15:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "Score not found for registration number: 01000001"
}
```

## Methods

### GET `/api/health`

Checks whether the backend is running.

Success response:

```json
{
  "status": "ok"
}
```

### GET `/api/scores/{registrationNumber}`

Finds one student's scores by registration number.

Path parameters:

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| `registrationNumber` | string | Yes | Exactly 8 digits, for example `01000001`. |

Success response fields:

| Field | Type | Description |
| --- | --- | --- |
| `registrationNumber` | string | Student registration number. |
| `foreignLanguageCode` | string or null | Foreign language code from the source CSV. |
| `groupATotal` | number or null | Math + Physics + Chemistry total. Null when one score is missing. |
| `scores` | array | Subject score objects for all supported subjects. |

Example response:

```json
{
  "registrationNumber": "01000001",
  "foreignLanguageCode": "N1",
  "groupATotal": 24.75,
  "scores": [
    {
      "code": "math",
      "nameEn": "Math",
      "nameVi": "Toan",
      "score": 8.4
    }
  ]
}
```

Errors:

| Status | Reason |
| --- | --- |
| `400` | Registration number is blank or not exactly 8 digits. |
| `404` | No imported record matches the registration number. |

### GET `/api/reports/score-levels`

Returns score distribution buckets for every supported subject.

Bucket rules:

| Field | Rule |
| --- | --- |
| `excellent` | Score `>= 8`. |
| `good` | Score `>= 6` and `< 8`. |
| `average` | Score `>= 4` and `< 6`. |
| `belowAverage` | Score `< 4`. |
| `missing` | No score in the source CSV for that subject. |

Success response fields:

| Field | Type | Description |
| --- | --- | --- |
| `importedRecords` | number | Total rows imported into the database. |
| `subjects` | array | One score bucket object per supported subject. |

Example response:

```json
{
  "importedRecords": 1066090,
  "subjects": [
    {
      "code": "math",
      "nameEn": "Math",
      "nameVi": "Toan",
      "excellent": 124533,
      "good": 314280,
      "average": 401120,
      "belowAverage": 98231,
      "missing": 127926
    }
  ]
}
```

### GET `/api/reports/top-group-a`

Returns the highest ranked Group A students by Math + Physics + Chemistry total.

Query parameters:

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| `limit` | integer | No | `10` | Number of students to return. Values below `1` become `1`; values above `100` become `100`. |

Success response fields:

| Field | Type | Description |
| --- | --- | --- |
| `registrationNumber` | string | Student registration number. |
| `math` | number | Math score. |
| `physics` | number | Physics score. |
| `chemistry` | number | Chemistry score. |
| `total` | number | Math + Physics + Chemistry total. |

Example response:

```json
[
  {
    "registrationNumber": "01000001",
    "math": 9.4,
    "physics": 9.75,
    "chemistry": 9.25,
    "total": 28.4
  }
]
```

Errors:

| Status | Reason |
| --- | --- |
| `400` | `limit` is not a valid integer. |

### GET `/api/subjects`

Lists all subjects used in lookup and report payloads.

Success response fields:

| Field | Type | Description |
| --- | --- | --- |
| `code` | string | Stable API/frontend subject code. |
| `csvColumn` | string | Column name in the source CSV. |
| `nameEn` | string | English display name. |
| `nameVi` | string | Vietnamese display name. |

Example response:

```json
[
  {
    "code": "math",
    "csvColumn": "toan",
    "nameEn": "Math",
    "nameVi": "Toan"
  }
]
```
