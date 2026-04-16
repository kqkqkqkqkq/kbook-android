package ru.k.kbook.features.product.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    onNavigateBack: () -> Unit,
) {
    val vm: ProductCreateViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    var newImageUrl by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("New Product", style = MaterialTheme.typography.headlineSmall)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { vm.updateName(it) },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.caloricity,
                onValueChange = { vm.updateCaloricity(it) },
                label = { Text("Caloricity (kcal)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.protein,
                onValueChange = { vm.updateProtein(it) },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.fat,
                onValueChange = { vm.updateFat(it) },
                label = { Text("Fat (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.carb,
                onValueChange = { vm.updateCarb(it) },
                label = { Text("Carbs (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { vm.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
            )

            // Category Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.category.name,
                    onValueChange = {},
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    ProductCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                vm.updateCategory(category)
                                expanded = false
                            },
                        )
                    }
                }
            }

            // Cooking Required
            var cookingExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = cookingExpanded,
                onExpandedChange = { cookingExpanded = !cookingExpanded },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.cookingRequired.name,
                    onValueChange = {},
                    label = { Text("Cooking Required") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cookingExpanded) },
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = cookingExpanded,
                    onDismissRequest = { cookingExpanded = false },
                ) {
                    CookingRequired.entries.forEach { req ->
                        DropdownMenuItem(
                            text = { Text(req.name) },
                            onClick = {
                                vm.updateCookingRequired(req)
                                cookingExpanded = false
                            },
                        )
                    }
                }
            }

            // Image URLs
            Text("Images (URLs)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newImageUrl,
                onValueChange = { newImageUrl = it },
                label = { Text("Enter image URL") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newImageUrl.isNotBlank()) {
                                vm.addImageUrl(ProductImage(0L, newImageUrl, null, ContentType.URL))
                                newImageUrl = ""
                            }
                        },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add URL")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.images) { image ->
                    Card(
                        modifier = Modifier.size(100.dp),
                        onClick = { vm.removeImageUrl(image) },
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    (image.url?.take(20) ?: "") + "...",
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { vm.removeImageUrl(image) },
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    vm.createProduct { onNavigateBack() }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Create Product")
                }
            }
        }

        // Показываем снэкбар при появлении ошибки
        LaunchedEffect(state.error) {
            if (state.error != null) {
                snackbarHostState.showSnackbar(
                    message = state.error!!,
                    duration = SnackbarDuration.Short
                )
                vm.clearError() // очищаем ошибку после показа
            }
        }
    }
}
