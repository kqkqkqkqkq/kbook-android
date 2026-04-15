package ru.k.kbook.features.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository
import coil.compose.AsyncImage

sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Data(val product: Product) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

class ProductDetailViewModel(
    private val id: Long,
    private val repo: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repo.getProduct(GetProductRequestDto(id)) }
                .onSuccess {
                    val product = it.product
                    if (product == null) _uiState.value = ProductDetailUiState.Error("Product not found")
                    else _uiState.value = ProductDetailUiState.Data(product)
                }
                .onFailure { _uiState.value = ProductDetailUiState.Error(it.message ?: "Load failed") }
        }
    }
}

@Composable
fun ProductDetailScreen(
    id: Long,
    onNavigateBack: () -> Unit,
) {
    val vm = viewModel<ProductDetailViewModel>(factory = ProductDetailVmFactory(id))
    when (val state = vm.uiState.collectAsStateWithLifecycle().value) {
        ProductDetailUiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) { CircularProgressIndicator() }
        is ProductDetailUiState.Error -> Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(state.message)
        }
        is ProductDetailUiState.Data -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(state.product.name, style = MaterialTheme.typography.headlineSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(if (state.product.images.isEmpty()) listOf(ProductImage(0, null, null, ContentType.URL)) else state.product.images) { image ->
                    Card(modifier = Modifier.fillParentMaxWidth(0.8f)) {
                        AsyncImage(
                            model = if (image.contentType == ContentType.URL) image.url else image.image,
                            contentDescription = state.product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentScale = ContentScale.Crop,
                        )
                        if (image.url != null) {
                            Text(
                                image.url.take(60),
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            Text("kcal ${state.product.caloricity} | p ${state.product.protein} | f ${state.product.fat} | c ${state.product.carb}")
            Text(state.product.description.orEmpty())
            Text("category: ${state.product.category}")
            Text("cooking: ${state.product.cookingRequired}")
            Text("flags: ${state.product.flags.joinToString()}")
            Button(onClick = onNavigateBack) { Text("Back") }
        }
    }
}

class ProductDetailVmFactory(
    private val id: Long,
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProductDetailViewModel(id) as T
}
