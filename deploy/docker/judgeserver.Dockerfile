FROM criyle/executorserver:v1.6.10

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
    unzip
