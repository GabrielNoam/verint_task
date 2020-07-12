package com.verint.task.api

import com.verint.task.model.Status

/**
 * Resource
 *
 * @author Gabriel Noam
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        var total: Int = 0
        fun <T> local(data: T?): Resource<T> {
            return Resource(
                Status.LOCAL,
                data,
                null
            )
        }

        fun <T> remote(data: T?): Resource<T> {
            return Resource(
                Status.REMOTE,
                data,
                null
            )
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                msg
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }
}