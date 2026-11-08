package com.example.bai02.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton quản lý cấu hình kết nối mạng của ứng dụng.
 */
object RetrofitClient {

    private const val BASE_URL = "https://share.tryapps.store/"
    private const val API_TOKEN = "TuanTruong-uth-student"

    // 1. Interceptor tự động gắn Bearer Token vào Header của mọi Request
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .header("Authorization", "Bearer $API_TOKEN")
            .build()
        chain.proceed(requestWithToken)
    }

    // 2. Interceptor ghi nhật ký mạng ra Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 3. Bộ máy OkHttpClient với thời gian chờ kết nối là 15 giây
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 4. Khởi tạo đối tượng TaskApiService dùng chung (chỉ tạo 1 lần khi được gọi đến)
    val taskApiService: TaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApiService::class.java)
    }
}