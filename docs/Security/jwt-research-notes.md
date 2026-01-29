# JWT Research Notes - Day 2

## 1. What is JWT (JSON Web Token)?

JWT is a compact, URL-safe token used for securely transmitting information between parties as a JSON object.

### JWT Structure (3 parts separated by dots):
```
header.payload.signature
```

**Example:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

### Parts Explained:

1. **Header** (Algorithm & Token Type)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

2. **Payload** (Claims - User Data)
```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "email": "john@example.com",
  "iat": 1516239022,
  "exp": 1516242622
}
```

3. **Signature** (Verification)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

---

## 2. How JWT Authentication Works

### Flow:
1. **User Login** → Sends username/password
2. **Server Validates** → Checks credentials
3. **Server Generates JWT** → Creates token with user info
4. **Client Receives Token** → Stores it (localStorage/sessionStorage)
5. **Client Sends Token** → Includes in Authorization header for protected requests
6. **Server Validates Token** → Verifies signature and expiration
7. **Server Processes Request** → If valid, returns data

---

## 3. Token Lifespan Strategy

### Access Tokens (Short-lived)
- **Duration:** 15 minutes
- **Purpose:** Used for API requests
- **Why short?** Security - limits exposure if stolen

### Refresh Tokens (Long-lived)
- **Duration:** 7 days
- **Purpose:** Get new access tokens without re-login
- **Storage:** More secure storage (httpOnly cookies recommended)

### Implementation Plan:
```java
// Access Token Configuration
private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minutes

// Refresh Token Configuration
private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days
```

---

## 4. Secret Key Strategy

### For Development:
- Store in `application.properties`
- Use environment variables
- **Example:**
```properties
jwt.secret=myVerySecretKeyThatIsAtLeast256BitsLong1234567890
jwt.expiration=900000
```

### For Production (Kubernetes):
- Use Kubernetes Secrets
- Never commit secrets to Git
- Rotate secrets regularly

**Kubernetes Secret Example:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
type: Opaque
data:
  secret-key: <base64-encoded-secret>
```

**Usage in Deployment:**
```yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: jwt-secret
        key: secret-key
```

---

## 5. Service-to-Service Communication

### Scenario:
User Service generates JWT → Order Service validates it

### How Order Service Validates Tokens from User Service:

1. **Same Secret Key** (CRITICAL!)
   - Both services MUST use the SAME secret key
   - If secrets differ, validation fails

2. **Shared JWT Utility**
   - Copy the EXACT same `JwtUtil.java` class
   - Identical validation logic

3. **Validation Process:**
```
User Service (Port 8081):
└─ Generates JWT with secret "ABC123"

Order Service (Port 8082):
└─ Receives JWT in request header
└─ Uses SAME secret "ABC123" to validate
└─ Decodes payload to get user info
└─ Processes request if valid
```

### What Order Service Checks:
- ✅ Signature is valid (using shared secret)
- ✅ Token hasn't expired
- ✅ Token format is correct
- ✅ Required claims are present

---

## Key Takeaways for Architecture Document:

1. **Consistency is Critical:** Both services must use identical JWT configuration
2. **Security First:** Short access tokens, secure secret storage
3. **Clear Flow:** Document login → token → validate → access
4. **Implementation Ready:** Provide exact config values for Kagiso

---

## Questions to Answer in Architecture Doc:

- [ ] What JWT library will we use? (jjwt 0.11.5)
- [ ] What algorithm? (HS256)
- [ ] What's the secret key? (Define one for team)
- [ ] Access token duration? (15 min)
- [ ] Which endpoints are public? (register, login)
- [ ] Which endpoints need JWT? (all others)
- [ ] How to handle token expiration? (Refresh token flow)

---

**Next Step:** Use this research to create the Security Architecture Document!
