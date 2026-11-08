package com.example.bai01.data.model

/**
 * Lớp đại diện cho thông tin người dùng trong ứng dụng (OOP - Encapsulation).
 * Đóng gói dữ liệu và tự động tính toán các thông tin hiển thị như Initials (chữ viết tắt avatar).
 */
data class UserProfile(
    val id: String = "user_01",
    val fullName: String = "Minh Anh",
    val email: String = "minhanh@gmail.com",
    val isGoogleLinked: Boolean = true,
    val greetingName: String = "UTH!",
    val customInitials: String? = null
) {
    /**
     * Thuộc tính tính toán (Computed Property):
     * Tự động lấy chữ cái đầu đại diện cho avatar (VD: "Minh Anh" -> "MA").
     */
    val initials: String
        get() = customInitials ?: calculateInitials(fullName)

    private fun calculateInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            name.length >= 2 -> name.take(2).uppercase()
            else -> name.uppercase().ifEmpty { "U" }
        }
    }

    val greetingText: String
        get() = "Chào $greetingName"
}
