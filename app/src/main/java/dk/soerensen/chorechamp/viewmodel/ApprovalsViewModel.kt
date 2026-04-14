package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.local.entity.UserProfileEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ApprovalsUiState(
    val pendingTasks: List<TaskEntity> = emptyList(),
    val childProfiles: Map<Int, UserProfileEntity> = emptyMap(),
    val isLoading: Boolean = true
)

class ApprovalsViewModel(
    private val repository: ChoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApprovalsUiState())
    val uiState: StateFlow<ApprovalsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getPendingApprovalTasks(),
                repository.getAllChildren()
            ) { tasks, children ->
                val childMap = children.associateBy { it.id }
                ApprovalsUiState(
                    pendingTasks = tasks,
                    childProfiles = childMap,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun approveTask(taskId: Int) {
        viewModelScope.launch {
            repository.approveTask(taskId)
        }
    }

    fun rejectTask(taskId: Int) {
        viewModelScope.launch {
            repository.rejectTask(taskId)
        }
    }

    class Factory(private val repository: ChoreRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ApprovalsViewModel(repository) as T
        }
    }
}
