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
docker run -d --name zookeeper -p 2181:2181 zookeeper

docker run -d --name kafka -p 9092:9092 --link zookeeper \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 \
  wurstmeister/kafka
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

