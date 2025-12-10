package com.ghostbusters.companion.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import com.ghostbusters.companion.data.repository.CharacterRepository
import com.ghostbusters.companion.data.repository.GameInstanceRepository
import com.ghostbusters.companion.domain.model.CharacterName
import com.ghostbusters.companion.domain.model.ExpansionType
import com.ghostbusters.companion.domain.model.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameSetupState(
    val gameName: String = "",
    val gameType: GameType = GameType.GHOSTBUSTERS,
    val selectedExpansions: Set<ExpansionType> = emptySet(),
    val selectedCharacters: Set<CharacterName> = emptySet(),
    val isCreating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GameSetupViewModel @Inject constructor(
    private val gameInstanceRepository: GameInstanceRepository,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameSetupState())
    val state: StateFlow<GameSetupState> = _state.asStateFlow()

    fun updateGameName(name: String) {
        _state.value = _state.value.copy(gameName = name)
    }

    fun selectGameType(gameType: GameType) {
        _state.value = _state.value.copy(
            gameType = gameType,
            selectedExpansions = emptySet()
        )
    }

    fun toggleExpansion(expansion: ExpansionType) {
        val currentExpansions = _state.value.selectedExpansions
        _state.value = _state.value.copy(
            selectedExpansions = if (currentExpansions.contains(expansion)) {
                currentExpansions - expansion
            } else {
                currentExpansions + expansion
            }
        )
    }

    fun toggleCharacter(character: CharacterName) {
        val currentCharacters = _state.value.selectedCharacters
        if (currentCharacters.size >= 4 && !currentCharacters.contains(character)) {
            return // Max 4 characters
        }
        _state.value = _state.value.copy(
            selectedCharacters = if (currentCharacters.contains(character)) {
                currentCharacters - character
            } else {
                currentCharacters + character
            }
        )
    }

    fun createGame(onGameCreated: (Long) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCreating = true, error = null)

            try {
                // Validate
                if (_state.value.gameName.isBlank()) {
                    _state.value = _state.value.copy(
                        isCreating = false,
                        error = "Please enter a game name"
                    )
                    return@launch
                }

                if (_state.value.selectedCharacters.isEmpty()) {
                    _state.value = _state.value.copy(
                        isCreating = false,
                        error = "Please select at least one character"
                    )
                    return@launch
                }

                // Create game instance
                val gameInstance = GameInstanceEntity(
                    name = _state.value.gameName,
                    gameType = _state.value.gameType,
                    expansions = _state.value.selectedExpansions.toList()
                )

                val gameId = gameInstanceRepository.createGameInstance(gameInstance)

                // Create characters
                val characters = _state.value.selectedCharacters.map { characterName ->
                    CharacterEntity(
                        gameInstanceId = gameId,
                        characterName = characterName,
                        xp = 0
                    )
                }

                characterRepository.createCharacters(characters)

                // Small delay to ensure database transaction completes
                kotlinx.coroutines.delay(100)

                _state.value = _state.value.copy(isCreating = false)
                onGameCreated(gameId)

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isCreating = false,
                    error = "Failed to create game: ${e.javaClass.simpleName} - ${e.message}"
                )
            }
        }
    }

    fun getAvailableCharacters(): List<CharacterName> {
        val baseCharacters = listOf(
            CharacterName.PETER_VENKMAN,
            CharacterName.EGON_SPENGLER,
            CharacterName.WINSTON_ZEDDEMORE,
            CharacterName.RAY_STANTZ
        )

        if (_state.value.gameType != GameType.GHOSTBUSTERS_II) {
            return baseCharacters
        }

        val expansionCharacters = mutableListOf<CharacterName>()
        if (_state.value.selectedExpansions.contains(ExpansionType.PLAZM_PHENOMENON)) {
            expansionCharacters.add(CharacterName.LOUIS_TULLY)
        }
        if (_state.value.selectedExpansions.contains(ExpansionType.SEA_FRIGHT)) {
            expansionCharacters.add(CharacterName.SLIMER)
        }

        return baseCharacters + expansionCharacters
    }
}

