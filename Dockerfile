FROM gradle:8.6-jdk21-alpine as build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:21-alpine3.19
COPY --from=build /app/build/libs/*-all.jar /bin/app.jar
WORKDIR /bin
CMD ["java","-jar","app.jar"]

  # TODO Add ENV vars in a .env file
  # pull same values in application.conf https://ktor.io/docs/configuration-file.html#environment-variables
  # and in Kotlin files https://stackoverflow.com/a/48104838/2561852