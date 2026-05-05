package ru.k.kbook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.k.kbook.api.grpc.DishApi
import ru.k.kbook.api.grpc.ProductApi
import ru.k.kbook.data.DishRepositoryImpl
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.dish.DishRepository
import ru.k.kbook.domain.product.ProductRepository
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productApi: ProductApi
    ): ProductRepository = ProductRepositoryImpl(productApi)

    @Provides
    @Singleton
    fun provideDishRepository(
        dishApi: DishApi
    ): DishRepository = DishRepositoryImpl(dishApi)
}
