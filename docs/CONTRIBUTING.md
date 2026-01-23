# Contributing Guidelines

## Before You Start
1. Ensure you have access to the repository
2. Clone the repository locally
3. Set up your development environment
4. Read the project documentation

## Making Changes

### 1. Create a Branch
```bash
git checkout -b feature/your-feature-name
```

### 2. Make Your Changes
- Write clean, readable code
- Follow Java coding standards
- Add comments where necessary
- Write tests for new features

### 3. Commit Your Changes
```bash
git add .
git commit -m "type: brief description"
```

**Commit Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `test`: Adding tests
- `refactor`: Code refactoring
- `chore`: Maintenance tasks

**Examples**:
- `feat: add user registration endpoint`
- `fix: resolve database connection timeout`
- `docs: update API documentation`

### 4. Push and Create PR
```bash
git push origin feature/your-feature-name
```

Then go to GitHub and create a Pull Request.

## Pull Request Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Refactoring

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing completed
- [ ] All tests passing

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added where necessary
- [ ] Documentation updated
```

## Code Quality
- Run tests before committing: `mvn test`
- Check code style
- Ensure no merge conflicts
- Keep commits atomic and focused

## Getting Help
- Ask in team chat
- Tag relevant team member in PR
- Schedule pair programming session
- Check documentation first
