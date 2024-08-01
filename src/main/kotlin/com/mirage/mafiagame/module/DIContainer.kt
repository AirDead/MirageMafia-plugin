package com.mirage.mafiagame.module

object DIContainer {
    private val services = mutableMapOf<Class<*>, Any>()

    fun <T : Any> register(serviceClass: Class<T>, instance: T) {
        services[serviceClass] = instance
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(serviceClass: Class<T>): T {
        return services[serviceClass] as? T
            ?: throw IllegalArgumentException("Service of type ${serviceClass.simpleName} not registered")
    }
}