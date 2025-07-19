# === Stage 1: Build with Maven ===
FROM eclipse-temurin:17-jdk-alpine AS build

# Установка Maven через apk (Alpine Linux)
RUN apk add --no-cache maven

# Рабочая директория
WORKDIR /usr/src/app

# Копируем ВЕСЬ проект (включая родительский POM)
COPY . .

# Собираем нужный модуль
RUN mvn -f safedeck-encrypt-service/pom.xml clean package

# === Stage 2: Runtime ===
FROM eclipse-temurin:17-jre-alpine

# Установка часового пояса
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM оптимизации
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Europe/Moscow"

# Рабочая директория
WORKDIR /app

# Копируем собранный JAR
COPY --from=build /usr/src/app/safedeck-encrypt-service/target/*.jar /app/spring-boot-application.jar

# Точка входа
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/spring-boot-application.jar"]