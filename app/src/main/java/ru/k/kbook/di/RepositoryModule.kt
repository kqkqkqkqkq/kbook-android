package ru.k.kbook.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.k.kbook.data.DishRepositoryImpl
import ru.k.kbook.data.ProductRepositoryImpl
import ru.k.kbook.domain.dish.DishRepository
import ru.k.kbook.domain.product.ProductRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(repository: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindDishRepository(repository: DishRepositoryImpl): DishRepository

}
