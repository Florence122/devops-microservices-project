# Security Architecture Document
## JWT Authentication for Microservices

**Author:** Florence (Team Leader & Security Architect)  
**Date:** Day 2  
**Version:** 1.0  
**Target Audience:** Development Team (Kagiso, Ntando, Florence)

---

## Table of Contents
1. [Authentication Flow](#1-authentication-flow)
2. [JWT Configuration](#2-jwt-configuration)
3. [Endpoint Protection](#3-endpoint-protection)
4. [Service-to-Service Communication](#4-service-to-service-communication)
5. [Implementation Checklist for Kagiso](#5-implementation-checklist-for-kagiso)

---

## 1. Authentication Flow

### Overview
Our microservices will use JWT (JSON Web Token) for stateless authentication. Users authenticate once with the User Service and receive a token that grants access to all protected endpoints across both services.

### Complete Authentication Flow

```
┌──────────┐                 ┌──────────────┐                 ┌──────────────┐
│  Client  │                 │ User Service │                 │Order Service │
│          │                 │  (Port 8081) │                 │ (Port 8082)  │
└─────┬────┘                 └──────┬───────┘                 └──────┬───────┘
      │                             │                                │
      │ 1. POST /api/auth/register  │                                │
      │    {username, email, pwd}   │                                │
      ├────────────────────────────>│                                │
      │                             │                                │
      │                        ┌────┴────┐                           │
      │                        │ Validate│                           │
      │                        │  & Save │                           │
      │                        │   User  │                           │
      │                        └────┬────┘                           │
      │                             │                                │
      │ 2. 201 Created              │                                │
      │    "User registered"        │                                │
      │<────────────────────────────┤                                │
      │                             │                                │
      │ 3. POST /api/auth/login     │                                │
      │    {username, password}     │                                │
      ├────────────────────────────>│                                │
      │                             │                                │
      │                        ┌────┴────┐                           │
      │                        │Validate │                           │
      │                        │Password │                           │
      │                        │Generate │                           │
      │                        │  JWT    │                           │
      │                        └────┬────┘                           │
      │                             │                                │
      │ 4. 200 OK                   │                                │
      │    {"token": "eyJhbGc..."}  │                                │
      │<────────────────────────────┤                                │
      │                             │                                │
      │ [Client stores token]       │                                │
      │                             │                                │
      │ 5. POST /api/orders                                          │
      │    Header: Authorization: Bearer eyJhbGc...                  │
      │    Body: {productName, quantity, price}                      │
      ├──────────────────────────────────────────────────────────────>│
      │                             │                                │
      │                             │                           ┌────┴────┐
      │                             │                           │ Extract │
      │                             │                           │  Token  │
      │                             │                           │Validate │
      │                             │                           │Username │
      │                             │                           └────┬────┘
      │                             │                                │
      │ 6. 201 Created                                               │
      │    {orderId, productName, username, ...}                     │
      │<─────────────────────────────────────────────────────────────┤
      │                             │                                │
```

### Detailed Step-by-Step Process

#### Phase 1: User Registration
**Step 1:** Client sends registration data to User Service
```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Step 2:** User Service processes registration
- Validates username is unique
- Validates email is unique
- Hashes password using BCrypt
- Stores user in PostgreSQL database

**Step 3:** User Service responds
```json
{
  "message": "User registered successfully"
}
```

#### Phase 2: User Login & Token Generation
**Step 4:** Client sends login credentials
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**Step 5:** User Service validates and generates token
- Retrieves user from database
- Verifies password using BCrypt
- Generates JWT token with username
- Sets expiration to 15 minutes from now

**Step 6:** User Service returns JWT
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTcwNjI4MDAwMCwiZXhwIjoxNzA2MjgwOTAwfQ.signature_here",
  "message": "Login successful"
}
```

#### Phase 3: Accessing Protected Resources
**Step 7:** Client includes JWT in request header
```http
POST http://localhost:8082/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productName": "MacBook Pro",
  "quantity": 1,
  "price": 2499.99
}
```

**Step 8:** Order Service validates token
- Extracts token from Authorization header
- Validates signature using shared secret
- Checks token hasn't expired
- Extracts username from token

**Step 9:** Order Service processes request
```json
{
  "orderId": 1,
  "productName": "MacBook Pro",
  "quantity": 1,
  "price": 2499.99,
  "username": "john_doe",
  "createdAt": "2024-01-26T10:30:00"
}
```

### Error Scenarios

#### Scenario 1: Invalid Credentials
```http
POST /api/auth/login
{"username": "john", "password": "wrong"}

Response: 401 Unauthorized
{"error": "Invalid username or password"}
```

#### Scenario 2: Missing Token
```http
POST /api/orders
(No Authorization header)

Response: 401 Unauthorized
{"error": "Full authentication is required"}
```

#### Scenario 3: Expired Token
```http
POST /api/orders
Authorization: Bearer <expired_token>

Response: 401 Unauthorized
{"error": "Token has expired"}
```

#### Scenario 4: Invalid Token
```http
POST /api/orders
Authorization: Bearer invalid_token_here

Response: 401 Unauthorized
{"error": "Invalid JWT token"}
```

---

## 2. JWT Configuration

### JWT Token Structure

Our JWT tokens consist of three parts separated by dots:

```
header.payload.signature
```

#### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
- **alg:** Algorithm used for signing (HMAC SHA-256)
- **typ:** Token type (JWT)

#### Payload (Claims)
```json
{
  "sub": "john_doe",
  "iat": 1706280000,
  "exp": 1706280900
}
```
- **sub:** Subject (username of authenticated user)
- **iat:** Issued At (timestamp when token was created)
- **exp:** Expiration (timestamp when token expires)

#### Signature
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

### Configuration Values

#### Application Properties
**File:** `application.properties` (both services)

```properties
# JWT Configuration
jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
jwt.expiration=900000

# 900000 milliseconds = 15 minutes
# Secret must be minimum 256 bits (32 characters)
```

#### JWT Library Dependencies
**File:** `pom.xml` (both services)

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Token Support -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Algorithm Choice: HS256

**Why HS256 (HMAC with SHA-256)?**
- ✅ Symmetric algorithm (same key for signing and verification)
- ✅ Fast performance
- ✅ Perfect for microservices with shared secrets
- ✅ Industry standard
- ✅ Well-supported by libraries

**Alternative: RS256 (Future consideration)**
- Asymmetric algorithm (public/private key pair)
- Better for scenarios where services can't share secrets
- Slightly slower performance
- More complex setup

### Token Expiration Strategy

| Token Type | Duration | Use Case |
|------------|----------|----------|
| Access Token | 15 minutes | API requests |
| Refresh Token (Future) | 7 days | Renew access tokens |

**Why 15 minutes?**
- Balances security and user experience
- Limits damage if token is stolen
- Short enough to minimize exposure
- Long enough to avoid frequent re-authentication

### Production Configuration (Kubernetes)

#### Step 1: Create Kubernetes Secret
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
  namespace: default
type: Opaque
stringData:
  jwt-secret: mySuperSecretKeyForJWTTokenGeneration12345678901234567890
  jwt-expiration: "900000"
```

#### Step 2: Reference in Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  template:
    spec:
      containers:
      - name: user-service
        image: user-service:latest
        env:
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: jwt-secret
        - name: JWT_EXPIRATION
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: jwt-expiration
```

#### Step 3: Update Application Properties
```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

---

## 3. Endpoint Protection

### Public Endpoints (No Authentication Required)

#### User Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | User login |
| GET | `/actuator/health` | Health check |

**Why these are public:**
- Registration: New users can't have tokens yet
- Login: Generates the initial token
- Health check: Kubernetes needs to monitor service

#### Order Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |

**Note:** Order Service has NO public business endpoints. All order operations require authentication.

### Protected Endpoints (JWT Required)

#### User Service
| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/users` | List all users | Authenticated |
| GET | `/api/users/{id}` | Get user by ID | Authenticated |
| PUT | `/api/users/{id}` | Update user | Authenticated |
| DELETE | `/api/users/{id}` | Delete user | Authenticated |

#### Order Service
| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| POST | `/api/orders` | Create order | Authenticated |
| GET | `/api/orders` | List all orders | Authenticated |
| GET | `/api/orders/{id}` | Get order by ID | Authenticated |
| PUT | `/api/orders/{id}` | Update order | Authenticated |
| DELETE | `/api/orders/{id}` | Delete order | Authenticated |

### Authorization Header Format

All protected endpoints require:
```
Authorization: Bearer <jwt_token>
```

**Example:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNzA2MjgwMDAwLCJleHAiOjE3MDYyODA5MDB9.signature
```

### Security Configuration

#### User Service Security Config
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()   // Public
                .requestMatchers("/actuator/**").permitAll()   // Health
                .anyRequest().authenticated()                  // Protected
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

#### Order Service Security Config
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()   // Health only
                .anyRequest().authenticated()                  // All else protected
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 4. Service-to-Service Communication

### The Challenge

```
Question: How does Order Service validate JWT tokens 
          generated by User Service?

Answer: Both services use the SAME secret key to 
        sign and validate tokens!
```

### Shared Secret Architecture

```
┌───────────────────────────┐       ┌───────────────────────────┐
│     User Service          │       │     Order Service         │
│     (Port 8081)           │       │     (Port 8082)           │
│                           │       │                           │
│  application.properties:  │       │  application.properties:  │
│  jwt.secret=ABC123        │       │  jwt.secret=ABC123        │
│           ↓               │       │           ↓               │
│  JwtUtil.java             │       │  JwtUtil.java             │
│  - generateToken()        │       │  - validateToken()        │
│  - Uses secret ABC123     │       │  - Uses secret ABC123     │
│           ↓               │       │           ↑               │
│  AuthController.java      │       │  OrderController.java     │
│  POST /api/auth/login     │       │  POST /api/orders         │
│  Returns JWT ────────────────────> Validates JWT             │
│                           │       │                           │
└───────────────────────────┘       └───────────────────────────┘
```

### Step-by-Step Token Validation

#### Step 1: Token Generation (User Service)
```java
// User logs in
String username = "john_doe";
String secret = "ABC123";

// Generate token
String token = Jwts.builder()
    .setSubject(username)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 900000))
    .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
    .compact();

// Return to client
return new AuthResponse(token);
```

#### Step 2: Token Usage (Client)
```http
POST http://localhost:8082/api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{"productName": "Laptop", "quantity": 1}
```

#### Step 3: Token Validation (Order Service)
```java
// Extract token
String header = request.getHeader("Authorization");
String token = header.substring(7); // Remove "Bearer "

// Validate using SAME secret
String secret = "ABC123"; // MUST match User Service!

try {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
    
    String username = claims.getSubject(); // "john_doe"
    
    // Token is valid! Process request...
    Order order = new Order();
    order.setUsername(username); // From token
    order.setProductName(requestBody.getProductName());
    // ... save order
    
} catch (JwtException e) {
    // Token invalid! Return 401
    throw new UnauthorizedException("Invalid token");
}
```

### Critical Requirements for Service Communication

#### ✅ MUST Have (Non-Negotiable)

1. **Identical Secret Keys**
   ```properties
   # User Service
   jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
   
   # Order Service  
   jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
   ```

2. **Same JWT Library**
   - Both services: jjwt 0.11.5
   - Both services: Same version

3. **Same Algorithm**
   - Both services: HS256

4. **Same Token Structure**
   - Both services: Use `sub` claim for username
   - Both services: Include `iat` and `exp` claims

5. **Identical JwtUtil Implementation**
   - Copy the EXACT same class
   - Don't modify validation logic

#### ❌ Will Fail If:

1. **Different Secrets**
   ```
   User Service:  jwt.secret=ABC123
   Order Service: jwt.secret=XYZ789
   Result: Signature validation fails ❌
   ```

2. **Different Algorithms**
   ```
   User Service:  HS256
   Order Service: RS256
   Result: Token parsing fails ❌
   ```

3. **Modified Token Structure**
   ```
   User Service:  Claims: {sub, iat, exp}
   Order Service: Expects: {username, created, expires}
   Result: Missing claims error ❌
   ```

### Testing Service Communication

#### Test 1: End-to-End Flow
```bash
# Step 1: Register user
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@mail.com","password":"pass123"}'

# Step 2: Login (User Service)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123"}'

# Response: {"token":"eyJhbGc..."}
# COPY THIS TOKEN!

# Step 3: Create order (Order Service with token)
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{"productName":"Laptop","quantity":2,"price":1200}'

# Expected: Order created with username="test" ✅
```

#### Test 2: Verify Shared Secret
```bash
# Create order without token
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productName":"Laptop","quantity":2}'

# Expected: 401 Unauthorized ❌
```

### No Service-to-Service HTTP Calls Required!

**Important:** Order Service does NOT need to call User Service to validate tokens. The shared secret allows independent validation.

```
❌ Wrong Approach:
Order Service → HTTP Call → User Service: "Is token XYZ valid?"
                            ← Response: "Yes"

✅ Correct Approach:
Order Service → Validate locally using shared secret
              → Token is valid!
```

**Benefits:**
- Faster (no network call)
- More reliable (no dependency on User Service availability)
- Scalable (no bottleneck at User Service)
- Stateless (true microservices architecture)

---

## 5. Implementation Checklist for Kagiso

### Day 3 Morning: User Service JWT Implementation

#### Phase 1: Setup (30 minutes)

**Task 1.1: Add Dependencies**
- [ ] Open `user-service/pom.xml`
- [ ] Add Spring Security dependency
- [ ] Add jjwt dependencies (0.11.5)
- [ ] Run `mvn clean install`
- [ ] Verify no compilation errors

**Task 1.2: Configure JWT Properties**
- [ ] Open `user-service/src/main/resources/application.properties`
- [ ] Add `jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890`
- [ ] Add `jwt.expiration=900000`
- [ ] Save file

#### Phase 2: Create Security Components (90 minutes)

**Task 2.1: Create JwtUtil Class**
- [ ] Create package: `com.userservice.security`
- [ ] Create file: `JwtUtil.java`
- [ ] Implement `generateToken(String username)` method
- [ ] Implement `validateToken(String token)` method
- [ ] Implement `getUsernameFromToken(String token)` method
- [ ] Implement `isTokenExpired(String token)` method
- [ ] Add `@Component` annotation
- [ ] Test compilation

**Task 2.2: Create JWT Authentication Filter**
- [ ] Create file: `JwtAuthenticationFilter.java`
- [ ] Extend `OncePerRequestFilter`
- [ ] Extract JWT from Authorization header
- [ ] Validate token using JwtUtil
- [ ] Set authentication in SecurityContext if valid
- [ ] Add `@Component` annotation

**Task 2.3: Create Security Configuration**
- [ ] Create file: `SecurityConfig.java`
- [ ] Add `@Configuration` and `@EnableWebSecurity` annotations
- [ ] Configure public endpoints: `/api/auth/**`, `/actuator/**`
- [ ] Configure protected endpoints: all others
- [ ] Disable CSRF (stateless API)
- [ ] Set session management to STATELESS
- [ ] Register JWT filter
- [ ] Create BCryptPasswordEncoder bean
- [ ] Create AuthenticationManager bean

#### Phase 3: Create Authentication Endpoints (60 minutes)

**Task 3.1: Create DTOs**
- [ ] Create `dto` package
- [ ] Create `LoginRequest.java` (username, password)
- [ ] Create `RegisterRequest.java` (username, email, password)
- [ ] Create `AuthResponse.java` (token, message)

**Task 3.2: Create AuthController**
- [ ] Create `AuthController.java`
- [ ] Add `@RestController` and `@RequestMapping("/api/auth")`
- [ ] Implement POST `/register` endpoint
  - [ ] Validate username doesn't exist
  - [ ] Validate email doesn't exist
  - [ ] Hash password with BCrypt
  - [ ] Save user to database
  - [ ] Return success message
- [ ] Implement POST `/login` endpoint
  - [ ] Find user by username
  - [ ] Verify password with BCrypt
  - [ ] Generate JWT token
  - [ ] Return token in response

**Task 3.3: Update User Repository**
- [ ] Open `UserRepository.java`
- [ ] Add `Optional<User> findByUsername(String username)`
- [ ] Add `Optional<User> findByEmail(String email)`

#### Phase 4: Testing (60 minutes)

**Task 4.1: Test Registration**
- [ ] Start User Service
- [ ] Use Postman to POST `/api/auth/register`
- [ ] Verify user created in database
- [ ] Verify password is hashed
- [ ] Test duplicate username rejection
- [ ] Test duplicate email rejection

**Task 4.2: Test Login**
- [ ] Use Postman to POST `/api/auth/login`
- [ ] Verify JWT token received
- [ ] Verify token is not empty
- [ ] Test invalid credentials rejection

**Task 4.3: Test Protected Endpoints**
- [ ] GET `/api/users` without token → expect 401
- [ ] GET `/api/users` with valid token → expect 200
- [ ] Wait 15 minutes, try with expired token → expect 401

**Task 4.4: Test Public Endpoints**
- [ ] POST `/api/auth/register` without token → expect 201
- [ ] POST `/api/auth/login` without token → expect 200
- [ ] GET `/actuator/health` without token → expect 200

#### Phase 5: Code Quality (30 minutes)

**Task 5.1: Documentation**
- [ ] Add JavaDoc comments to JwtUtil methods
- [ ] Add comments explaining security config
- [ ] Update README with authentication instructions

**Task 5.2: Error Handling**
- [ ] Handle invalid token format
- [ ] Handle expired tokens
- [ ] Handle missing Authorization header
- [ ] Return appropriate HTTP status codes

**Task 5.3: Code Review Prep**
- [ ] Run `mvn clean test`
- [ ] Verify all tests pass
- [ ] Commit code to feature branch
- [ ] Push to GitHub
- [ ] Create pull request (don't merge yet)

#### Phase 6: Handoff to Florence (30 minutes)

**Task 6.1: Share Implementation Files**
- [ ] Copy `JwtUtil.java` to shared location
- [ ] Copy `SecurityConfig.java` to shared location
- [ ] Copy `JwtAuthenticationFilter.java` to shared location
- [ ] Document any custom changes made

**Task 6.2: Sync Meeting**
- [ ] Demo working authentication to Florence
- [ ] Share Postman collection
- [ ] Share a working JWT token for testing
- [ ] Discuss any challenges faced
- [ ] Confirm JWT secret is documented

**Task 6.3: Verification**
- [ ] Florence can generate JWT from User Service
- [ ] Florence understands token structure
- [ ] Florence has all necessary files
- [ ] Ready for Order Service implementation

### Success Criteria

By end of Day 3 morning, Kagiso should have:
- ✅ JWT authentication working in User Service
- ✅ Users can register and login
- ✅ Protected endpoints require valid JWT
- ✅ Public endpoints work without JWT
- ✅ All tests passing
- ✅ Code pushed to Git
- ✅ Files shared with Florence

### Common Issues & Solutions

**Issue 1: "Cannot autowire JwtUtil"**
- **Solution:** Add `@Component` annotation to JwtUtil class

**Issue 2: "Invalid JWT signature"**
- **Solution:** Verify `jwt.secret` is configured in application.properties

**Issue 3: "403 Forbidden on /api/auth/register"**
- **Solution:** Check SecurityConfig permits `/api/auth/**`

**Issue 4: "Bean creation error"**
- **Solution:** Verify all dependencies in pom.xml, run `mvn clean install`

**Issue 5: "Token null pointer exception"**
- **Solution:** Check JwtAuthenticationFilter handles missing Authorization header

---

## Summary

This security architecture document defines:

1. **Authentication Flow:** Complete process from registration to protected resource access
2. **JWT Configuration:** Library, algorithm, expiration, and secret management
3. **Endpoint Protection:** Public vs protected endpoints across both services
4. **Service Communication:** How Order Service validates User Service tokens
5. **Implementation Checklist:** Step-by-step guide for Kagiso's Day 3 work

### Key Decisions Made

| Decision | Value | Rationale |
|----------|-------|-----------|
| JWT Library | jjwt 0.11.5 | Industry standard, secure |
| Algorithm | HS256 | Fast, suitable for shared secrets |
| Token Lifespan | 15 minutes | Security vs UX balance |
| Secret Storage (Dev) | application.properties | Easy for development |
| Secret Storage (Prod) | Kubernetes Secrets | Secure, scalable |
| Public Endpoints | register, login, health | Minimum required |

### Next Steps

1. **Day 2 (Today):** Florence reviews and approves this document
2. **Day 2 EOD:** Schedule sync with Kagiso for Day 3
3. **Day 3 Morning:** Kagiso implements JWT in User Service
4. **Day 3 Afternoon:** Florence implements JWT in Order Service
5. **Day 3 EOD:** Team demo of working authentication

---

**Document Status:** ✅ Ready for Team Review  
**Last Updated:** Day 2  
**Next Review:** Day 3 Morning Standup

---

**Contact:** Florence (Team Leader & Security Architect)  
**Questions?** Schedule sync or ping on Slack
