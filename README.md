# Kafka Chat Example (Spring Boot + Kafka + H2 + HTML)

간단한 **Kafka 기반 채팅 예제**입니다.  
- 백엔드: Spring Boot 2.7.18  
- 메시징: Apache Kafka  
- DB: H2 (In-Memory)  
- 프론트: HTML (Thymeleaf 기반)  
- 목적: 교육용 / 개념 학습용

---

## 🧱 프로젝트 구조

```
src/
└── main/
    ├── java/
    │   └── com/example/kafka_chat/
    │       ├── controller/      # REST 컨트롤러
    │       ├── kafka/           # Kafka Producer, Consumer
    │       ├── model/           # ChatMessage 엔티티
    │       └── repository/      # Spring Data JPA 리포지토리
    └── resources/
        ├── templates/           # HTML (Thymeleaf 템플릿)
        └── application.yml      # 환경 설정 파일
```

---

## ⚙️ 환경

- **Java**: 8  
- **Spring Boot**: 2.7.18  
- **Kafka**: 로컬 or Docker 기반  
- **Gradle**: Groovy DSL

---

## 🧩 주요 라이브러리

```groovy
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-web'
  implementation 'org.springframework.boot:spring-boot-starter-websocket'
  implementation 'org.springframework.kafka:spring-kafka'
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
  runtimeOnly 'com.h2database:h2'

  compileOnly 'org.projectlombok:lombok'
  annotationProcessor 'org.projectlombok:lombok'

  testImplementation 'org.springframework.boot:spring-boot-starter-test'
  testImplementation 'org.springframework.kafka:spring-kafka-test'
}
```

---

## 🛠 실행 방법

### 1. Kafka 실행 (Docker 예시)

```bash
docker-compose up -d
```

> 또는 로컬에 설치된 Kafka 사용

### 2. Kafka 토픽 생성

```bash
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 --topic chat-messages
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

> 접속: [http://localhost:8080](http://localhost:8080)

---

## ✉️ 예제 흐름

1. HTML에서 채팅 메시지 입력  
2. Spring Controller가 Kafka Producer로 메시지 전송  
3. Kafka Consumer가 메시지 수신  
4. H2 DB에 저장  
5. 채팅 기록 API 또는 웹소켓으로 클라이언트에게 전달

---

## ✅ 학습 포인트

- Kafka Topic 구성 및 메시지 처리 흐름
- Kafka Producer/Consumer 구현 방법
- Spring Boot와 Kafka 통합
- HTML 기반 간단한 채팅 UI 구현
- WebSocket or Ajax 방식 비교

🔧 실습용 부하 테스트 모음 (Kafka 채팅 프로젝트 연동)
Kafka 기반 채팅 프로젝트에 대해 3가지 부하 테스트 시나리오를 제공합니다.

✅ 구성별 테스트 목록
테스트 항목	목적	기술	실행 방식
1. WebSocket STOMP 테스트	STOMP 기반 채팅 부하 테스트	locust + websocket-client	사용자 수 및 메시지 전송 시뮬레이션
2. REST API 채팅 메시지 전송	REST 기반 메시지 전송 부하 테스트	locust + requests	Kafka Producer 호출 부하 측정
3. 메시지 이력 조회 (DB Read)	DB 읽기 부하 테스트	locust + requests	/api/chat/history 조회 부하 측정

🧰 사전 준비 (Windows 기준)
1. Python 설치
공식 다운로드: https://www.python.org/downloads/windows/

설치확인
```
python3 --version
```
설치 시 Add Python to PATH 체크
pip 설치확인
```
pip3 --version
```
locust 설치
```
pip3 install locust
```
locust 확인
```
locust --version
```

2. 필수 패키지 설치
bash
복사
편집
pip install locust websocket-client

📁 테스트 파일 구성
```
chat-load-test/
├── websocket_test.py     # STOMP WebSocket 부하 테스트
├── rest_send_test.py     # REST 메시지 전송 테스트
└── rest_read_test.py     # 메시지 이력 조회 테스트
```
🚀 테스트 실행 방법
🧪 1. WebSocket STOMP 테스트
bash
복사
편집
locust -f websocket_test.py
접속: http://localhost:8089

입력 예시:

Number of users: 10

Spawn rate: 2

Host: http://localhost:8080

내부적으로 ws://localhost:8080/ws/chat/websocket STOMP WebSocket 경로 사용

🧪 2. api 메시지 전송 테스트
bash
복사
편집
locust -f locustfile_simple.py
/api/chat/send API에 메시지를 POST 방식으로 전송

Kafka Producer 트래픽 부하 측정용

🧪 3. 카프카 테스트
bash
복사
편집
locust -f locustfile_kafka.py
/api/chat/history?roomId=1 등 메시지 이력 조회 API 테스트

DB Read (H2 쿼리 성능) 부하 측정

📌 테스트 팁
각 테스트는 localhost:8089 Locust UI에서 동시 사용자 수, 메시지 전송 속도 조절 가능

Kafka 또는 WebSocket 서버가 실행 중이어야 정상 작동

Kafka 메시지 저장/읽기 성능, WebSocket 처리량을 실시간으로 모니터링 가능



