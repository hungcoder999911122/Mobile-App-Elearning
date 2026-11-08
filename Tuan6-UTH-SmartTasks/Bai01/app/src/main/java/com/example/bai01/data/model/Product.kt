package com.example.bai01.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Model cho Sản phẩm.
 * Sử dụng alternate names để tương thích linh hoạt với các định dạng JSON khác nhau của mock API.
 */
data class Product(
    @SerializedName("id")
    val id: Any? = null,

    @SerializedName("name", alternate = ["title", "product_name"])
    val name: String? = null,

    @SerializedName("price", alternate = ["product_price"])
    val price: Any? = null,

    @SerializedName("image", alternate = ["imageUrl", "img", "avatar", "thumbnail", "photo"])
    val image: String? = null,

    @SerializedName("description", alternate = ["desc", "detail"])
    val description: String? = null
) {
    // Trả về tên hiển thị (nếu null thì dùng giá trị mặc định)
    fun getDisplayName(): String {
        return name ?: "Sản phẩm chưa có tên"
    }

    // Định dạng giá bán hiển thị
    fun getFormattedPrice(): String {
        val raw = price?.toString()?.trim() ?: "0"
        return if (raw.contains("đ", ignoreCase = true) || raw.contains("$")) {
            raw
        } else {
            "$rawđ"
        }
    }

    // Trả về mô tả hiển thị
    fun getDisplayDescription(): String {
        return description ?: "Chưa có mô tả chi tiết cho sản phẩm này."
    }
}

