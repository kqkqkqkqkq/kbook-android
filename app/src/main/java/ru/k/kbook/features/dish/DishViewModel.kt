package ru.k.kbook.features.dish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.DeleteDishRequestDto
import ru.k.kbook.api.grpc.request.ListDishesRequestDto
import ru.k.kbook.api.grpc.schema.Dish
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.data.DishRepositoryImpl
import ru.k.kbook.domain.dish.DishRepository
import javax.inject.Inject

sealed class DishListUiState {
    data object Loading : DishListUiState()
    data class Data(val value: DishListState) : DishListUiState()
    data class Error(val message: String) : DishListUiState()
}

data class DishListState(
    val search: String = "",
    val categories: List<DishCategory> = emptyList(),
    val flags: List<DishFlag> = emptyList(),
    val refreshing: Boolean = false,
    val dishes: List<Dish> = emptyList(),
)

sealed class DishEvent {
    data class Message(val text: String) : DishEvent()
}

@HiltViewModel
class DishViewModel @Inject constructor (
    private val repo: DishRepository,
) : ViewModel() {
    private var state = DishListState()
    private val _uiState = MutableStateFlow<DishListUiState>(DishListUiState.Loading)
    val uiState: StateFlow<DishListUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<DishEvent>()
    val events: SharedFlow<DishEvent> = _events.asSharedFlow()


    fun onSearch(value: String) {
        state = state.copy(search = value)
        load()
    }

    fun onCategory(value: DishCategory) {
        val items = if (value in state.categories) state.categories - value else state.categories + value
        state = state.copy(categories = items)
        load()
    }

    fun onFlag(value: DishFlag) {
        val items = if (value in state.flags) state.flags - value else state.flags + value
        state = state.copy(flags = items)
        load()
    }

    fun refresh() {
        state = state.copy(refreshing = true)
        load()
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching { repo.deleteDish(DeleteDishRequestDto(id)) }
                .onSuccess { load() }
                .onFailure { _events.emit(DishEvent.Message(it.message ?: "Delete failed")) }
        }
    }

    fun load() {
        viewModelScope.launch {
            runCatching {
                repo.listDishes(
                    ListDishesRequestDto(
                        searchQuery = state.search.ifBlank { null },
                        categories = state.categories,
                        flags = state.flags,
                    ),
                )
            }.onSuccess {
                state = state.copy(dishes = it.dishes, refreshing = false)
                _uiState.value = DishListUiState.Data(state)
            }.onFailure {
                state = state.copy(refreshing = false)
                _uiState.value = DishListUiState.Error(it.message ?: "Load failed")
            }
        }
    }
}
