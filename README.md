# 🐾 Meowng Backend Repository

**동물 시점 SNS 플랫폼**의 백엔드 레포지토리입니다.  
이벤트 기반 투표, 좋아요 랭킹, 채팅, 알림 등 다양한 기능을 제공합니다.

## 🏗️ 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle
- **Database**: MySQL, Redis
- **Authentication**: JWT, Kakao OAuth
- **Messaging**: Kafka
- **Realtime**: WebSocket (STOMP), SSE

---
## API 명세서
https://github.com/100-hours-a-week/9_meow_wiki/wiki/%5BBE%5DAPI_%EC%84%A4%EA%B3%84

---
## ERD 구조
https://github.com/100-hours-a-week/9_meow_wiki/wiki/%5BBE%5DERD_%EC%84%A4%EA%B3%84

---
## 프로젝트 아키텍처 구조

---

## 📦 프로젝트 디렉토리 구조
meow_be\
┣ chat # 실시간 채팅 관련 (WebSocket, ChatRoom, Message)\
┣ notification/ # SSE 기반 알림 기능\
┣ posts/ # 게시글, 좋아요, 댓글 기능\
┣ eventposts/ # 이벤트 투표 시스템 (랭킹, 종료 트리거 등)\
┣ login/ # 회원 도메인 (JWT 인증, Kakao 로그인 등)\
┣ users/ # 회원 도메인 (회원정보 수정, 마이 페이지 등)\
┣ config/ # WebSocket, Security, Redis 설정

---

## 📚 주요 기능 요약

- 🔐 **JWT 기반 인증/인가**, 카카오 소셜 로그인
- 📝 **게시글**, 댓글, 좋아요 기능
- 🔔 **SSE 기반 알림** (실시간 좋아요 알림 등)
- 📊 **이벤트 투표**: Redis Sorted Set으로 실시간 랭킹 처리
- 🗳 **자동 투표 종료**: TTL + KeyExpirationEvent
- 💬 **실시간 채팅**: WebSocket + ChatRoom 구조

---

## 📈 성능 최적화 사례

| 문제                 | 시도 방식                             | 결과                                      |
|----------------------|----------------------------------------|-------------------------------------------|
| 좋아요 트래픽 폭주   | Redis Sorted Set 사용                  | 100TPS에서도 지연 없이 랭킹 반영           |
| 투표 종료 자동화     | Redis TTL + KeyExpirationEvent 활용     | 운영 리소스 감소 + 무효 투표 차단          |
| UI 실시간 반응       | SSE(Server-Sent Events) 적용           | 사용자 만족도 향상                         |
