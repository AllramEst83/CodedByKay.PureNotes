package com.codedbykay.purenotes.db.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ToDo::class, ToDoGroup::class], version = 1)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun getTodoGroupDao(): ToDoGroupDao
    abstract fun getTodoDao(): ToDoDao

    companion object {
        const val NAME = "ToDo_DB"

        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getInstance(context: Context): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    NAME
                )
                    .addMigrations(MIGRATION_1)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration definition (if needed)
        val MIGRATION_1 = object : Migration(1, 1) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create ToDoGroup table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ToDoGroup (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                    """
                )

                // Create ToDo table with all required fields
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ToDo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT,
                        createdAt INTEGER NOT NULL,
                        done INTEGER NOT NULL DEFAULT 0,
                        groupId INTEGER DEFAULT NULL,
                        notificationTime INTEGER,
                        FOREIGN KEY (groupId) REFERENCES ToDoGroup(id) ON DELETE CASCADE
                        notificationRequestCode INTEGER DEFAULT NULL,
                        notificationAction TEXT DEFAULT NULL,
                        notificationDataUri TEXT DEFAULT NULL                        
                    )
                    """
                )

                // Create an index on groupId for optimized queries
                database.execSQL("CREATE INDEX IF NOT EXISTS index_ToDo_groupId ON ToDo(groupId)")

                // Enable foreign keys
                database.execSQL("PRAGMA foreign_keys=on;")
            }
        }
    }
}
