package se.ju.dimp2022.battleship.group21.OE.MyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.garrit.android.multiplayer.Player
import se.ju.dimp2022.battleship.group21.OE.R
import se.ju.dimp2022.battleship.group21.OE.ViewModels.LobbyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(lobbyViewModel: LobbyViewModel = viewModel(), navController: NavController = rememberNavController()) {
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.u),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.8f
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Battleship Game",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


            OutlinedTextField(
                value = name,


                onValueChange = {

                    name = it

                    nameError = false
                },
                label = { Text(text = "Enter Your Name",
                    color = Color.Red,) },

                isError = nameError ,




            )


            if (nameError) {
                Text(
                    text = "Name must be between 3 and 10 characters.",
                    color = Color.Red
                )
            }


            Button(
                onClick = {

                    if (name.isNotBlank() && !nameError && name.length in 3..10) {
                        val player = Player(name = name)
                         lobbyViewModel.joinLobby(player)

                            navController.navigate(Screen.LobbyScreen.route)

                    } else {
                        nameError = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)

            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start Game")
                Text("Game Lobby",color = Color.Red)
            }
        }
    }
}