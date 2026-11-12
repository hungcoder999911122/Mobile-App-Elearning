package com.example.bai01.data.network

import com.google.gson.JsonElement
import retrofit2.http.GET

interface ApiService {
    /**
     * Gọi GET đến endpoint: https://mock.apidog.com/m1/890655-872447-default/v2/product
     * Dùng JsonElement để đọc linh hoạt dù response là 1 Object, 1 Mảng [] hay bọc trong data: {...}
     */
    @GET("v2/product")
    suspend fun getProductData(): JsonElement
}

