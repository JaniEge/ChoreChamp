package dk.soerensen.chorechamp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dk.soerensen.chorechamp.data.local.dao.*
import dk.soerensen.chorechamp.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        TaskEntity::class,
        RewardEntity::class,
        ChildStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChoreChampDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun taskDao(): TaskDao
    abstract fun rewardDao(): RewardDao
    abstract fun childStatsDao(): ChildStatsDao

    companion object {
        @Volatile
        private var INSTANCE: ChoreChampDatabase? = null

        fun getDatabase(context: Context): ChoreChampDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChoreChampDatabase::class.java,
                    "chorechamp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
