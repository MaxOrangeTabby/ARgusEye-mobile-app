package com.example.argus_eye.data.remote.api

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser
        
        val requestBuilder = originalRequest.newBuilder()
        
        if (user != null) {
            try {
                // Synchronously get the token. 
                // Note: Interceptor.intercept runs on a background thread (OkHttp thread pool), 
                // so this synchronous wait is generally acceptable.
                val task = user.getIdToken(false)
                val tokenResult = Tasks.await(task)
                val token = tokenResult.token
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
