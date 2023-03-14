package esser.marcelo.team.draw.core.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import esser.marcelo.team.draw.core.model.Soccer

@Entity(tableName = "soccer")
class SoccerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val rating: Int = 0
)

fun SoccerEntity.toModel(): Soccer {
    return Soccer(
        id, name, rating
    )
}