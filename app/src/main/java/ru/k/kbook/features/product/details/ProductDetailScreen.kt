package ru.k.kbook.features.product.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ru.k.kbook.R
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductImage
import java.time.Instant

@Composable
fun ProductDetailScreen(
    id: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
) {
    val vm = viewModel<ProductDetailViewModel>(factory = ProductDetailVmFactory(id))
    when (val state = vm.uiState.collectAsStateWithLifecycle().value) {
        ProductDetailUiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Loading...")
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }
        
        is ProductDetailUiState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(state.message)
        }

        is ProductDetailUiState.Data -> ProductDetailScreenContent(state.product, onNavigateBack, onNavigateToEdit)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreenContent(
    product: Product,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(product.name, style = MaterialTheme.typography.headlineSmall)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(product.id) }) {
                        Icon(Icons.Default.Edit, null)
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.images) { image ->
                    AsyncImage(
                        model = if (image.contentType == ContentType.URL) image.url else image.image,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background)
                    )
                }
            }
            Text("kcal ${product.caloricity} | p ${product.protein} | f ${product.fat} | c ${product.carb}")
            if (!product.description.isNullOrEmpty()) {
                Text(product.description)
            }
            Text("Category: ${product.category}")
            Text("Cooking: ${product.cookingRequired}")
            Text("Flags: ${product.flags.joinToString()}")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentPreview() {
    ProductDetailScreenContent(
        Product(
            1L, "Name",
            images = listOf(ProductImage(1L, "", null, ContentType.URL)), 15.0,
            15.0, 15.0, 15.0,
            "Description",
            ProductCategory.MEAT, CookingRequired.REQUIRES_COOKING,
            emptyList(), Instant.now(),
            Instant.now(),
        ),
        {}, {}
    )
}
