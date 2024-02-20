FROM gradle:8.6-jdk21-alpine as build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM amazoncorretto:21-alpine3.19
COPY --from=build /app/build/libs/*-all.jar /bin/app.jar
WORKDIR /bin
CMD ["java","-jar","app.jar"]