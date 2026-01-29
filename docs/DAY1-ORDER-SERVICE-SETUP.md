# Day 1: Order Service Setup Documentation

**Date:** January 27, 2026  
**Developer:** Ntando Miya  
**Task:** Order Service Initialization & DB Setup  
**Time:** 3.5 hours  
**Status:**  Complete

---

## What Was Built

### Components Created
1. **Order Entity** - Database model with validations
2. **OrderRepository** - JPA repository with custom queries
3. **OrderService** - Business logic layer
4. **PostgreSQL Database** - Running in Docker container

### Tech Stack
- Spring Boot 3.4.2
- Java 21
- PostgreSQL 15
- Docker
- Maven

---

## Step-by-Step Process

### 1. Repository Setup (30 mins)
```bash
# Clone repository
git clone https://github.com/Florence122/devops-microservices-project.git
cd devops-microservices-project

# Create and switch to dev branch
git fetch origin
git checkout -b dev origin/dev
```

### 2. Initialize Spring Boot Project (15 mins)
- Used Spring Initializr (start.spring.io)
- Selected: Spring Boot 3.4.2, Java 21, Maven
- Added dependencies: Web, JPA, PostgreSQL, Validation, Lombok, Actuator
- Extracted to `order-service/` folder

### 3. Set Up PostgreSQL (10 mins)
```bash
docker run --name order-db \
  -e POSTGRES_DB=orderdb \
  -e POSTGRES_USER=orderuser \
  -e POSTGRES_PASSWORD=orderpass \
  -p 5433:5432 \
  -d postgres:15-alpine

# Verify running
docker ps
```

### 4. Configure Database Connection (10 mins)
**File:** `src/main/resources/application.properties`
```properties
server.port=8081
spring.application.name=order-service

spring.datasource.url=jdbc:postgresql://localhost:5433/orderdb
spring.datasource.username=orderuser
spring.datasource.password=orderpass
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.postgresql.dialect.PostgreSQLDialect

logging.level.org.springframework=INFO
logging.level.com.devops=DEBUG
```

### 5. Create Order Entity (30 mins)
**File:** `src/main/java/com/devops/orderservice/entity/Order.java`

Key features:
- ID, userId, productName, quantity, price, totalAmount
- OrderStatus enum (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- Auto-calculated totalAmount (price × quantity)
- Timestamps (createdAt, updatedAt)
- Validation annotations (@NotNull, @NotBlank, @Min, @DecimalMin)

### 6. Create OrderRepository (15 mins)
**File:** `src/main/java/com/devops/orderservice/repository/OrderRepository.java`

Methods:
- `findByUserId(Long userId)`
- `findByStatus(OrderStatus status)`
- `findByUserIdAndStatus(Long userId, OrderStatus status)`
- `findOrdersWithAmountGreaterThan(Double amount)`

### 7. Create OrderService (30 mins)
**File:** `src/main/java/com/devops/orderservice/service/OrderService.java`

Methods:
- `createOrder()`, `getOrderById()`, `getAllOrders()`
- `getOrdersByUserId()`, `getOrdersByStatus()`
- `updateOrder()`, `updateOrderStatus()`, `deleteOrder()`

### 8. Test & Commit (30 mins)
```bash
./mvnw clean compile
./mvnw spring-boot:run  # Verify it starts

git add order-service/
git commit -m "feat: Complete Order Service with Entity, Repository, and Service layer"
git push origin dev
```

---

## Challenges Faced & Solutions

### Challenge 1: Git Repository in Wrong Location
**Problem:** Git was tracking home directory instead of project folder  
**Solution:** Removed `~/.git`, re-initialized in project directory

### Challenge 2: JAVA_HOME Not Set
**Problem:** Maven couldn't find Java  
**Solution:**
```bash
export JAVA_HOME="$HOME/java/jdk-21.0.10"
export PATH="$JAVA_HOME/bin:$PATH"
# Added to ~/.bashrc for persistence
```

### Challenge 3: Missing Validation Dependency
**Problem:** Compilation errors - validation annotations not found  
**Solution:** Added to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Challenge 4: Empty application.properties
**Problem:** Application failed to start - no database config  
**Solution:** Added complete database configuration (see step 4)

### Challenge 5: Merge Conflicts on Dev Branch
**Problem:** Conflicts when merging local work to dev  
**Solution:** Used `git checkout --ours` to keep local version

---

## For User Service Developer (Kagiso)

### Prerequisites
1. **Java Setup:**
   ```bash
   export JAVA_HOME="$HOME/java/jdk-21.0.10"
   export PATH="$JAVA_HOME/bin:$PATH"
   echo 'export JAVA_HOME="$HOME/java/jdk-21.0.10"' >> ~/.bashrc
   echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
   ```

2. **Clone & Setup:**
   ```bash
   cd ~/workspace/devops-microservices-project
   git checkout dev
   git pull origin dev
   ```

3. **Start User Database:**
   ```bash
   docker run --name user-db \
     -e POSTGRES_DB=userdb \
     -e POSTGRES_USER=useruser \
     -e POSTGRES_PASSWORD=userpass \
     -p 5432:5432 \
     -d postgres:15-alpine
   ```

### User Service Configuration
Use **port 8080** (Order Service uses 8081)

**application.properties:**
```properties
server.port=8080
spring.application.name=user-service
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=useruser
spring.datasource.password=userpass
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
```

### Required Dependencies (pom.xml)
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- postgresql
- lombok
- spring-boot-starter-actuator

### File Structure to Create
```
user-service/
├── src/main/java/com/devops/userservice/
│   ├── entity/
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   │   └── UserService.java
│   └── UserServiceApplication.java
└── src/main/resources/
    └── application.properties
```

### User Entity Fields (Suggested)
- id (Long, auto-generated)
- username (String, unique)
- email (String, unique)
- password (String, hashed)
- firstName, lastName (String)
- createdAt, updatedAt (LocalDateTime)

### Testing Commands
```bash
./mvnw clean compile    # Compile
./mvnw spring-boot:run  # Run application
# Stop with Ctrl+C
```

### Git Workflow
```bash
git add user-service/
git commit -m "feat: your commit message"
git pull origin dev
git push origin dev
```

---

## Key Learnings

1. **Always set JAVA_HOME permanently** in `.bashrc`
2. **Check application.properties first** when database connection fails
3. **Use correct branch** - work on `dev`, not `main`
4. **Verify Docker containers** are running before starting app
5. **Add all required dependencies** before compiling
6. **Use different ports** for each service (8080, 8081, etc.)

---

## Next Steps (Day 2)

For Order Service:
- [ ] Create REST Controllers (OrderController)
- [ ] Add Swagger documentation
- [ ] Implement JWT security
- [ ] Write unit tests

---

## Useful Commands Reference

```bash
# Docker
docker ps                    # List running containers
docker start order-db        # Start container
docker restart order-db      # Restart container
docker logs order-db         # View logs

# Maven
./mvnw clean compile         # Compile
./mvnw spring-boot:run       # Run application
./mvnw test                  # Run tests

# Git
git status                   # Check status
git pull origin dev          # Get latest
git add .                    # Stage changes
git commit -m "message"      # Commit
git push origin dev          # Push changes

# Check ports
netstat -ano | findstr :8080
netstat -ano | findstr :8081
```

---

**Documentation created by:** Ntando Miya  
**For questions:** Check with team or refer to Spring Boot docs