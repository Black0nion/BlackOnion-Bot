FROM gradle:7.5-jdk17-alpine AS build

COPY --chown=gradle:gradle . /bot

WORKDIR /bot

RUN gradle build downloadDependencies --no-daemon

FROM eclipse-temurin:17-jre-alpine AS run

WORKDIR /blackonionbot

# Copy the libraries to the container
COPY --from=build /bot/libraries/* ./

# Copy the built application to the container
COPY --from=build /bot/build/libs/BlackOnion-Bot.jar ./bot.jar

ENTRYPOINT [ "java", "-cp", "*", "com.github.black0nion.blackonionbot.Main" ]