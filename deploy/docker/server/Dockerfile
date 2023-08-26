FROM amazoncorretto:17 as builder

WORKDIR /app

COPY ../../.. /app

RUN ./gradlew :server:bootJar

FROM amazoncorretto:17

WORKDIR /app

COPY --from=builder /app/server/build/libs/server-*.jar /app/server.jar

ENV LANG=C.UTF-8
ENV TZ=Asia/Shanghai

ENV NTOJ_FILE_BASE_DIR=/data

EXPOSE 18080

ENTRYPOINT java -jar /app/server.jar
