package ru.k.kbook.features.dish

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.k.kbook.api.grpc.schema.Dish
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.features.dish.components.DishCard

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DishScreen(
    onDetail: (Dish) -> Unit,
    onCreate: () -> Unit,
) {
    val vm = hiltViewModel<DishViewModel>()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is DishEvent.Message -> Toast.makeText(context, event.text, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.load()
    }
    when (val state = vm.uiState.collectAsStateWithLifecycle().value) {
        DishListUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { Text("Загрузка") }

        is DishListUiState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { Text(state.message) }

        is DishListUiState.Data -> DishListContent(state.value, onDetail, onCreate, vm)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DishListContent(
    state: DishListState,
    onDetail: (Dish) -> Unit,
    onCreate: () -> Unit,
    vm: DishViewModel,
) {
    Scaffold(
        modifier = Modifier
            .padding(bottom = 80.dp)
            .fillMaxSize()
            .testTag(DishScreenTag.CONTENT),
        topBar = { TopAppBar(title = { Text("Блюда") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate,
                modifier = Modifier.testTag(DishScreenTag.CREATE_BUTTON),
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                )
                .padding(horizontal = 12.dp)
                .testTag(DishScreenTag.SCROLLABLE_COLUMN),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                OutlinedTextField(
                    value = state.search,
                    onValueChange = vm::onSearch,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Поиск") },
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DishCategory.values()) { item ->
                        FilterChip(
                            selected = item in state.categories,
                            onClick = { vm.onCategory(item) },
                            label = { Text(item.getRu()) },
                        )
                    }
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(DishFlag.values()) { item ->
                        FilterChip(
                            selected = item in state.flags,
                            onClick = { vm.onFlag(item) },
                            label = { Text(item.getRu()) },
                        )
                    }
                }
            }
            items(state.dishes) { dish ->
                DishCard(dish = dish, onClick = onDetail, onDelete = { vm.delete(it.id) })
            }
        }
    }
}
