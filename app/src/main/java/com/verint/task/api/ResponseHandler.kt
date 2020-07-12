package com.verint.task.api

import retrofit2.HttpException
import java.net.SocketTimeoutException
/**
 * ResponseHandler
 *
 * @author Gabriel Noam
 */
open class ResponseHandler {
    fun <T : Any> handleSuccess(data: T, total: Int): Resource<T> {
        Resource.total = total
        return Resource.remote(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(getErrorMessage(800), null)
            else -> Resource.error(getErrorMessage(Int.MAX_VALUE), null)
        }
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            800 -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong"
        }
    }
}