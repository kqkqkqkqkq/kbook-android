package ru.k.kbook.features.product

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val state = viewModel.products.collectAsStateWithLifecycle()
    when {
        state.value.isNotEmpty() -> ProductScreenContent(state.value, onNavigate, viewModel)
        else -> ProductScreenEmpty()
    }
}

@Composable
fun ProductScreenEmpty() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    products: List<Product>,
    onNavigate: (Product) -> Unit,
    viewModel: ProductViewModel,
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val searchQuery by remember { mutableStateOf(viewModel.searchQuery) }

    // TODO("update ui and mock data from api")
    var categories by remember { mutableStateOf(viewModel.categories) }
    var cookingRequired by remember { mutableStateOf(viewModel.cookingRequired) }
    var flags by remember { mutableStateOf(viewModel.flags) }
    var sortBy by remember { mutableStateOf(viewModel.sortBy) }
    var sortDirection by remember { mutableStateOf(viewModel.sortDirection) }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
        topBar = { TopAppBar(title = { Text("Блюда") }) },
        snackbarHost = { snackbarHostState },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                // TODO("search bar")
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
                            selected = category in categories,
                            onClick = {
                                if (category in viewModel.categories) {
                                    viewModel.updateCategories(viewModel.categories - category)
                                } else {
                                    viewModel.updateCategories(viewModel.categories + category)
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
                            selected = category in viewModel.cookingRequired,
                            onClick = {
                                if (category in viewModel.cookingRequired) {
                                    viewModel.updateCookingRequired(viewModel.cookingRequired - category)
                                } else {
                                    viewModel.updateCookingRequired(viewModel.cookingRequired + category)
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
                            selected = category in viewModel.flags,
                            onClick = {
                                if (category in viewModel.flags) {
                                    viewModel.updateFlags(viewModel.flags - category)
                                } else {
                                    viewModel.updateFlags(viewModel.flags + category)
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
                            selected = category == viewModel.sortBy,
                            onClick = {
                                viewModel.updateSortBy(category)
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
                            selected = category == viewModel.sortDirection,
                            onClick = {
                                viewModel.updateSortDirection(category)
                            },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            items(products) { product ->
                ProductCard(product, onNavigate)
            }
        }
    }
}
