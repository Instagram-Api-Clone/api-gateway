# 🚪 API Gateway (Instagram Clone Backend)

The **API Gateway** acts as the single entry point for all client requests in the [Instagram Clone Microservices Backend](https://github.com/Instagram-Api-Clone). It manages dynamic request routing, path rewriting, security filter orchestration (JWT validation), and exposes the interactive API documentation portal.

---

## 🛠️ Features & Responsibilities
* **Dynamic Routing**: Dispatches incoming traffic to downstream microservices (`user-service`, `post-service`, etc.) using Spring Cloud Gateway and Eureka Discovery or Kubernetes DNS.
* **Pre-Routing JWT Security Filter**: Decodes, validates, and verifies JWT tokens on all protected routes. Extracts the authenticated `userId` and forwards it as a request header to downstream microservices.
* **Static API Reference Portal**: Serves the fully customized, premium **Scalar API Reference Portal** at `/reference.html` for interactive sandbox API testing.
* **CORS Management**: Enforces global CORS configurations to permit seamless integration with the React/Vite web UI on localhost or public domains.

---

## 🧱 Tech Stack
* **Framework**: Spring Boot 3 & Spring Cloud Gateway
* **Language**: Java 21
* **Security**: JWT (JSON Web Tokens) via `jjwt`
* **Discovery & Integration**: Spring Cloud Netflix Eureka / Kubernetes CoreDNS
* **Monitoring**: New Relic APM agent
* **Containerization**: Docker (via Jib container builder)

---

## 📡 Dynamic Routes & Endpoints Map

All inbound requests enter via port `8080` and are redirected based on paths:

| Incoming Path Prefix | Target Microservice | Security Filter | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/auth/**` | `USER-SERVICE` | Public | Authentication endpoints (Signup, Login) |
| `/api/v1/users/**` | `USER-SERVICE` | Bearer JWT | Profile details and avatar updates |
| `/api/v1/follow/**` | `FOLLOW-SERVICE` | Bearer JWT | Social connections and follow requests |
| `/api/v1/posts/**` | `POST-SERVICE` | Bearer JWT | Posts feed, post creation, and likes |
| `/api/v1/notifications/**` | `NOTIFICATION-SERVICE` | Bearer JWT | Event-driven alert listings |
| `/api/v1/chats/**` | `CHAT-SERVICE` | Bearer JWT | Real-time chat messages and history |
| `/reference.html` | API Gateway Static | Public | Scalar Interactive API spec sandbox |

---

## ⚙️ Running Locally

### Prerequisites
1. Spring Cloud **Discovery Server** must be running on port `8761`.
2. Spring Cloud **Configuration Server** must be running on port `8888`.

### Launching
1. Set the following environment variables:
   * `JWT_SECRET_KEY`: The symmetric signature key used to decode access tokens (minimum 256-bit).
   * `CONFIG_SERVER_URL` (Optional): Overrides default config server location (defaults to `http://localhost:8888`).
2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
   * The Gateway starts up on port **`8080`**.
