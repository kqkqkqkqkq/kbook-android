package ru.k.kbook.features.dish.edit

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import ru.k.kbook.R
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook_api.grpc.dish.dish

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DishEditScreen(
    id: Long,
    onNavigateBack: () -> Unit,
) {
    val vm = viewModel<DishEditViewModel>(factory = DishEditVmFactory(id))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    var imageUrl by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var productExpanded by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val bytes = uri?.let { context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() } } ?: return@rememberLauncherForActivityResult
        vm.addImage(DishImage(0L, null, bytes, "IMAGE"))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Редактирование блюда") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) } }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(state.name, vm::updateName, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.portionSize, vm::updatePortionSize, label = { Text("Размер порции, г") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(readOnly = true, value = state.category?.name ?: "Не выбрано", onValueChange = {}, label = { Text("Категория") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    DropdownMenuItem(text = { Text("Не выбрано") }, onClick = { vm.updateCategory(null); categoryExpanded = false })
                    DishCategory.entries.forEach { item -> DropdownMenuItem(text = { Text(item.name) }, onClick = { vm.updateCategory(item); categoryExpanded = false }) }
                }
            }
            OutlinedTextField(state.caloricity, vm::updateCaloricity, label = { Text("Калории на порцию (draft: ${state.autoCaloricity})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(state.protein, vm::updateProtein, label = { Text("Б (draft: ${state.autoProtein})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(state.fat, vm::updateFat, label = { Text("Ж (draft: ${state.autoFat})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(state.carb, vm::updateCarb, label = { Text("У (draft: ${state.autoCarb})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
            Row {
                ExposedDropdownMenuBox(
                    expanded = productExpanded,
                    onExpandedChange = { productExpanded = !productExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = state.products.firstOrNull { it.id == state.selectedProductId }?.name
                            ?: "Продукт",
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(productExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = productExpanded,
                        onDismissRequest = { productExpanded = false }) {
                        state.products.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    vm.updateSelectedProduct(p.id); productExpanded = false
                                })
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
                    modifier = Modifier.weight(1f)
                )
            }
            Button(onClick = vm::addCompositionItem) { Text("Добавить") }
            state.composition.forEachIndexed { i, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${item.productName}: ${item.quantity} г",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    IconButton(onClick = { vm.removeCompositionItem(i) }) { Icon(Icons.Default.Delete, null) }
                }
            }
            DishFlag.entries.forEach { flag ->
                FilterChip(selected = flag in state.flags, onClick = { vm.toggleFlag(flag) }, enabled = flag in state.availableFlags, label = { Text(flag.name) })
            }
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL фото") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { if (imageUrl.isNotBlank()) { vm.addImage(DishImage(0L, imageUrl, null, "URL")); imageUrl = "" } }) { Text("Добавить URL") }
                Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Галерея") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.images) { image ->
                    Box(
                        modifier = Modifier
                            .size(64.dp),
                    ) {
                        AsyncImage(
                            model = if (image.contentType == "url") image.url else image.image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            error = painterResource(R.drawable.ic_launcher_background)
                        )
                        IconButton(
                            onClick = { vm.removeImage(image) },
                            modifier = Modifier.size(24.dp).align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }
            Button(onClick = { vm.save(onNavigateBack) }, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) { Text("Сохранить") }
        }
    }
    LaunchedEffect(state.error) { state.error?.let { snackbar.showSnackbar(it); vm.clearError() } }
}
