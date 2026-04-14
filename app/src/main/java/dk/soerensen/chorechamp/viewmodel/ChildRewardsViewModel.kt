package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChildRewardsUiState(
    val approvedTasks: List<TaskEntity> = emptyList(),
    val rewards: List<RewardEntity> = emptyList(),
    val totalPoints: Int = 0,
    val isLoading: Boolean = true,
    val redeemMessage: String? = null
)

class ChildRewardsViewModel(
    private val repository: ChoreRepository,
    private val username: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChildRewardsUiState())
    val uiState: StateFlow<ChildRewardsUiState> = _uiState.asStateFlow()

    private var childId: Int = -1

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profile = repository.findOrCreateUser(username, "CHILD")
            childId = profile.id

            combine(
                repository.getApprovedTasksForChild(childId),
                repository.getAllRewards(),
                repository.getChildStats(childId)
            ) { approvedTasks, rewards, stats ->
                ChildRewardsUiState(
                    approvedTasks = approvedTasks,
                    rewards = rewards,
                    totalPoints = stats?.totalPoints ?: 0,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(redeemMessage = current.redeemMessage)
                }
            }
        }
    }

    fun redeemReward(reward: RewardEntity) {
        val currentPoints = _uiState.value.totalPoints
        if (currentPoints < reward.cost) return

        viewModelScope.launch {
            repository.redeemReward(childId, reward.cost)
            _uiState.update { it.copy(redeemMessage = "🎉 Redeemed: ${reward.title}!") }
        }
    }

    fun clearRedeemMessage() {
        _uiState.update { it.copy(redeemMessage = null) }
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChildRewardsViewModel(repository, username) as T
        }
    }
}
