# CI/CD Architecture Plan
**Project:** DevOps Microservices Project  
**GitHub Repo:** https://github.com/Florence122/devops-microservices-project  
**Prepared by:** Tumelo Malebane  
**Date:** January 27, 2026  
**Duration:** 2 Weeks

---

## 1. Overview

### What is this pipeline doing?
This CI/CD pipeline automates the process of testing, building, and deploying our two microservices (User Service and Order Service) from code commit to Kubernetes deployment.

### Pipeline Goals
- Automatically test code on every push
- Build Docker images when tests pass
- Push images to Docker Hub
- Deploy to Kubernetes automatically (Week 2)
- Provide fast feedback to developers

---

## 2. Current Repository Structure

Based on Florence's setup:
```
devops-microservices-project/
├── user-service/          # User management microservice (Kagiso)
├── order-service/         # Order processing microservice (Ntando)
├── k8s/                   # Kubernetes manifests (Florence - Week 2)
├── terraform/             # Infrastructure as Code (Florence - Week 2)
├── ci-cd/                 # CI/CD configs (Tumelo - YOU!)
├── monitoring/            # Prometheus & Grafana (Tumelo - Week 2)
├── logging/               # ELK Stack (Ntando - Week 2)
├── docs/                  # Documentation
├── docker-compose.yml     # Local development (Ntando)
└── README.md
```

### What YOU'LL Create in `/ci-cd/` folder:
```
ci-cd/
├── jenkins/
│   ├── Jenkinsfile.user-service
│   ├── Jenkinsfile.order-service
│   └── README.md
├── github-actions/
│   └── workflows/
│       ├── user-service-tests.yml
│       └── order-service-tests.yml
└── README.md  # Your pipeline documentation
```

---

## 3. Tools Selection

### GitHub Actions
**Purpose:** Automated Testing  
**Why:** 
- Already integrated with GitHub
- Free for public repositories
- Easy to set up
- Runs tests in isolated environments

**What it will do:**
- Run unit tests on every push
- Run tests on pull requests
- Generate test reports
- Block merges if tests fail

### Jenkins
**Purpose:** Build, Containerization & Deployment  
**Why:**
- Industry standard for CI/CD
- Powerful plugin ecosystem
- Better for Docker and Kubernetes integration
- Can handle complex deployment workflows

**What it will do:**
- Build the Java applications (mvn clean package)
- Create Docker images
- Push images to Docker Hub
- Deploy to Kubernetes (Week 2)
- Send notifications on build status

---

## 4. Pipeline Flow

```
┌─────────────────────────────────────────────────────────────┐
│  DEVELOPER WORKFLOW                                         │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
    Developer pushes to Florence's repo
    github.com/Florence122/devops-microservices-project
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 1: TESTING (GitHub Actions)                          │
│  Location: .github/workflows/                               │
│  - Checkout code from Florence's repo                       │
│  - Set up Java 17                                           │
│  - cd user-service && mvn test                              │
│  - cd order-service && mvn test                             │
│  - Generate test reports                                    │
└─────────────────────────────────────────────────────────────┘
                         │
                    Tests Pass?
                    │         │
                   Yes       No → Notify developer, stop here
                    │
                    ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 2: BUILD (Jenkins)                                   │
│  Location: ci-cd/jenkins/Jenkinsfile.*                      │
│  - Checkout code from Florence's repo                       │
│  - cd user-service && mvn clean package                     │
│  - cd order-service && mvn clean package                    │
│  - Create JAR files                                         │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 3: CONTAINERIZATION (Jenkins)                        │
│  Uses: Ntando's Dockerfiles in each service folder         │
│  - Build Docker image for user-service                      │
│  - Build Docker image for order-service                     │
│  - Tag images with build number and 'latest'                │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 4: PUSH TO REGISTRY (Jenkins)                        │
│  - Login to Docker Hub (your account)                       │
│  - Push user-service image with tags                        │
│  - Push order-service image with tags                       │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│  STAGE 5: DEPLOY (Jenkins - Week 2)                         │
│  Uses: Florence's k8s/ manifests                            │
│  - Update Kubernetes deployment                             │
│  - kubectl apply -f k8s/                                    │
│  - Rolling update with new image                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Branch Strategy

### Branches We'll Use
- **main**: Production-ready code
  - Triggers: Full pipeline (test → build → push → deploy)
  - Protected by Florence (Team Leader)
  
- **develop**: Integration branch for features
  - Triggers: Test + Build (no deployment)
  - Team merges features here first
  
- **feature/***: Individual feature branches
  - Triggers: Tests only
  - Naming: feature/tumelo-jenkins-setup, feature/kagiso-user-api, etc.

### Workflow for Team
1. Kagiso creates: `feature/kagiso-user-service-jwt`
2. Pushes code → GitHub Actions runs tests automatically
3. Creates Pull Request to `develop`
4. Florence reviews and approves
5. Merge to `develop` → Jenkins builds and pushes to Docker Hub
6. When ready: Florence merges `develop` to `main`
7. `main` merge triggers full deployment to Kubernetes

---

## 6. Docker Image Naming & Tagging

### Docker Hub Account
**YOUR Docker Hub username**: tumelo62

### Image Names
```
tumelo62/user-service
tumelo62/order-service
```

### Tags We'll Use
- **latest**: Most recent successful build from `main` branch
- **build-{number}**: Jenkins build number (e.g., `build-42`)
- **{commit-sha}**: Git commit hash (first 7 chars, e.g., `a3f5c2d`)

### Example
With your Docker Hub username `tumelo62`:
```
tumelo62/user-service:latest
tumelo62/user-service:build-42
tumelo62/user-service:a3f5c2d

