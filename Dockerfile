FROM gradle:8.6-jdk21-alpine AS backend-build
COPY --chown=gradle:gradle gradle /app/gradle
COPY --chown=gradle:gradle gradlew /app
COPY --chown=gradle:gradle gradle.properties /app
COPY --chown=gradle:gradle build.gradle.kts /app
COPY --chown=gradle:gradle settings.gradle.kts /app
COPY --chown=gradle:gradle src /app/src
#COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:21-alpine3.19
COPY --from=backend-build /app/build/libs/*-all.jar /bin/app.jar
WORKDIR /bin
CMD ["java","-jar","app.jar"]

  # TODO Add ENV vars in a .env file
  # pull same values in application.conf https://ktor.io/docs/configuration-file.html#environment-variables
  # and in Kotlin files https://stackoverflow.com/a/48104838/2561852