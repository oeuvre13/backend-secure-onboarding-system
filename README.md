# Backend Secure on-boarding system

## TEST REGISTER

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "TestPass123!",
    "phone": "+6281234567890",
    "age": 25
  }'
```

LOGIN

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }'
```

Access Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt
```

LOGOUT

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt \
  -c cookies.txt
```

🔑 Token Flow Summary

- Backend:

```flow
Login → 
Generate JWT → 
Set HTTP-only cookie
Protected endpoints → 
Read cookie → 
Validate JWT → 
Return data
Logout → 
Clear cookie
```

- Frontend:

```flow
Login form → Send credentials → Cookie auto-saved
Protected pages → Cookie auto-sent → Check auth status
All API calls → Use credentials: 'include'
Logout → Clear cookie → Redirect to login
```

- User Experience:

```flow
Login once → Stay authenticated for 24 hours
Visit any page → Automatic auth check
Token expires → Automatic redirect to login
```

## Branching Strategy

[Referensi](https://github.com/discover-devops/Git_Commands/blob/main/Best%20Practices%20for%20Git%20Branching.md)

- `main`
- `develop`
- `feature/*`

![branching-strategy](./img/desain-repo-repo-strategy.png)

![deployment-strategy](./img/desain-deployment-k8s.png)
