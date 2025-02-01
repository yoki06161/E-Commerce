# E-Commerce
**Spring Boot + JPA + MySQL + RabbitMQ** 등을 사용해 구현한 간단한 전자상거래(E-Commerce) 시스템 예제입니다.  
주문 생성 시 재고 차감, RabbitMQ 이벤트 발행, 주문 내역 조회 등 전반적인 온라인 쇼핑 시나리오를 간단하게나마 체험할 수 있습니다.

<br>

## 1. 프로젝트 개요
- **주문** → **재고 감소** → **이벤트 발행(RabbitMQ)** 순으로 동작하는 단일(모놀리틱) 웹 애플리케이션 예제  
- **User / Product / Order** 도메인을 설계하여, 회원 등록・상품 등록・주문 생성 흐름을 간단히 익힐 수 있음  
- Docker Compose로 **MySQL**과 **RabbitMQ**를 로컬에서 손쉽게 구동 가능  
- 확장/학습 방향: 마이크로서비스 분리, JWT 인증, 결제/배송 서비스 추가, CI/CD 파이프라인 구성 등  

<br>

## 2. 주요 기능
### 회원 등록 (User)
- `/api/users`로 회원(username, email) 생성  
- 회원 데이터 조회: `/api/users/{id}`

### 상품 등록/조회 (Product)
- `/api/products`로 상품(name, price, stockQuantity) 등록  
- `/api/products/{id}`로 상품 조회

### 주문 생성 (Order)
- `/api/orders`로 주문 생성 (주문 시 자동 재고 차감)  
- `/api/orders/{id}`로 주문 조회  
- 주문 생성 시 **RabbitMQ**로 “주문 생성” 이벤트 발행 → `OrderCreatedConsumer`가 로그 출력 (추후 알림/이메일 전송 등 확장 가능)

### 재고 관리
- 주문 요청 수량이 재고보다 많으면 예외 발생  
- 주문 성공 시 재고 감소 처리  

<br>

## 3. 기술 스택
- **Back-end**  
  - Java 17  
  - Spring Boot 3.x  
  - Spring Data JPA (Hibernate)  
  - RabbitMQ (Spring AMQP)  
  - MySQL  

- **빌드/관리**  
  - Gradle  

- **기타**  
  - Docker Compose (로컬 환경에서 DB, 메시지 브로커 실행)  

<br>

## 4. 프로젝트 구조
```
ecommerce-demo
 ┣ docker-compose.yml                  // MySQL & RabbitMQ 컨테이너 구성
 ┣ build.gradle                        // Gradle 설정
 ┣ src
 ┃ ┣ main
 ┃ ┃ ┣ java/com/example/ecommerce
 ┃ ┃ ┃ ┣ config/                       // RabbitMQ 설정
 ┃ ┃ ┃ ┣ controller/                   // REST 컨트롤러 (User, Product, Order)
 ┃ ┃ ┃ ┣ domain/                       // JPA 엔티티 (User, Product, Order)
 ┃ ┃ ┃ ┣ dto/                          // DTO 클래스 (Create 요청 등)
 ┃ ┃ ┃ ┣ messaging/                    // 메시징 프로듀서/컨슈머
 ┃ ┃ ┃ ┣ repository/                   // JPA 레포지토리
 ┃ ┃ ┃ ┣ service/                      // 비즈니스 로직 (User, Product, Order)
 ┃ ┃ ┃ ┗ EcommerceApplication.java     // Spring Boot 메인 클래스
 ┃ ┃ ┗ resources
 ┃ ┃    ┗ application.yml             // DB, RabbitMQ 설정 (포트/계정정보 등)
 ┗ ...
```
- docker-compose.yml:
  - MySQL 8.x, RabbitMQ 3.x 컨테이너 한꺼번에 띄움
  - MySQL 접속: localhost:13306 (user/pass)
  - RabbitMQ 접속: localhost:5672 (admin/admin), Management UI: localhost:15672

## 5. 사용 방법
### 5.1 로컬 환경에서 실행하기
1. **Docker Compose 실행**
   ```
   docker-compose up -d
   ```
   - MySQL(포트 13306), RabbitMQ(5672/15672) 컨테이너가 백그라운드에서 실행됨
   - RabbitMQ 관리자 페이지: http://localhost:15672 (ID/PW: `admin` / `admin`)
     
2. **DB 초기 설정**
   - `application.yml`에 명시된 대로 DB `ecommerce_db`가 자동 생성 (DDL auto: update)
   - 사용자 이름/비밀번호: `user / pass`
     
3. **프로젝트 빌드 & 실행**
   - Gradle 예시
   ```
   ./gradlew bootRun
   ```
   - 실행 후, http://localhost:8080 에서 애플리케이션 확인
     
### 5.2 API 테스트
   - **회원 등록:**
     - `POST /api/users`
```
{
  "username": "alice",
  "email": "alice@example.com"
}
```
   - **상품 등록:**
     - `POST /api/products`
```
{
  "name": "Keyboard",
  "price": 30000,
  "stockQuantity": 50
}
```
   - **주문 생성:**
     - `POST /api/orders`
```
{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}
```
   - 주문 성공 시, 재고 수량이 감소 (`50 → 48`), 주문 정보가 DB에 저장, **RabbitMQ 이벤트 발행**
   - `OrderCreatedConsumer`가 이벤트 메시지를 수신하여 콘솔 로그 출력

<br>
  
   - **데이터 조회:**
     - 회원 조회: `GET /api/users/{id}`
     - 상품 조회: `GET /api/products/{id}`
     - 주문 조회: `GET /api/orders/{id}`

<br>

## 6. 시연 예시
1. **회원 등록 → 상품 등록**  
   - 예: 회원 `alice`, 상품 `Keyboard(재고 50)`
2. **주문 생성**  
   - 예: `userId=1`, `productId=1`, `quantity=2`  
   - DB에 `Order` 레코드 생성, `Product.stockQuantity` = 48로 감소
   - 콘솔 로그 예시:
     ```
     [RabbitMQ] Received: OrderCreated - OrderID:1, User:alice, TotalPrice:60000
     ```
3. **RabbitMQ 대시보드**  
   - http://localhost:15672 접속  
   - `order.created.queue` 메시지 처리 현황 확인
4. **오류 시나리오**  
   - 재고 부족 시 `"Not enough stock"` 예외 발생

<br>

## 7. 주요 학습 포인트
1. **Spring Boot & JPA 기본**  
   - User, Product, Order 엔티티 설계  
   - `@ManyToOne` 관계 설정, 재고 차감 로직
2. **메시징(RabbitMQ)**  
   - 주문 생성 시 이벤트 발행 → 메시지 큐 소비자(Consumer)에서 처리  
   - 추가로 이메일/SMS, 알림, 배송 서비스 연동 등 확장 가능
3. **Docker Compose**  
   - 로컬 환경에서 MySQL, RabbitMQ 등 개발 인프라를 빠르게 구성  
   - 운영 환경 확장(쿠버네티스, AWS ECS 등) 전 단계 학습
4. **REST API 설계**  
   - Controller/Service/Repository 계층 분리  
   - DTO를 통한 요청/응답 데이터 분리
5. **트랜잭션 & 예외 처리**  
   - `@Transactional` 로 주문 시 **원자적(atomic)**으로 DB 반영  
   - 재고 부족 시 예외 발생 처리
