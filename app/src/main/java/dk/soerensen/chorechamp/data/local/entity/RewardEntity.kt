package dk.soerensen.chorechamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val cost: Int
)
