package esser.marcelo.team.draw.core.repository.player

import esser.marcelo.team.draw.core.model.Soccer
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    suspend fun getAll(): Flow<List<Soccer>>
    suspend fun insert(soccer: Soccer)
    suspend fun delete(soccer: Soccer)
    suspend fun update(soccer: Soccer)
}