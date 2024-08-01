package com.mirage.mafiagame.module

import kotlin.reflect.KProperty

class ModuleDelegate<T : BaseModule>(private val moduleClass: Class<T>) {
    private var moduleInstance: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (moduleInstance == null) {
            moduleInstance = DIContainer.resolve(moduleClass)
        }
        return moduleInstance!!
    }
}

inline fun <reified T : BaseModule> module() = ModuleDelegate(T::class.java)