package com.ghostbusters.companion.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghostbusters.companion.data.database.dao.CharacterDao
import com.ghostbusters.companion.data.database.dao.GameInstanceDao
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity

@Database(
    entities = [
        GameInstanceEntity::class,
        CharacterEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameInstanceDao(): GameInstanceDao
    abstract fun characterDao(): CharacterDao
}

