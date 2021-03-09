FROM gradle:6.5.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/bot.jar

ARG token_ARG

ENV token=${token_ARG}

ENTRYPOINT ["/bin/sh", "-c", "exec java -jar /app/bot.jar -token ${token}"]