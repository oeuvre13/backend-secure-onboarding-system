# Backend Secure on-boarding system   

## Penggunaan .env

Untuk pengujian local, pastikan terdapat file `.env` yang sejajar dengan `pom.xml` dengan minimum isi sebagai berikut:

```conf
DB_URL=jdbc:postgresql://localhost:5432/customer_registration
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=90385881f4876e643cdf5fa2b28c1494469133ddf0e6aee2784eeab3f4f342e82b51ea0c
JWT_EXPIRATION=86400000
SERVER_PORT=8081
DUKCAPIL_SERVICE_URL=http://dukcapil-dummy:8081
FIREBASE_CONFIG_PATH="model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json"
```

simpan file private key (`.js`) di folder `/resources/` :

```tree
.
├── application.properties
├── application.yml
├── banner.txt
└── model-parsec-465503-p3-firebase-adminsdk-fbsvc-1e9901efad.json

1 directory, 4 files
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
