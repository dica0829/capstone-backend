# Zoopick Server

- Redis 서비스는 로컬에서 실행 중이어야 합니다.

### 환경변수

```bash .env
# Optional ==============================
# default: jdbc:postgresql://mir.lalaalal.com:5432/zoopick
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/zoopick
# default: false
SPRING_JPA_SHOW_SQL=false

# Mandatory =============================
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_MAIL_USERNAME=example@example.com
SPRING_MAIL_PASSWORD=password
FIREBASE_ACCOUNT_KEY_PATH=/path/to/firebase-adminsdk.json
# 32 바이트 이상
JWT_SECRET=secret
```

### 빌드

```bash
./mvnw clean package
```

### 실행

```bash
# setup env first
cd target
java -jar zoopick-server-x.x.x.jar
```