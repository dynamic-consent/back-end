# 데이터베이스 설정 가이드

## 현재 설정

이 프로젝트는 현재 **H2 인메모리 데이터베이스**를 사용하고 있습니다. 개발 환경에서는 자동으로 샘플 데이터가 로드됩니다.

## PostgreSQL로 전환하기

### 1. PostgreSQL 설치
- Windows: https://www.postgresql.org/download/windows/
- macOS: `brew install postgresql`
- Ubuntu: `sudo apt-get install postgresql postgresql-contrib`

### 2. 데이터베이스 생성
```sql
-- PostgreSQL에 접속 후 실행
CREATE DATABASE dynamicconsent;
CREATE USER dynamicconsent WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE dynamicconsent TO dynamicconsent;
```

### 3. 환경 변수 설정
```bash
# Windows
set DB_USERNAME=dynamicconsent
set DB_PASSWORD=your_password

# Linux/macOS
export DB_USERNAME=dynamicconsent
export DB_PASSWORD=your_password
```

### 4. 프로파일 변경
```bash
# 개발 환경 (H2 사용)
java -jar app.jar --spring.profiles.active=dev

# 프로덕션 환경 (PostgreSQL 사용)
java -jar app.jar --spring.profiles.active=prod
```

## 데이터베이스 접속 정보

### H2 콘솔 (개발 환경)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:consentdb`
- Username: `sa`
- Password: (비어있음)

### PostgreSQL (프로덕션)
- Host: localhost
- Port: 5432
- Database: dynamicconsent
- Username: dynamicconsent
- Password: (설정한 비밀번호)

## 샘플 데이터

`DataSeeder` 클래스가 자동으로 다음 데이터를 생성합니다:

### 사용자
- user1: John Doe (john@example.com)
- user2: Jane Smith (jane@example.com)

### 조직
- First Bank (BANK001) - 금융
- Second Bank (BANK002) - 금융
- City Hospital (HOSPITAL001) - 의료
- Life Insurance (INSURANCE001) - 보험
- Government Office (GOV001) - 정부

### 동의서
- 각 사용자별로 다양한 조직과의 동의서가 생성됩니다.

### 공지사항
- 시스템 점검 공지
- 신규 기능 출시 공지
- 보안 경고

## 문제 해결

### 연결 오류
1. PostgreSQL 서비스가 실행 중인지 확인
2. 방화벽 설정 확인
3. 데이터베이스 사용자 권한 확인

### 데이터 초기화
H2 데이터베이스는 애플리케이션 재시작 시 자동으로 초기화됩니다.
PostgreSQL의 경우 `spring.jpa.hibernate.ddl-auto=create`로 설정하면 테이블이 재생성됩니다.

## 보안 고려사항

1. 프로덕션에서는 강력한 비밀번호 사용
2. 데이터베이스 접근 권한 최소화
3. SSL 연결 사용 고려
4. 정기적인 백업 설정
