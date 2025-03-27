import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val repo: Repo) : ViewModel() {
    private val _selectedLanguage = MutableStateFlow(repo.getSetting("language", "English"))
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedTemperature = MutableStateFlow(repo.getSetting("temperature", "Celsius"))
    val selectedTemperature: StateFlow<String> = _selectedTemperature

    private val _selectedLocation = MutableStateFlow(repo.getSetting("location", "GPS"))
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedWindSpeed = MutableStateFlow(repo.getSetting("windSpeed", "Meter/sec"))
    val selectedWindSpeed: StateFlow<String> = _selectedWindSpeed

    private val _settingsUpdated = MutableStateFlow(false)
    var settingsUpdated: StateFlow<Boolean> = _settingsUpdated.asStateFlow()

    fun updateLanguage(language: String) {
        _selectedLanguage.value = language
        repo.saveSetting("language", language)
        notifySettingsChanged()
    }

    fun updateTemperature(temp: String) {
        _selectedTemperature.value = temp
        repo.saveSetting("temperature", temp)
        notifySettingsChanged()
    }

    fun updateLocation(location: String) {
        _selectedLocation.value = location
        repo.saveSetting("location", location)
        notifySettingsChanged()
    }

    fun updateWindSpeed(speed: String) {
        _selectedWindSpeed.value = speed
        repo.saveSetting("windSpeed", speed)
        notifySettingsChanged()
    }

    fun notifySettingsChanged(updated: Boolean = false) {
        _settingsUpdated.value = updated
    }
    // In your settings screen/viewmodel:
    fun onLanguageSelected(languageCode: String) { // e.g., "ar" or "en"
        repo.saveSetting("language", languageCode)
        // Trigger reload in HomeViewModel
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