tumelo62/order-service:latest
tumelo62/order-service:build-42
tumelo62/order-service:a3f5c2d
```

---

## 7. Environment Variables & Secrets Needed

### Jenkins Credentials (YOU need to set these up on Day 5)
| Credential ID | Type | Purpose | How to Get |
|--------------|------|---------|------------|
| `docker-hub-credentials` | Username + Password | Push to Docker Hub | Create Docker Hub account + generate token |
| `github-token` | Secret Text | Clone Florence's repo | Generate GitHub Personal Access Token |
| `kubeconfig` | Secret File | Deploy to K8s (Week 2) | Florence will provide from Terraform VM |

### GitHub Secrets (Florence needs to add these)
| Secret Name | Purpose | Value |
|-------------|---------|-------|
| `DOCKER_USERNAME` | Docker Hub login | Your Docker Hub username |
| `DOCKER_TOKEN` | Docker Hub authentication | Your Docker Hub access token |

### Jenkins Environment Variables
```groovy
environment {
    DOCKER_USERNAME = 'tumelo62'
    USER_SERVICE_IMAGE = "${DOCKER_USERNAME}/user-service"
    ORDER_SERVICE_IMAGE = "${DOCKER_USERNAME}/order-service"
    GIT_REPO = 'https://github.com/Florence122/devops-microservices-project.git'
}
```

---

## 8. GitHub Actions Configuration

### Files YOU'LL Create

**File 1:** `.github/workflows/user-service-tests.yml`
```yaml
name: User Service - Tests

on:
  push:
    paths:
      - 'user-service/**'
      - '.github/workflows/user-service-tests.yml'
  pull_request:
    paths:
      - 'user-service/**'

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Run tests
      working-directory: ./user-service
      run: mvn clean test
    
    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: user-service-test-results
        path: user-service/target/surefire-reports/
```

**File 2:** `.github/workflows/order-service-tests.yml`
(Same as above but change `user-service` to `order-service`)

### Where These Files Go
Florence's repo already has folders, but you need to create:
```
.github/
└── workflows/
    ├── user-service-tests.yml  ← YOU CREATE THIS
    └── order-service-tests.yml ← YOU CREATE THIS
```

---

## 9. Jenkins Pipeline Configuration

### Jenkinsfile for User Service

**File Location:** `ci-cd/jenkins/Jenkinsfile.user-service`

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_USERNAME = 'tumelo62'
        IMAGE_NAME = "${DOCKER_USERNAME}/user-service"
        GIT_REPO = 'https://github.com/Florence122/devops-microservices-project.git'
        BUILD_TAG = "${BUILD_NUMBER}"
        GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: "${GIT_REPO}",
                    credentialsId: 'github-token'
            }
        }
        
        stage('Build Application') {
            steps {
                dir('user-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                dir('user-service') {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit 'user-service/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('user-service') {
                    script {
                        dockerImage = docker.build("${IMAGE_NAME}:${BUILD_TAG}")
                        docker.build("${IMAGE_NAME}:latest")
                        docker.build("${IMAGE_NAME}:${GIT_COMMIT_SHORT}")
                    }
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('', 'docker-hub-credentials') {
                        sh "docker push ${IMAGE_NAME}:${BUILD_TAG}"
                        sh "docker push ${IMAGE_NAME}:latest"
                        sh "docker push ${IMAGE_NAME}:${GIT_COMMIT_SHORT}"
                    }
                }
            }
        }
        
        stage('Clean Up') {
            steps {
                sh "docker rmi ${IMAGE_NAME}:${BUILD_TAG} || true"
                sh "docker rmi ${IMAGE_NAME}:latest || true"
                sh "docker rmi ${IMAGE_NAME}:${GIT_COMMIT_SHORT} || true"
            }
        }
    }
    
    post {
        success {
            echo "✅ Pipeline SUCCESS! Images pushed to Docker Hub"
            echo "User Service: ${IMAGE_NAME}:${BUILD_TAG}"
        }
        failure {
            echo "❌ Pipeline FAILED! Check logs above"
        }
        always {
            cleanWs()
        }
    }
}
```

