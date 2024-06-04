package se.ju.dimp2022.battleship.group21.OE.MyScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.garrit.android.demo.memoryexample.screens.GameEndScreen
import io.garrit.android.multiplayer.GameResult
import se.ju.dimp2022.battleship.group21.OE.R
import se.ju.dimp2022.battleship.group21.OE.ViewModels.GameViewModel
import se.ju.dimp2022.battleship.group21.OE.ViewModels.attackcell


@Composable
fun MyAttackScreen(gameViewModel: GameViewModel, navController: NavController) {

    if (gameViewModel.relaseTurn.value){
        gameViewModel.relaseTurn.value = false
        navController.navigateUp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
        ) {

            Image(
                painter = painterResource(id = R.drawable.lobby),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(150.dp))
                Button(onClick = {

                }) {
                    Text(text = "Your  Turn", color = Color.Green)
                }


                AttackGameboard( gameViewModel)
                if (gameViewModel.gameFinished.value) {
                    val result = gameViewModel.gameResult ?: GameResult.WIN
                    GameEndScreen(
                        gameResult = result,
                        navController = navController
                    )
                }

            }
        }
    }



@Composable
fun AttackGameboard(gameViewModel: GameViewModel ) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(10),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(gameViewModel.attackCellGrid) { cell ->
            AttackCardView(cell, gameViewModel)
        }
    }
}
@Composable
fun AttackCardView(cell: attackcell, gameViewModel: GameViewModel ) {

    val backgroundColor = when {
        cell.sunk.value -> Color.Black
        cell.miss.value -> Color.Gray
        cell.hit.value -> Color.Red
        else -> Color.Cyan
    }
//   val backgroundColor = if (cell.empty.value) Color.Cyan else Color.Gray

    Log.d("Cell State", "Hit: ${cell.hit.value}, Miss: ${cell.miss.value}, Sunk: ${cell.sunk.value}")
    Column(
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .background(backgroundColor)
            .clickable {
                if (!(cell.hit.value || cell.miss.value || cell.sunk.value)) {

                    gameViewModel.performAttack(cell.x, cell.y)
                }

            },
        content = {}


    )
}
