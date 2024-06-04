
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService
import se.ju.dimp2022.battleship.group21.OE.MyScreen.Screen
import se.ju.dimp2022.battleship.group21.OE.R
import se.ju.dimp2022.battleship.group21.OE.ViewModels.LobbyViewModel




@Composable
fun LobbyScreen(lobbyViewModel: LobbyViewModel = viewModel(), navController: NavController = rememberNavController()) {

    val invite = lobbyViewModel.myInvites()

    if(lobbyViewModel.serverStatus.collectAsState().value.toString()=="GAME"){
        navController.navigate(Screen.GameScreen.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Image(
            painter = painterResource(id = R.drawable.lobby),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LobbyPlayerList(players = lobbyViewModel.players, lobbyViewModel = lobbyViewModel)
        }


        invite?.let { game ->
            AcceptDeclineButtons(
                onAccept = { lobbyViewModel.acceptGameRequest(game)


                           },
                onDecline = { lobbyViewModel.declineGameRequest(game) },
                navController = navController
            )
        }
    }
}

@Composable
fun LobbyPlayerList(players: List<Player>, lobbyViewModel: LobbyViewModel) {


    val currentUser = SupabaseService.player


    val filteredPlayers = players.filter { it.id != currentUser?.id }

    LazyColumn(contentPadding = PaddingValues(top = 10.dp)) {
        items(filteredPlayers) { player ->
            PlayerCard(player = player, lobbyViewModel = lobbyViewModel)
        }
    }
//    LazyColumn(contentPadding = PaddingValues(top = 10.dp)) {
//        items(players) { player ->
//
//               PlayerCard(player = player, lobbyViewModel = lobbyViewModel)
//        }
//    }
}

@Composable
fun PlayerCard(player: Player, lobbyViewModel: LobbyViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = CircleShape)

//                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loobybayern),
                    contentDescription = "Player Image",


                    modifier = Modifier.fillMaxSize()

                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()
            ){
                Spacer(modifier = Modifier.height(8.dp))


                val inviteSent by remember(player.id) { mutableStateOf(lobbyViewModel.isInviteSent(player)) }

                PlayerInviteActions(
                    player = player,
                    lobbyViewModel = lobbyViewModel,
                    inviteSent = inviteSent
                ) { updatedInviteSent ->
                    lobbyViewModel.updateInviteSent(player, updatedInviteSent)
                }

            }
        }
    }
}

@Composable
fun PlayerInviteActions(player: Player, lobbyViewModel: LobbyViewModel, inviteSent: Boolean, onInviteSent: (Boolean) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = player.name, modifier = Modifier.padding(16.dp))
            PlayerActionButtons(
                onInvite = {
                    lobbyViewModel.sendGameRequest(player)

                    onInviteSent(true)
                },

                inviteSent = inviteSent,

            )
        }
    }
}



@Composable
fun PlayerActionButtons(
    onInvite: () -> Unit,

    inviteSent: Boolean,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!inviteSent) {
            Button(
                onClick = { onInvite() }, modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Invite")
            }

        }
    }
}
@Composable
fun AcceptDeclineButtons(onAccept: () -> Unit, onDecline: () -> Unit, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onAccept()

                },
                modifier = Modifier
                    .weight(1.5f)
                    .padding(horizontal = 1.dp)
            ) {
                Text("Accept")
            }

            Button(
                onClick = { onDecline() },
                modifier = Modifier
                    .weight(1.5f)
                    .padding(horizontal = 1.dp)
            ) {
                Text("Decline")
            }
        }
    }
}
