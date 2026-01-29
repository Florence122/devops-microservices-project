# Docker Hub Setup - Tumelo Malebane

**Completed:** January 27, 2026  
**Task:** Day 4 - Docker Hub Repository Setup

## Repositories Created

### 1. User Service
- **URL:** https://hub.docker.com/r/tumelo62/user-service
- **Name:** tumelo62/user-service
- **Visibility:** Public
- **Purpose:** Stores Docker images for User Service microservice

### 2. Order Service
- **URL:** https://hub.docker.com/r/tumelo62/order-service
- **Name:** tumelo62/order-service
- **Visibility:** Public
- **Purpose:** Stores Docker images for Order Service microservice

## Image Tagging Strategy

All images will be tagged with:
- `latest` - Most recent build from main branch
- `build-{number}` - Jenkins build number
- `{commit-sha}` - Git commit hash (first 7 characters)

**Example:**
```
tumelo62/user-service:latest
tumelo62/user-service:build-42
tumelo62/user-service:a3f5c2d
```

## Authentication

Access token has been generated for Jenkins with Read/Write/Delete permissions.

**Credentials stored in Jenkins:**
- Credential ID: `docker-hub-credentials`
- Type: Username with password
- Username: tumelo62
- Password: [Access Token - configured in Jenkins on Day 5]

## Pull Images (For Team)

Anyone can pull these images:
```bash
# Pull user service
docker pull tumelo62/user-service:latest

# Pull order service
docker pull tumelo62/order-service:latest
```

## For Florence (Kubernetes Deployment)

When creating Kubernetes deployments in Week 2, use these image references:

**User Service:**
```yaml
spec:
  containers:
  - name: user-service
    image: tumelo62/user-service:latest
```

**Order Service:**
```yaml
spec:
  containers:
  - name: order-service
    image: tumelo62/order-service:latest
```

## Next Steps

- **Day 5:** Configure Jenkins with Docker Hub credentials
- **Day 5:** Test pushing images through CI/CD pipeline
- **Week 2:** Florence will reference these images in Kubernetes manifests

---

**Status:** ✅ Complete  
**Docker Hub Account:** tumelo62  
**Repositories:** 2 (user-service, order-service)  
**Access Token:** Generated and stored securely