FROM ubuntu:20.04

ENV TZ=Asia/Shanghai

WORKDIR /judgehost

ENV ONLINE_JUDGE=1

COPY ./ /judgehost

RUN sed -i 's@//.*archive.ubuntu.com@//mirrors.ustc.edu.cn@g' /etc/apt/sources.list && \
    sed -i 's@//.*ports.ubuntu.com@//mirrors.ustc.edu.cn@g' /etc/apt/sources.list

RUN apt update && \
    apt install -y  \
    libseccomp-dev  \
    libseccomp2  \
    seccomp \
    python3 \
    python3-requests \
    gcc \
    g++ \
    openjdk-17-jdk-headless

# RUN gcc /judgehost/sandbox.c -lseccomp -o /judgehost/sandbox.exe

CMD ["python3", "-u", "/judgehost/main.py"]
