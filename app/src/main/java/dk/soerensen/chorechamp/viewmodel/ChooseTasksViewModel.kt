package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ChooseTasksUiState(
    val availableTasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

class ChooseTasksViewModel(
    private val repository: ChoreRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChooseTasksUiState())
    val uiState: StateFlow<ChooseTasksUiState> = _uiState.asStateFlow()

    private var childId: Int = -1

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profile = repository.findOrCreateUser(username, "CHILD")
            childId = profile.id
            val today = LocalDate.now().toString()

            repository.getAvailableTasksForDate(today).collect { tasks ->
                _uiState.value = ChooseTasksUiState(availableTasks = tasks, isLoading = false)
            }
        }
    }

    fun selectTask(taskId: Int) {
        viewModelScope.launch {
            repository.selectTask(taskId, childId)
        }
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChooseTasksViewModel(repository, username) as T
        }
    }
}
