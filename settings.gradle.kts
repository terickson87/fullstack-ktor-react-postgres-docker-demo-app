pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val ktorVersion: String by settings
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("io.ktor.plugin") version ktorVersion
    }
}
rootProject.name = "io.github.terickson87.fullstack-ktor-react-postgres-docker-demo-app"
