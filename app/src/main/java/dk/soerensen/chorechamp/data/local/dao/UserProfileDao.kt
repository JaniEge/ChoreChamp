package dk.soerensen.chorechamp.data.local.dao

import androidx.room.*
import dk.soerensen.chorechamp.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE role = 'CHILD'")
    fun getAllChildren(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity): Long

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UserProfileEntity?
}
