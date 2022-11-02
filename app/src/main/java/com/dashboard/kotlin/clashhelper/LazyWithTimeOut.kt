package com.dashboard.kotlin.clashhelper

import kotlin.reflect.KProperty

class LazyWithTimeOut<T> (private val timeOut: Long, val initializer: () -> T) {
    private object UNINITIALIZED_VALUE
    private var value: Any? = UNINITIALIZED_VALUE
    private var lastTime = 0L
    private val time
        get() = System.currentTimeMillis()

    @Synchronized
    private fun updateValue(): T {
        value = initializer()
        lastTime = time
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    operator fun getValue(nothing: Any?, property: KProperty<*>) =
        if (value !== UNINITIALIZED_VALUE && time < lastTime + timeOut)
            @Suppress("UNCHECKED_CAST") (value as T)
        else
            synchronized(this) {
                if (value !== UNINITIALIZED_VALUE && time < lastTime + timeOut)
                    @Suppress("UNCHECKED_CAST") (value as T)
                else
                    updateValue()
            }

}