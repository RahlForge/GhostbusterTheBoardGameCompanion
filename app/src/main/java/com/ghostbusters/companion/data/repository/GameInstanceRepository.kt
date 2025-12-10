package com.ghostbusters.companion.data.repository

import com.ghostbusters.companion.data.database.dao.GameInstanceDao
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameInstanceRepository @Inject constructor(
    private val gameInstanceDao: GameInstanceDao
) {
    fun getAllGameInstances(): Flow<List<GameInstanceEntity>> =
        gameInstanceDao.getAllGameInstances()

    fun getGameInstanceById(id: Long): Flow<GameInstanceEntity?> =
        gameInstanceDao.getGameInstanceById(id)

    suspend fun createGameInstance(gameInstance: GameInstanceEntity): Long =
        gameInstanceDao.insertGameInstance(gameInstance)

    suspend fun updateGameInstance(gameInstance: GameInstanceEntity) =
        gameInstanceDao.updateGameInstance(gameInstance.copy(lastModified = System.currentTimeMillis()))

    suspend fun deleteGameInstance(gameInstance: GameInstanceEntity) =
        gameInstanceDao.deleteGameInstance(gameInstance)

    suspend fun deleteGameInstanceById(id: Long) =
        gameInstanceDao.deleteGameInstanceById(id)
}

