# Florence's Day 2 Research Notes
## JWT Security Architecture - Research Phase

**Date:** Day 2  
**Time:** 30 minutes  
**Purpose:** Understand JWT concepts before creating architecture document

---

## 1. Understand JWT Basics

### What is JWT?
JWT (JSON Web Token) is a compact, URL-safe token format used for secure information transmission between parties.

### JWT Structure:
```
header.payload.signature
```

**Example Token:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### How Token Generation Works:

**Step 1: User Logs In**
```
POST /api/auth/login
Body: { username: "john", password: "pass123" }
```

**Step 2: Server Validates Credentials**
- Check username exists in database
- Verify password matches (using BCrypt)

**Step 3: Server Generates JWT**
```java
// Create token with username and expiration
String token = Jwts.builder()
    .setSubject(username)              // Username
    .setIssuedAt(new Date())          // Created now
    .setExpiration(new Date() + 15min) // Expires in 15 min
    .signWith(secretKey, HS256)       // Sign with secret
    .compact();
```

**Step 4: Client Receives Token**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Step 5: Client Uses Token for Requests**
```
POST /api/orders
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### How Token Validation Works:

**Step 1: Extract Token from Header**
```java
String header = request.getHeader("Authorization");
String token = header.substring(7); // Remove "Bearer "
```

**Step 2: Verify Signature**
```java
Jwts.parserBuilder()
    .setSigningKey(secretKey)  // Use SAME secret as generation
    .build()
    .parseClaimsJws(token);    // Throws exception if invalid
```

**Step 3: Check Expiration**
```java
if (token.getExpiration().before(new Date())) {
    throw new Exception("Token expired");
}
```

**Step 4: Extract Username**
```java
String username = token.getSubject();
// Now we know who is making the request!
```

---

## 2. Define Token Lifespan

### Access Tokens (Short-lived)
- **Duration:** 15 minutes (900,000 milliseconds)
- **Purpose:** Used for API requests
- **Why short?** 
  - Limits damage if token is stolen
  - Forces periodic re-authentication
  - Better security posture

### Refresh Tokens (Long-lived) - Future Phase
- **Duration:** 7 days (604,800,000 milliseconds)
- **Purpose:** Get new access tokens without re-login
- **Why long?** 
  - Better user experience
  - Reduces login frequency
  - Still more secure than no expiration

### Configuration for Our Project:
```properties
# application.properties
jwt.expiration=900000  # 15 minutes in milliseconds
```

### Token Lifecycle:
```
User Logs In
    ↓
Receives Access Token (15 min lifespan)
    ↓
Uses token for requests (within 15 min)
    ↓
Token Expires
    ↓
User must login again (or use refresh token in future)
```

---

## 3. Secret Key Strategy

### What is the Secret Key?
The secret key is used to sign and verify JWT tokens. Both services MUST use the SAME key.

### Development Environment:
**Store in:** `application.properties`

```properties
jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
```

**Requirements:**
- Minimum 256 bits (32 characters)
- Random, complex string
- MUST be identical in both services

### Production Environment (Kubernetes):

**Step 1: Create Kubernetes Secret**
```yaml
# jwt-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
  namespace: default
type: Opaque
data:
  secret-key: bXlTdXBlclNlY3JldEtleQ==  # Base64 encoded
```

**Step 2: Reference in Deployment**
```yaml
# deployment.yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: jwt-secret
        key: secret-key
```

**Step 3: Application Reads from Environment**
```properties
jwt.secret=${JWT_SECRET}
```

### Security Best Practices:
✅ Never commit secrets to Git  
✅ Use environment variables  
✅ Rotate secrets periodically  
✅ Use strong, random values  
❌ Don't use dictionary words  
❌ Don't share secrets via email/Slack  
❌ Don't log secret values  

---

## 4. Service Communication

### The Challenge:
- User Service generates JWT tokens
- Order Service needs to validate those tokens
- How does Order Service trust User Service's tokens?

### The Solution: Shared Secret Key

**Both services use the SAME secret key!**

```
┌──────────────────┐           ┌──────────────────┐
│  User Service    │           │  Order Service   │
│  Port 8081       │           │  Port 8082       │
│                  │           │                  │
│ jwt.secret=ABC   │           │ jwt.secret=ABC   │
│       ↓          │           │       ↓          │
│ Generates JWT    │ --------> │ Validates JWT    │
│ Signs with ABC   │  Token    │ Verifies with ABC│
└──────────────────┘           └──────────────────┘
```

### How Order Service Validates Tokens:

**Step 1: Receive Request**
```
POST /api/orders
Authorization: Bearer eyJhbGc...
```

**Step 2: Extract Token**
```java
String token = request.getHeader("Authorization").substring(7);
```

**Step 3: Validate Signature**
```java
// Order Service uses SAME secret as User Service
Jwts.parserBuilder()
    .setSigningKey(sharedSecret)  // MUST be identical!
    .build()
    .parseClaimsJws(token);
```

**Step 4: Extract Username**
```java
String username = jwtUtil.getUsernameFromToken(token);
// Now Order Service knows who created this order!
```

### Critical Points:

**✅ MUST:**
- Use identical secret keys
- Use same JWT library (jjwt 0.11.5)
- Use same algorithm (HS256)
- Use same token structure

**❌ WILL FAIL IF:**
- Secrets are different
- Algorithms are different
- Token format is different

### Testing Service Communication:

**Step 1: Register User (User Service)**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -d '{"username":"test","password":"pass123"}'
```

**Step 2: Login (User Service)**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -d '{"username":"test","password":"pass123"}'
# Returns: {"token":"eyJhbGc..."}
```

**Step 3: Create Order (Order Service with token)**
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{"productName":"Laptop","quantity":2}'
# Should work if secrets match!
```

---

## Key Decisions for Architecture Document:

### 1. JWT Library
**Choice:** jjwt 0.11.5  
**Why:** Industry standard, well-maintained, secure

### 2. Algorithm
**Choice:** HS256 (HMAC SHA-256)  
**Why:** Symmetric, fast, suitable for microservices with shared secret

### 3. Token Expiration
**Choice:** 15 minutes  
**Why:** Balance between security and user experience

### 4. Secret Management
**Dev:** application.properties  
**Prod:** Kubernetes Secrets  
**Why:** Secure, scalable, industry standard

### 5. Public Endpoints
**User Service:**
- POST /api/auth/register
- POST /api/auth/login

**Order Service:**
- None (all endpoints require JWT)

---

## Questions to Address in Architecture Doc:

- [x] What JWT library to use?
- [x] What algorithm for signing?
- [x] What's the secret key?
- [x] How long should tokens last?
- [x] Which endpoints are public?
- [x] Which endpoints need JWT?
- [x] How do services communicate?
- [x] Where to store secrets in production?

---

## Next Step: Create Security Architecture Document

Now that research is complete, create the full architecture document with:
1. Authentication Flow diagrams
2. JWT Configuration details
3. Endpoint Protection strategy
4. Service-to-Service Communication explanation
5. Implementation Checklist for Kagiso

**Research Phase Complete!** ✅  
**Time to create the architecture document!** 📝
