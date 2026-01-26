# 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -pl river-server -am

# 运行阶段
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/river-server/target/river-server.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod
ENV ARGS=""

EXPOSE 48080

CMD java ${JAVA_OPTS} -jar app.jar ${ARGS}
