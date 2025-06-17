# yxq/back/Dockerfile

# --- 第一阶段: 构建 ---
# 使用官方的Maven镜像作为构建环境，该镜像基于OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖，利用Docker的层缓存机制
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制所有源代码
COPY src ./src

# 执行打包命令，跳过测试
RUN mvn clean package -DskipTests

# --- 第二阶段: 运行 ---
# 使用一个轻量级的Java 17运行时环境
FROM openjdk:17-slim

# 设置工作目录
WORKDIR /app

# 从第一阶段(builder)复制构建好的JAR包到当前阶段
# 注意：请将 'tjufood-project-backend-0.0.1-SNAPSHOT.jar' 替换为您 pom.xml 中定义的实际jar包名称
COPY --from=builder /app/target/tjufood-project-backend-0.0.1-SNAPSHOT.jar ./app.jar

# 暴露应用程序端口
EXPOSE 8090

# 设置容器启动时执行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]