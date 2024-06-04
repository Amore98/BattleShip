package io.garrit.android.demo.memoryexample.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.garrit.android.multiplayer.GameResult
import se.ju.dimp2022.battleship.group21.OE.MyScreen.Screen
import se.ju.dimp2022.battleship.group21.OE.R
import se.ju.dimp2022.battleship.group21.OE.ViewModels.GameViewModel
import se.ju.dimp2022.battleship.group21.OE.ViewModels.Ship
import se.ju.dimp2022.battleship.group21.OE.ViewModels.cell

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    navController: NavController
) {
    val ships = gameViewModel.ships
    if (gameViewModel.relaseTurn.value&&gameViewModel.gameOn.value){
        gameViewModel.relaseTurn.value = false
        navController.navigate(Screen.MyAttackScreen.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {

        Image(
            painter = painterResource(id = R.drawable.u),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center


        ) {
            if(!gameViewModel.imReady.value ) {
                Button(

                    onClick = {
                        gameViewModel.playerReadyLogic()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),

                ) {
                    Text("Ready")
                }
            }

            Gameboard(ships, gameViewModel)

            ships.forEach { ship ->
                ShipView(ship = ship, gameViewModel = gameViewModel)
            }
            if (gameViewModel.gameFinished.value) {
                val result = gameViewModel.gameResult ?: GameResult.LOSE
                GameEndScreen(
                    gameResult = result,
                    navController = navController
                )
            }



        }


    }

}


@Composable
fun Gameboard(ships: List<Ship>, gameViewModel: GameViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)

    ) {
        items(gameViewModel.gameCellGrid) {cell ->
            CardView(cell,gameViewModel)
        }


    }


}


@Composable
fun CardView(cell: cell, gameViewModel: GameViewModel) {


val backgroundColor = if(cell.empty.value) Color.Cyan else Color.Gray


    Column(
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .background(backgroundColor)
             , content = {}

    )
}



@Composable
fun ShipView(ship: Ship,gameViewModel: GameViewModel) {
    val shipSize = 40.dp * (ship.size ?: 1)

    var myrotate by remember { mutableStateOf(0f)}


    Image(
        painter = painterResource(id = getShipImage(ship.size)),
        contentDescription = "",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .size(40.dp, shipSize)



            .offset(
                (ship.x.value / LocalDensity.current.density).dp,
                (ship.y.value / LocalDensity.current.density).dp
            )
            .rotate(myrotate)


            .clickable {


                gameViewModel.toggleShipOrientation(ship)

                myrotate+=90

            }
           .pointerInput(Unit) {


                detectDragGestures(

                    onDragEnd = {


                        gameViewModel.updateShipPosition(ship)

                        println("position ${ship.x.value},${ship.y.value} " )
                                },
                    onDrag = { change, dragAmount ->
                        if (!gameViewModel.imReady.value) {

                            change.consume()
                            ship.x.value += dragAmount.x
                            ship.y.value += dragAmount.y

                        }
                    }

                )



            }

    )
}
private fun getShipImage(size: Int): Int {
    return when (size) {
        1 -> R.drawable.ship1
        2 -> R.drawable.ship2
        3 -> R.drawable.ship3
        4 -> R.drawable.ship4
        else -> R.drawable.game
    }
}
@Composable
fun GameEndScreen(gameResult: GameResult, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (gameResult) {
                GameResult.WIN -> {
                    Text(text = "Congratulations! You won!", color = Color.Green)
                }
                GameResult.LOSE -> {
                    Text(text = "Game over! You lost!", color = Color.Red)
                }
                else -> {
                    // Handle other game result states if needed
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Navigate back to the lobby or any other desired screen
                    navController.navigate(Screen.MainScreen.route)
                }
            ) {
                Text(text = " Play Again")
            }
        }
    }
}
