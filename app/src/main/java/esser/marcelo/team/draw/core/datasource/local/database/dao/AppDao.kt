package esser.marcelo.team.draw.core.datasource.local.database.dao

import androidx.room.Insert

interface AppDao<T> {
    @Insert
    suspend fun insert(vararg obj: T)
}
