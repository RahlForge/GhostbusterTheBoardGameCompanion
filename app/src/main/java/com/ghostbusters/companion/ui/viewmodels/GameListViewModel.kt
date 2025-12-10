package com.ghostbusters.companion.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostbusters.companion.data.database.entities.GameInstanceEntity
import com.ghostbusters.companion.data.repository.GameInstanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameListViewModel @Inject constructor(
    private val gameInstanceRepository: GameInstanceRepository
) : ViewModel() {

    val gameInstances: StateFlow<List<GameInstanceEntity>> =
        gameInstanceRepository.getAllGameInstances()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun deleteGameInstance(gameInstance: GameInstanceEntity) {
        viewModelScope.launch {
            gameInstanceRepository.deleteGameInstance(gameInstance)
        }
    }

    fun deleteGameInstanceById(id: Long) {
        viewModelScope.launch {
            gameInstanceRepository.deleteGameInstanceById(id)
        }
    }
}

