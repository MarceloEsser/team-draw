package esser.marcelo.team.draw.core.model

import esser.marcelo.team.draw.core.datasource.local.entity.SoccerEntity

data class Soccer(
    val id: Long? = null,
    val name: String,
    val isFatDude: Boolean = false,
    var isPlaying: Boolean = false
) {
    fun changeSoccerStatus() {
        isPlaying = isPlaying.not()
    }
}

fun Soccer.toEntity(): SoccerEntity {
    return SoccerEntity(name = name, isFatDude = isFatDude)
}