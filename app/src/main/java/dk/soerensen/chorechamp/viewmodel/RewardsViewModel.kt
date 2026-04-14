package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RewardsUiState(
    val rewards: List<RewardEntity> = emptyList(),
    val childPoints: Int = 0,
    val isLoading: Boolean = true
)

class RewardsViewModel(
    private val repository: ChoreRepository,
    private val username: String,
    private val isParent: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    private var userId: Int = -1

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val role = if (isParent) "PARENT" else "CHILD"
            val profile = repository.findOrCreateUser(username, role)
            userId = profile.id

            if (isParent) {
                repository.getAllRewards().collect { rewards ->
                    _uiState.value = RewardsUiState(rewards = rewards, isLoading = false)
                }
            } else {
                combine(
                    repository.getAllRewards(),
                    repository.getChildStats(userId)
                ) { rewards, stats ->
                    RewardsUiState(
                        rewards = rewards,
                        childPoints = stats?.totalPoints ?: 0,
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }

    fun addReward(title: String, cost: Int) {
        viewModelScope.launch {
            repository.addReward(RewardEntity(title = title, cost = cost))
        }
    }

    class Factory(
        private val repository: ChoreRepository,
        private val username: String,
        private val isParent: Boolean
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RewardsViewModel(repository, username, isParent) as T
        }
    }
}
