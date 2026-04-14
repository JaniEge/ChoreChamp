package dk.soerensen.chorechamp.data.local.dao

import androidx.room.*
import dk.soerensen.chorechamp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("""
        SELECT * FROM tasks
        WHERE (recurrence = 'NONE' AND assignedDate = :date)
           OR (recurrence = 'WEEKLY' AND dayOfWeek = :dayOfWeek)
    """)
    fun getTasksForDate(date: String, dayOfWeek: Int): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE selectedByChildId = :childId
          AND ((recurrence = 'NONE' AND assignedDate = :date)
            OR (recurrence = 'WEEKLY' AND dayOfWeek = :dayOfWeek))
    """)
    fun getTasksForChildOnDate(date: String, childId: Int, dayOfWeek: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING_APPROVAL'")
    fun getPendingApprovalTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE status = 'AVAILABLE' AND selectedByChildId IS NULL
          AND ((recurrence = 'NONE' AND assignedDate = :date)
            OR (recurrence = 'WEEKLY' AND dayOfWeek = :dayOfWeek))
    """)
    fun getAvailableTasksForDate(date: String, dayOfWeek: Int): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE selectedByChildId = :childId AND status = 'APPROVED'")
    fun getApprovedTasksForChild(childId: Int): Flow<List<TaskEntity>>
}
