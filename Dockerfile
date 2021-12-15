FROM gradle:7.3-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-alpine

WORKDIR /root/blackonionbot
EXPOSE 187

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/BlackOnion-Bot.jar /app/bot.jar

ENTRYPOINT ["java", "-jar", "/app/bot.jar"]