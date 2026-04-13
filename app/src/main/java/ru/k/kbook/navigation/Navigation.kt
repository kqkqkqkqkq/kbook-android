package ru.k.kbook.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.k.kbook.features.product.ProductDetailScreen
import ru.k.kbook.features.product.ProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Products,
    ) {
        composable<Screen.Products> {
            ProductScreen { product ->
                navController.navigate(Screen.ProductDetails(product.id))
            }
        }
        composable<Screen.DishDetail> {  }
        composable<Screen.Dishes> {  }
        composable<Screen.ProductDetails> { backStackEntry ->
            ProductDetailScreen(backStackEntry.toRoute<Screen.ProductDetails>().productId) {
                navController.popBackStack()
            }
        }
    }
}
