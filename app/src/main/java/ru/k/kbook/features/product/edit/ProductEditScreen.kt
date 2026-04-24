package ru.k.kbook.features.product.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.features.product.ProductImagePreview
import ru.k.kbook.features.product.contextUriToProductImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    id: Long,
    onNavigateBack: () -> Unit,
) {
    val vm = hiltViewModel<ProductEditViewModel>()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var newImageUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.loadProduct(id)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            contextUriToProductImage(context, uri)?.let(vm::addImage)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Редактировать продукт", style = MaterialTheme.typography.headlineSmall)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    vm.updateName(it)
                },
                label = { Text("Имя продукта") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = state.caloricity,
                onValueChange = vm::updateCaloricity,
                label = { Text("Калорийность") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.protein,
                onValueChange = vm::updateProtein,
                label = { Text("Белки") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.fat,
                onValueChange = vm::updateFat,
                label = { Text("Жиры") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.carb,
                onValueChange = vm::updateCarb,
                label = { Text("Углеводы") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = vm::updateDescription,
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
            )

            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.category.getRu(),
                    onValueChange = {},
                    label = { Text("Категория") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    ProductCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.getRu()) },
                            onClick = {
                                vm.updateCategory(category)
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }

            var cookingExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = cookingExpanded,
                onExpandedChange = { cookingExpanded = !cookingExpanded },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.cookingRequired.getRu(),
                    onValueChange = {},
                    label = { Text("Тип приготовления") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cookingExpanded)
                    },
                    modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = cookingExpanded,
                    onDismissRequest = { cookingExpanded = false },
                ) {
                    CookingRequired.entries.forEach { req ->
                        DropdownMenuItem(
                            text = { Text(req.getRu()) },
                            onClick = {
                                vm.updateCookingRequired(req)
                                cookingExpanded = false
                            },
                        )
                    }
                }
            }

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductFlag.entries.forEach { flag ->
                    FilterChip(
                        selected = flag in state.flags,
                        onClick = { vm.toggleFlag(flag) },
                        label = { Text(flag.getRu()) },
                    )
                }
            }

            Text("Картинки", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = newImageUrl,
                onValueChange = { newImageUrl = it },
                label = { Text("Введите URL") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (newImageUrl.isNotBlank()) {
                                vm.addImage(ProductImage(0L, newImageUrl, null, ContentType.URL))
                                newImageUrl = ""
                            }
                        },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add URL")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Добавьте из галереи")
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.images) { image ->
                    androidx.compose.material3.Card(modifier = Modifier.size(100.dp)) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ProductImagePreview(image)
                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = { vm.removeImage(image) },
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove image",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { vm.save(id, onNavigateBack) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Сохранить")
                }
            }
        }

        LaunchedEffect(state.error) {
            state.error?.let {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Long,
                    withDismissAction = true,
                )
                vm.clearError()
            }
        }
    }
}
