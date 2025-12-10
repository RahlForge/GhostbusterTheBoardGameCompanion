package com.ghostbusters.companion.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ghostbusters.companion.domain.model.CharacterName

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = GameInstanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameInstanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("gameInstanceId")]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameInstanceId: Long,
    val characterName: CharacterName,
    val xp: Int = 0,
    val protonStreamsUsed: Int = 0,
    val actionsUsed: Int = 0,
    val ghostTrapDeployed: Boolean = false,
    val trappedGhosts: List<TrappedGhostData> = emptyList()
)

data class TrappedGhostData(
    val ghostId: String,
    val rating: Int,
    val name: String
)

