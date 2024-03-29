package io.github.terickson87.ktorpostgresdemo.adapter.accessor

interface EnvAccessor {
    fun getDbHost(): String
    fun getIsFrontEndDev(): Boolean
}