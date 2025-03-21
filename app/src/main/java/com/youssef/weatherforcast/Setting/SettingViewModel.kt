import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: Repo) : ViewModel() {
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedTemperature = MutableStateFlow("Celsius")
    val selectedTemperature: StateFlow<String> = _selectedTemperature

    private val _selectedLocation = MutableStateFlow("GPS")
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedWindSpeed = MutableStateFlow("Meter/sec")
    val selectedWindSpeed: StateFlow<String> = _selectedWindSpeed

    fun updateLanguage(language: String) {
        _selectedLanguage.value = language
        saveSettings()
    }

    fun updateTemperature(temp: String) {
        _selectedTemperature.value = temp
        saveSettings()
    }

    fun updateLocation(location: String) {
        _selectedLocation.value = location
        saveSettings()
    }

    fun updateWindSpeed(speed: String) {
        _selectedWindSpeed.value = speed
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            repo.updateSettings(
                _selectedLanguage.value,
                _selectedTemperature.value,
                _selectedLocation.value,
                _selectedWindSpeed.value
            )
        }
    }
}

class SettingsViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}