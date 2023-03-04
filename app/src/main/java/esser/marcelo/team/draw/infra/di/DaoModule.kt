package esser.marcelo.team.draw.infra.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import esser.marcelo.team.draw.core.datasource.local.database.AppDatabase
import esser.marcelo.team.draw.core.datasource.local.database.dao.player.PlayerDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao {
        return appDatabase.playerDao()
    }
}