package dk.soerensen.chorechamp.data.local.dao

import androidx.room.*
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE assignedDate = :date")
    fun getTasksForDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assignedDate = :date AND selectedByChildId = :childId")
    fun getTasksForChildOnDate(date: String, childId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING_APPROVAL'")
    fun getPendingApprovalTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assignedDate = :date AND (status = 'AVAILABLE' OR selectedByChildId IS NULL)")
    fun getAvailableTasksForDate(date: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): TaskEntity?
}
