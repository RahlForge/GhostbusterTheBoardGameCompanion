package com.ghostbusters.companion.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import com.ghostbusters.companion.data.database.entities.TrappedGhostData
import com.ghostbusters.companion.data.repository.CharacterRepository
import com.ghostbusters.companion.data.repository.GameInstanceRepository
import com.ghostbusters.companion.domain.model.AbilityType
import com.ghostbusters.companion.domain.model.CharacterAbility
import com.ghostbusters.companion.domain.model.CharacterName
import com.ghostbusters.companion.domain.model.CharacterStats
import com.ghostbusters.companion.domain.model.Ghost
import com.ghostbusters.companion.domain.model.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CharacterSheetViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val gameInstanceRepository: GameInstanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Long = savedStateHandle.get<Long>("characterId") ?: 0L

    private val _character = MutableStateFlow<CharacterEntity?>(null)
    val character: StateFlow<CharacterEntity?> = _character.asStateFlow()

    private val _gameInstance = MutableStateFlow<GameInstanceEntity?>(null)
    val gameInstance: StateFlow<GameInstanceEntity?> = _gameInstance.asStateFlow()

    init {
        loadCharacter()
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            characterRepository.getCharacterById(characterId).collect { character ->
                _character.value = character
                character?.let {
                    loadGameInstance(it.gameInstanceId)
                }
            }
        }
    }

    private fun loadGameInstance(gameId: Long) {
        viewModelScope.launch {
            gameInstanceRepository.getGameInstanceById(gameId).collect { gameInstance ->
                _gameInstance.value = gameInstance
            }
        }
        // Load all characters in this game for team passive abilities
        viewModelScope.launch {
            characterRepository.getCharactersByGameInstance(gameId).collect { characters ->
                _allCharacters.value = characters
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

        updateCharacter(current.copy(
            trappedGhosts = updatedGhosts
        ))
    }

    fun getCharactersWithActiveStreams(): List<Pair<CharacterEntity, Int>> {
        val allChars = _allCharacters.value
        val current = _character.value

        return allChars
            .filter { it.id != current?.id } // Exclude current character
            .mapNotNull { char ->
                val activeStreams = (0..4).count { index ->
                    val bitMask = 1 shl index
                    (char.protonStreamsUsed and bitMask) != 0
                }
                if (activeStreams > 0) {
                    Pair(char, activeStreams)
                } else {
                    null
                }
            }
    }

    fun handleTrapIt(trapped: Boolean, assistingCharacterIds: List<Long>) {
        val current = _character.value ?: return

        // Count active proton streams for current character
        val myActiveStreams = (0..4).count { isProtonStreamUsed(it) }

        if (myActiveStreams > 0) {
            viewModelScope.launch {
                var totalStreams = myActiveStreams

                // Get all assisting characters from the cached list
                val allChars = _allCharacters.value
                val assistingCharacters = allChars.filter { it.id in assistingCharacterIds }

                // Process assisting characters
                for (assistChar in assistingCharacters) {
                    // Count their active streams
                    val assistStreams = (0..4).count { index ->
                        val bitMask = 1 shl index
                        (assistChar.protonStreamsUsed and bitMask) != 0
                    }

                    if (assistStreams > 0) {
                        totalStreams += assistStreams

                        // Grant XP to assisting character
                        val assistXp = (assistChar.xp + assistStreams).coerceIn(0, 30)
                        characterRepository.updateCharacter(
                            assistChar.copy(
                                xp = assistXp,
                                protonStreamsUsed = 0 // Deactivate their streams
                            )
                        )
                    }
                }

                // Grant XP to current character based on their streams
                val myNewXp = (current.xp + myActiveStreams).coerceIn(0, 30)

                // If ghost was trapped, add it with rating = total streams
                if (trapped) {
                    val ghostId = UUID.randomUUID().toString()
                    val ghost = TrappedGhostData(
                        ghostId = ghostId,
                        rating = totalStreams,
                        name = "Ghost (Rating $totalStreams)"
                    )
                    val updatedGhosts = current.trappedGhosts + ghost

                    updateCharacter(current.copy(
                        xp = myNewXp,
                        trappedGhosts = updatedGhosts,
                        protonStreamsUsed = 0 // Deactivate current character's streams
                    ))
                } else {
                    // Just grant XP and deactivate streams
                    updateCharacter(current.copy(
                        xp = myNewXp,
                        protonStreamsUsed = 0 // Deactivate current character's streams
                    ))
                }
            }
        }
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

    fun hasActionsAvailable(): Boolean {
        val maxActions = getMaxActions()
        for (i in 0 until maxActions) {
            if (!isActionUsed(i)) {
                return true
            }
        }
        return false
    }

    private val _allCharacters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val allCharacters: StateFlow<List<CharacterEntity>> = _allCharacters.asStateFlow()

    fun getCharacterStats(): CharacterStats {
        val current = _character.value ?: return CharacterAbility.getBaseStats(
            CharacterName.PETER_VENKMAN
        )

        val baseStats = CharacterAbility.getBaseStats(current.characterName)
        val allChars = _allCharacters.value

        // Apply level-based stat modifications
        val move: Int
        val drive: Int
        val los: Int

        // Check ALL characters in the game for team passive abilities
        // Venkman Level 5: +1 LoS for all Ghostbusters
        val hasVenkmanLevel5 = allChars.any {
            it.characterName == CharacterName.PETER_VENKMAN &&
            Level.fromXp(it.xp) >= Level.LEVEL_5
        }
        los = if (hasVenkmanLevel5) {
            baseStats.lineOfSight + 1
        } else {
            baseStats.lineOfSight
        }

        // Winston Level 5: +1 Move for all Ghostbusters
        val hasWinstonLevel5 = allChars.any {
            it.characterName == CharacterName.WINSTON_ZEDDEMORE &&
            Level.fromXp(it.xp) >= Level.LEVEL_5
        }
        if (hasWinstonLevel5) {
            move = baseStats.move + 1
            drive = baseStats.drive + 1
        } else {
            move = baseStats.move
            drive = baseStats.drive
        }

        return CharacterStats(
            characterName = current.characterName,
            move = move,
            drive = drive,
            lineOfSight = los
        )
    }

    fun useAbility(ability: CharacterAbility) {
        val current = _character.value ?: return

        when (ability.abilityType) {
            AbilityType.TAPPABLE -> {
                // Handle tappable abilities
                when (current.characterName) {
                    CharacterName.PETER_VENKMAN -> {
                        when (ability.level) {
                            Level.LEVEL_1,
                            Level.LEVEL_2 -> {
                                // Find first available action and toggle it to slime
                                val maxActions = getMaxActions()
                                for (i in 0 until maxActions) {
                                    if (!isActionUsed(i)) {
                                        toggleAction(i)
                                        break
                                    }
                                }
                                // Add XP for Level 1 ability
                                if (ability.level == Level.LEVEL_1) {
                                    addXp(1)
                                }
                            }
                            else -> {}
                        }
                    }

                    CharacterName.RAY_STANTZ -> {
                        when (ability.level) {
                            Level.LEVEL_1 -> {
                                // Ray Level 1: Gain 1 XP when removing slime from another
                                addXp(1)
                            }
                            Level.LEVEL_2 -> {
                                // Ray Level 2: Remove a slime from yourself
                                // Find first used action (slime) and toggle it back to action
                                val maxActions = getMaxActions()
                                for (i in 0 until maxActions) {
                                    if (isActionUsed(i)) {
                                        toggleAction(i)
                                        break
                                    }
                                }
                            }
                            else -> {}
                        }
                    }

                    else -> {}
                }
            }
            else -> {
                // Other ability types will be handled when implemented
            }
        }
    }

    fun depositGhosts(ghostIds: List<String>) {
        val current = _character.value ?: return
        val ghostsToDeposit = current.trappedGhosts.filter { it.ghostId in ghostIds }

        // Calculate total rating
        val totalRating = ghostsToDeposit.sumOf { it.rating }

        // Remove deposited ghosts
        val remainingGhosts = current.trappedGhosts.filter { it.ghostId !in ghostIds }

        // For Winston: gain XP based on total rating / 3
        if (current.characterName == CharacterName.WINSTON_ZEDDEMORE &&
            getCurrentLevel() >= Level.LEVEL_1) {
            val xpGained = totalRating / 3
            if (xpGained > 0) {
                val newXp = (current.xp + xpGained).coerceIn(0, 30)
                updateCharacter(current.copy(
                    trappedGhosts = remainingGhosts,
                    xp = newXp
                ))
                return
            }
        }

        updateCharacter(current.copy(trappedGhosts = remainingGhosts))
    }

    fun transferGhosts(ghostIds: List<String>) {
        val current = _character.value ?: return
        val ghostsToTransfer = current.trappedGhosts.filter { it.ghostId in ghostIds }

        // Remove ghosts from current character
        val remainingGhosts = current.trappedGhosts.filter { it.ghostId !in ghostIds }
        updateCharacter(current.copy(trappedGhosts = remainingGhosts))

        // Note: The actual transfer to another character will be handled by the screen
        // which will call receiveTransferredGhosts on the target character
    }

    fun transferGhostsToCharacter(ghostIds: List<String>, targetCharacterId: Long) {
        viewModelScope.launch {
            val current = _character.value ?: return@launch
            val ghostsToTransfer = current.trappedGhosts.filter { it.ghostId in ghostIds }

            // Remove ghosts from current character
            val remainingGhosts = current.trappedGhosts.filter { it.ghostId !in ghostIds }
            characterRepository.updateCharacter(current.copy(trappedGhosts = remainingGhosts))

            // Add ghosts to target character - use first() to get single value instead of collecting flow
            val targetChar = characterRepository.getCharacterById(targetCharacterId).first()
            if (targetChar != null) {
                val updatedGhosts = targetChar.trappedGhosts + ghostsToTransfer
                characterRepository.updateCharacter(targetChar.copy(trappedGhosts = updatedGhosts))
            }
        }
    }

    fun receiveTransferredGhosts(ghosts: List<TrappedGhostData>) {
        val current = _character.value ?: return
        val updatedGhosts = current.trappedGhosts + ghosts
        updateCharacter(current.copy(trappedGhosts = updatedGhosts))
    }

    private fun updateCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            characterRepository.updateCharacter(character)
        }
    }
}
