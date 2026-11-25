package com.example.bai02.data.model

/**
 * Enum đại diện cho các bộ lọc trên màn hình Danh sách (OOP - Type-safe Enum).
 * Giúp tránh việc sử dụng String tự do dễ sai chính tả (ví dụ: "Tat ca", "all", "ALL").
 */
enum class TaskFilterType(val label: String) {
    ALL("Tất cả"),
    STUDY("Học tập"),
    LIFE("Cuộc sống")
}