### Similar Jenkinsfile for Order Service
Create: `ci-cd/jenkins/Jenkinsfile.order-service`
(Copy above and change `user-service` to `order-service`)

---

## 10. Pipeline Triggers

### GitHub Actions (Testing)
**Triggers:**
- Any push to any branch (if changes in service folders)
- Pull requests to `main` or `develop`
- Manual trigger via GitHub UI (workflow_dispatch)

**Purpose:** Fast feedback on code quality

### Jenkins (Build & Deploy)
**Triggers (Day 5 setup):**
- GitHub webhook after successful tests
- Manual build via Jenkins UI
- Scheduled: Daily builds at midnight (optional)

**Purpose:** Create deployable artifacts

---

## 11. Week 1 Implementation Timeline

### Day 1 (TODAY - 2 hours)
- [x] Review Florence's repository structure
- [ ] Fill in YOUR Docker Hub username in this document
- [ ] Create `/ci-cd/` folder structure plan
- [ ] Share this plan with team (Florence, Kagiso, Ntando)
- [ ] Get approval from Florence

### Day 4 (0.5 hours)
**Dependencies:** Need Ntando's Dockerfiles completed
- [ ] Create Docker Hub account (if don't have one)
- [ ] Create repositories: user-service, order-service
- [ ] Generate Docker Hub access token
- [ ] Document credentials for Day 5

### Day 5 (10 hours total)
**Dependencies:** Need Kagiso's & Ntando's services completed

**Morning (4 hours):**
- [ ] Install Jenkins on your machine/VM
- [ ] Install required Jenkins plugins
- [ ] Configure Jenkins credentials
- [ ] Test Jenkins can access Florence's GitHub repo

**Afternoon (4 hours):**
- [ ] Create GitHub Actions workflow files
- [ ] Test workflows by making small commits
- [ ] Create Jenkinsfiles for both services
- [ ] Create Jenkins pipeline jobs
- [ ] Run test builds

**Evening (2 hours with Florence):**
- [ ] End-to-end pipeline test
- [ ] Verify images in Docker Hub
- [ ] Document any issues
- [ ] Create demo for team checkpoint

---

## 12. Dependencies & Communication

### What YOU Need From Team

| From | What | When Needed | Why |
|------|------|-------------|-----|
| **Florence** | Repo admin access | Day 1 | To create webhook and add secrets |
| **Florence** | Approval of this plan | Day 1 | Ensure alignment with her setup |
| **Kagiso** | User Service code complete | Day 4 | To test builds |
| **Ntando** | Dockerfiles ready | Day 4 | To build images |
| **Ntando** | docker-compose.yml working | Day 4 | To verify local builds |

### What YOU'LL Provide to Team

| To | What | When | Why |
|----|------|------|-----|
| **All** | This architecture doc | Day 1 EOD | Team alignment |
| **Florence** | List of GitHub secrets needed | Day 4 | She'll add them to repo |
| **All** | CI/CD documentation | Day 5 | How to use the pipeline |
| **All** | Docker Hub image URLs | Day 5 | For K8s deployments in Week 2 |

---

## 13. Questions for Team Discussion (Day 1)

Ask these in your team meeting or Slack/WhatsApp:

1. **To Florence:** 
   - Can you give me webhook permissions for the repo?
   - Should I create a separate GitHub organization or use personal accounts for Docker Hub?

2. **To Kagiso & Ntando:**
   - Will your services have tests ready by Day 4?
   - What Maven command do you use to run tests? (`mvn test` or something else?)

3. **To Everyone:**
   - Should we set up Slack/Discord for Jenkins notifications, or just use email?
   - Who should get notified when builds fail?

---

## 14. Success Criteria

### This Pipeline is Successful When:
- ✅ Push to any branch → GitHub Actions runs tests within 2 minutes
- ✅ Tests pass → Green checkmark shows on GitHub
- ✅ Merge to `develop` → Jenkins builds and pushes to Docker Hub
- ✅ Images visible in Docker Hub with correct tags
- ✅ Team can trigger builds manually via Jenkins UI
- ✅ Failed builds send notifications
- ✅ You can pull and run images locally:
  ```bash
  docker pull <your-username>/user-service:latest
  docker run -p 8081:8081 <your-username>/user-service:latest
  ```

---

## 15. Day 5 Testing Checklist

Work through this with Florence during your 2-hour end-to-end test:

```
GitHub Actions Testing:
[ ] Create test branch: git checkout -b test/tumelo-pipeline
[ ] Make small change in user-service/README.md
[ ] Push: git push origin test/tumelo-pipeline
[ ] Check GitHub Actions tab - workflow should run
[ ] Verify tests pass and show green checkmark

Jenkins Testing:
[ ] Open Jenkins UI: http://localhost:8080
[ ] Click "User-Service-Pipeline"
[ ] Click "Build Now"
[ ] Watch console output
[ ] Verify all stages complete successfully
[ ] Repeat for "Order-Service-Pipeline"

Docker Hub Verification:
[ ] Go to hub.docker.com/u/tumelo62
[ ] Check user-service repository exists
[ ] Check order-service repository exists
[ ] Verify tags: latest, build-1, commit-sha

Local Testing:
[ ] docker pull tumelo62/user-service:latest
[ ] docker run -p 8081:8081 tumelo62/user-service:latest
[ ] curl http://localhost:8081/actuator/health
[ ] Should return: {"status":"UP"}

Documentation:
[ ] Take screenshots of successful pipeline runs
[ ] Document any issues encountered
[ ] Update README in ci-cd/ folder
```

---

## 16. Potential Issues & Solutions

### Issue 1: Maven Tests Fail
**Symptom:** GitHub Actions shows red X
**Solution:** 
- Check if Kagiso/Ntando wrote tests
- Verify Java version (should be 17)
- Check database connection in tests

### Issue 2: Jenkins Can't Access GitHub
**Symptom:** "Permission denied" errors
**Solution:**
- Verify github-token credential is correct
- Check token has repo access permissions
- Ask Florence if repo is private (need different approach)

### Issue 3: Docker Push Fails
**Symptom:** "unauthorized" or "access denied"
**Solution:**
- Verify Docker Hub credentials in Jenkins
- Check if repositories exist in Docker Hub
- Try: `docker login` manually first

### Issue 4: Jenkins Not Starting
**Symptom:** Can't access localhost:8080
**Solution:**
- Check if port 8080 is already used
- Verify Jenkins service is running: `sudo systemctl status jenkins`
- Check logs: `sudo journalctl -u jenkins -f`

---

## 17. Stretch Goals (If Time Permits)

After completing basic pipeline on Day 5:
- [ ] Add code coverage reporting (JaCoCo)
- [ ] Set up Slack notifications
- [ ] Add build status badges to README
- [ ] Create pipeline visualization diagram
- [ ] Add automatic rollback on deployment failure

---

## 18. Handoff to Week 2

By end of Day 5, YOU provide:
1. **Docker Hub URLs** → To Florence for Kubernetes deployments
2. **Jenkins pipeline docs** → To team for understanding the flow
3. **Monitoring hooks** → Integration points for Prometheus (Week 2)

Florence will use your images in Week 2:
```yaml
# Florence's k8s/user-service-deployment.yaml will reference:
spec:
  containers:
  - name: user-service
    image: tumelo62/user-service:latest
```

---

## NEXT STEPS FOR TODAY (Day 1)

1. **Fill in your Docker Hub username** throughout this document
2. **Save this as:** `ci-cd/README.md` in Florence's repo
3. **Send to team:** "Please review my CI/CD architecture plan"
4. **Create a quick diagram** (optional but impressive):
   - Use draw.io or Lucidchart
   - Show: GitHub → GitHub Actions → Jenkins → Docker Hub
5. **Wait for Florence's approval** before Day 5 implementation

---

**Document Status:** DRAFT - Awaiting Team Review  
**Next Action:** Share with Florence, Kagiso, Ntando  
**Implementation Date:** Day 5 (January 31, 2026)
