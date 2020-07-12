package com.verint.task

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 *
 *
 * @author Gabriel Noam
 */

//pattern for singleton https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
fun <T> T?.singleton(lock: Any, creator: () -> T): T {
    val instance = this
    if (instance != null) return instance

    return synchronized(lock) {
        var instance2 = this
        if (instance2 != null) {
            instance2
        } else {
            instance2 = creator()
            instance2
        }
    }
}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}