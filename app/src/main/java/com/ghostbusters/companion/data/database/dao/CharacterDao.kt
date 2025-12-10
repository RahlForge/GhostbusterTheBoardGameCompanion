package com.ghostbusters.companion.data.database.dao

import androidx.room.*
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE gameInstanceId = :gameInstanceId")
    fun getCharactersByGameInstance(gameInstanceId: Long): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Long): Flow<CharacterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE gameInstanceId = :gameInstanceId")
    suspend fun deleteCharactersByGameInstance(gameInstanceId: Long)
}

