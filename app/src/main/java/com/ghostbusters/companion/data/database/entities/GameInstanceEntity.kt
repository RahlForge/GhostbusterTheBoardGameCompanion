package com.ghostbusters.companion.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ghostbusters.companion.domain.model.ExpansionType
import com.ghostbusters.companion.domain.model.GameType

@Entity(tableName = "game_instances")
data class GameInstanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gameType: GameType,
    val expansions: List<ExpansionType>,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

