# 데이터베이스 입력 가이드

## 현재 사용 가능한 API 엔드포인트

### 1. 사용자 관련 API
- **기본 URL**: `http://172.19.26.41:8080/api/v1`

#### 사용자 프로필 조회
```bash
GET /api/v1/me
```

#### 사용자 프로필 수정
```bash
PATCH /api/v1/me
Content-Type: application/json

{
  "displayName": "홍길동",
  "email": "hong@example.com",
  "phoneNumber": "010-1234-5678"
}
```

#### 사용자 환경설정 수정
```bash
PATCH /api/v1/me/preferences
Content-Type: application/json

{
  "notifications": true,
  "theme": "dark",
  "riskThreshold": "MEDIUM"
}
```

### 2. 조직 관련 API
- **기본 URL**: `http://172.19.26.41:8080/api/v1/orgs`

#### 조직 목록 조회
```bash
GET /api/v1/orgs?category=FINANCE&page=0&size=10
```

#### 조직 상세 정보
```bash
GET /api/v1/orgs/{orgId}
```

#### 조직별 동의서 목록
```bash
GET /api/v1/orgs/{orgId}/consents?status=ACTIVE&page=0&size=20
```

### 3. 동의서 관련 API
- **기본 URL**: `http://172.19.26.41:8080/api/v1`

#### 동의서 목록 조회
```bash
GET /api/v1/consents?status=ACTIVE&orgId=BANK001&page=0&size=20
```

#### 동의서 상세 정보
```bash
GET /api/v1/consents/{consentId}
```

#### 동의서 생성
```bash
POST /api/v1/consents
Content-Type: application/json

{
  "orgId": "BANK001",
  "title": "개인정보 수집 및 이용 동의",
  "description": "계좌 개설을 위한 개인정보 수집 및 이용에 동의합니다."
}
```

#### 동의서 수정
```bash
PATCH /api/v1/consents/{consentId}
Content-Type: application/json

{
  "scopes": ["PERSONAL_INFO", "FINANCIAL_INFO"],
  "validUntil": "2024-12-31T23:59:59Z",
  "purpose": "계좌 개설 및 금융 서비스 제공"
}
```

#### 동의서 철회
```bash
POST /api/v1/consents/{consentId}:revoke
Content-Type: application/json

{
  "reason": "서비스 이용 중단"
}
```

### 4. 동의서 이벤트 API

#### 전체 동의서 이벤트 목록
```bash
GET /api/v1/consents/events?from=2024-01-01T00:00:00Z&to=2024-12-31T23:59:59Z&type=CREATED&page=0&size=20
```

#### 특정 동의서 이벤트 목록
```bash
GET /api/v1/consents/{consentId}/events?page=0&size=20
```

## 데이터 입력 예시

### 1. 새로운 사용자 등록 (DataSeeder를 통해 자동 생성됨)
현재 시스템에서는 사용자가 자동으로 생성됩니다. 추가 사용자가 필요하면 DataSeeder를 수정하세요.

### 2. 새로운 조직 추가
```bash
# 조직은 현재 DataSeeder를 통해 자동 생성됩니다.
# 추가 조직이 필요하면 DataSeeder.java를 수정하세요.
```

### 3. 새로운 동의서 생성
```bash
curl -X POST "http://172.19.26.41:8080/api/v1/consents" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "orgId": "BANK001",
    "title": "신용카드 발급 동의",
    "description": "신용카드 발급을 위한 개인정보 수집 및 이용에 동의합니다."
  }'
```

### 4. 동의서 상태 변경
```bash
curl -X PATCH "http://172.19.26.41:8080/api/v1/consents/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "scopes": ["PERSONAL_INFO", "FINANCIAL_INFO", "CONTACT_INFO"],
    "validUntil": "2025-12-31T23:59:59Z",
    "purpose": "신용카드 발급 및 관리"
  }'
```

## 인증 방법

현재 시스템은 인증이 필요합니다. API 호출 시 다음 헤더를 포함해야 합니다:

```bash
Authorization: Bearer YOUR_ACCESS_TOKEN
```

## 테스트 방법

### 1. Swagger UI 사용
- URL: `http://172.19.26.41:8080/swagger-ui/index.html`
- 브라우저에서 API를 직접 테스트할 수 있습니다.

### 2. curl 명령어 사용
```bash
# 동의서 목록 조회
curl -X GET "http://172.19.26.41:8080/api/v1/consents" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 조직 목록 조회
curl -X GET "http://172.19.26.41:8080/api/v1/orgs" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Postman 사용
- Base URL: `http://172.19.26.41:8080`
- Authorization: Bearer Token 설정

## 주의사항

1. **인증 필요**: 모든 API 호출에는 유효한 토큰이 필요합니다.
2. **데이터 검증**: 입력 데이터는 유효성 검사를 통과해야 합니다.
3. **권한 확인**: 사용자는 자신의 데이터에만 접근할 수 있습니다.
4. **CORS 설정**: 현재 모든 도메인에서 접근 가능하도록 설정되어 있습니다.

## 샘플 데이터

현재 시스템에는 다음 샘플 데이터가 자동으로 생성됩니다:

- **사용자**: user1 (John Doe), user2 (Jane Smith)
- **조직**: First Bank, Second Bank, City Hospital, Life Insurance, Government Office
- **동의서**: 각 사용자별로 다양한 조직과의 동의서
- **이벤트**: 동의서 생성, 수정, 철회 이벤트
- **공지사항**: 시스템 점검, 신규 기능, 보안 경고


