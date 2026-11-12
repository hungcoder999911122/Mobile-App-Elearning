package com.example.bai01.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Any? = null,

    @SerializedName("name", alternate = ["title", "product_name"])
    val name: String? = null,

    @SerializedName("price", alternate = ["product_price"])
    val price: Any? = null,

    // Bổ sung "imgURL" (viết hoa URL như API thực tế trả về)
    @SerializedName("image", alternate = ["imgURL", "imageUrl", "img", "avatar", "thumbnail", "photo"])
    val image: String? = null,

    // Bổ sung "des" (như API thực tế trả về)
    @SerializedName("description", alternate = ["des", "desc", "detail"])
    val description: String? = null
) {
    fun getDisplayName(): String {
        return name ?: "Sản phẩm chưa có tên"
    }

    fun getFormattedPrice(): String {
        val raw = price?.toString()?.trim() ?: "0"
        return if (raw.contains("đ", ignoreCase = true) || raw.contains("$")) {
            raw
        } else {
            // Sửa lỗi cú pháp string template: dùng ${raw}
            "${raw}đ"
        }
    }

    fun getDisplayDescription(): String {
        return description ?: "Chưa có mô tả chi tiết cho sản phẩm này."
    }
}