package com.example.bai01.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bai01.data.model.Product
import com.example.bai01.data.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val product: Product) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class ProductViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        fetchProduct()
    }

    fun fetchProduct() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val jsonElement = RetrofitClient.apiService.getProductData()
                val gson = Gson()
                val product: Product? = when {
                    // Nếu API trả về một mảng danh sách [ {...}, {...} ]
                    jsonElement.isJsonArray -> {
                        val type = object : TypeToken<List<Product>>() {}.type
                        val list: List<Product> = gson.fromJson(jsonElement, type)
                        list.firstOrNull()
                    }
                    // Nếu API trả về 1 object { ... }
                    jsonElement.isJsonObject -> {
                        val jsonObject = jsonElement.asJsonObject
                        // Trường hợp mock API bọc trong data: { ... } hoặc data: [ ... ]
                        if (jsonObject.has("data")) {
                            val dataElement = jsonObject.get("data")
                            if (dataElement.isJsonArray) {
                                val type = object : TypeToken<List<Product>>() {}.type
                                val list: List<Product> = gson.fromJson(dataElement, type)
                                list.firstOrNull()
                            } else {
                                gson.fromJson(dataElement, Product::class.java)
                            }
                        } else {
                            gson.fromJson(jsonElement, Product::class.java)
                        }
                    }
                    else -> null
                }

                if (product != null) {
                    _uiState.value = ProductUiState.Success(product)
                } else {
                    _uiState.value = ProductUiState.Error("Không tìm thấy thông tin sản phẩm từ API.")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Lỗi kết nối API: ${e.localizedMessage ?: "Vui lòng kiểm tra lại mạng"}")
            }
        }
    }
}

