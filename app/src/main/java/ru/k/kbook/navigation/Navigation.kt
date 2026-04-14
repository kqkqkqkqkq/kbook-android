package ru.k.kbook.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.k.kbook.features.product.ProductDetailScreen
import ru.k.kbook.features.product.ProductScreen
import ru.k.kbook.features.dish.DishScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val productsRoute = Screen.Products::class.qualifiedName
    val dishesRoute = Screen.Dishes::class.qualifiedName

    val isBottomBarVisible = currentDestination?.route == productsRoute || currentDestination?.route == dishesRoute

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == productsRoute } == true,
                        onClick = {
                            navController.navigate(Screen.Products) {
                                popUpTo(Screen.Products) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("Продукты") },
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == dishesRoute } == true,
                        onClick = {
                            navController.navigate(Screen.Dishes) {
                                popUpTo(Screen.Products) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                        label = { Text("Блюда") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products,
            modifier = Modifier,
        ) {
            composable<Screen.Products> {
                ProductScreen { product ->
                    navController.navigate(Screen.ProductDetails(product.id))
                }
            }
            composable<Screen.Dishes> {
                DishScreen()
            }
            composable<Screen.DishDetail> { }
            composable<Screen.ProductDetails> { backStackEntry ->
                ProductDetailScreen(backStackEntry.toRoute<Screen.ProductDetails>().productId) {
                    navController.popBackStack()
                }
            }
        }
    }
}
