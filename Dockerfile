# ===== Stage 1: 构建前端 (Vue.js) =====
FROM node:18 AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# ===== Stage 2: 构建后端 (Spring Boot JAR) =====
FROM maven:3.8-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/ .
COPY --from=frontend /app/frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# ===== Stage 3: 运行 =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]