# Zoopick Server

- Redis 서비스가 로컬에서 실행 중이어야 합니다.

### 환경변수

```bash .env
# Optional
# default: jdbc:postgresql://mir.lalaalal.com:5432/zoopick
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/zoopick
# default: false
export SPRING_JPA_SHOW_SQL=true

# Mandatory
export SPRING_DATASOURCE_USERNAME=username
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_MAIL_USERNAME=example@example.com
export SPRING_MAIL_PASSWORD=password
export FIREBASE_ACCOUNT_KEY_PATH=/path/to/firebase-adminsdk.json
# 32 바이트 이상
export JWT_SECRET=secret
```

### 빌드

```bash
./mvnw clean package
```

### 실행

```bash
# source .env
cd target
java -jar zoopick-server-x.x.x.jar
```