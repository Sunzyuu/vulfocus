# docker部署

## 后端

注意：rabbitMQ需要单独创建，后续会考虑添加到docker-compose.yml中，实现一键启动

deploy/docker-compose/docker-compose.yml

```yaml
version: "3"

services:
  # 定义 Spring Boot 应用程序所需要的服务（容器）
  vulfocus:
    # 构建镜像的路径。"." 表示 Dockerfile 文件所在的当前目录
    build: .
    # 指定容器名称
    container_name: vulfocus
    # 容器所要使用的端口号
    ports:
      - "8001:8001"
    # 指定容器启动后所需要等待的其它服务的启动时间
    depends_on:
      - database
      - redis
    # 环境变量设置
    environment:
      - PROFILES_ACTIVE=prod
      - DATASOURCE_URL=jdbc:mysql://database:3306/vulfocus?useSSL=false
      - DATABASE_USER=root
      - DATABASE_PASSWORD=root
      - REDIS_HOST=redis
      - REDIS_PORT=6379

  # 定义数据库服务（容器）
  database:
    image: mysql:5.7.26
    # network_mode: "host" # 如果需要容器使用宿主机IP(内网IP)，则可以配置此项
    container_name: database # 指定容器名称，如果不设置此参数，则由系统自动生成
    restart: unless-stopped # 设置容器自启模式
    command: mysqld
    environment:
      - TZ=Asia/Shanghai # 设置容器时区与宿主机保持一致
      - MYSQL_ROOT_PASSWORD=root # 设置root密码
    ports:
      - 3306:3306
    volumes:
      # 数据挂载目录自行修改哦！
      #      - /etc/localtime:/etc/localtime:ro # 设置容器时区与宿主机保持一致
      #      - /data/mysql/data:/var/lib/mysql/data # 映射数据库保存目录到宿主机，防止数据丢失
      - ./mysql/conf/my.cnf:/etc/mysql/conf.d/my.cnf # 映射数据库配置文件
      - ./mysql/init:/docker-entrypoint-initdb.d

  # 定义 Redis 服务（容器）
  redis:
    image: redis:alpine
    container_name: redis
    ports:
      - "6379:6379"
```

Dockerfile

```dockerfile
# 使用官方 OpenJDK 8 映像作为基础镜像
FROM openjdk:8-jdk-alpine

# 将当前目录下的所有 jar 包复制到容器中的 /app 目录下
COPY vulfocus-0.0.1.jar /app/

EXPOSE 8001
# 设置容器启动时执行的命令
ENTRYPOINT sleep 60 && java -Dspring.profiles.active=prod -Dspring.datasource.url=$DATASOURCE_URL -Dspring.datasource.username=$DATABASE_USER -Dspring.datasource.password=$DATABASE_PASSWORD -Dspring.redis.host=$REDIS_HOST -Dspring.redis.port=$REDIS_PORT  -jar /app/vulfocus-0.0.1.jar
```

### 前端

`deploy/frontend/Dockerfile`

```yaml
FROM nginx
COPY dist/ /usr/share/nginx/html/
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
```
