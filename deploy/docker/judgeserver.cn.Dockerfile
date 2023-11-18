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
    nodejs \
    unzip

RUN wget https://github.com/JetBrains/kotlin/releases/download/v1.9.20/kotlin-compiler-1.9.20.zip -O /tmp/kotlin.zip && \
    unzip /tmp/kotlin.zip -d /usr/lib && \
    rm /tmp/kotlin.zip && \
    ln -s /usr/lib/kotlinc/bin/kotlinc /usr/bin/kotlinc && \
    ln -s /usr/lib/kotlinc/bin/kotlin /usr/bin/kotlin
