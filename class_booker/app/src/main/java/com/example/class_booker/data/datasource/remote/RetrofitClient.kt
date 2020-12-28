package com.example.class_booker.data.datasource.remote

import android.util.Log
import com.example.class_booker.utils.convertToUtcFromString
import com.example.class_booker.utils.toUTCString
import com.google.gson.*
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*


object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGsonConverter()))
                .build()
        }
        return retrofit!!
    }

    fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .eventListener(object : EventListener() {
                override fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) {
                    Log.i(RetrofitClient::class.simpleName, "Can't connect to the server", ioe)
                }
            }).build()
    }

    fun getGsonConverter(): Gson {
        return GsonBuilder().registerTypeAdapter(Date::class.java, DateSerializer()).create()
    }
}

class DateSerializer : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toUTCString())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return json?.let {
            convertToUtcFromString(it.asString)
        } ?: Date()
    }
}
