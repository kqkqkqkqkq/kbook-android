package ru.k.kbook.features.dish.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.features.dish.edit.round1

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DishCreateScreen(
    onNavigateBack: () -> Unit,
) {
    val vm = hiltViewModel<DishCreateViewModel>()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    var imageUrl by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val bytes = uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            } ?: return@rememberLauncherForActivityResult
            vm.addImage(DishImage(0L, null, bytes, "IMAGE"))
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новое блюдо") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(DishCreateScreenTag.BACK_BUTTON),
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            null,
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier.testTag(DishCreateScreenTag.SNACKBAR),
            )
        },
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
                return@Scaffold
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
                .testTag(DishCreateScreenTag.SCROLLABLE_COLUMN),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                state.name,
                vm::updateName,
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth().testTag(DishCreateScreenTag.NAME_INPUT),
            )
            OutlinedTextField(
                state.portionSize,
                vm::updatePortionSize,
                label = { Text("Размер порции, г") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag(DishCreateScreenTag.SIZE_INPUT),
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.testTag(DishCreateScreenTag.CATEGORY_INPUT),
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = state.category?.getRu() ?: "Не выбрано",
                    onValueChange = {},
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Не выбрано") },
                        onClick = { vm.updateCategory(null); categoryExpanded = false },
                    )
                    DishCategory.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.getRu()) },
                            onClick = { vm.updateCategory(item); categoryExpanded = false },
                            modifier = Modifier.testTag("${DishCreateScreenTag.PREFIX}_${item.name}"),
                        )
                    }
                }
            }
            OutlinedTextField(
                state.caloricity,
                vm::updateCaloricity,
                label = { Text("Калории на порцию (авто: ${state.autoCaloricity.round1()})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag(DishCreateScreenTag.CALORICITY_INPUT),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    state.protein,
                    vm::updateProtein,
                    label = { Text("Б (авто: ${state.autoProtein.round1()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag(DishCreateScreenTag.PROTEIN_INPUT),
                )
                OutlinedTextField(
                    state.fat,
                    vm::updateFat,
                    label = { Text("Ж (авто: ${state.autoFat.round1()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag(DishCreateScreenTag.FAT_INPUT),
                )
                OutlinedTextField(
                    state.carb,
                    vm::updateCarb,
                    label = { Text("У (авто: ${state.autoCarb.round1()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag(DishCreateScreenTag.CARB_INPUT),
                )
            }
            Row {
                ExposedDropdownMenuBox(
                    expanded = productExpanded,
                    onExpandedChange = { productExpanded = !productExpanded },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(DishCreateScreenTag.ADD_COMPOSITION_BUTTON),
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = state.products.firstOrNull { it.id == state.selectedProductId }?.name
                            ?: "Продукт",
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(productExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = productExpanded,
                        onDismissRequest = { productExpanded = false },
                    ) {
                        // Product
                        state.products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    vm.updateSelectedProduct(p.id); productExpanded = false
                                },
                                modifier = Modifier.testTag("${DishCreateScreenTag.PREFIX}_${p.name}"),
                            )
                        }
                    }
                }
            }
            Row {
                OutlinedTextField(
                    state.selectedQuantity,
                    vm::updateSelectedQuantity,
                    label = { Text("Граммы") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).testTag(DishCreateScreenTag.WEIGHT_INPUT),
                )
            }
            Button(
                onClick = vm::addCompositionItem,
                modifier = Modifier.testTag(DishCreateScreenTag.ADD_COMPOSITION_SUBMIT_BUTTON),
            ) {
                Text("Добавить")
            }
            state.composition.forEachIndexed { i, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${item.productName}: ${item.quantity} г")
                    IconButton(
                        onClick = { vm.removeCompositionItem(i) },
                        modifier = Modifier.testTag(DishCreateScreenTag.REMOVE_PRODUCT_BUTTON),
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                        )
                    }
                }
            }
            DishFlag.entries.forEach { flag ->
                FilterChip(
                    selected = flag in state.flags,
                    onClick = { vm.toggleFlag(flag) },
                    enabled = flag in state.availableFlags,
                    label = { Text(flag.getRu()) },
                    modifier = Modifier.testTag("${DishCreateScreenTag.PREFIX}_${flag.name}")
                )
            }
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL фото") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(DishCreateScreenTag.URL_INPUT),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (imageUrl.isNotBlank()) {
                            vm.addImage(DishImage(0L, imageUrl, null, "URL")); imageUrl = ""
                        }
                    },
                    modifier = Modifier.testTag(DishCreateScreenTag.ADD_IMAGE_BUTTON),
                ) { Text("Добавить URL") }
                Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Галерея") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.images) { image ->
                    Card(
                        modifier = Modifier.size(100.dp),
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                AsyncImage(
                                    model = image.url ?: image.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.ic_launcher_background),
                                    error = painterResource(R.drawable.ic_launcher_background),
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag(DishCreateScreenTag.REMOVE_IMAGE_BUTTON),
                                onClick = { vm.removeImage(image) },
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
                onClick = { vm.save(onNavigateBack) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().testTag(DishCreateScreenTag.SAVE_BUTTON),
            ) { Text("Создать блюдо") }
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            vm.clearError()
        }
    }
}
