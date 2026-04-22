package ru.k.kbook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.k.kbook.api.grpc.DishApi
import ru.k.kbook.api.grpc.DishApiImpl
import ru.k.kbook.api.grpc.ProductApi
import ru.k.kbook.api.grpc.ProductApiImpl
import ru.k.kbook.config.GrpcChannel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideProductApi(channel: GrpcChannel): ProductApi = ProductApiImpl(channel)

    @Provides
    @Singleton
    fun provideDishApi(channel: GrpcChannel): DishApi = DishApiImpl(channel)

}
