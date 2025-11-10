# Angler

Angler는 이미지(스크린샷) 또는 텍스트로 받은 문자 메시지를 분석해 피싱(사기/스미싱) 의심 여부와 신뢰도(trustScore)를 반환하는 AI + 규칙 기반 분석 서비스입니다.

---

## 주요 기능
- 이미지(OCR) → 텍스트 변환(현재 OpenAI Vision 연결)
- AI(또는 OCR) 결과에서 URL / 전화번호 / 계좌번호 추출
- 정규식·평판·룰 기반 Validator로 서브스코어 산출
- RiskScoreEngine으로 종합 신뢰도(trustScore, 0~100) 계산
- `isPhishing` 판정 및 판정 근거(reasons) 반환
- 결과를 사람 친화적 문장으로 포맷하여 응답

---

## 저장소 구조(권장)

```
/project file
  /Angler (Spring Boot)
    src/main/java/...
    src/main/resources/application.yml
  /Front-End(Next.js)
```

---

## 빠른 시작 (Backend)

### 요구사항
- JDK 17+
- Gradle 7+ (또는 프로젝트 포함 wrapper)
- MySQL (선택: RDS)
- OpenAI API 키

### 환경변수 (예시)
- `OPENAI_API_KEY` — OpenAI의 API 키 (예: `sk-...`) — `OpenAiService`에서 사용
- `SPRING_DATASOURCE_URL` , `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` — DB 연결 정보 (옵션)

> 보안: 로컬 개발 시 `.env` 나 OS 환경변수로 설정하고, 배포 시에는 Secrets Manager/Parameter Store 사용 권장.

### 실행
1. 프로젝트 루트로 이동
2. 백엔드 실행

```bash
# Gradle wrapper 사용 예시
./gradlew :backend:bootRun
```

3. 기본 포트: `http://localhost:8080`

---

## API (현재 주요 엔드포인트)

### POST `/angler/fishing`
- 설명: 이미지를 받아 분석 후 최종 결과 문자열(또는 DTO) 반환
- Content-Type: `application/json`
- Request
```

- Response (예: 문자열)

```
이 문자는 87점의 위험도가 있습니다.
의심 링크이니 클릭하지 마세요.


> 내부적으로는 `AnglerResponseDto`(trustScore, isPhishing, reasons)를 생성하고, `AnglerResponseFormatter`로 사람이 읽기 좋은 메시지로 변환한 뒤 응답합니다.

---

## 주요 클래스/컴포넌트 설명

- `AnglerController` — API 엔드포인트(요청 위임/응답)
- `AnglerService` (인터페이스) / `AnglerServiceImpl` — 비즈니스 로직 오케스트레이션
- `OpenAiService` — OpenAI API 호출(텍스트/이미지 처리 담당)
- `ExtractService` — AI 응답 문자열을 `ExtractedData(url, phone, account)`로 정제
- `ValidatorService<T>` (인터페이스) / `UrlValidatorImpl`, `PhoneValidatorImpl`, `AccountValidatorImpl` — 각 항목의 서브스코어·이유 제공
- `RiskScoreEngine` — 서브스코어를 가중합으로 합쳐 `trustScore` 계산
- `AnglerResponseDto` — (trustScore, isPhishing, reasons)
- `AnglerResponseFormatter` — DTO → 사용자 문장 변환

---

## 데이터 모델 (간단)

- `ExtractedData` (DTO)
  - `String url`
  - `String phone`
  - `String account`

- `AnglerResponseDto`
  - `double trustScore` (0~100)
  - `boolean isPhishing`
  - `List<String> reasons`

---

## 스코어링(요약)

- Validator들이 각각 `0.0 ~ 1.0` 범위의 서브스코어를 반환
- RiskScoreEngine은 예시 가중치를 사용해 리스크(riskScore)를 계산하고,
  `trustScore = (1 - riskScore) * 100` 으로 반환
- 기본 임계값 예시: `trustScore < 50` → 의심(또는 관리자가 설정한 값)

---

## 테스트 가이드

- Postman으로 API 테스트
  - POST `http://localhost:8080/api/angler/fishing` → JSON body
- 단위테스트
  - Validator, ExtractService, RiskScoreEngine 단위 테스트 권장
- 통합테스트
  - OpenAiService는 Mock(예: WireMock 또는 Mockito)으로 대체하여 Controller→Service 흐름 검증

---

## 향후 추가 기능(우선순위 제안)

1. 이미지 multipart 업로드 지원(`@RequestPart`) + Vision 모델 호출
2. 외부 평판 API 연동(도메인 평판, 스팸 번호 DB, 금융사 차단리스트)
3. 관리 UI (로그 조회, 임계값 조정, 통계 대시보드)
4. 사용자 신고/차단 자동화 옵션
5. 모델 학습/가중치 자동 튜닝(로그 기반)

---

## 컨벤션 & 코딩 스타일

- 패키지 기준: `com.example.Angler` 아래에 `controller`, `service`, `service.impl`, `dto`, `extract`, `validation`, `util` 등으로 분리
- 의존성 주입은 생성자 주입 사용
- 외부 서비스 키는 환경변수 또는 Secret Manager 사용

---

## 라이선스

MIT License

---
