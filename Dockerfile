FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=UTC

RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    git \
    build-essential \
    openjdk-11-jdk \
    libncurses5 \
    libstdc++6 \
    zlib1g \
    lib32z1 \
    lib32ncurses6 \
    lib32stdc++6 \
    lib32gcc-s1 \
    tzdata \
    && rm -rf /var/lib/apt/lists/*

RUN echo "Etc/UTC" > /etc/timezone && \
    ln -sf /usr/share/zoneinfo/Etc/UTC /etc/localtime && \
    dpkg-reconfigure --frontend=noninteractive tzdata

RUN mkdir -p /usr/local/android-sdk-linux/cmdline-tools

WORKDIR /usr/local/android-sdk-linux/cmdline-tools
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O android-sdk.zip && \
    unzip android-sdk.zip && \
    rm android-sdk.zip

RUN mv /usr/local/android-sdk-linux/cmdline-tools/cmdline-tools /usr/local/android-sdk-linux/cmdline-tools/latest

ENV ANDROID_HOME=/usr/local/android-sdk-linux
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/30.0.3

RUN echo "ANDROID_HOME: $ANDROID_HOME" && \
    ls -l $ANDROID_HOME/cmdline-tools/latest/bin && \
    ls -l $ANDROID_HOME/platform-tools || echo "platform-tools directory not found"

RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses && \
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3" "tools"

RUN wget https://services.gradle.org/distributions/gradle-7.3.3-bin.zip -P /opt/ && \
    unzip /opt/gradle-7.3.3-bin.zip -d /opt/ && \
    rm /opt/gradle-7.3.3-bin.zip

ENV GRADLE_HOME /opt/gradle-7.3.3
ENV PATH $GRADLE_HOME/bin:$PATH

COPY . /workspace/

RUN gradle clean build

EXPOSE 8080

CMD ["gradle", "assembleDebug"]