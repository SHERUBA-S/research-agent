# 🔬 AI Research Agent

An autonomous, full-stack, production-grade AI Research Agent web application. Given any research question or topic, the agent executes autonomous web searches, gathers credible sources, extracts deep page contents, and synthesizes structured intelligence reports with verified citations.

---

## 🌟 Key Features

- **Autonomous Research Pipeline**: Multi-stage pipeline executing query processing, multi-source web crawling, content extraction, deduplication, and AI synthesis.
- **Strictly Grounded Reports**: Synthesizes verified findings with direct citation links (`[1]`, `[2]`) pointing to live web sources, eliminating hallucinations.
- **Structured Executive Briefs**:
  - **Executive Summary**: High-level strategic briefing.
  - **Key Findings**: Evidence-backed factual points with interactive citation pills.
  - **Detailed Analysis**: Comprehensive multi-paragraph deep-dive.
  - **Important Insights**: Actionable strategic takeaways and future implications.
  - **Verified Sources**: Card grid showing source titles, domains, excerpts, and direct external links.
- **Interactive Citation Navigation**: Clicking any citation marker (`[1]`, `[2]`) in the report smoothly scrolls and highlights the exact reference source.
- **Export & Sharing**: One-click Markdown export (`.md` file download) and clipboard copy.
- **Full Research History**: Persistent research dashboard with search history, status tracking, and permalink routing (`/research/[id]`).
- **Resilient Multi-Provider Fallbacks**:
  - **Search Providers**: DuckDuckGo (zero-key live search), Tavily Search API, Google Serper API.
  - **AI Providers**: Google Gemini API (`gemini-1.5-flash`), OpenAI API (`gpt-4o-mini`).
- **Database Flexibility**: PostgreSQL with JPA/Hibernate, with automatic embedded H2 fallback for zero-configuration local development.
- **Interactive API Documentation**: Embedded OpenAPI 3.0 / Swagger UI at `/swagger-ui.html`.

---

## 🏗 Architecture & Research Pipeline

```
┌─────────────────┐
│  USER QUESTION  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ResearchRequest │ (Validation & UUID initialization)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  SearchService  │ (Tavily / Serper / DuckDuckGo live search)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  SourceService  │ (URL normalization, deduplication & domain parsing)
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│ ContentExtractionService │ (HTML text extraction & sanitization via JSoup)
└────────┬─────────────────┘
         │
         ▼
┌─────────────────┐
│    AIService    │ (Grounded prompt engineering with Gemini / OpenAI)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  ReportService  │ (JSON serialization & citation number assignment)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ PostgreSQL / DB │ (Transactional JPA persistence of research & sources)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ResearchResponse│ (Delivered to Next.js Frontend)
└─────────────────┘
```

---

## 🛠 Technology Stack

### Frontend
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **UI Library**: React 18, Tailwind CSS, Lucide Icons
- **Design**: Modern glassmorphism, responsive dark mode, animated progress tracker

### Backend
- **Framework**: Java 21+ / Spring Boot 3.3.4
- **Build System**: Maven (with `mvnw.cmd` and `mvnw.ps1` wrappers)
- **Persistence**: Spring Data JPA, Hibernate
- **Database**: PostgreSQL (with in-memory H2 fallback)
- **Web Scraping & Parsing**: JSoup 1.18.1
- **API Documentation**: SpringDoc OpenAPI / Swagger UI 2.6.0

---

## 📂 Project Structure

```
research-agent/
├── frontend/
│   ├── app/
│   │   ├── layout.tsx             # Root layout with dark mode
│   │   ├── page.tsx               # Main research dashboard
│   │   ├── globals.css            # Tailored design system & animations
│   │   └── research/[id]/page.tsx # Permalink report view
│   ├── components/
│   │   ├── Header.tsx             # Top navigation & API status
│   │   ├── Hero.tsx               # Hero headline & value proposition
│   │   ├── ResearchInput.tsx      # Multi-line input & quick prompts
│   │   ├── ResearchReport.tsx     # Structured report presentation & export
│   │   ├── SourcesList.tsx        # Verified source cards & links
│   │   ├── StatusBadge.tsx        # Status pills (Completed, In Progress, Failed)
│   │   ├── LoadingSkeleton.tsx    # 4-stage pipeline animated tracker
│   │   └── HistorySidebar.tsx     # Past research flyout drawer
│   ├── lib/
│   │   ├── api.ts                 # REST API client
│   │   └── utils.ts               # Date formatters & clipboard helpers
│   ├── types/
│   │   └── index.ts               # TypeScript data models
│   ├── package.json
│   ├── tailwind.config.js
│   └── tsconfig.json
│
├── backend/
│   ├── src/main/java/com/sheruba/research_agent/
│   │   ├── ResearchAgentApplication.java
│   │   ├── config/                # CORS, OpenAPI, RestClient configurations
│   │   ├── controller/            # ResearchController REST endpoints
│   │   ├── dto/                   # Request & Response DTOs
│   │   ├── entity/                # Research & ResearchSource JPA entities
│   │   ├── exception/             # Global exception handlers & custom errors
│   │   ├── repository/            # Spring Data JPA repositories
│   │   └── service/               # AIService, SearchService, Pipeline orchestration
│   ├── src/main/resources/
│   │   └── application.yml        # Spring Boot application configuration
│   ├── src/test/java/             # Controller & Service unit tests
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── mvnw.ps1
│
├── .env.example
├── .gitignore
└── README.md
```

