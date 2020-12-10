package com.cdms.android.network


import androidx.multidex.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object NetworkClient {

    private val dispatcher = Dispatcher()
    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    val client: OkHttpClient
        get() {
            dispatcher.maxRequests = 2

            // we using this OkHttp, you can add authenticator, interceptors, dispatchers,
            // logging stuff etc. easily for all your requests just editing this OkHttp
           val builder =  OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor { chain ->
                        val original = chain.request()

                        val request = original.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .build()

                        chain.proceed(request)
                    }
                    .dispatcher(dispatcher)
                    .cache(null)

            if(BuildConfig.DEBUG)
                builder.addInterceptor(loggingInterceptor)

            return builder.build()
        }
}