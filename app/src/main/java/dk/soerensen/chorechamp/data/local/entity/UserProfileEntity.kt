package dk.soerensen.chorechamp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val role: String,
    val profileImageUri: String? = null
)
