package com.ghostbusters.companion.data.repository

import com.ghostbusters.companion.data.database.dao.CharacterDao
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository @Inject constructor(
    private val characterDao: CharacterDao
) {
    fun getCharactersByGameInstance(gameInstanceId: Long): Flow<List<CharacterEntity>> =
        characterDao.getCharactersByGameInstance(gameInstanceId)

    fun getCharacterById(id: Long): Flow<CharacterEntity?> =
        characterDao.getCharacterById(id)

    suspend fun createCharacter(character: CharacterEntity): Long =
        characterDao.insertCharacter(character)

    suspend fun createCharacters(characters: List<CharacterEntity>) =
        characterDao.insertCharacters(characters)

    suspend fun updateCharacter(character: CharacterEntity) =
        characterDao.updateCharacter(character)

    suspend fun deleteCharacter(character: CharacterEntity) =
        characterDao.deleteCharacter(character)

    suspend fun deleteCharactersByGameInstance(gameInstanceId: Long) =
        characterDao.deleteCharactersByGameInstance(gameInstanceId)
}

