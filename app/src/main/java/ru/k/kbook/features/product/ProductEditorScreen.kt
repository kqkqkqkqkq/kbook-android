package ru.k.kbook.features.product

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.ImageInput
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.product.ProductRepository

private data class ProductEditorImage(
    val label: String,
    val input: ImageInput,
)

private data class ProductEditorState(
    val name: String = "",
    val caloricity: String = "",
    val protein: String = "",
    val fat: String = "",
    val carb: String = "",
    val description: String = "",
    val category: ProductCategory = ProductCategory.VEGETABLES,
    val cookingRequired: CookingRequired = CookingRequired.READY_TO_EAT,
    val flags: Set<ProductFlag> = emptySet(),
    val images: List<ProductEditorImage> = emptyList(),
    val urlInput: String = "",
    val saving: Boolean = false,
)

private sealed class ProductEditorEvent {
    data class Info(val message: String) : ProductEditorEvent()
    data object Saved : ProductEditorEvent()
}

private class ProductEditorViewModel(
    private val repo: ProductRepository = ProductRepositoryImpl(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductEditorState())
    val uiState: StateFlow<ProductEditorState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductEditorEvent>()
    val events: SharedFlow<ProductEditorEvent> = _events.asSharedFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onCaloricityChange(value: String) = _uiState.update { it.copy(caloricity = value) }
    fun onProteinChange(value: String) = _uiState.update { it.copy(protein = value) }
    fun onFatChange(value: String) = _uiState.update { it.copy(fat = value) }
    fun onCarbChange(value: String) = _uiState.update { it.copy(carb = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onUrlInputChange(value: String) = _uiState.update { it.copy(urlInput = value) }
    fun onCategoryChange(value: ProductCategory) = _uiState.update { it.copy(category = value) }
    fun onCookingRequiredChange(value: CookingRequired) = _uiState.update { it.copy(cookingRequired = value) }

    fun onFlagToggle(value: ProductFlag) {
        _uiState.update {
            val next = if (value in it.flags) it.flags - value else it.flags + value
            it.copy(flags = next)
        }
    }

    fun addImageFromGallery(uri: Uri, bytes: ByteArray) {
        _uiState.update {
            if (it.images.size >= 5) it else it.copy(
                images = it.images + ProductEditorImage(
                    label = uri.toString(),
                    input = ImageInput(
                        url = null,
                        image = bytes,
                        contentType = ContentType.IMAGE,
                    ),
                ),
            )
        }
    }

    fun addImageUrl() {
        val state = _uiState.value
        if (state.images.size >= 5) {
            viewModelScope.launch { _events.emit(ProductEditorEvent.Info("Max 5 images")) }
            return
        }
        val normalized = state.urlInput.trim()
        if (normalized.isBlank()) {
            viewModelScope.launch { _events.emit(ProductEditorEvent.Info("Enter image url")) }
            return
        }
        _uiState.update {
            it.copy(
                images = it.images + ProductEditorImage(
                    label = normalized,
                    input = ImageInput(
                        url = normalized,
                        image = null,
                        contentType = ContentType.URL,
                    ),
                ),
                urlInput = "",
            )
        }
    }

    fun removeImage(index: Int) {
        _uiState.update { state ->
            state.copy(images = state.images.filterIndexed { i, _ -> i != index })
        }
    }

    fun save() {
        val state = _uiState.value
        val caloricity = state.caloricity.toDoubleOrNull()
        val protein = state.protein.toDoubleOrNull()
        val fat = state.fat.toDoubleOrNull()
        val carb = state.carb.toDoubleOrNull()

        val error = when {
            state.name.trim().length < 2 -> "Name must be at least 2 chars"
            caloricity == null || caloricity < 0 -> "Calories must be >= 0"
            protein == null || protein < 0 || protein > 100 -> "Protein must be in [0, 100]"
            fat == null || fat < 0 || fat > 100 -> "Fat must be in [0, 100]"
            carb == null || carb < 0 || carb > 100 -> "Carb must be in [0, 100]"
            protein + fat + carb > 100 -> "P + F + C must not exceed 100"
            state.images.size > 5 -> "Max 5 images"
            else -> null
        }
        if (error != null) {
            viewModelScope.launch { _events.emit(ProductEditorEvent.Info(error)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(saving = true) }
            runCatching {
                repo.createProduct(
                    CreateProductRequestDto(
                        name = state.name.trim(),
                        images = state.images.map { it.input },
                        caloricity = caloricity!!,
                        protein = protein!!,
                        fat = fat!!,
                        carb = carb!!,
                        description = state.description.trim().ifBlank { null },
                        category = state.category,
                        cookingRequired = state.cookingRequired,
                        flags = state.flags.toList(),
                    ),
                )
            }.onSuccess {
                _events.emit(ProductEditorEvent.Saved)
            }.onFailure {
                _events.emit(ProductEditorEvent.Info(it.message ?: "Save failed"))
            }
            _uiState.update { it.copy(saving = false) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditorScreen(
    onDone: () -> Unit,
) {
    val vm = viewModel<ProductEditorViewModel>()
    val state = vm.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        val freeSlots = (5 - state.images.size).coerceAtLeast(0)
        uris.take(freeSlots).forEach { uri ->
            readBytesFromUri(context, uri)?.let { bytes ->
                vm.addImageFromGallery(uri, bytes)
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.events.collect { event ->
            when (event) {
                is ProductEditorEvent.Info -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                ProductEditorEvent.Saved -> onDone()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Product form") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = vm::onNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.caloricity,
                    onValueChange = vm::onCaloricityChange,
                    label = { Text("Calories per 100g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.protein,
                    onValueChange = vm::onProteinChange,
                    label = { Text("Protein per 100g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.fat,
                    onValueChange = vm::onFatChange,
                    label = { Text("Fat per 100g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.carb,
                    onValueChange = vm::onCarbChange,
                    label = { Text("Carb per 100g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = vm::onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Text("Category", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(ProductCategory.entries) { _, category ->
                        FilterChip(
                            selected = category == state.category,
                            onClick = { vm.onCategoryChange(category) },
                            label = { Text(category.name) },
                        )
                    }
                }
            }
            item {
                Text("Cooking Required", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(CookingRequired.entries) { _, cookingRequired ->
                        FilterChip(
                            selected = cookingRequired == state.cookingRequired,
                            onClick = { vm.onCookingRequiredChange(cookingRequired) },
                            label = { Text(cookingRequired.name) },
                        )
                    }
                }
            }
            item {
                Text("Flags", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(ProductFlag.entries) { _, flag ->
                        FilterChip(
                            selected = flag in state.flags,
                            onClick = { vm.onFlagToggle(flag) },
                            label = { Text(flag.name) },
                        )
                    }
                }
            }
            item {
                Text("Photos (${state.images.size}/5)", style = MaterialTheme.typography.labelMedium)
                Button(
                    onClick = { launcher.launch("image/*") },
                    enabled = state.images.size < 5,
                    modifier = Modifier.padding(top = 6.dp),
                ) {
                    Text("Add from gallery")
                }
            }
            if (state.images.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.images.forEachIndexed { index, image ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                AsyncImage(
                                    model = when (image.input.contentType) {
                                        ContentType.URL -> image.input.url
                                        ContentType.IMAGE -> image.input.image
                                    },
                                    contentDescription = "Image preview",
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(onClick = { vm.removeImage(index) }) {
                                    Text("Remove")
                                }
                            }
                            Text(
                                text = image.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.urlInput,
                    onValueChange = vm::onUrlInputChange,
                    label = { Text("Image url") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Button(
                    onClick = vm::addImageUrl,
                    enabled = state.images.size < 5,
                ) {
                    Text("Add url")
                }
            }
            item {
                Button(
                    onClick = vm::save,
                    enabled = !state.saving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.saving) {
                        CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}

private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }.getOrNull()
}
