package dk.soerensen.chorechamp.data.local.dao

import androidx.room.*
import dk.soerensen.chorechamp.data.local.entity.ChildStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildStatsDao {
    @Query("SELECT * FROM child_stats WHERE childId = :childId LIMIT 1")
    fun getStats(childId: Int): Flow<ChildStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: ChildStatsEntity)

    @Update
    suspend fun update(stats: ChildStatsEntity)

    @Query("SELECT * FROM child_stats WHERE childId = :childId LIMIT 1")
    suspend fun getStatsOnce(childId: Int): ChildStatsEntity?
}
