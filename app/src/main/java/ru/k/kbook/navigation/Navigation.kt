package ru.k.kbook.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.k.kbook.features.dish.DishScreen
import ru.k.kbook.features.dish.create.DishCreateScreen
import ru.k.kbook.features.dish.details.DishDetailsScreen
import ru.k.kbook.features.dish.edit.DishEditScreen
import ru.k.kbook.features.product.create.ProductCreateScreen
import ru.k.kbook.features.product.details.ProductDetailScreen
import ru.k.kbook.features.product.edit.ProductEditScreen
import ru.k.kbook.features.product.list.ProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val productsRoute = Screen.Products::class.qualifiedName
    val dishesRoute = Screen.Dishes::class.qualifiedName

    val isBottomBarVisible =
        currentDestination?.route == productsRoute || currentDestination?.route == dishesRoute

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                ) {
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
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        label = { Text("Блюда") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Products,
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
        ) {
            composable<Screen.Products> {
                ProductScreen(
                    onNavigate = { product -> navController.navigate(Screen.ProductDetails(product.id)) },
                    onCreate = { navController.navigate(Screen.ProductCreate) },
                )
            }
            composable<Screen.Dishes> {
                DishScreen(
                    onDetail = { dish -> navController.navigate(Screen.DishDetail(dish.id)) },
                    onCreate = { navController.navigate(Screen.DishCreate) },
                )
            }
            composable<Screen.ProductDetails> { backStackEntry ->
                val id = backStackEntry.toRoute<Screen.ProductDetails>().productId
                ProductDetailScreen(id,
                    {
                        navController.popBackStack()
                    }, { pid ->
                        navController.navigate(Screen.ProductEdit(pid))
                    })

            }
            composable<Screen.DishDetail> { backStackEntry ->
                val id = backStackEntry.toRoute<Screen.DishDetail>().dishId
                DishDetailsScreen(
                    id = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { dishId -> navController.navigate(Screen.DishEdit(dishId)) },
                    onNavigateToProduct = { productId -> navController.navigate(Screen.ProductDetails(productId)) },
                )
            }
            composable<Screen.ProductCreate> {
                ProductCreateScreen { navController.popBackStack() }
            }
            composable<Screen.DishCreate> {
                DishCreateScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable<Screen.ProductEdit> { backStackEntry ->
                val id = backStackEntry.toRoute<Screen.ProductEdit>().productId
                ProductEditScreen(id) { navController.popBackStack() }
            }
            composable<Screen.DishEdit> { backStackEntry ->
                val id = backStackEntry.toRoute<Screen.DishEdit>().dishId
                DishEditScreen(id = id, onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
