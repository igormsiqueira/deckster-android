package com.igorapp.deckster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igorapp.deckster.model.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeListViewModel @Inject constructor(private val service: Deckster) : ViewModel() {

    val uiState: StateFlow<DecksterUiState> = decksterUiState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DecksterUiState.Loading
    )

    private fun loadFirstPage() {
        viewModelScope.launch {
            service.loadGames(INITIAL_PAGE, SIZE)
                .flowOn(Dispatchers.IO)
                .catch { exc ->
                    //screen state: error
                    println(exc)
                }
                .collect {
                    //screen state: data
                    println(it)
                }
        }

    }

    private fun decksterUiState(): Flow<DecksterUiState> {
        val gameStream: Flow<List<Game>> = service.loadGames(INITIAL_PAGE, SIZE)
        val choiceStream: Flow<List<Game>> = service.loadChoiceGames()

        return combine(gameStream, choiceStream, ::Pair).asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val (games, choiceGames) = result.data
                        DecksterUiState.Success(
                            games,
                            choiceGames
                        )
                    }
                    is Result.Error -> DecksterUiState.Error
                    is Result.Loading -> DecksterUiState.Loading
                }
            }
    }

    companion object {
        var INITIAL_PAGE = 0
        const val SIZE = 20
    }

}