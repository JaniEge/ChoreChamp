package dk.soerensen.chorechamp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import dk.soerensen.chorechamp.data.repository.ChoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ParentRewardsUiState(
    val rewards: List<RewardEntity> = emptyList(),
    val isLoading: Boolean = true
)

class ParentRewardsViewModel(
    private val repository: ChoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentRewardsUiState())
    val uiState: StateFlow<ParentRewardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllRewards().collect { rewards ->
                _uiState.value = ParentRewardsUiState(rewards = rewards, isLoading = false)
            }
        }
    }

    fun addReward(title: String, cost: Int) {
        viewModelScope.launch {
            repository.addReward(RewardEntity(title = title, cost = cost))
        }
    }

    fun deleteReward(reward: RewardEntity) {
        viewModelScope.launch {
            repository.deleteReward(reward)
        }
    }

    class Factory(
        private val repository: ChoreRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ParentRewardsViewModel(repository) as T
        }
    }
}
