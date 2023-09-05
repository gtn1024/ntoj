FROM criyle/executorserver:v1.6.10

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
    nodejs
