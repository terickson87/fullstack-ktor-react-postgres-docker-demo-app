FROM gradle:8.6-jdk21-alpine as BackendBuild
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle buildFatJar --no-daemon

FROM node:21-alpine3.19 as FrontendBuild
COPY fullstack-frontend-demo-react-vite /website
WORKDIR /website
RUN npm ci && npm run build

FROM amazoncorretto:21-alpine3.19
COPY --from=FrontendBuild /website/dist /bin/website
COPY --from=BackendBuild /app/build/libs/*-all.jar /bin/app.jar
WORKDIR /bin
CMD ["java","-jar","app.jar"]

  # TODO Add ENV vars in a .env file
  # pull same values in application.conf https://ktor.io/docs/configuration-file.html#environment-variables
  # and in Kotlin files https://stackoverflow.com/a/48104838/2561852