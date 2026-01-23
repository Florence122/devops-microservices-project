# DevOps Microservices Project

## 🎯 Project Overview
A comprehensive DevOps project demonstrating microservices architecture with CI/CD pipelines and Kubernetes orchestration.

### Team Members
- **Florence Chauke** - Team Leader (Orchestration & Security)
- **Tumelo Malebane** - CI/CD & Monitoring
- **Kagiso Masebe** - Backend & Security
- **Ntando Miya** - Containerization & Logging

## 📋 Tech Stack
- **Backend**: Java 17, Spring Boot 3.2
- **Database**: PostgreSQL 15
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes (K3s)
- **CI/CD**: Jenkins, GitHub Actions
- **Infrastructure**: Terraform
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)

## 🏗️ Project Structure
```
├── user-service/          # User management microservice
├── order-service/         # Order processing microservice
├── k8s/                   # Kubernetes manifests
├── terraform/             # Infrastructure as Code
├── ci-cd/                 # CI/CD pipeline configurations
├── monitoring/            # Monitoring stack configs
├── logging/               # Logging stack configs
└── docs/                  # Project documentation
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- kubectl
- Terraform

### Local Development
```bash
# Clone the repository
git clone https://github.com/YOUR-USERNAME/devops-microservices-project.git
cd devops-microservices-project

# Run with Docker Compose
docker-compose up -d

# Check services are running
docker-compose ps

# View logs
docker-compose logs -f

# Access services
# User Service: http://localhost:8081/actuator/health
# Order Service: http://localhost:8082/actuator/health
```

## 📖 Documentation
Detailed documentation can be found in the `/docs` folder:
- [Collaboration Guide](docs/COLLABORATION.md)
- [Contributing Guidelines](docs/CONTRIBUTING.md)

## 🤝 Contributing
1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -m "feat: add your feature"`
3. Push to branch: `git push origin feature/your-feature`
4. Create Pull Request

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
**Sprint Timeline**: 2 Weeks  
**Status**: In Progress 🚧
