package esser.marcelo.team.draw.core.datasource.local.database.dao.player

import androidx.room.*
import esser.marcelo.team.draw.core.datasource.local.database.dao.AppDao
import esser.marcelo.team.draw.core.datasource.local.entity.SoccerEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlayerDao: AppDao<SoccerEntity> {
    @Query("SELECT * FROM soccer")
    abstract fun getAll(): Flow<List<SoccerEntity>>

    @Query("DELETE FROM soccer WHERE id = :id")
    abstract suspend fun delete(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(player: SoccerEntity)
}