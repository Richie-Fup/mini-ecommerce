## Full-stack Mini Commerce (Spring Boot 3 + Taro)

This repo contains a **minimal full-stack e-commerce app**:

- **Backend**: Spring Boot 3 (Java 17), in-memory storage, OpenAPI docs
- **Frontend**: Taro (React + TypeScript) H5 app

### Requirements covered

- **GET** `/products`: list products (`id`, `name`, `price`, `stock`)
- **POST** `/orders`: create order (input `productId`, `quantity`) and reduce stock if possible
- **GET** `/orders/{id}`: retrieve order details (**bonus**, implemented)

---

## Backend

### Prerequisites

- Java 17+
- Maven 3.9+ (or compatible)

### Run

```bash
cd backend
mvn spring-boot:run
```

Backend starts on `http://localhost:8080`.

If your `mvn` is using an older Java (e.g. Java 8), set `JAVA_HOME` first:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

### OpenAPI / Swagger UI

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Example API calls

```bash
curl -s http://localhost:8080/products | jq
```

```bash
curl -s -X POST http://localhost:8080/orders \
  -H 'content-type: application/json' \
  -H 'Idempotency-Key: demo-001' \
  -d '{"productId":1,"quantity":2}' | jq
```

Note: `POST /orders` requires the `Idempotency-Key` header to prevent duplicate orders on retries.

```bash
curl -s http://localhost:8080/orders/1 | jq
```

---

## Frontend (Taro H5)

### Prerequisites

- Node.js 18+
- pnpm (recommended) / npm / yarn

### Install & Run (H5)

```bash
cd frontend
npm install
npm run dev:h5
```

Open `http://localhost:10086` in your browser.

### Backend URL

By default the frontend calls `http://localhost:8080`.
You can change it in `frontend/src/config.ts`.

---

## WeChat Mini Program (Taro weapp) Preview

### Prerequisites

- WeChat DevTools
- Node.js 18+

### Steps

1. Install deps:

```bash
cd frontend
npm install
```

2. Start Taro weapp build (watch):

```bash
npm run dev:weapp
```

3. Open WeChat DevTools → Import Project:
   - **Project directory**: `frontend/`
   - It uses `frontend/project.config.json` and points **miniprogramRoot** to `dist/`.
   - If you have your own AppID, replace the `appid` value in `frontend/project.config.json`.

4. Click **Preview** / **Simulator** in DevTools.


