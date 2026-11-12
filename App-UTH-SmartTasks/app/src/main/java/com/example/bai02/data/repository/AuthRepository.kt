package com.example.bai02.data.repository

import com.example.bai02.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
/**
 * 1. Interface định nghĩa các chức năng xác thực (OOP - Tính trừu tượng Abstraction)
 */
interface AuthRepository {
    val currentUser: FirebaseUser?
    fun signInAnonymously(onSuccess: (UserProfile) -> Unit, onError: (String) -> Unit)
    fun signOut()
    fun getUserProfile(): UserProfile
}
/**
 * 2. Lớp triển khai dịch vụ gọi trực tiếp Firebase Authentication (OOP - Encapsulation)
 */
class FirebaseAuthRepository : AuthRepository {
    // Đối tượng chính của Firebase Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Lấy thông tin user hiện tại đang đăng nhập
    override val currentUser: FirebaseUser?
        get() = auth.currentUser
    /**
     * Đăng nhập ẩn danh (Dành cho nút "Dùng thử không cần tài khoản")
     */
    override fun signInAnonymously(
        onSuccess: (UserProfile) -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. XÓA BỘ NHỚ ĐỆM CŨ (Để không bị tình trạng lấy lại phiên đăng nhập cũ)
        auth.signOut()
        // 2. Gửi yêu cầu mới lên Firebase
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                val user = result.user
                val profile = UserProfile(
                    id = user?.uid ?: "guest_id",
                    fullName = "Minh Anh",
                    email = "minhanh@gmail.com",
                    isGoogleLinked = false,
                    greetingName = "Minh Anh!"
                )
                onSuccess(profile)
            }
            .addOnFailureListener { exception ->
                // Trả về lỗi nếu Firebase từ chối hoặc mất kết nối
                onError(exception.localizedMessage ?: "Đăng nhập thất bại. Máy chủ từ chối kết nối!")
            }
    }
    /**
     * Đăng xuất khỏi Firebase
     */
    override fun signOut() {
        auth.signOut()
    }
    /**
     * Lấy thông tin người dùng đang đăng nhập
     */
    override fun getUserProfile(): UserProfile {
        val user = auth.currentUser
        return if (user != null) {
            UserProfile(
                id = user.uid,
                fullName = user.displayName ?: "Minh Anh",
                email = user.email ?: "minhanh@gmail.com",
                isGoogleLinked = user.email != null,
                greetingName = (user.displayName ?: "Minh Anh") + "!"
            )
        } else {
            UserProfile()
        }
    }
}