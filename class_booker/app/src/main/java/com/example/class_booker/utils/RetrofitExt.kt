package com.example.class_booker.utils

import okhttp3.Protocol
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

fun <T> Call<T>.executeSafe(): Response<T> {
    return try {
        execute()
    } catch (e: IOException) {
        Response.error(
            EMPTY_RESPONSE,
            okhttp3.Response.Builder()
                .request(request())
                .protocol(Protocol.HTTP_2)
                .code(503)
                .message("Failed to connect to server")
                .build()
        )
    }
}
