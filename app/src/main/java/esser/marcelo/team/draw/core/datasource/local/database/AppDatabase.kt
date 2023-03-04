package esser.marcelo.team.draw.core.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import esser.marcelo.team.draw.core.datasource.local.database.dao.player.PlayerDao
import esser.marcelo.team.draw.core.datasource.local.entity.SoccerEntity

@Database(entities = [SoccerEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}