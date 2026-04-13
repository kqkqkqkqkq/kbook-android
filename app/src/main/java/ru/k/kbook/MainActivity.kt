package ru.k.kbook

import android.R.attr.name
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import io.grpc.Channel
import io.grpc.okhttp.OkHttpChannelBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.k.kbook_api.grpc.product.ListProductsRequest
import ru.k.kbook_api.grpc.product.ListProductsResponse
import ru.k.kbook_api.grpc.product.ProductServiceGrpcKt
import ru.k.kbook.ui.theme.KbookandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channel = OkHttpChannelBuilder
            .forAddress("10.0.2.2", 9090)
            .usePlaintext()
            .build()

        val productClient = ProductClient(lifecycleScope, channel)

        lifecycleScope.launch {
            try {
                val response = productClient.listProducts().productsList
                Log.e("GRPC", response.toString())
            } catch (e: Exception) {
                Log.e("GRPC", "Error", e)
            }
        }

        enableEdgeToEdge()
        setContent {
            KbookandroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "Hello!",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}


class ProductClient(
    private val scope: CoroutineScope,
    private val channel: Channel,
) {
    private val stub = ProductServiceGrpcKt.ProductServiceCoroutineStub(channel)

    suspend fun listProducts(): ListProductsResponse {
        val request = ListProductsRequest.newBuilder()
            .setLimit(10)
            .setOffset(0)
            .build()
        return stub.listProducts(request)
    }
}
