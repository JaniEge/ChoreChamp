package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.ChildStatsEntity
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.local.entity.UserProfileEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ChildUiState(
    val profile: UserProfileEntity? = null,
    val stats: ChildStatsEntity? = null,
    val todayTasks: List<TaskEntity> = emptyList(),
    val dragonLabel: String = "Dragon Egg 🥚",
    val isLoading: Boolean = true
)

class ChildViewModel(
    private val repository: ChoreRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChildUiState())
    val uiState: StateFlow<ChildUiState> = _uiState.asStateFlow()

    private var childId: Int = -1

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profile = repository.findOrCreateUser(username, "CHILD")
            childId = profile.id
            val today = LocalDate.now().toString()
            val dayOfWeek = LocalDate.now().dayOfWeek.value

            combine(
                repository.getTasksForChildOnDate(today, childId, dayOfWeek),
                repository.getChildStats(childId)
            ) { tasks, stats ->
                val points = stats?.totalPoints ?: 0
                val dragonLabel = when {
                    points >= 100 -> "Dragon Level 3 🐲"
                    points >= 50 -> "Young Dragon 🐉"
                    else -> "Dragon Egg 🥚"
                }
                ChildUiState(
                    profile = profile,
                    stats = stats,
                    todayTasks = tasks,
                    dragonLabel = dragonLabel,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun requestCompletion(taskId: Int) {
        viewModelScope.launch {
            repository.requestCompletion(taskId)
        }
    }

    fun deselectTask(taskId: Int) {
        viewModelScope.launch {
            repository.deselectTask(taskId)
        }
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChildViewModel(repository, username) as T
        }
    }
}
