# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FundPilot AI is a Chinese-language fund investment intelligent analysis platform (基金智能分析平台). It is a front-end/back-end separated monorepo with two independent projects — no shared build tool or root-level package.json.

## Repository Structure

```
FundPilot AI/
├── fundpilot-ai-backend/   # Spring Boot 3.3.5 + Java 17
├── fundpilot-ai-frontend/  # Vue 3 + Vite + Tailwind CSS v4
└── CLAUDE.md
```

## Build & Run Commands

### Backend (from `fundpilot-ai-backend/`)

```bash
mvn clean package                    # Build
mvn spring-boot:run                  # Run (port 8080)
mvn test                             # Run tests
java -jar target/fundpilot-ai-backend-1.0.0-SNAPSHOT.jar  # Run JAR
```

API docs at runtime: `http://localhost:8080/doc.html` (Knife4j/Swagger UI)

### Frontend (from `fundpilot-ai-frontend/`)

```bash
npm install          # Install dependencies
npm run dev          # Dev server (port 5173, proxies /api to localhost:8080)
npm run build        # Production build (outputs to dist/)
npm run preview      # Preview production build
```

## Architecture

### Backend (`fundpilot-ai-backend/`)

Standard layered Spring Boot architecture under `com.fundpilot`:

- **`controller/`** — REST controllers. All endpoints prefixed `/api/`. Key controllers: `AuthController`, `FundController`, `PortfolioController`, `AgentController`, `ReportController`, `AnalyticsController`
- **`service/`** + `service/impl/` — Business logic. `FundAnalyticsEngine` contains pure Java financial calculations (no Spring dependency)
- **`mapper/`** — MyBatis-Plus mapper interfaces. XML mappers in `resources/mapper/`
- **`entity/`** — Database entities with `@TableLogic` soft delete (`deleted` column)
- **`dto/`** — Request DTOs; **`vo/`** — Response view objects
- **`config/`** — Spring configuration (AI, auth, CORS, MyBatis, Redis, etc.)
- **`agent/`** — Multi-agent AI system (core differentiator):
  - `FundAdvisorAgent` — Orchestrator: loads memory → routes intent → dispatches to specialist
  - `AgentRouter` — Two-phase routing: keyword regex (fast) → LLM classification (fallback)
  - 4 specialist agents: `FundAnalysisAgent`, `FundComparisonAgent`, `FundRecommendationAgent`, `PortfolioDiagnosisAgent`
  - `tool/` — 8 Tool Calling functions (`FundInfoTool`, `FundReturnTool`, `FundRiskTool`, etc.)
  - `prompt/` — System prompts per agent
  - `report/` — Report generation subsystem
- **`common/`** — `R.java` (unified response wrapper), `PageResult`, `BizException`, `GlobalExceptionHandler`, enums

### Frontend (`fundpilot-ai-frontend/`)

Vue 3 SPA with Composition API (`<script setup>`):

- **`src/router/index.js`** — Routes. `/login` has no layout; `/` uses `MainLayout` with sidebar nav. Route guard redirects to `/login` if no token in localStorage
- **`src/stores/`** — Pinia stores: `user.js`, `fund.js`, `portfolio.js`, `chat.js`, `compare.js`
- **`src/api/`** — Axios API modules. `request.js` sets `baseURL: '/api'`, adds Bearer token interceptor
- **`src/views/`** — Page components (Dashboard, FundList, FundDetail, FundCompare, Portfolio, PortfolioAnalysis, Advisor, ReportView, UserCenter, Login)
- **`src/components/charts/`** — ECharts wrapper components
- **`src/assets/main.css`** — Dark theme design system (CSS custom properties + Tailwind v4 theme + Element Plus overrides)
- **`src/utils/chart.js`** — ECharts option builders with dark theme

### Key Integrations

- **AI**: Spring AI 1.0.0-M5 with OpenAI-compatible mode → Alibaba DashScope (`qwen-plus` model)
- **Auth**: Sa-Token 1.39.0 (Bearer token, UUID style, 24h TTL, Redis session store)
- **ORM**: MyBatis-Plus 3.5.9 with logical delete, XML mappers for complex queries
- **DB**: MySQL 8.0+ (`fundpilot` database). Schema in `resources/db/schema.sql`, seed data in `resources/db/data.sql`. Both run on every startup (`spring.sql.init.mode=always`)

### Database Tables (9)

`user`, `fund_manager`, `fund`, `fund_nav`, `fund_holding`, `portfolio`, `portfolio_item`, `chat_history`, `analysis_report`

All tables use logical delete (`deleted` column). Foreign keys are logical only (no DB constraints).

### Unified Response Format

All API responses use `R<T>` wrapper:
```json
{ "code": 200, "message": "success", "data": <T>, "timestamp": 1234567890 }
```

### Frontend-Backend Route Mapping

Frontend calls `/api/*` (via axios baseURL), Vite dev proxy forwards to `http://localhost:8080`. Backend controllers must use `@RequestMapping("/api/...")` prefix.

| Frontend Path | Backend Controller |
|---|---|
| `/api/auth/*` | `AuthController` |
| `/api/fund/*` | `FundController` |
| `/api/portfolio/*` | `PortfolioController` |
| `/api/agent/*` | `AgentController` |
| `/api/chat/*` | `ChatController` |
| `/api/report/*` | `ReportController` |
| `/api/analytics/*` | `AnalyticsController` |

## Infrastructure Requirements

- **MySQL 8.0+** on `localhost:3306`, database `fundpilot`, user `root`
- **Redis** on `localhost:6379`, no password, database 0
- **DashScope API key** configured in `application-dev.yml`

## Testing

Backend has one test file: `FundAnalyticsEngineTest.java` (pure JUnit 5 unit tests for financial calculation engine). No integration or controller tests exist.

```bash
cd fundpilot-ai-backend && mvn test
```

## Conventions

- Backend uses Lombok (`@Data`, `@Builder`, `@Slf4j`, `@RequiredArgsConstructor`) — do not write getters/setters/constructors manually
- All entities use `@TableLogic` on `deleted` field for soft delete — never hard-delete records
- Frontend uses `<script setup>` Composition API exclusively — no Options API
- Frontend uses Tailwind CSS v4 utility classes + CSS custom properties from `main.css` — avoid inline styles
- ECharts charts use the dark theme from `src/utils/chart.js` — use the exported option builder functions
- Element Plus components are auto-imported — no manual import needed for Element Plus components/icons
