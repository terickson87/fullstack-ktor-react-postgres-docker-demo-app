package io.github.terickson87.ktorpostgresdemo.adapter.accessor

/*
In case this need to be mocked: https://notwoods.github.io/mockk-guidebook/docs/mocking/static/
Env Vars with `docker compose`:
https://docs.docker.com/compose/environment-variables/set-environment-variables/#set-environment-variables-with-docker-compose-run---env

With reference to Ktor explicitly:
https://github.com/sharpmind-de/ktor-env-config?tab=readme-ov-file#java--jar
*/
object SystemEnvAccessor : EnvAccessor {

    private const val DB_HOST_ENV_VAR = "DB_HOST"
    private const val DEFAULT_DB_HOST = "127.0.0.1:5432"
    override fun getDbHost(): String = System.getenv(DB_HOST_ENV_VAR) ?: DEFAULT_DB_HOST
}