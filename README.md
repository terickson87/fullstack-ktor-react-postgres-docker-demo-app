# Ktor-Postgres-Demo
This is a demo fullstack app using:
* Kotlin
* ktor
* postgreSQL
* Exposed Kotlin ORM
* Docker
* Docker Compose

## Local dev
To run the app as a dev locally, you can either run the whole thing with:  
`docker compose up --build`  
You can then access the app by navigating to http://127.0.0.1:8080 in a browser.

or you can run it separately by:
in one terminal running:
`docker compose up postgres pgadmin`

and in another terminal running:  
`./gradlew frontEndNpmInstall`  
`./gradlew frontEndNpmBuild`  
`./gradlew frontEndCopyDist`  
`./gradlew build`  
`java -jar build/libs/io.github.terickson87.dullstack-ktor-react-postgres-docker-demo-app-all.jar -port=8080`

### WSL note:
If you are running the app on WSL, you need to look up your WSL ip for local dev running with `ifconfig`, and then use
the `eth0` interface's `inet` address, which in my case was `172.19.101.149`. So you would need to navigate to:
http://172.19.101.149:8080. Or if you have a browser installed on WSLm, you can open that and then access
http://127.0.0.1:8080 directly.