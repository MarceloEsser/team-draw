package esser.marcelo.team.draw.core.model

import esser.marcelo.team.draw.core.datasource.local.entity.SoccerEntity

data class Soccer(
    val id: Long? = null,
    val name: String,
    val rating: Int = 0,
    var isPlaying: Boolean = false
)
fun Soccer.toEntity(): SoccerEntity {
    return SoccerEntity(name = name, rating = rating)
}