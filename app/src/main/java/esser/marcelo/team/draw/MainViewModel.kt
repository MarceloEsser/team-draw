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
import esser.marcelo.team.draw.core.model.Team
import esser.marcelo.team.draw.core.repository.player.PlayerRepository
import esser.marcelo.team.draw.infra.di.PlayersRepository
import kotlinx.coroutines.launch
import java.util.function.Consumer
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    @PlayersRepository private val repository: PlayerRepository,
) : ViewModel() {

    private val _selectedSoccer: MutableState<Soccer?> = mutableStateOf(null)
    val selectedSoccer: State<Soccer?>
        get() = _selectedSoccer

    var progress: MutableState<Float> = mutableStateOf(0f)

    private var _soccerPlayers: SnapshotStateList<Soccer> = mutableStateListOf()
    var soccerPlayers: SnapshotStateList<Soccer>
        get() {
            fetchPlayers()
            return _soccerPlayers
        }
        set(value) {
            _soccerPlayers = value
        }

    private var _teams: SnapshotStateList<Team> = mutableStateListOf()
    var teams: SnapshotStateList<Team>
        get() {
            return _teams
        }
        set(value) {
            _teams = value
        }

    fun setSelectedSoccer(soccer: Soccer?) {
        if (_selectedSoccer.value?.id == soccer?.id) {
            _selectedSoccer.value = null
            return
        }
        _selectedSoccer.value = soccer
    }

    private fun fetchPlayers() {
        viewModelScope.launch {
            repository.getAll().collect { entities ->
                _soccerPlayers.clear()
                entities.forEach { entity ->
                    entity.isPlaying = true
                }
                _soccerPlayers.addAll(entities)
            }
        }
    }

    fun addPlayer(name: String) {
        val soccer = Soccer(name = name, rating = progress.value.toInt())
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
        val index = _soccerPlayers.indexOfFirst { it.id == id }
        val player = _soccerPlayers[index]
        _soccerPlayers[index] = player.copy(isPlaying = player.isPlaying.not())
    }

    fun drawTeams() {
        _teams.clear()
        val playingPlayers = _soccerPlayers.filter { it.isPlaying }.toMutableList()
        while (playingPlayers.size >= 5) {
            val teamPlayers = mutableListOf<Soccer>()
            var teamPower = 0;
            for (i in 0..4) {
                val player = playingPlayers.random()
                teamPlayers.add(player)
                teamPower += player.rating
                playingPlayers.remove(player)
            }
            teamPlayers.sortBy { it.rating }
            _teams.add(Team(power = teamPower, players = ArrayList(teamPlayers)))
            _teams.sortBy { it.power }
        }
        reEquilibrateTeams()
    }

    private fun reEquilibrateTeams() {
        if (teams.size < 2) return

        while (teams[_teams.size - 1].power - _teams.first().power >= 4) {
            val strongestPlayer: Soccer = _teams[_teams.size - 1].players[0]
            val weakestPlayer: Soccer = _teams[0].players[0]
            _teams[_teams.size - 1].players.removeAt(0)
            _teams[_teams.size - 1].power = _teams[_teams.size - 1].power - strongestPlayer.rating
            _teams[0].players.removeAt(0)
            _teams[0].power = _teams[0].power - weakestPlayer.rating
            _teams[_teams.size - 1].players.add(weakestPlayer)
            _teams[_teams.size - 1].power = _teams[_teams.size - 1].power + weakestPlayer.rating
            _teams[0].players.add(strongestPlayer)
            _teams[0].power = _teams[0].power + strongestPlayer.rating
            _teams.sortedBy { it.power }
        }

        val randomPositions: MutableList<Int> = ArrayList()
        while (randomPositions.size < (Math.random() * 4).toInt()) {
            val randomNumber = (Math.random() * 4).toInt() + 1
            if (!randomPositions.contains(randomNumber)) {
                randomPositions.add(randomNumber)
            }
        }

        for (randomPosition in randomPositions) {
            val (_, players) = teams[0]
            val (_, players1) = teams[1]
            val firstTeamRandomPLayer = players[randomPosition]

            val secondRandomPlayer = players1.stream()
                .findFirst()
                .filter { player: Soccer -> player.rating == firstTeamRandomPLayer.rating }

            secondRandomPlayer.ifPresent { player: Soccer? ->
                players.remove(firstTeamRandomPLayer)
                players.add(player!!)
                players1.remove(player)
                players1.add(firstTeamRandomPLayer)
            }
        }

        teams.sortBy { it.power }
        teams.forEach { team ->
            team.players.sortBy { player -> player.rating }
        }
    }
}