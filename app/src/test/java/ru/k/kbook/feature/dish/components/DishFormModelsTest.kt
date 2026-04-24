//package ru.k.kbook.feature.dish.components
//
//import io.qameta.allure.Description
//import io.qameta.allure.Feature
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import ru.k.kbook.api.grpc.schema.Product
//import ru.k.kbook.api.grpc.schema.ProductImage
//import ru.k.kbook.api.grpc.schema.ProductCategory
//import ru.k.kbook.api.grpc.schema.CookingRequired
//import ru.k.kbook.api.grpc.schema.ProductFlag
//import ru.k.kbook.features.dish.components.DishCompositionInput
//import ru.k.kbook.features.dish.components.Quadruple
//import ru.k.kbook.features.dish.components.bjuSumPer100
//import ru.k.kbook.features.dish.components.calculateAutoNutrition
//import java.time.Instant
//import java.util.Collections.emptyList
//import java.util.Collections.emptyMap
//
//@Feature("Dish Form Models - Nutrition Calculation")
//class DishFormModelsTest {
//
//    private fun mockProductImage(id: Long = 1) = ProductImage(
//        id = id,
//        url = null,
//        image = null,
//        contentType = "image/jpeg"
//    )
//
//    private fun mockProduct(
//        id: Long,
//        name: String,
//        caloricity: Double = 0.0,
//        protein: Double = 0.0,
//        fat: Double = 0.0,
//        carb: Double = 0.0,
//        flags: List<ProductFlag> = emptyList(),
//        category: ProductCategory = ProductCategory.OTHER,
//    ) = Product(
//        id = id,
//        name = name,
//        images = listOf(mockProductImage()),
//        caloricity = caloricity,
//        protein = protein,
//        fat = fat,
//        carb = carb,
//        description = null,
//        category = category,
//        cookingRequired = CookingRequired.NO,
//        flags = flags,
//        createdAt = Instant.now(),
//        updatedAt = Instant.now(),
//    )
//
//    @Test
//    @Description("GIVEN portion size is 0 WHEN bjuSumPer100 is called THEN total BJU is returned without scaling")
//    fun `GIVEN portion size is 0 WHEN bjuSumPer100 is called THEN total BJU is returned without scaling`() {
//        val result = bjuSumPer100(portionSize = 0.0, protein = 10.0, fat = 20.0, carb = 30.0)
//        assertEquals(60.0, result, 0.001)
//    }
//
//    @Test
//    @Description("GIVEN portion size is negative WHEN bjuSumPer100 is called THEN total BJU is returned without scaling")
//    fun `GIVEN portion size is negative WHEN bjuSumPer100 is called THEN total BJU is returned without scaling`() {
//        val result = bjuSumPer100(portionSize = -50.0, protein = 5.0, fat = 10.0, carb = 15.0)
//        assertEquals(30.0, result, 0.001)
//    }
//
//    @Test
//    @Description("GIVEN valid portion size of 200g and known macros WHEN bjuSumPer100 is called THEN correct scaled sum per 100g is returned")
//    fun `GIVEN valid portion size of 200g and known macros WHEN bjuSumPer100 is called THEN correct scaled sum per 100g is returned`() {
//        val result = bjuSumPer100(portionSize = 200.0, protein = 10.0, fat = 20.0, carb = 30.0)
//        assertEquals(30.0, result, 0.001)
//    }
//
//    @Test
//    @Description("GIVEN portion size is 100g WHEN bjuSumPer100 is called THEN total BJU equals sum of macros")
//    fun `GIVEN portion size is 100g WHEN bjuSumPer100 is called THEN total BJU equals sum of macros`() {
//        val result = bjuSumPer100(portionSize = 100.0, protein = 8.0, fat = 12.0, carb = 20.0)
//        assertEquals(40.0, result, 0.001)
//    }
//
//    @Test
//    @Description("GIVEN empty composition WHEN calculateAutoNutrition is called THEN zero nutrition values are returned")
//    fun `GIVEN empty composition WHEN calculateAutoNutrition is called THEN zero nutrition values are returned`() {
//        val result = calculateAutoNutrition(composition = emptyList(), productsById = emptyMap())
//        assertEquals(Quadruple(0.0, 0.0, 0.0, 0.0), result)
//    }
//
//    @Test
//    @Description("GIVEN composition with one product and quantity 100g WHEN calculateAutoNutrition is called THEN product's exact nutrition is returned")
//    fun `GIVEN composition with one product and quantity 100g WHEN calculateAutoNutrition is called THEN product's exact nutrition is returned`() {
//        val product = mockProduct(
//            id = 1,
//            name = "Chicken",
//            caloricity = 200.0,
//            protein = 30.0,
//            fat = 10.0,
//            carb = 0.0
//        )
//        val composition = listOf(
//            DishCompositionInput(
//                productId = 1,
//                productName = "Chicken",
//                quantity = "100"
//            )
//        )
//        val result = calculateAutoNutrition(composition, mapOf(1L to product))
//        assertEquals(Quadruple(200.0, 30.0, 10.0, 0.0), result)
//    }
//
//    @Test
//    @Description("GIVEN composition with one product and quantity 50g WHEN calculateAutoNutrition is called THEN half of product's nutrition is returned")
//    fun `GIVEN composition with one product and quantity 50g WHEN calculateAutoNutrition is called THEN half of product's nutrition is returned`() {
//        val product = mockProduct(
//            id = 1,
//            name = "Rice",
//            caloricity = 150.0,
//            protein = 3.0,
//            fat = 1.0,
//            carb = 30.0
//        )
//        val composition = listOf(
//            DishCompositionInput(
//                productId = 1,
//                productName = "Rice",
//                quantity = "50"
//            )
//        )
//        val result = calculateAutoNutrition(composition, mapOf(1L to product))
//        assertEquals(Quadruple(75.0, 1.5, 0.5, 15.0), result)
//    }
//
//    @Test
//    @Description("GIVEN composition with invalid quantity string WHEN calculateAutoNutrition is called THEN item is skipped and not included in calculation")
//    fun `GIVEN composition with invalid quantity string WHEN calculateAutoNutrition is called THEN item is skipped and not included in calculation`() {
//        val product = mockProduct(id = 1, name = "Beef", caloricity = 250.0, protein = 26.0, fat = 20.0, carb = 0.0)
//        val composition = listOf(
//            DishCompositionInput(productId = 1, productName = "Beef", quantity = "abc"),
//            DishCompositionInput(productId = 1, productName = "Beef", quantity = "not_a_number")
//        )
//        val result = calculateAutoNutrition(composition, mapOf(1L to product))
//        assertEquals(Quadruple(0.0, 0.0, 0.0, 0.0), result)
//    }
//
//    @Test
//    @Description("GIVEN composition with missing product in map WHEN calculateAutoNutrition is called THEN missing product is skipped")
//    fun `GIVEN composition with missing product in map WHEN calculateAutoNutrition is called THEN missing product is skipped`() {
//        val composition = listOf(
//            DishCompositionInput(
//                productId = 999,
//                productName = "Missing",
//                quantity = "100"
//            )
//        )
//        val result = calculateAutoNutrition(composition, productsById = emptyMap())
//        assertEquals(Quadruple(0.0, 0.0, 0.0, 0.0), result)
//    }
//
//    @Test
//    @Description("GIVEN composition with multiple products WHEN calculateAutoNutrition is called THEN total nutrition is correctly summed")
//    fun `GIVEN composition with multiple products WHEN calculateAutoNutrition is called THEN total nutrition is correctly summed`() {
//        val chicken = mockProduct(
//            id = 1,
//            name = "Chicken",
//            caloricity = 200.0,
//            protein = 30.0,
//            fat = 10.0,
//            carb = 0.0
//        )
//        val rice = mockProduct(
//            id = 2,
//            name = "Rice",
//            caloricity = 150.0,
//            protein = 3.0,
//            fat = 1.0,
//            carb = 30.0
//        )
//        val composition = listOf(
//            DishCompositionInput(productId = 1, productName = "Chicken", quantity = "100"),
//            DishCompositionInput(productId = 2, productName = "Rice", quantity = "200")
//        )
//        val productsById = mapOf(1L to chicken, 2L to rice)
//        val result = calculateAutoNutrition(composition, productsById)
//        // Chicken: 100g → 200 kcal, 30g P, 10g F, 0g C
//        // Rice: 200g → 300 kcal, 6g P, 2g F, 60g C
//        assertEquals(Quadruple(500.0, 36.0, 12.0, 60.0), result)
//    }
//}