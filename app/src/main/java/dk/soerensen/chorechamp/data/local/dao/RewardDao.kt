package dk.soerensen.chorechamp.data.local.dao

import androidx.room.*
import dk.soerensen.chorechamp.data.local.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reward: RewardEntity): Long

    @Delete
    suspend fun delete(reward: RewardEntity)
}
