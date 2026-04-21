package ru.k.kbook.features.dish.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ru.k.kbook.R
import ru.k.kbook.features.dish.components.ProductDishCard
import ru.k.kbook.features.product.components.ProductCard

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DishDetailsScreen(
    id: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
) {
    val vm = viewModel<DishDetailsViewModel>(factory = DishDetailsVmFactory(id))
    LaunchedEffect(Unit) {
        vm.load()
    }
    when (val state = vm.uiState.collectAsStateWithLifecycle().value) {
        DishDetailsUiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { CircularProgressIndicator() }

        is DishDetailsUiState.Error -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { Text(state.message) }

        is DishDetailsUiState.Data -> Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.dish.name) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                null,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToEdit(state.dish.id) }) {
                            Icon(
                                Icons.Default.Edit,
                                null,
                            )
                        }
                    },
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.dish.images) { image ->
                            AsyncImage(
                                model = if (image.contentType == "url") image.url else image.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ic_launcher_background),
                                error = painterResource(R.drawable.ic_launcher_background),
                            )
                        }
                    }
                }
                item { Text("Калории: ${state.dish.caloricity}") }
                item { Text("Б: ${state.dish.protein}, Ж: ${state.dish.fat}, У: ${state.dish.carb}") }
                item { Text("Порция: ${state.dish.portionSize} г") }
                item { Text("Категория: ${state.dish.category}") }
                item { Text("Флаги: ${state.dish.flags.joinToString()}") }
                item { Text("Состав:") }
                items(state.products) { product ->
//                    Text(
//                        "- ${product.productName}: ${product.quantity} г",
//                        modifier = Modifier.fillMaxWidth(),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                    )
                    ProductDishCard(
                        product = product.product,
                        quantity = product.quantity,
                        onClick = { onNavigateToProduct(product.product.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
