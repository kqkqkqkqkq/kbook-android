package ru.k.kbook.features.product

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
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
    onCreate: () -> Unit,
) {
    val viewModel = viewModel<ProductViewModel>()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.events.collect {
            val text = when (it) {
                is ProductListEvent.Error -> it.message
                is ProductListEvent.Info -> it.message
            }
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
    when (val state = viewModel.uiState.collectAsStateWithLifecycle().value) {
        ProductListUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading") }
        is ProductListUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
        is ProductListUiState.Data -> ProductScreenContent(state.value, onNavigate, onCreate, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    state: ProductListState,
    onNavigate: (Product) -> Unit,
    onCreate: () -> Unit,
    viewModel: ProductViewModel,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Products") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate,
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = {
                        viewModel.onSearchChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search product") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
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
                                viewModel.onCategoryToggle(category)
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
                                viewModel.onCookingToggle(category)
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
                                viewModel.onFlagToggle(category)
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
                    items(SortField.entries) { field ->
                        FilterChip(
                            selected = field == state.sortBy,
                            onClick = { viewModel.onSort(field, state.sortDirection) },
                            label = { Text(field.name) },
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
                    items(SortDirection.entries) { direction ->
                        FilterChip(
                            selected = direction == state.sortDirection,
                            onClick = { viewModel.onSort(state.sortBy, direction) },
                            label = { Text(direction.name) },
                        )
                    }
                }
            }
                if (state.products.isNotEmpty()) {
                items(state.products) { product ->
                    ProductCard(
                        product = product,
                        onNavigate = onNavigate,
                        onDelete = { viewModel.deleteProduct(product.id) },
                    )
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
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Products are empty")
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ProductScreenContentPreview(modifier: Modifier = Modifier) {
    ProductScreenContent(state = ProductListState(), {}, {}, ProductViewModel())
}
