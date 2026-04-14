package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.UserProfileEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoleUiState(
    val username: String = "",
    val selectedRole: String? = null,
    val isLoading: Boolean = false,
    val navigateTo: UserProfileEntity? = null
)

class RoleViewModel(private val repository: ChoreRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RoleUiState())
    val uiState: StateFlow<RoleUiState> = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onRoleSelected(role: String) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onContinueClicked() {
        val state = _uiState.value
        val username = state.username.trim()
        val role = state.selectedRole ?: return
        if (username.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            val user = repository.findOrCreateUser(username, role)
            _uiState.value = _uiState.value.copy(isLoading = false, navigateTo = user)
        }
    }

    fun onNavigated() {
        _uiState.value = _uiState.value.copy(navigateTo = null)
    }

    class Factory(private val repository: ChoreRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RoleViewModel(repository) as T
        }
    }
}