---

## ⚙️ Prerequisites

- **Java**: JDK 21 or higher (`java -version`)
- **Node.js**: Node 18.x or higher & npm (`node -v`, `npm -v`)
- **PostgreSQL** *(Optional)*: PostgreSQL 14+ (an in-memory database runs automatically if PostgreSQL is not active)

---

## 🔑 Environment Variables

Copy `.env.example` to configure your keys:

```bash
cp .env.example .env
```

| Variable | Description | Default |
|---|---|---|
| `DATABASE_URL` | PostgreSQL JDBC connection URL | `jdbc:h2:mem:research_agent...` |
| `DATABASE_USERNAME` | PostgreSQL database username | `sa` |
| `DATABASE_PASSWORD` | PostgreSQL database password | *(empty)* |
| `AI_PROVIDER` | AI provider (`gemini` or `openai` or `auto`) | `auto` |
| `AI_API_KEY` | Google Gemini API key or OpenAI API key | *(required for AI)* |
| `GEMINI_MODEL` | Gemini model name | `gemini-1.5-flash` |
| `SEARCH_PROVIDER` | Search provider (`duckduckgo`, `tavily`, `serper`, `auto`) | `auto` |
| `SEARCH_API_KEY` | Optional Tavily/Serper search key | *(DuckDuckGo used if empty)* |
| `BACKEND_URL` | Backend URL for Next.js proxy | `http://localhost:8080` |
| `NEXT_PUBLIC_API_URL`| API URL accessed from browser | `http://localhost:8080/api` |

---

## 🚀 Running the Application

### 1. Run Backend (Spring Boot)

On Windows PowerShell:
```powershell
cd backend
.\mvnw.ps1 spring-boot:run
```

On Windows Command Prompt:
```cmd
cd backend
mvnw.cmd spring-boot:run
```

The backend starts at `http://localhost:8080`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 2. Run Frontend (Next.js)

In a new terminal:
```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000` in your browser.

---

## 🗄 Database Setup (PostgreSQL)

If using a local PostgreSQL instance:

1. Create the database:
   ```sql
   CREATE DATABASE research_agent;
   ```
2. Set environment variables in your terminal or `.env`:
   ```bash
   DATABASE_URL=jdbc:postgresql://localhost:5432/research_agent
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=your_password
   DATABASE_DRIVER=org.postgresql.Driver
   ```
3. Hibernate will automatically create and update the required tables (`researches`, `research_sources`) on startup (`ddl-auto: update`).

---

## 📡 API Endpoints

### 1. Conduct Research Task
`POST /api/research`

**Request:**
```json
{
  "query": "Applications of AI in healthcare"
}
```

**Response (200 OK):**
```json
{
  "id": "27abf6a7-101c-4236-aaeb-7b5ff38a87ff",
  "query": "Applications of AI in healthcare",
  "status": "COMPLETED",
  "title": "Transformative Applications of AI in Modern Healthcare",
  "summary": "Artificial intelligence is revolutionizing patient diagnostics, medical imaging, and personalized treatment workflows...",
  "keyFindings": [
    "AI diagnostics achieve expert-level accuracy in radiology and pathology [1].",
    "Predictive analytics reduce hospital readmission rates and optimize ICU bed utilization [2]."
  ],
  "analysis": "Recent breakthroughs in computer vision and large medical foundation models have enabled automated screening...",
  "importantInsights": [
    "Interoperability standards and clinical validation remain critical adoption barriers.",
    "Federated learning enables cross-hospital AI training while preserving patient privacy."
  ],
  "sources": [
    {
      "id": "3c8ef123-...",
      "title": "AI in Healthcare: 2026 Breakthroughs",
      "url": "https://www.nature.com/articles/...",
      "domain": "nature.com",
      "snippet": "Overview of deep learning deployment across clinical workflows...",
      "citationNumber": 1
    }
  ],
  "createdAt": "2026-10-07T10:30:00Z",
  "updatedAt": "2026-10-07T10:30:15Z"
}
```

### 2. Get Research Report by ID
`GET /api/research/{id}`

### 3. List Research History
`GET /api/research`

### 4. Delete Research Task
`DELETE /api/research/{id}`

### 5. Health Check
`GET /api/health`

---

## 🧪 Testing

To run backend tests:
```powershell
cd backend
.\mvnw.ps1 test
```

To run frontend build test:
```bash
cd frontend
npm run build
```

---

## 🔒 Security Practices

- **Zero Hardcoded Secrets**: All API keys and credentials are read strictly from environment variables.
- **Input Validation**: Bean Validation on queries with length and blank checks.
- **XSS & Injection Protection**: Content sanitization through JSoup and React JSX escaping.
- **CORS Protection**: Configurable allowed origins.
