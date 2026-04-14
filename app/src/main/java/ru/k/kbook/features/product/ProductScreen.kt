package ru.k.kbook.features.product

import android.util.Log
import android.util.Log.e
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField
import ru.k.kbook.features.product.components.ProductCard

@Composable
fun ProductScreen(
    onNavigate: (Product) -> Unit,
) {
    val viewModel = viewModel<ProductViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle()
    when {
        state.value.error != null -> ProductScreenError(state.value.error ?: "Unknown error")
        else -> ProductScreenContent(
            state.value,
            onNavigate,
            viewModel,
        )
    }
}

@Composable
fun ProductScreenError(e: String) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(e, color = Color.Red)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    state: ProductScreenState,
    onNavigate: (Product) -> Unit,
    viewModel: ProductViewModel,
) {

    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Блюда") }) },
        snackbarHost = { snackbarHostState },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO("open detail screen with null data")
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                OutlinedTextField(
                    value = state.searchQuery.orEmpty(),
                    onValueChange = {
                        viewModel.updateState(state.copy(searchQuery = it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Text("Product category", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(ProductCategory.entries) { category ->
                        FilterChip(
                            selected = category in state.categories,
                            onClick = {
                                Log.d("UI-PRODUCT", "$category - ${state.categories}")
                                if (category in state.categories) {
                                    viewModel.updateState(state.copy(categories = state.categories - category))
                                } else {
                                    viewModel.updateState(state.copy(categories = state.categories + category))
                                }
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            item {
                Text("Cooking Require", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(CookingRequired.entries) { category ->
                        FilterChip(
                            selected = category in state.cookingRequired,
                            onClick = {
                                if (category in state.cookingRequired) {
                                    viewModel.updateState(state.copy(cookingRequired = state.cookingRequired - category))
                                } else {
                                    viewModel.updateState(state.copy(cookingRequired = state.cookingRequired + category))
                                }
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            item {
                Text("Flags", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(ProductFlag.entries) { category ->
                        FilterChip(
                            selected = category in state.flags,
                            onClick = {
                                if (category in state.flags) {
                                    viewModel.updateState(state.copy(flags = state.flags - category))
                                } else {
                                    viewModel.updateState(state.copy(flags = state.flags + category))
                                }
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            item {
                Text("Sort by", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(SortField.entries) { category ->
                        FilterChip(
                            selected = category == state.sortBy,
                            onClick = {
                                viewModel.updateState(state.copy(sortBy = category))
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            item {
                Text("Sort direction", style = MaterialTheme.typography.labelMedium)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(SortDirection.entries) { category ->
                        FilterChip(
                            selected = category == state.sortDirection,
                            onClick = {
                                viewModel.updateState(state.copy(sortDirection = category))
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            if (state.products.isNotEmpty()) {
                items(state.products) { product ->
                    ProductCard(product, onNavigate)
                }
            } else {
                item {
                    ProductScreenEmpty()
                }
            }
        }
    }
}

@Composable
fun ProductScreenEmpty() {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Products is Empty", color = Color.Red)
        }
    }
}
