package ru.k.kbook.features.product

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.k.kbook.api.grpc.schema.Product

@Composable
fun ProductScreen(
    onNavigate: (Product) -> Unit,
) {
    val viewModel = viewModel<ProductViewModel>()
    val state = viewModel.products.collectAsStateWithLifecycle()
    when {
        state.value.isNotEmpty() -> ProductScreenContent(state.value, onNavigate)
        else -> ProductScreenEmpty()
    }
}

@Composable
fun ProductScreenEmpty() {

}

@Composable
fun ProductScreenContent(
    products: List<Product>,
    onNavigate: (Product) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(products){ product ->
            Button(
                onClick = {
                    onNavigate(product)
                }
            ) {
                Text(
                    product.name
                )
            }
        }
    }
}
