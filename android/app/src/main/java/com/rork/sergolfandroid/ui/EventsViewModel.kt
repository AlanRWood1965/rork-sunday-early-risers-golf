package com.rork.sergolfandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rork.sergolfandroid.data.BookwhenService
import com.rork.sergolfandroid.data.EventType
import com.rork.sergolfandroid.data.GolfEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventsUiState(
    val events: List<GolfEvent> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val specialEvents: List<GolfEvent> get() = events.filter { it.type == EventType.SPECIAL }
}

class EventsViewModel : ViewModel() {

    private val service = BookwhenService()

    private val _uiState = MutableStateFlow(EventsUiState(isLoading = true))
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        load(initial = true)
    }

    fun load(initial: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = initial,
                isRefreshing = !initial,
                errorMessage = null,
            )
            try {
                val events = service.fetchEvents()
                _uiState.value = EventsUiState(events = events)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = e.message ?: "Couldn't load events",
                )
            }
        }
    }

    fun refresh() = load(initial = false)

    fun eventById(id: String?): GolfEvent? =
        if (id == null) null else _uiState.value.events.find { it.id == id }
}
