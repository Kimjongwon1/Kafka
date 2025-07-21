import requests
import json
from websocket import create_connection
import time

def debug_server():
    print("🔍 서버 상태 디버깅 시작...\n")
    
    # 1. 기본 서버 응답 확인
    print("1️⃣ 서버 기본 상태 확인")
    try:
        response = requests.get("http://localhost:8080", timeout=5)
        print(f"✅ 서버 응답: {response.status_code}")
        print(f"   응답 헤더: {dict(response.headers)}")
    except Exception as e:
        print(f"❌ 서버 응답 실패: {e}")
        return
    
    # 2. Actuator 상태 확인
    print("\n2️⃣ Actuator Health 확인")
    try:
        health = requests.get("http://localhost:8080/actuator/health", timeout=5)
        print(f"✅ Health 체크: {health.status_code}")
        if health.status_code == 200:
            print(f"   상태: {health.json()}")
    except Exception as e:
        print(f"❌ Health 체크 실패: {e}")
    
    # 3. WebSocket 엔드포인트 정보 확인
    print("\n3️⃣ WebSocket 관련 엔드포인트 확인")
    
    # Spring Boot Actuator로 매핑 정보 확인
    try:
        mappings = requests.get("http://localhost:8080/actuator/mappings", timeout=5)
        if mappings.status_code == 200:
            mapping_data = mappings.json()
            print("✅ 엔드포인트 매핑 정보:")
            # WebSocket 관련 매핑 찾기
            for context in mapping_data.get('contexts', {}).values():
                for mapping in context.get('mappings', {}).get('dispatcherHandlers', {}).get('webHandler', []):
                    if 'ws' in str(mapping).lower() or 'websocket' in str(mapping).lower():
                        print(f"   🔌 {mapping}")
        else:
            print(f"❌ 매핑 정보 조회 실패: {mappings.status_code}")
    except Exception as e:
        print(f"❌ 매핑 정보 에러: {e}")
    
    # 4. SockJS 정보 확인
    print("\n4️⃣ SockJS 정보 확인")
    sockjs_paths = [
        "/ws/chat/info",
        "/ws/info",
        "/chat/info",
        "/websocket/info"
    ]
    
    for path in sockjs_paths:
        try:
            info_response = requests.get(f"http://localhost:8080{path}", timeout=3)
            if info_response.status_code == 200:
                print(f"✅ SockJS 정보 ({path}): {info_response.json()}")
            else:
                print(f"❌ {path}: {info_response.status_code}")
        except Exception as e:
            print(f"❌ {path}: 연결 실패")
    
    # 5. 기본 HTTP 요청으로 CORS 확인
    print("\n5️⃣ CORS 설정 확인")
    try:
        # OPTIONS 요청으로 CORS 확인
        options_response = requests.options("http://localhost:8080/ws/chat", timeout=5)
        print(f"✅ OPTIONS 응답: {options_response.status_code}")
        print(f"   CORS 헤더: {dict(options_response.headers)}")
    except Exception as e:
        print(f"❌ OPTIONS 요청 실패: {e}")
    
    # 6. 가능한 WebSocket 엔드포인트 테스트
    print("\n6️⃣ 가능한 WebSocket 엔드포인트 테스트")
    possible_endpoints = [
        "ws://localhost:8080/ws",
        "ws://localhost:8080/websocket", 
        "ws://localhost:8080/ws/chat",
        "ws://localhost:8080/chat",
        "ws://localhost:8080/ws/chat-direct",
        "ws://localhost:8080/stomp",
        "ws://localhost:8080/ws-stomp",
    ]
    
    working_endpoints = []
    
    for endpoint in possible_endpoints:
        try:
            print(f"   테스트: {endpoint}")
            ws = create_connection(endpoint, timeout=3)
            ws.close()
            print(f"   ✅ 성공: {endpoint}")
            working_endpoints.append(endpoint)
        except Exception as e:
            error_msg = str(e)
            if "400" in error_msg:
                print(f"   ❌ 400 Bad Request")
            elif "404" in error_msg:
                print(f"   ❌ 404 Not Found")
            elif "403" in error_msg:
                print(f"   ❌ 403 Forbidden")
            else:
                print(f"   ❌ {error_msg[:50]}")
    
    print(f"\n🎯 결과 요약:")
    print(f"   사용 가능한 WebSocket 엔드포인트: {working_endpoints}")
    
    if not working_endpoints:
        print("\n💡 해결 방법:")
        print("1. 서버 설정에 WebSocket 엔드포인트 추가:")
        print("   registry.addEndpoint(\"/ws\").setAllowedOriginPatterns(\"*\");")
        print("2. 서버 재시작")
        print("3. 방화벽/포트 확인")
        print("4. Spring Boot 버전 확인")

if __name__ == "__main__":
    debug_server()