package dk.soerensen.chorechamp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dk.soerensen.chorechamp.data.local.dao.*
import dk.soerensen.chorechamp.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        TaskEntity::class,
        RewardEntity::class,
        ChildStatsEntity::class
    ],
    version = 3,
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE child_stats ADD COLUMN dragonType INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): ChoreChampDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChoreChampDatabase::class.java,
                    "chorechamp_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
