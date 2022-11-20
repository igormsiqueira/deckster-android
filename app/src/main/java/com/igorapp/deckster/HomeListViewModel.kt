package com.igorapp.deckster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeListViewModel @Inject constructor(private val service: Deckster) : ViewModel() {

    init {
        loadFirstPage()
    }

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

    companion object {
        const val INITIAL_PAGE = 0
        const val SIZE = 20
    }

}