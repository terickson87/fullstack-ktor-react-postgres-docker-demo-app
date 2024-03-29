package io.github.terickson87.ktorpostgresdemo.adapter.accessor

// In case this need to be mocked: https://notwoods.github.io/mockk-guidebook/docs/mocking/static/
object SystemEnvAccessor : EnvAccessor {

    private const val DB_HOST_ENV_VAR = "DB_HOST"
    private const val DEFAULT_DB_HOST = "127.0.0.1:5432"
    private const val IS_FRONT_END_DEV_ENV_VAR = "IS_FRONT_END_DEV"
    override fun getDbHost(): String = System.getenv(DB_HOST_ENV_VAR) ?: DEFAULT_DB_HOST

    override fun getIsFrontEndDev(): Boolean = System.getenv(IS_FRONT_END_DEV_ENV_VAR).toBoolean()
}