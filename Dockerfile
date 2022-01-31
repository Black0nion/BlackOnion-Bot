FROM gradle:7.3.2-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/blackonionbot/
WORKDIR /home/gradle/blackonionbot
RUN ls /home/gradle/blackonionbot -la
RUN gradle build downloadDependencies --no-daemon

FROM eclipse-temurin:17-alpine

WORKDIR /root/blackonionbot

# API Port
EXPOSE 187

RUN mkdir /app

# Copy the built application to the container
COPY --from=build /home/gradle/blackonionbot/build/libs/BlackOnion-Bot.jar /app/libs/bot.jar
# Copy the libraries to the container
COPY --from=build /home/gradle/blackonionbot/libraries/* /app/libs/

ENTRYPOINT ["java", "-cp", "/app/libs/*", "com.github.black0nion.blackonionbot.Main"]