package com.ghostbusters.companion.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbusters.companion.data.database.entities.CharacterEntity
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import com.ghostbusters.companion.data.repository.CharacterRepository
import com.ghostbusters.companion.data.repository.GameInstanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameDetailState(
    val gameInstance: GameInstanceEntity? = null,
    val characters: List<CharacterEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameInstanceRepository: GameInstanceRepository,
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gameId: Long = savedStateHandle.get<Long>("gameId") ?: 0L

    private val _state = MutableStateFlow(GameDetailState())
    val state: StateFlow<GameDetailState> = _state.asStateFlow()

    init {
        loadGameData()
    }

    private fun loadGameData() {
        viewModelScope.launch {
            try {
                // Collect game instance updates
                launch {
                    gameInstanceRepository.getGameInstanceById(gameId).collect { gameInstance ->
                        _state.value = _state.value.copy(
                            gameInstance = gameInstance,
                            isLoading = false
                        )
                    }
                }

                // Collect character updates
                launch {
                    characterRepository.getCharactersByGameInstance(gameId).collect { characters ->
                        _state.value = _state.value.copy(characters = characters)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the full stack trace
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load game: ${e.javaClass.simpleName} - ${e.message}"
                )
            }
        }
    }
}

