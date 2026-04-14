package dk.soerensen.chorechamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val points: Int,
    val assignedDate: String,
    val selectedByChildId: Int? = null,
    val status: String = "AVAILABLE",
    val createdByParentId: Int = 0
)
