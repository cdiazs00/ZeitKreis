FROM openjdk:11-jdk

WORKDIR /workspace

RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    git \
    build-essential \
    libncurses5 \
    libstdc++6 \
    zlib1g \
    lib32z1 \
    lib32ncurses6 \
    lib32stdc++6 \
    lib32gcc-s1 \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/android-sdk-linux && \
    cd /opt/android-sdk-linux && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip && \
    unzip commandlinetools-linux-7583922_latest.zip && \
    rm commandlinetools-linux-7583922_latest.zip

ENV ANDROID_SDK_ROOT /opt/android-sdk-linux
ENV PATH $ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH

RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3" "tools"

RUN wget https://services.gradle.org/distributions/gradle-7.3.3-bin.zip -P /opt/ && \
    unzip /opt/gradle-7.3.3-bin.zip -d /opt/ && \
    rm /opt/gradle-7.3.3-bin.zip

ENV GRADLE_HOME /opt/gradle-7.3.3
ENV PATH $GRADLE_HOME/bin:$PATH

COPY . /workspace/

RUN gradle clean build

EXPOSE 8080

CMD ["gradle", "assembleDebug"]