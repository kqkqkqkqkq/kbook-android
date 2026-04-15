package ru.k.kbook.features.dish

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.k.kbook.api.grpc.schema.Dish
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DishScreen(
    onDetail: (Dish) -> Unit,
) {
    val vm = viewModel<DishViewModel>()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is DishEvent.Message -> Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
            }
        }
    }
    when (val state = vm.uiState.collectAsStateWithLifecycle().value) {
        DishListUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading") }
        is DishListUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
        is DishListUiState.Data -> DishListContent(state.value, onDetail, vm)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DishListContent(
    state: DishListState,
    onDetail: (Dish) -> Unit,
    vm: DishViewModel,
) {
    Scaffold(topBar = { TopAppBar(title = { Text("Dishes") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                item {
                    OutlinedTextField(value = state.search, onValueChange = vm::onSearch, modifier = Modifier.fillMaxWidth(), label = { Text("Search") })
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DishCategory.entries) { item ->
                            FilterChip(selected = item in state.categories, onClick = { vm.onCategory(item) }, label = { Text(item.name) })
                        }
                    }
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DishFlag.entries) { item ->
                            FilterChip(selected = item in state.flags, onClick = { vm.onFlag(item) }, label = { Text(item.name) })
                        }
                    }
                }
                items(state.dishes) { dish ->
                    Card(onClick = { onDetail(dish) }, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(dish.name)
                            Text("${dish.caloricity} kcal/portion")
                            Text("portion: ${dish.portionSize}")
                            Text("flags: ${dish.flags.joinToString()}")
                            TextButton(onClick = { vm.delete(dish.id) }) { Text("Delete") }
                        }
                    }
                }
            }
    }
}
