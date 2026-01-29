# JWT Authentication Flow - Visual Guide

## Simple 3-Step Process

### Step 1: Registration (One Time)
```
┌─────────┐                                    ┌──────────────┐
│ Client  │─────(1) Register────────────────>  │ User Service │
│         │    POST /api/auth/register         │              │
│         │    {username, email, password}     │              │
│         │                                     │  Stores user │
│         │<────(2) Success────────────────────│  in database │
│         │    "User registered"                │              │
└─────────┘                                    └──────────────┘
```

### Step 2: Login & Get Token
```
┌─────────┐                                    ┌──────────────┐
│ Client  │─────(3) Login─────────────────────>│ User Service │
│         │    POST /api/auth/login            │              │
│         │    {username, password}            │  Validates   │
│         │                                     │  credentials │
│         │<────(4) JWT Token──────────────────│  Generates   │
│         │    {"token": "eyJ..."}             │  JWT         │
│         │                                     │              │
│  Stores │                                     └──────────────┘
│  Token  │
└─────────┘
```

### Step 3: Access Protected Resources
```
┌─────────┐                                    ┌──────────────┐
│ Client  │─────(5) Create Order──────────────>│Order Service │
│         │    POST /api/orders                │              │
│         │    Header: Bearer {JWT}            │  Validates   │
│         │    Body: {order data}              │  JWT token   │
│         │                                     │              │
│         │<────(6) Order Created──────────────│  Creates     │
│         │    {orderId: 1, ...}               │  order       │
└─────────┘                                    └──────────────┘
```

---

## Key Points for Implementation

### 🔑 Secret Key (MOST IMPORTANT!)
```
User Service:     jwt.secret=ABC123
Order Service:    jwt.secret=ABC123
                            ↑
                    MUST BE IDENTICAL!
```

### 📦 What's in a JWT Token?
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload (What we store):
{
  "sub": "username",      ← Username of logged in user
  "iat": 1234567890,      ← When token was created
  "exp": 1234568790       ← When token expires (15 min)
}

Signature:
HMACSHA256(header + payload, secret)
```

### 🛡️ Endpoint Protection
```
PUBLIC (No JWT needed):
✅ POST /api/auth/register
✅ POST /api/auth/login

PROTECTED (JWT required):
🔒 All User Service endpoints except auth
🔒 All Order Service endpoints
```

---

## Testing Quick Reference

### 1. Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@test.com","password":"pass123"}'
```

### 2. Login (Get Token)
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'

# Response: {"token":"eyJhbGciOiJIUz..."}
```

### 3. Use Token
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUz..." \
  -d '{"productName":"Laptop","quantity":2,"price":1200}'
```

---

## Implementation Order (Day 3)

### Morning (Kagiso - User Service):
1. ⏰ 9:00 - Add dependencies
2. ⏰ 9:30 - Create JwtUtil.java
3. ⏰ 10:00 - Create SecurityConfig.java
4. ⏰ 10:30 - Create JwtAuthenticationFilter.java
5. ⏰ 11:00 - Create AuthController.java
6. ⏰ 11:30 - Test & fix bugs

### Afternoon (Florence - Order Service):
1. ⏰ 13:00 - Sync with Kagiso (get files)
2. ⏰ 13:30 - Copy security files
3. ⏰ 14:00 - Configure JWT properties
4. ⏰ 14:30 - Test integration
5. ⏰ 15:00 - Demo to team

---

## Success Criteria ✅

- [ ] Can register new user
- [ ] Can login and receive JWT token
- [ ] Can create order WITH token
- [ ] Cannot create order WITHOUT token (401 error)
- [ ] Token expires after 15 minutes
- [ ] Both services use same secret key

---

**Ready for Day 3 Implementation!**
