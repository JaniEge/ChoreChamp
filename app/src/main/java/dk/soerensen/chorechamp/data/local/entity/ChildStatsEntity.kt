package dk.soerensen.chorechamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_stats")
data class ChildStatsEntity(
    @PrimaryKey val childId: Int,
    val totalPoints: Int = 0,
    val dragonLevel: Int = 0
)
