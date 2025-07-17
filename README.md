# Backend Secure on-boarding system



## Penggunaan .env

Untuk pengujian local, pastikan terdapat file `.env` yang sejajar dengan `pom.xml` dengan minimum isi sebagai berikut:

```conf
DB_URL=jdbc:postgresql://localhost:5432/customer_registration
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=mySecretKey123456789mySecretKey123456789
JWT_EXPIRATION=86400000
SERVER_PORT=8081
```

- dependency yang ditambahkan di `pom.xml` :

```xml
  <dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>2.3.2</version>
  </dependency>
```

- kode yang ditambahkan di main application Java (`RegistrationAbsoluteApplication.java`) :

```java
public static void main(String[] args) {
    // Load .env variables
    Dotenv dotenv = Dotenv.load();
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

    // ...
}
```

## 🔑 Token Flow Summary

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

## Deployment Strategy (plan)

![deployment-strategy](./img/desain-deployment-k8s.png)