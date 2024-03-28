# Ktor-Postgres-Demo
This is a demo fullstack app using:
* Kotlin
* ktor
* postgreSQL
* Exposed Kotlin ORM
* Docker
* Docker Compose

## Checking out the repository
This repository uses git submodules. So in order to to clone it out use:
`git clone --recurse-submodules git@github.com:terickson87/fullstack-ktor-react-postgres-docker-demo-app.git`

If you cloned it without using the `--recurse-submodules` flag, you then must run the command: `git submodule update --init --recursive`. At this point there shouldn't be any recursive submodules, but it is the reccommeded "foolproof" way to clone a repo with submodules.

To pull changes in the submodules use the command: `git submodule update --remote`.

If you have local changes to the submodule that you want to merge the upstream submodule changes into, then use the command: `git submodule update --remote --merge`

If you have local changes to the submodule that you want to rebase on top of the upstream submodule changes, then use the command: `git submodule update --remote --rebase`

Then, in order to push the changes to the main repository, to make sure any submodule changes are pushed too, use the command: `git push --recurse-submodules=check`

Some Submodules References:
https://git-scm.com/docs/gitsubmodules  
https://git-scm.com/docs/git-submodule  
https://git-scm.com/book/en/v2/Git-Tools-Submodules

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