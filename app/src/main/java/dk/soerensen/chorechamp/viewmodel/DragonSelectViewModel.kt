package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DragonSelectUiState(
    val selectedDragonType: Int = 1,
    val isLoading: Boolean = false,
    val isDone: Boolean = false
)

class DragonSelectViewModel(
    private val repository: ChoreRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DragonSelectUiState())
    val uiState: StateFlow<DragonSelectUiState> = _uiState.asStateFlow()

    fun onDragonSelected(dragonType: Int) {
        _uiState.value = _uiState.value.copy(selectedDragonType = dragonType)
    }

    fun onConfirm() {
        val dragonType = _uiState.value.selectedDragonType
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val profile = repository.findOrCreateUser(username, "CHILD")
            repository.selectDragon(profile.id, dragonType)
            _uiState.value = _uiState.value.copy(isLoading = false, isDone = true)
        }
    }

    fun onNavigated() {
        _uiState.value = _uiState.value.copy(isDone = false)
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DragonSelectViewModel(repository, username) as T
        }
    }
}
