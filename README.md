# Backend Secure on-boarding system

## Branching Strategy

[Referensi](https://github.com/discover-devops/Git_Commands/blob/main/Best%20Practices%20for%20Git%20Branching.md)

- `main`
- `develop`
- `feature/*`


TEST REGISTER
```
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
```
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }'
```

Access Protected Endpoint
```
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt
```

LOGOUT
```
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt \
  -c cookies.txt
```

🔑 Token Flow Summary
```
Backend:

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
```
Frontend:

Login form → Send credentials → Cookie auto-saved
Protected pages → Cookie auto-sent → Check auth status
All API calls → Use credentials: 'include'
Logout → Clear cookie → Redirect to login
```
```
User Experience:

Login once → Stay authenticated for 24 hours
Visit any page → Automatic auth check
Token expires → Automatic redirect to login
```
