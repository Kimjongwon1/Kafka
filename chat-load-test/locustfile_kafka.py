from locust import HttpUser, task, between
import datetime

class KafkaChatUser(HttpUser):
    wait_time = between(1, 2) # 각 task 실행 사이에 1~2초 랜덤 대기

    @task
    def send_message(self):
        self.client.post(
            "/api/kafka/send",
            json={
                "sender": "locustUser",
                "content": "Kafka 메시지 테스트",
                "timestamp": datetime.datetime.now().isoformat()
            }
        )
