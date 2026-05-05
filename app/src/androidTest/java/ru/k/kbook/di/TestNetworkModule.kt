package ru.k.kbook.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.k.kbook.config.GrpcChannel
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
object TestNetworkModule {

    @Provides
    @Singleton
    fun provideGrpcChannel(): GrpcChannel = GrpcChannel()
}
