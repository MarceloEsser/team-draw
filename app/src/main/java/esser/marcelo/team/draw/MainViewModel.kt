package esser.marcelo.team.draw

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import esser.marcelo.team.draw.core.model.Soccer
import esser.marcelo.team.draw.core.repository.player.PlayerRepository
import esser.marcelo.team.draw.infra.di.PlayersRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @PlayersRepository private val repository: PlayerRepository
) : ViewModel() {

    private val _selectedSoccer: MutableState<Soccer?> = mutableStateOf(null)
    val selectedSoccer: State<Soccer?>
        get() = _selectedSoccer

    private var _soccers: SnapshotStateList<Soccer> = mutableStateListOf()
    var soccers: SnapshotStateList<Soccer>
        get() {
            fetchPlayers()
            return _soccers
        }
        set(value) {
            _soccers = value
        }

    private var _teams: SnapshotStateList<List<Soccer>> = mutableStateListOf()
    var teams: SnapshotStateList<List<Soccer>>
        get() {
            return _teams
        }
        set(value) {
            _teams = value
        }

    fun setSelectedSoccer(soccer: Soccer?) {
        if(_selectedSoccer.value?.id == soccer?.id) {
            _selectedSoccer.value = null
            return
        }
        _selectedSoccer.value = soccer
    }

    fun drawTeams() {
        val players = _soccers
        val numberOfTeams = players.filter { it.isPlaying }.size / 5
        for (i in 0 until numberOfTeams) {
            _teams.add(players.shuffled().take(5))
        }
    }


    private fun fetchPlayers() {
        viewModelScope.launch {
            repository.getAll().collect { entities ->
                _soccers.clear()
                _soccers.addAll(entities)
            }
        }
    }

    fun addPlayer(name: String, isFatDude: Boolean) {
        val soccer = Soccer(name = name, isFatDude = isFatDude)
        viewModelScope.launch {
            repository.insert(soccer)
        }
    }

    fun deletePlayer(soccer: Soccer) {
        viewModelScope.launch {
            repository.delete(soccer)
        }
    }

    fun changeSoccerStatus(id: Long?) {
        val index = _soccers.indexOfFirst { it.id == id }
        val player = _soccers[index]
        _soccers[index] = player.copy(isPlaying = player.isPlaying.not())
    }
}