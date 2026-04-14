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

data class AddTaskUiState(
    val title: String = "",
    val points: String = "",
    val selectedDayOffset: Int = 0,
    val selectedChildId: Int? = null,
    val children: List<UserProfileEntity> = emptyList(),
    val isSaved: Boolean = false,
    val recurrence: String = "NONE",
    val selectedDayOfWeek: Int = 1
)

class AddTaskViewModel(
    private val repository: ChoreRepository,
    private val parentUsername: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    private var parentId: Int = 0

    init {
        loadChildren()
    }

    private fun loadChildren() {
        viewModelScope.launch {
            val profile = repository.findOrCreateUser(parentUsername, "PARENT")
            parentId = profile.id
            repository.getAllChildren().collect { children ->
                _uiState.value = _uiState.value.copy(children = children)
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onPointsChange(points: String) {
        _uiState.value = _uiState.value.copy(points = points)
    }

    fun onDayOffsetChange(offset: Int) {
        _uiState.value = _uiState.value.copy(selectedDayOffset = offset)
    }

    fun onChildSelected(childId: Int?) {
        _uiState.value = _uiState.value.copy(selectedChildId = childId)
    }

    fun onRecurrenceChange(recurrence: String) {
        _uiState.value = _uiState.value.copy(recurrence = recurrence)
    }

    fun onDayOfWeekChange(dayOfWeek: Int) {
        _uiState.value = _uiState.value.copy(selectedDayOfWeek = dayOfWeek)
    }

    fun saveTask() {
        val state = _uiState.value
        val title = state.title.trim()
        val points = state.points.trim().toIntOrNull() ?: return
        if (title.isEmpty()) return

        val assignedDate = if (state.recurrence == "NONE") {
            LocalDate.now().plusDays(state.selectedDayOffset.toLong()).toString()
        } else {
            LocalDate.now().toString()
        }

        val dayOfWeek = if (state.recurrence == "WEEKLY") state.selectedDayOfWeek else null

        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                points = points,
                assignedDate = assignedDate,
                selectedByChildId = state.selectedChildId,
                status = if (state.selectedChildId != null) "SELECTED" else "AVAILABLE",
                createdByParentId = parentId,
                recurrence = state.recurrence,
                dayOfWeek = dayOfWeek
            )
            repository.addTask(task)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    fun onSavedHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    class Factory(
        private val repository: ChoreRepository,
        private val parentUsername: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddTaskViewModel(repository, parentUsername) as T
        }
    }
}
