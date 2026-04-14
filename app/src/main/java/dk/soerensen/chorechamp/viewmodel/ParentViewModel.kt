package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.local.entity.UserProfileEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ParentUiState(
    val todayTasks: List<TaskEntity> = emptyList(),
    val pendingCount: Int = 0,
    val childProfiles: Map<Int, UserProfileEntity> = emptyMap(),
    val isLoading: Boolean = true
)

class ParentViewModel(
    private val repository: ChoreRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentUiState())
    val uiState: StateFlow<ParentUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()

            combine(
                repository.getTasksForDate(today),
                repository.getPendingApprovalTasks(),
                repository.getAllChildren()
            ) { todayTasks, pendingTasks, children ->
                val childMap = children.associateBy { it.id }
                ParentUiState(
                    todayTasks = todayTasks,
                    pendingCount = pendingTasks.size,
                    childProfiles = childMap,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ParentViewModel(repository, username) as T
        }
    }
}
