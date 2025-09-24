// 데이터베이스 입력 테스트 스크립트
// Node.js 환경에서 실행

const API_BASE_URL = 'http://172.19.26.41:8080/api/v1';

// 인증 토큰 (실제 사용 시 유효한 토큰으로 교체)
const AUTH_TOKEN = 'YOUR_ACCESS_TOKEN_HERE';

// API 호출 헤더
const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${AUTH_TOKEN}`
};

// 1. 사용자 프로필 조회
async function getUserProfile() {
    try {
        const response = await fetch(`${API_BASE_URL}/me`, {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        console.log('사용자 프로필:', data);
        return data;
    } catch (error) {
        console.error('사용자 프로필 조회 오류:', error);
    }
}

// 2. 사용자 프로필 수정
async function updateUserProfile() {
    const updateData = {
        displayName: '홍길동',
        email: 'hong@example.com',
        phoneNumber: '010-1234-5678'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/me`, {
            method: 'PATCH',
            headers: headers,
            body: JSON.stringify(updateData)
        });
        const data = await response.json();
        console.log('사용자 프로필 수정 완료:', data);
        return data;
    } catch (error) {
        console.error('사용자 프로필 수정 오류:', error);
    }
}

// 3. 조직 목록 조회
async function getOrganizations() {
    try {
        const response = await fetch(`${API_BASE_URL}/orgs?category=FINANCE&page=0&size=10`, {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        console.log('조직 목록:', data);
        return data;
    } catch (error) {
        console.error('조직 목록 조회 오류:', error);
    }
}

// 4. 동의서 목록 조회
async function getConsents() {
    try {
        const response = await fetch(`${API_BASE_URL}/consents?status=ACTIVE&page=0&size=20`, {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        console.log('동의서 목록:', data);
        return data;
    } catch (error) {
        console.error('동의서 목록 조회 오류:', error);
    }
}

// 5. 새로운 동의서 생성
async function createConsent() {
    const consentData = {
        orgId: 'BANK001',
        title: '신용카드 발급 동의',
        description: '신용카드 발급을 위한 개인정보 수집 및 이용에 동의합니다.'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/consents`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(consentData)
        });
        const data = await response.json();
        console.log('동의서 생성 완료:', data);
        return data;
    } catch (error) {
        console.error('동의서 생성 오류:', error);
    }
}

// 6. 동의서 수정
async function updateConsent(consentId) {
    const updateData = {
        scopes: ['PERSONAL_INFO', 'FINANCIAL_INFO', 'CONTACT_INFO'],
        validUntil: '2025-12-31T23:59:59Z',
        purpose: '신용카드 발급 및 관리'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/consents/${consentId}`, {
            method: 'PATCH',
            headers: headers,
            body: JSON.stringify(updateData)
        });
        const data = await response.json();
        console.log('동의서 수정 완료:', data);
        return data;
    } catch (error) {
        console.error('동의서 수정 오류:', error);
    }
}

// 7. 동의서 철회
async function revokeConsent(consentId) {
    const revokeData = {
        reason: '서비스 이용 중단'
    };

    try {
        const response = await fetch(`${API_BASE_URL}/consents/${consentId}:revoke`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(revokeData)
        });
        console.log('동의서 철회 완료:', response.status);
        return response.status;
    } catch (error) {
        console.error('동의서 철회 오류:', error);
    }
}

// 8. 동의서 이벤트 목록 조회
async function getConsentEvents() {
    try {
        const response = await fetch(`${API_BASE_URL}/consents/events?from=2024-01-01T00:00:00Z&to=2024-12-31T23:59:59Z&page=0&size=20`, {
            method: 'GET',
            headers: headers
        });
        const data = await response.json();
        console.log('동의서 이벤트 목록:', data);
        return data;
    } catch (error) {
        console.error('동의서 이벤트 조회 오류:', error);
    }
}

// 테스트 실행 함수
async function runTests() {
    console.log('=== 데이터베이스 입력 테스트 시작 ===\n');

    // 1. 사용자 프로필 조회
    console.log('1. 사용자 프로필 조회');
    await getUserProfile();
    console.log('');

    // 2. 사용자 프로필 수정
    console.log('2. 사용자 프로필 수정');
    await updateUserProfile();
    console.log('');

    // 3. 조직 목록 조회
    console.log('3. 조직 목록 조회');
    await getOrganizations();
    console.log('');

    // 4. 동의서 목록 조회
    console.log('4. 동의서 목록 조회');
    await getConsents();
    console.log('');

    // 5. 새로운 동의서 생성
    console.log('5. 새로운 동의서 생성');
    const newConsent = await createConsent();
    console.log('');

    // 6. 동의서 수정 (생성된 동의서 ID 사용)
    if (newConsent && newConsent.consentId) {
        console.log('6. 동의서 수정');
        await updateConsent(newConsent.consentId);
        console.log('');
    }

    // 7. 동의서 이벤트 목록 조회
    console.log('7. 동의서 이벤트 목록 조회');
    await getConsentEvents();
    console.log('');

    console.log('=== 테스트 완료 ===');
}

// 스크립트 실행
if (typeof window === 'undefined') {
    // Node.js 환경
    runTests().catch(console.error);
} else {
    // 브라우저 환경
    console.log('브라우저에서 실행하려면 runTests() 함수를 호출하세요.');
    window.runTests = runTests;
}

// 개별 함수들을 전역으로 노출 (브라우저 환경용)
if (typeof window !== 'undefined') {
    window.getUserProfile = getUserProfile;
    window.updateUserProfile = updateUserProfile;
    window.getOrganizations = getOrganizations;
    window.getConsents = getConsents;
    window.createConsent = createConsent;
    window.updateConsent = updateConsent;
    window.revokeConsent = revokeConsent;
    window.getConsentEvents = getConsentEvents;
}


