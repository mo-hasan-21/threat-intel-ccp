# 🔐 Threat Intelligence Processing Platform
**Course:** COMP-370 – Software Construction and Development  
**CCP Assignment:** Development of Security System using Microservices  
**Instructor:** Dr. Malik Nabeel Ahmed Awan  
**Group Members:** [Muhammad Hasan Tahir (B23F0001SE039)], [Waseem Khan (B23F0001SE006)], [Rajab Ali (B23F0134SE044)]  

---

## 📖 Overview

A distributed microservices platform that ingests threat intelligence data, extracts Indicators of Compromise (IP addresses & domains), streams them through an event-driven Kafka pipeline, enriches them with severity scores, and stores them in MySQL for querying and analytics.

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Messaging | Apache Kafka 7.5 |
| Database | MySQL 8.0 |
| Build | Maven |
| Infrastructure | Docker Compose |

---

## 🚀 Quick Start

### 1. Start Infrastructure

```bash
docker-compose up -d
```

✅ Verify: `docker-compose ps` shows `kafka`, `zookeeper`, and `mysql` as `Up`.

### 2. Run Services (Open 4 separate terminals)

```bash
# Terminal 1
cd ingestion-service && mvn spring-boot:run

# Terminal 2
cd processing-service && mvn spring-boot:run

# Terminal 3
cd ranking-service && mvn spring-boot:run

# Terminal 4
cd storage-service && mvn spring-boot:run
```

✅ Wait for `Started [Service]Application in X.XX seconds` in each terminal.

### 3. Test the Pipeline

```bash
# Trigger ingestion
curl -X POST http://localhost:8081/api/ingest/trigger

# Query stored IOCs
curl http://localhost:8084/api/iocs

# Get analytics
curl http://localhost:8084/api/iocs/stats
```

---

## 📡 API Endpoints

| Service | Endpoint | Method | Description |
|---------|----------|--------|-------------|
| Ingestion | `/api/ingest/trigger` | POST | Fetch & publish IOCs to Kafka |
| Ingestion | `/api/ingest/health` | GET | Health check |
| Processing | `/api/processing/status` | GET | Consumer/topic status |
| Ranking | `/api/ranking/health` | GET | Health check |
| Storage | `/api/iocs` | GET | List all IOCs |
| Storage | `/api/iocs/{value}` | GET | Get IOC by IP/Domain |
| Storage | `/api/iocs/stats` | GET | Analytics summary |
| Storage | `/api/iocs/search?keyword=` | GET | Search IOCs |

📦 Postman collection available in `docs/threat-intel-ccp-postman-collection.json`

---

## 🧪 Verification

Check MySQL directly:

```bash
docker exec -it threat-intel-ccp-mysql-1 mysql -u root -proot threat_intel -e "SELECT * FROM iocs LIMIT 5;"
```

---

## 📄 Submission

- **Technical Report:** `docs/technical_report.pdf`
- **Architecture & Flow Diagrams:** Included in report
- **Group Contribution Log:** `docs/contributions.md`
- **Screenshots & Test Results:** `docs/screenshots/`

---

## 📚 References

- Spring Boot: https://spring.io/projects/spring-boot  
- Apache Kafka: https://kafka.apache.org/  
- AbuseIPDB & AlienVault APIs  
- UN Sustainable Development Goals (SDG 3, 4, 9, 16)

---

**© 2026 Pak-Austria Fachhochschule (PAF-IAST)**  
*School of Computing Sciences | COMP-370 CCP Submission*
