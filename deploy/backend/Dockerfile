# 使用官方 OpenJDK 8 映像作为基础镜像
FROM openjdk:8-jdk-alpine

# 将当前目录下的所有 jar 包复制到容器中的 /app 目录下
COPY vulfocus-0.0.1.jar /app/

EXPOSE 8001
# 设置容器启动时执行的命令
ENTRYPOINT  sleep 20 && java -jar /app/vulfocus-0.0.1.jar