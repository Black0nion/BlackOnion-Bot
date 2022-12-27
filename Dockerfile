FROM gradle:7.6-jdk19-alpine AS build

WORKDIR /bot

COPY --chown=gradle:gradle . .

RUN gradle build downloadDependencies --no-daemon

FROM eclipse-temurin:19-jre-alpine AS run

WORKDIR /blackonionbot

# Copy the libraries to the container
COPY --from=build /bot/libraries/* ./

# Copy the built application to the container
COPY --from=build /bot/build/libs/BlackOnion-Bot.jar ./bot.jar

ENTRYPOINT [ "java", "-cp", "*", "com.github.black0nion.blackonionbot.Main" ]
