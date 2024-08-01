package com.mirage.mafiagame.module

object DIContainer {
    private val services = mutableMapOf<Class<out BaseModule>, BaseModule>()

    fun <T : BaseModule> register(clazz: Class<out T>, service: T) {
        services[clazz] = service
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseModule> resolve(clazz: Class<out T>): T? {
        return services[clazz] as? T
    }

    fun <T : BaseModule> unregister(clazz: Class<out T>) {
        services.remove(clazz)?.onUnload()
    }
}
