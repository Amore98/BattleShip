package se.ju.dimp2022.battleship.group21.OE.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.ServerState
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class LobbyViewModel : ViewModel() {
    val players = SupabaseService.users
    val games = SupabaseService.games
    val serverStatus: MutableStateFlow<ServerState> = SupabaseService.serverState

    private val inviteSentMap = mutableMapOf<Player, Boolean>()

    fun isInviteSent(player: Player): Boolean {
        return inviteSentMap[player] ?: false
    }



    fun updateInviteSent(player: Player, sent: Boolean) {
        inviteSentMap[player] = sent
    }
    fun myInvites(): Game? {
        return games.firstOrNull { it?.player2?.id == SupabaseService.player?.id }
    }


    fun sendGameRequest(player: Player) {
        viewModelScope.launch {
            SupabaseService.invite(player)

            updateInviteSent(player, true)
        }
    }

    fun acceptGameRequest(game: Game) {
        viewModelScope.launch {
            SupabaseService.acceptInvite(game)



        }

    }


    fun declineGameRequest(game: Game) {
        viewModelScope.launch {
            SupabaseService.declineInvite(game)


        }
    }
    fun joinLobby(player: Player){
        viewModelScope.launch {
            SupabaseService.joinLobby(player)
        }

    }
}








