from locust import User, task
from websocket import create_connection
import time
import json
import random
import uuid
import threading

class MeaningfulWebSocketUser(User):
    wait_time = lambda self: random.uniform(1, 3)
    
    def on_start(self):
        self.ws = None
        self.connected = False
        self.user_id = f"user_{random.randint(1000, 9999)}"
        self.session_id = str(uuid.uuid4())
        self.my_messages = {}  # 내가 보낸 메시지 추적
        
        try:
            self.ws = create_connection("ws://localhost:8080/ws/chat-direct", timeout=10)
            print(f"✅ {self.user_id} 연결 성공!")
            self._stomp_connect()
            
            # 백그라운드에서 내 메시지 응답 확인
            self.listen_thread = threading.Thread(target=self._track_my_messages)
            self.listen_thread.daemon = True
            self.listen_thread.start()
            
        except Exception as e:
            print(f"❌ {self.user_id} 연결 실패: {e}")
            self.ws = None

    def _stomp_connect(self):
        """STOMP 연결"""
        try:
            connect_frame = f"CONNECT\naccept-version:1.0,1.1,2.0\n\n\x00"
            self.ws.send(connect_frame)
            
            response = self.ws.recv()
            if "CONNECTED" in response:
                print(f"📡 {self.user_id} STOMP 연결 성공!")
                self.connected = True
                
                # 구독
                subscribe_frame = f"SUBSCRIBE\nid:sub-{self.session_id}\ndestination:/topic/messages\n\n\x00"
                self.ws.send(subscribe_frame)
                
        except Exception as e:
            print(f"❌ {self.user_id} STOMP 연결 실패: {e}")

    def _track_my_messages(self):
        """내가 보낸 메시지만 추적해서 라운드트립 시간 측정"""
        while self.connected and self.ws:
            try:
                response = self.ws.recv()
                
                if "MESSAGE" in response:
                    # 메시지 파싱
                    try:
                        body_start = False
                        message_body = ""
                        for line in response.split('\n'):
                            if body_start:
                                message_body += line
                            if line == "":
                                body_start = True
                        
                        if message_body:
                            data = json.loads(message_body.replace('\x00', ''))
                            
                            # 내가 보낸 메시지인지 확인
                            if data.get('sender') == self.user_id:
                                content = data.get('content', '')
                                
                                # 내가 보낸 메시지와 매칭
                                if content in self.my_messages:
                                    start_time = self.my_messages.pop(content)
                                    round_trip_time = int((time.time() - start_time) * 1000)
                                    
                                    # 🎯 진짜 의미있는 측정: Kafka 라운드트립
                                    self.environment.events.request.fire(
                                        request_type="websocket",
                                        name="kafka_round_trip",
                                        response_time=round_trip_time,
                                        response_length=len(response)
                                    )
                                    
                                    print(f"🔄 {self.user_id} Kafka 라운드트립: {round_trip_time}ms")
                    except:
                        pass
                        
            except:
                continue

    @task(10)
    def send_message_and_track(self):
        """메시지 전송 + 라운드트립 추적"""
        if not self.ws or not self.connected:
            return
            
        start_time = time.time()
        
        try:
            # 유니크한 메시지 생성
            unique_content = f"{self.user_id}_msg_{random.randint(1000, 9999)}_{int(time.time())}"
            
            chat_message = {
                "sender": self.user_id,
                "content": unique_content,
                "timestamp": None
            }
            
            # 라운드트립 추적을 위해 기록
            self.my_messages[unique_content] = start_time
            
            # 메시지 전송
            send_frame = f"SEND\ndestination:/app/chat/send\ncontent-type:application/json\n\n{json.dumps(chat_message)}\x00"
            self.ws.send(send_frame)
            
            # 전송 시간 측정 (WebSocket → 서버)
            send_time = int((time.time() - start_time) * 1000)
            
            # 🎯 의미있는 측정 1: 메시지 전송 시간
            self.environment.events.request.fire(
                request_type="websocket",
                name="send_message",
                response_time=send_time,
                response_length=len(json.dumps(chat_message))
            )
            
            print(f"📤 {self.user_id} 전송 완료: {send_time}ms")
            
        except Exception as e:
            response_time = int((time.time() - start_time) * 1000)
            
            self.environment.events.request.fire(
                request_type="websocket",
                name="send_message",
                response_time=response_time,
                response_length=0,
                exception=e
            )

    @task(2)
    def connection_health_check(self):
        """연결 상태 확인 (의미있는 측정)"""
        if not self.ws or not self.connected:
            return
            
        start_time = time.time()
        
        try:
            # STOMP heartbeat
            self.ws.send("\n")
            
            # 응답시간 측정
            response_time = int((time.time() - start_time) * 1000)
            
            # 🎯 의미있는 측정 2: 연결 상태 확인
            self.environment.events.request.fire(
                request_type="websocket",
                name="health_check",
                response_time=response_time,
                response_length=1
            )
            
        except Exception as e:
            response_time = int((time.time() - start_time) * 1000)
            
            self.environment.events.request.fire(
                request_type="websocket",
                name="health_check",
                response_time=response_time,
                response_length=0,
                exception=e
            )

    def on_stop(self):
        self.connected = False
        try:
            if self.ws:
                disconnect_frame = f"DISCONNECT\n\n\x00"
                self.ws.send(disconnect_frame)
                self.ws.close()
                print(f"🔚 {self.user_id} 종료")
        except:
            pass