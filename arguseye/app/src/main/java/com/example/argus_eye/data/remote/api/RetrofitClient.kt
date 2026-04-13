package com.example.argus_eye.data.remote.api

import android.content.Context
import com.example.argus_eye.data.local.PrefManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var _apiService: ApiService? = null

    val apiService: ApiService
        get() = _apiService ?: throw IllegalStateException("RetrofitClient not initialized. Call getApiService(context) first.")

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = originalRequest.newBuilder()

        if (user != null) {
            try {
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

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    // This function will recreate the ApiService using the latest URL from SharedPreferences
    fun getApiService(context: Context): ApiService {
        val baseUrl = PrefManager(context).getBaseUrl()

        // Check if we need to rebuild (if URL changed or first time)
        if (retrofit == null || retrofit?.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            _apiService = retrofit!!.create(ApiService::class.java)
        }
        return _apiService!!
    }
}