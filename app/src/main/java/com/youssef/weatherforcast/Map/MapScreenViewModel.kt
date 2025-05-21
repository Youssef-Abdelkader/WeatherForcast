package com.youssef.weatherforcast.Favourite

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapScreenViewModel(private val repository: Repo) : ViewModel() {

    private var _insertState = MutableStateFlow<Boolean?>(null)
    val insertState = _insertState.asStateFlow()

    fun insertFavoriteLocation(lat: Double, lon: Double, name: String, context: Context) {
        viewModelScope.launch {
            try {
                // ✅ تشغيل `Geocoder` داخل `Dispatchers.IO`
                val countryCode = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    addresses?.firstOrNull()?.countryCode ?: "US" // ✅ إذا لم يُعثر على كود الدولة، اجعل "US" الافتراضي
                }

                val favoriteLocation = FavoriteLocation(
                    latitude = lat,
                    longitude = lon,
                    locationName = name,
                    countryCode = countryCode.lowercase() // ✅ حفظ كود الدولة بأحرف صغيرة
                )

                repository.insertFavorite(favoriteLocation)
                _insertState.value = true
                Log.d("MapScreenViewModel", "Location saved: $favoriteLocation")
            } catch (e: Exception) {
                _insertState.value = false
                Log.e("MapScreenViewModel", "Error saving location: ${e.message}")
            }
        }
    }
}

// ✅ الكلاس الخاص بإنشاء الـ ViewModel
class MapScreenViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapScreenViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
