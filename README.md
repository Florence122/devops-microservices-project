# Security Documentation

This directory contains the JWT authentication architecture and implementation guidelines for the microservices project.

## 📚 Documents

### 1. JWT Research Notes
**File:** [01-jwt-research.md](./01-jwt-research.md)  
**Purpose:** Foundational research on JWT concepts, token lifespan, and security strategies  
**Audience:** Development team reference

### 2. Security Architecture Document ⭐
**File:** [02-security-architecture.md](./02-security-architecture.md)  
**Purpose:** Complete JWT authentication architecture specification  
**Audience:** All developers  

**Contents:**
1. Authentication Flow (diagrams and step-by-step process)
2. JWT Configuration (library, algorithm, expiration settings)
3. Endpoint Protection (public vs protected endpoints)
4. Service-to-Service Communication (shared secret approach)
5. Implementation Checklist (detailed guide for developers)

### 3. Team Meeting Guide
**File:** [03-team-meeting-guide.md](./03-team-meeting-guide.md)  
**Purpose:** Guide for security architecture review meetings  
**Audience:** Team leads

---

## 🔑 Quick Reference

### JWT Configuration
```properties
jwt.secret=mySuperSecretKeyForJWTTokenGeneration12345678901234567890
jwt.expiration=900000  # 15 minutes
```

### Key Specifications
- **Library:** jjwt 0.11.5
- **Algorithm:** HS256 (HMAC SHA-256)
- **Token Expiration:** 15 minutes
- **Token Format:** `Authorization: Bearer <token>`

### Public Endpoints
- POST `/api/auth/register` - User registration
- POST `/api/auth/login` - User login
- GET `/actuator/health` - Health check

### Protected Endpoints
- All `/api/users/*` endpoints (User Service)
- All `/api/orders/*` endpoints (Order Service)

---

## 🚀 Implementation Status

- [x] Day 2: Security architecture designed and documented
- [ ] Day 3: JWT implementation in User Service (Kagiso)
- [ ] Day 3: JWT implementation in Order Service (Florence)
- [ ] Day 4: Docker containerization with JWT
- [ ] Week 2: Kubernetes deployment with JWT secrets

---

## 📖 How to Use This Documentation

### For Developers Implementing JWT:
1. Read [02-security-architecture.md](./02-security-architecture.md) first
2. Follow the implementation checklist (Section 5)
3. Refer to [01-jwt-research.md](./01-jwt-research.md) for concepts

### For Team Leads:
1. Use [03-team-meeting-guide.md](./03-team-meeting-guide.md) for team alignment
2. Review [02-security-architecture.md](./02-security-architecture.md) before meetings
3. Share implementation checklist with developers

### For Code Reviews:
Verify implementations match the specifications in the security architecture document.

---

## 🔒 Security Notes

⚠️ **CRITICAL:** The JWT secret key MUST be identical in both services!

⚠️ **NEVER commit secrets to Git!** Use environment variables or Kubernetes Secrets in production.

⚠️ **Token Expiration:** Tokens expire after 15 minutes for security. Users must re-authenticate.

---

## 📞 Contact

**Architecture Owner:** Florence (Team Leader)  
**Questions?** Create an issue or ping on Slack

---

**Last Updated:** Day 2  
**Version:** 1.0  
**Status:** Ready for Implementation
