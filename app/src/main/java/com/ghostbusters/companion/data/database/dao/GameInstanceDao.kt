package com.ghostbusters.companion.data.database.dao

import androidx.room.*
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameInstanceDao {
    @Query("SELECT * FROM game_instances ORDER BY lastModified DESC")
    fun getAllGameInstances(): Flow<List<GameInstanceEntity>>

    @Query("SELECT * FROM game_instances WHERE id = :id")
    fun getGameInstanceById(id: Long): Flow<GameInstanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameInstance(gameInstance: GameInstanceEntity): Long

    @Update
    suspend fun updateGameInstance(gameInstance: GameInstanceEntity)

    @Delete
    suspend fun deleteGameInstance(gameInstance: GameInstanceEntity)

    @Query("DELETE FROM game_instances WHERE id = :id")
    suspend fun deleteGameInstanceById(id: Long)
}

