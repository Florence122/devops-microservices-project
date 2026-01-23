# Team Collaboration Guide

## Daily Standup
**Time**: 9:00 AM daily  
**Format**: 
- What did you do yesterday?
- What will you do today?
- Any blockers?

## Git Workflow

### Branching Strategy
- `main` - Production-ready code
- `develop` - Integration branch
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `hotfix/*` - Emergency fixes

### Example Workflow
```bash
# 1. Start new feature
git checkout -b feature/jwt-authentication

# 2. Make changes and commit
git add .
git commit -m "feat: implement JWT authentication"

# 3. Push to remote
git push origin feature/jwt-authentication

# 4. Create Pull Request on GitHub
# 5. Request review from team member
# 6. After approval, merge to main
```

## Code Review Guidelines
- Review PRs within 24 hours
- Be constructive and respectful
- Test the code before approving
- Ask questions if unclear

## Communication Channels
- **Urgent Issues**: Team WhatsApp/Slack group
- **Code Reviews**: GitHub PR comments
- **Documentation**: Update `/docs` folder
- **Decisions**: Document in team meetings

## Critical Dependencies
1. **Day 1**: GitHub setup (Florence) → BLOCKS all development
2. **Day 3**: JWT alignment (Florence & Kagiso) → Security consistency
3. **Day 6**: Infrastructure setup (Florence) → BLOCKS Week 2
