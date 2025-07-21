from locust import HttpUser, task, between

class SimpleChatUser(HttpUser):
    wait_time = between(1, 2)

    @task
    def send_form(self):
        self.client.post(
            "/simple/send",
            data={
                "sender": "locustUser",
                "content": "단순 채팅 테스트"
            },
            headers={"Content-Type": "application/x-www-form-urlencoded"}
        )
