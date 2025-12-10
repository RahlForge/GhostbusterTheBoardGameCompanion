package com.ghostbusters.companion.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import com.ghostbusters.companion.data.database.entities.TrappedGhostData
import com.ghostbusters.companion.data.repository.CharacterRepository
import com.ghostbusters.companion.domain.model.Ghost
import com.ghostbusters.companion.domain.model.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CharacterSheetViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Long = savedStateHandle.get<Long>("characterId") ?: 0L

    private val _character = MutableStateFlow<CharacterEntity?>(null)
    val character: StateFlow<CharacterEntity?> = _character.asStateFlow()

    init {
        loadCharacter()
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            characterRepository.getCharacterById(characterId).collect { character ->
                _character.value = character
            }
        }
    }

    fun addXp(amount: Int) {
        val current = _character.value ?: return
        val newXp = (current.xp + amount).coerceIn(0, 30)
        updateCharacter(current.copy(xp = newXp))
    }

    fun setXp(xp: Int) {
        val current = _character.value ?: return
        val newXp = xp.coerceIn(0, 30)
        updateCharacter(current.copy(xp = newXp))
    }

    fun toggleProtonStream(index: Int) {
        val current = _character.value ?: return
        if (index < 0 || index >= 5) return
        
        val usedStreams = current.protonStreamsUsed
        val bitMask = 1 shl index
        val newUsedStreams = if ((usedStreams and bitMask) != 0) {
            usedStreams and bitMask.inv() // Turn off
        } else {
            usedStreams or bitMask // Turn on
        }
        
        updateCharacter(current.copy(protonStreamsUsed = newUsedStreams))
    }

    fun isProtonStreamUsed(index: Int): Boolean {
        val current = _character.value ?: return false
        if (index < 0 || index >= 5) return false
        val bitMask = 1 shl index
        return (current.protonStreamsUsed and bitMask) != 0
    }

    fun toggleAction(index: Int) {
        val current = _character.value ?: return
        val maxActions = if (getCurrentLevel() >= Level.LEVEL_3) 3 else 2
        if (index < 0 || index >= maxActions) return
        
        val usedActions = current.actionsUsed
        val bitMask = 1 shl index
        val newUsedActions = if ((usedActions and bitMask) != 0) {
            usedActions and bitMask.inv() // Turn off (Action -> Slime)
        } else {
            usedActions or bitMask // Turn on (Slime -> Action)
        }
        
        updateCharacter(current.copy(actionsUsed = newUsedActions))
    }

    fun isActionUsed(index: Int): Boolean {
        val current = _character.value ?: return false
        val maxActions = if (getCurrentLevel() >= Level.LEVEL_3) 3 else 2
        if (index < 0 || index >= maxActions) return false
        val bitMask = 1 shl index
        return (current.actionsUsed and bitMask) != 0
    }

    fun trapGhost(rating: Int) {
        val current = _character.value ?: return
        val ghostId = UUID.randomUUID().toString()
        val ghost = TrappedGhostData(
            ghostId = ghostId,
            rating = rating,
            name = "Ghost (Rating $rating)"
        )
        
        val updatedGhosts = current.trappedGhosts + ghost
        val newXp = (current.xp + rating).coerceIn(0, 30)
        
        updateCharacter(current.copy(
            trappedGhosts = updatedGhosts,
            xp = newXp
        ))
    }

    fun transferGhost(ghost: Ghost) {
        val current = _character.value ?: return
        val ghostData = TrappedGhostData(
            ghostId = ghost.id,
            rating = ghost.rating,
            name = ghost.name
        )
        
        val updatedGhosts = current.trappedGhosts + ghostData
        updateCharacter(current.copy(trappedGhosts = updatedGhosts))
    }

    fun removeGhost(ghostId: String) {
        val current = _character.value ?: return
        val updatedGhosts = current.trappedGhosts.filter { it.ghostId != ghostId }
        updateCharacter(current.copy(trappedGhosts = updatedGhosts))
    }

    fun toggleGhostTrapDeployed() {
        val current = _character.value ?: return
        updateCharacter(current.copy(ghostTrapDeployed = !current.ghostTrapDeployed))
    }

    fun getCurrentLevel(): Level {
        val current = _character.value ?: return Level.LEVEL_1
        return Level.fromXp(current.xp)
    }

    fun getMaxActions(): Int {
        return if (getCurrentLevel() >= Level.LEVEL_3) 3 else 2
    }

    private fun updateCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            characterRepository.updateCharacter(character)
        }
    }
}

