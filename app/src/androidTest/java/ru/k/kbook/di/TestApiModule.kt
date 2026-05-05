package ru.k.kbook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.k.kbook.api.grpc.DishApi
import ru.k.kbook.api.grpc.DishApiImpl
import ru.k.kbook.api.grpc.ProductApi
import ru.k.kbook.api.grpc.ProductApiImpl
import ru.k.kbook.config.GrpcChannel
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
object TestApiModule {

    @Provides
    @Singleton
    fun provideProductApi(channel: GrpcChannel): ProductApi = ProductApiImpl(channel)

    @Provides
    @Singleton
    fun provideDishApi(channel: GrpcChannel): DishApi = DishApiImpl(channel)

}