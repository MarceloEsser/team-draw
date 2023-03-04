package esser.marcelo.team.draw.core.repository.player

import esser.marcelo.team.draw.core.datasource.local.database.dao.player.PlayerDao
import esser.marcelo.team.draw.core.datasource.local.entity.toModel
import esser.marcelo.team.draw.core.model.Soccer
import esser.marcelo.team.draw.core.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(private val dao: PlayerDao) : PlayerRepository {

    override suspend fun getAll(): Flow<List<Soccer>> {
        return flow {
            dao.getAll().collect {
                this.emit(it.map { entity -> entity.toModel() })
            }
        }
    }

    override suspend fun insert(soccer: Soccer) {
        dao.insert(soccer.toEntity())
    }

    override suspend fun delete(soccer: Soccer) {
        if (soccer.id == null) throw IllegalArgumentException("Player id cannot be null")
        dao.delete(soccer.id)
    }

    override suspend fun update(soccer: Soccer) {
        if (soccer.id == null) throw IllegalArgumentException("Player id cannot be null")
        dao.update(soccer.toEntity())
    }
}