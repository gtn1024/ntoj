FROM amazoncorretto:17 as builder

WORKDIR /app

COPY ../../.. /app

RUN ./gradlew :judger:shadowJar

FROM debian:bookworm-slim

WORKDIR /app

RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list.d/debian.sources

RUN apt update && \
    apt install -y \
    gcc \
    g++ \
    clang \
    python3 \
    python3-numpy \
    pypy3 \
    openjdk-17-jdk-headless \
    rustc \
    golang \
    fpc \
    mono-complete \
    nodejs \
    npm \
    wget

RUN npm config set registry https://registry.npmmirror.com

RUN npm install -g pm2 && \
    wget https://github.com/criyle/go-judge/releases/download/v1.6.10/executorserver_1.6.10_linux_amd64 -O /usr/bin/sandbox && \
    chmod +x /usr/bin/sandbox

COPY ./deploy/docker/judge/entrypoint.sh /app/entrypoint.sh
COPY ./deploy/docker/judge/mount.yaml /app/mount.yaml
COPY ./deploy/docker/judge/pm2.json /app/pm2.json

RUN chmod +x /app/entrypoint.sh

COPY --from=builder /app/judger/build/libs/judger-*-all.jar /app/judger.jar

ENV LANG=C.UTF-8
ENV TZ=Asia/Shanghai

ENTRYPOINT /app/entrypoint.sh
