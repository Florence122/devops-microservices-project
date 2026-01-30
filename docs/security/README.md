# DevOps Microservices Project
## Java Spring Boot Microservices with CI/CD & Kubernetes

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Security](https://img.shields.io/badge/security-JWT-blue)]()
[![License](https://img.shields.io/badge/license-MIT-green)]()

## 📋 Project Overview

This project demonstrates a complete DevOps pipeline for Java microservices, including:
- Java Spring Boot microservices (User Service & Order Service)
- JWT authentication and security
- Docker containerization
- Kubernetes orchestration
- CI/CD with Jenkins & GitHub Actions
- Infrastructure as Code with Terraform
- Monitoring with Prometheus & Grafana
- Centralized logging with ELK Stack

---

## 🏗️ Architecture

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│   Client    │────────>│ User Service │────────>│  PostgreSQL  │
│  (Postman)  │         │  Port 8081   │         │  Database    │
└─────────────┘         └──────────────┘         └──────────────┘
      │                         │
      │                         │ JWT Token
      │                         ↓
      │                 ┌──────────────┐         ┌──────────────┐
      └────────────────>│Order Service │────────>│  PostgreSQL  │
                        │  Port 8082   │         │  Database    │
                        └──────────────┘         └──────────────┘
```

---

## 🛠️ Tech Stack

### Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **Build Tool:** Maven

### DevOps
- **CI/CD:** Jenkins, GitHub Actions
- **Containerization:** Docker
- **Orchestration:** Kubernetes (Minikube/K3s)
- **IaC:** Terraform
- **Monitoring:** Prometheus, Grafana
- **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana)

---

## 📁 Project Structure

```
devops-microservices-project/
├── docs/
│   └── security/                    # JWT & Security documentation
│       ├── 01-jwt-research.md
│       ├── 02-security-architecture.md
│       ├── 03-team-meeting-guide.md
│       └── README.md
├── user-service/                    # User management microservice
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── order-service/                   # Order management microservice
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── infrastructure/                  # Terraform & K8s manifests
│   ├── terraform/
│   └── kubernetes/
├── ci-cd/                          # CI/CD pipeline configs
│   ├── Jenkinsfile
│   └── .github/workflows/
├── monitoring/                     # Monitoring & logging configs
│   ├── prometheus/
│   ├── grafana/
│   └── elk/
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+
- Git

### 1. Clone Repository
```bash
git clone https://github.com/Florence122/devops-microservices-project.git
cd devops-microservices-project
```

### 2. Start PostgreSQL
```bash
docker run --name postgres-db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -e POSTGRES_DB=microservices_db \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Run User Service
```bash
cd user-service
mvn clean install
mvn spring-boot:run
```
User Service will start on **http://localhost:8081**

### 4. Run Order Service
```bash
cd order-service
mvn clean install
mvn spring-boot:run
```
Order Service will start on **http://localhost:8082**

---

## 🔐 Authentication & Security

This project implements JWT (JSON Web Token) authentication for secure API access.

### Quick Start - Authentication

**1. Register a User**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

**2. Login (Get JWT Token)**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123"
  }'
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

**3. Use Token for Protected Endpoints**
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "quantity": 1,
    "price": 999.99
  }'
```

### 📖 Security Documentation

For complete security architecture, implementation guides, and best practices:

➡️ **[View Security Documentation](./docs/security/README.md)**

Key documents:
- [JWT Research & Concepts](./docs/security/01-jwt-research.md)
- [Security Architecture](./docs/security/02-security-architecture.md) ⭐
- [Team Implementation Guide](./docs/security/03-team-meeting-guide.md)

---

## 📡 API Endpoints

### User Service (Port 8081)

#### Public Endpoints (No Authentication)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/actuator/health` | Health check |

#### Protected Endpoints (JWT Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Order Service (Port 8082)

#### Protected Endpoints (JWT Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PUT | `/api/orders/{id}` | Update order |
| DELETE | `/api/orders/{id}` | Delete order |
| GET | `/actuator/health` | Health check (public) |

---

## 🐳 Docker Deployment

### Using Docker Compose
```bash
docker-compose up --build
```

### Individual Services
```bash
# Build User Service
cd user-service
docker build -t user-service:latest .

# Build Order Service
cd order-service
docker build -t order-service:latest .

# Run with Docker
docker run -p 8081:8081 user-service:latest
docker run -p 8082:8082 order-service:latest
```

---

## ☸️ Kubernetes Deployment

### Deploy to Kubernetes
```bash
# Apply PostgreSQL
kubectl apply -f infrastructure/kubernetes/postgres-statefulset.yaml

# Deploy User Service
kubectl apply -f infrastructure/kubernetes/user-service-deployment.yaml

# Deploy Order Service
kubectl apply -f infrastructure/kubernetes/order-service-deployment.yaml

# Check status
kubectl get pods
kubectl get services
```

### Access Services
```bash
# Port forward User Service
kubectl port-forward service/user-service 8081:8081

# Port forward Order Service
kubectl port-forward service/order-service 8082:8082
```

---

## 🔧 Configuration

### Environment Variables

#### User Service
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/user_db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123

# JWT
JWT_SECRET=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
JWT_EXPIRATION=900000
```

#### Order Service
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/order_db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin123

# JWT (MUST match User Service)
JWT_SECRET=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
JWT_EXPIRATION=900000
```

---

## 📊 Monitoring & Logging

### Prometheus Metrics
Access metrics at:
- User Service: http://localhost:8081/actuator/prometheus
- Order Service: http://localhost:8082/actuator/prometheus

### Grafana Dashboards
Access Grafana at: http://localhost:3000
- Default credentials: admin/admin

### ELK Stack
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

---

## 🧪 Testing

### Run Unit Tests
```bash
cd user-service
mvn test

cd order-service
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Manual Testing with Postman
Import the Postman collection from: `postman/microservices-collection.json`

---

## 📈 Project Timeline

### Week 1: Microservices Development
- ✅ Day 1: Order Service CRUD Implementation
- ✅ Day 2: Security Architecture Design
- 🔄 Day 3: JWT Authentication Implementation
- ⏳ Day 4: Docker Containerization & Testing
- ⏳ Day 5: CI/CD Pipeline Setup

### Week 2: Infrastructure & Deployment
- ⏳ Day 6-7: Terraform Infrastructure Provisioning
- ⏳ Day 8-9: Kubernetes Deployment
- ⏳ Day 10: Monitoring & Logging Setup

---

## 🤝 Contributing

### Team Members
- **Florence** - Team Leader, Security Architecture, Order Service
- **Kagiso** - User Service Development
- **Ntando** - DevOps, CI/CD, Containerization

### Development Workflow
1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -m "Description"`
3. Push to branch: `git push origin feature/your-feature`
4. Create Pull Request for review
5. Merge after approval

---

## 📝 Documentation

- [Security Architecture](./docs/security/README.md) - JWT authentication & security
- [API Documentation](./docs/api/README.md) - Swagger/OpenAPI specs (coming soon)
- [Deployment Guide](./docs/deployment/README.md) - Kubernetes & Docker (coming soon)
- [Monitoring Guide](./docs/monitoring/README.md) - Prometheus & Grafana (coming soon)

---

## 🐛 Troubleshooting

### Common Issues

**Issue: "Port already in use"**
```bash
# Find process using port
lsof -i :8081
# Kill process
kill -9 <PID>
```

**Issue: "Cannot connect to PostgreSQL"**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres
# Restart PostgreSQL
docker restart postgres-db
```

**Issue: "Invalid JWT token"**
- Verify JWT secret is identical in both services
- Check token hasn't expired (15 minute lifespan)
- Ensure Authorization header format: `Bearer <token>`

For more troubleshooting, see [Security Architecture](./docs/security/02-security-architecture.md)

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

- Spring Boot Team for excellent framework
- JWT.io for authentication standards
- DevOps community for best practices

---

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/Florence122/devops-microservices-project/issues)
- **Email:** florence@example.com
- **Slack:** #devops-microservices

---

**Project Status:** 🟢 Active Development  
**Last Updated:** Day 2  
**Next Milestone:** Day 3 - JWT Implementation
