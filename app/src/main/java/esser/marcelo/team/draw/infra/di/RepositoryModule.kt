package esser.marcelo.team.draw.infra.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import esser.marcelo.team.draw.core.repository.player.PlayerRepository
import esser.marcelo.team.draw.core.repository.player.PlayerRepositoryImpl
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @PlayersRepository
    @Singleton
    abstract fun providePlayerRepository(repositoryImpl: PlayerRepositoryImpl): PlayerRepository
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlayersRepository