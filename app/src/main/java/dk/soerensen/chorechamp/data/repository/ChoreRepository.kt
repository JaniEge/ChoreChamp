package dk.soerensen.chorechamp.data.repository

import dk.soerensen.chorechamp.data.local.dao.*
import dk.soerensen.chorechamp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class ChoreRepository(
    private val userProfileDao: UserProfileDao,
    private val taskDao: TaskDao,
    private val rewardDao: RewardDao,
    private val childStatsDao: ChildStatsDao
) {
    suspend fun findOrCreateUser(username: String, role: String): UserProfileEntity {
        val existing = userProfileDao.findByUsername(username)
        if (existing != null) return existing

        val id = userProfileDao.insert(UserProfileEntity(username = username, role = role))
        if (role == "CHILD") {
            childStatsDao.insert(ChildStatsEntity(childId = id.toInt()))
        }
        return userProfileDao.findByUsername(username)!!
    }

    suspend fun updateProfileImage(userId: Int, uri: String) {
        val user = userProfileDao.findById(userId) ?: return
        userProfileDao.update(user.copy(profileImageUri = uri))
    }

    suspend fun getUserById(id: Int): UserProfileEntity? = userProfileDao.findById(id)

    fun getAllChildren(): Flow<List<UserProfileEntity>> = userProfileDao.getAllChildren()

    fun getTasksForDate(date: String, dayOfWeek: Int): Flow<List<TaskEntity>> =
        taskDao.getTasksForDate(date, dayOfWeek)

    fun getTasksForChildOnDate(date: String, childId: Int, dayOfWeek: Int): Flow<List<TaskEntity>> =
        taskDao.getTasksForChildOnDate(date, childId, dayOfWeek)

    fun getPendingApprovalTasks(): Flow<List<TaskEntity>> = taskDao.getPendingApprovalTasks()

    fun getAvailableTasksForDate(date: String, dayOfWeek: Int): Flow<List<TaskEntity>> =
        taskDao.getAvailableTasksForDate(date, dayOfWeek)

    suspend fun addTask(task: TaskEntity) = taskDao.insert(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.update(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.delete(task)

    suspend fun findTaskById(id: Int): TaskEntity? = taskDao.findById(id)

    suspend fun selectTask(taskId: Int, childId: Int) {
        val task = taskDao.findById(taskId) ?: return
        taskDao.update(task.copy(selectedByChildId = childId, status = "SELECTED"))
    }

    suspend fun requestCompletion(taskId: Int) {
        val task = taskDao.findById(taskId) ?: return
        taskDao.update(task.copy(status = "PENDING_APPROVAL"))
    }

    suspend fun approveTask(taskId: Int) {
        val task = taskDao.findById(taskId) ?: return
        taskDao.update(task.copy(status = "APPROVED"))
        val childId = task.selectedByChildId ?: return
        val stats = childStatsDao.getStatsOnce(childId)
            ?: ChildStatsEntity(childId = childId)
        val newPoints = stats.totalPoints + task.points
        childStatsDao.update(stats.copy(totalPoints = newPoints, dragonLevel = calculateDragonLevel(newPoints)))
    }

    suspend fun rejectTask(taskId: Int) {
        val task = taskDao.findById(taskId) ?: return
        taskDao.update(task.copy(status = "SELECTED"))
    }

    suspend fun deselectTask(taskId: Int) {
        val task = taskDao.findById(taskId) ?: return
        taskDao.update(task.copy(selectedByChildId = null, status = "AVAILABLE"))
    }

    fun getAllRewards(): Flow<List<RewardEntity>> = rewardDao.getAllRewards()

    suspend fun addReward(reward: RewardEntity) = rewardDao.insert(reward)

    suspend fun deleteReward(reward: RewardEntity) = rewardDao.delete(reward)

    fun getChildStats(childId: Int): Flow<ChildStatsEntity?> = childStatsDao.getStats(childId)

    fun getApprovedTasksForChild(childId: Int): Flow<List<TaskEntity>> =
        taskDao.getApprovedTasksForChild(childId)

    suspend fun redeemReward(childId: Int, rewardCost: Int) {
        val stats = childStatsDao.getStatsOnce(childId) ?: return
        val newPoints = (stats.totalPoints - rewardCost).coerceAtLeast(0)
        childStatsDao.update(stats.copy(totalPoints = newPoints, dragonLevel = calculateDragonLevel(newPoints)))
    }

    private fun calculateDragonLevel(points: Int): Int = when {
        points >= 100 -> 3
        points >= 50 -> 2
        else -> 1
    }
}
