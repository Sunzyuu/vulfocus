# 使用官方 OpenJDK 8 映像作为基础镜像
FROM openjdk:8-jdk-alpine

# 将当前目录下的所有 jar 包复制到容器中的 /app 目录下
COPY vulfocus-0.0.1.jar /app/

EXPOSE 8001
# 设置容器启动时执行的命令
ENTRYPOINT sleep 60 && java -Dspring.profiles.active=prod -Dspring.datasource.url=$DATASOURCE_URL -Dspring.datasource.username=$DATABASE_USER -Dspring.datasource.password=$DATABASE_PASSWORD -Dspring.redis.host=$REDIS_HOST -Dspring.redis.port=$REDIS_PORT  -jar /app/vulfocus-0.0.1.jar

