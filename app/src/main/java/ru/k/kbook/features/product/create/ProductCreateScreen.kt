package ru.k.kbook.features.product.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ru.k.kbook.R
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.features.product.contextUriToProductImage
import ru.k.kbook.features.product.list.ProductScreenTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreateScreen(
    onNavigateBack: () -> Unit,
) {
    val vm = hiltViewModel<ProductCreateViewModel>()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var newImageUrl by remember { mutableStateOf("") }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            val image = contextUriToProductImage(context, uri)
            if (image != null) {
                vm.addImageUrl(image)
            }
        }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Новый продукт", style = MaterialTheme.typography.headlineSmall)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag(ProductCreateScreenTag.BACK_BUTTON),
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .testTag(ProductCreateScreenTag.SNACKBAR),
            )
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
                .testTag(ProductCreateScreenTag.SCROLLABLE_COLUMN),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { vm.updateName(it) },
                label = { Text("Название") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.NAME_INPUT),
            )

            OutlinedTextField(
                value = state.caloricity,
                onValueChange = { vm.updateCaloricity(it) },
                label = { Text("Калорийность") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.CALORICITY_INPUT),
            )

            OutlinedTextField(
                value = state.protein,
                onValueChange = { vm.updateProtein(it) },
                label = { Text("Белки") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.PROTEIN_INPUT),
            )

            OutlinedTextField(
                value = state.fat,
                onValueChange = { vm.updateFat(it) },
                label = { Text("Жиры") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.FAT_INPUT),
            )

            OutlinedTextField(
                value = state.carb,
                onValueChange = { vm.updateCarb(it) },
                label = { Text("Углеводы") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.CARB_INPUT),
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { vm.updateDescription(it) },
                label = { Text("Состав") },
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.DESCRIPTION_INPUT),
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.category.getRu(),
                    onValueChange = {},
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .testTag(ProductCreateScreenTag.CATEGORY_INPUT),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    ProductCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.getRu()) },
                            onClick = {
                                vm.updateCategory(category)
                                expanded = false
                            },
                            modifier = Modifier.testTag("${ProductCreateScreenTag.PREFIX}_${category.name}"),
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cookingExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .testTag(ProductCreateScreenTag.COOKING_REQUIRED_INPUT),
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
                            modifier = Modifier.testTag("${ProductCreateScreenTag.PREFIX}_${req.name}"),
                        )
                    }
                }
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductFlag.entries.forEach { category ->
                    FilterChip(
                        selected = category in state.flags,
                        onClick = {
                            vm.updateFlags(category)
                        },
                        label = { Text(category.getRu()) },
                        modifier = Modifier.testTag("${ProductCreateScreenTag.PREFIX}_${category.name}"),
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
                                vm.addImageUrl(ProductImage(0L, newImageUrl, null, ContentType.URL))
                                newImageUrl = ""
                            }
                        },
                        modifier = Modifier.testTag(ProductCreateScreenTag.ADD_IMAGE_BUTTON),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add URL")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.URL_INPUT),
            )
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Добавить из галереи")
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.images) { image ->
                    Card(
                        modifier = Modifier.size(72.dp),
                        onClick = { vm.removeImageUrl(image) },
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            AsyncImage(
                                model = if (image.contentType == ContentType.URL) image.url else image.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.ic_launcher_background),
                                error = painterResource(R.drawable.ic_launcher_background),
                            )
                            IconButton(
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag(ProductCreateScreenTag.REMOVE_IMAGE_BUTTON),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProductCreateScreenTag.SAVE_BUTTON),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Создать")
                }
            }
        }

        LaunchedEffect(state.error) {
            if (state.error != null) {
                snackbarHostState.showSnackbar(
                    message = state.error!!,
                    duration = SnackbarDuration.Short,
                    withDismissAction = true,
                )
                vm.clearError()
            }
        }
    }
}
