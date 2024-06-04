package se.ju.dimp2022.battleship.group21.OE.ViewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.ActionResult
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.SupabaseCallback
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch

class Ship(
    val size: Int,

    x : Float,
    y : Float,
    orientation: Orientation = Orientation.VERTICAL,

    ) {
    var x = mutableFloatStateOf(x)
    var y = mutableFloatStateOf(y)
    var orientation = mutableStateOf(orientation)
    var hits = mutableIntStateOf(0)
    fun isSunk(): Boolean {
        return hits.value == size
    }






}
class cell(){
    var empty = mutableStateOf(true)

}
class attackcell(
    x: Int,
    y: Int,
){
//    var empty = mutableStateOf(true)
    val x = x
    val y = y
    var hit = mutableStateOf(false)
    var miss = mutableStateOf(false)
    var sunk = mutableStateOf(false)

}
enum class Orientation {
    HORIZONTAL, VERTICAL
}

@SuppressLint("SuspiciousIndentation")
class GameViewModel : ViewModel(), SupabaseCallback {

    private val _ships = listOf(
        Ship(size = 4, -585.5292f, 1024.0963f, Orientation.VERTICAL),
        Ship(size = 3, -365.75513f, 1007.4595f, Orientation.VERTICAL),
        Ship(size = 2, -163.72134f, 973.02344f, Orientation.VERTICAL),
        Ship(size = 2, 74.60945f, 980.7355f, Orientation.VERTICAL),
        Ship(size = 1, 336.9688f, 1004.18533f, Orientation.VERTICAL),
        Ship(size = 1, 573.9590f, 992.188233f, Orientation.VERTICAL)
    )

    var gameCellGrid = mutableListOf<cell>()

    var imReady = mutableStateOf(false)
    var opponentReady by mutableStateOf(false)
        private set
    var relaseTurn = mutableStateOf(false)
    var attackCellGrid = mutableListOf<attackcell>()
    private var CellL: MutableState<attackcell?> = (mutableStateOf(null))
    var gameOn = mutableStateOf(false)
    var gameFinished = mutableStateOf(false)
    var attackResults = mutableMapOf<attackcell, ActionResult>()
    var gameResult by mutableStateOf<GameResult?>(null)
    ////////////////////////////
    var gameResultMessage by mutableStateOf("")
   ////////////


    val ships: List<Ship>
        get() = _ships.toList()

    init {
        SupabaseService.callbackHandler = this
        for (ix in 0 until 100) {
            gameCellGrid.add(cell())
        }
        for (y in 0..9) {
            for (x in 0..9)
                attackCellGrid.add(attackcell(x, y))
        }

    }

    fun updateShipPosition(ship: Ship) {
        for (i in 0 until gameCellGrid.size) {
            gameCellGrid[i].empty.value = true
        }

        for (currentShip in _ships) {
            for (length in 0 until currentShip.size) {
                var x = ((currentShip.x.value + 700) / 140).toInt()
                var y = ((currentShip.y.value + 700) / 140).toInt()

                when (currentShip.orientation.value) {
                    Orientation.VERTICAL -> y += length
                    Orientation.HORIZONTAL -> x += length
                }

                val position = x + (10 * y)
                if (position in 0 until 100) {
                    gameCellGrid[position].empty.value = false
                }
            }
        }


    }

    private fun allShipsPlaced(): Boolean {
        var x = 0
        for (all in 0 until 100) {
            if (!gameCellGrid[all].empty.value) {
                x += 1
            }
        }
        return x == 13

    }

    fun toggleShipOrientation(ship: Ship) {
        ship.orientation.value = when (ship.orientation.value) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }

        updateShipPosition(ship)
    }

    fun playerReadyLogic() {
        if (allShipsPlaced()) {
            imReady.value = true

            viewModelScope.launch {
                SupabaseService.playerReady()
            }
            if(imReady.value&&opponentReady){
                gameOn.value=true
            }
            if ( SupabaseService.currentGame!!.player1.id != SupabaseService.player!!.id)

                viewModelScope.launch {
                    SupabaseService.releaseTurn()
                }
        }
    }

    private fun sendAnswerToOpponent(result: ActionResult) {

        viewModelScope.launch {
            SupabaseService.sendAnswer(result)
        }
    }
    private fun updateGameOnHit(x: Int, y: Int) {
        val position = x + (10 * y)
        gameCellGrid[position].empty.value = false
        var result: ActionResult

        val hitShip = _ships.find { ship ->
            val shipX = ((ship.x.value + 700) / 140).toInt()
            val shipY = ((ship.y.value + 700) / 140).toInt()

            (shipX == x && shipY == y) || (ship.orientation.value == Orientation.VERTICAL && shipX == x && shipY + ship.size > y && shipY <= y) ||
                    (ship.orientation.value == Orientation.HORIZONTAL && shipY == y && shipX + ship.size > x && shipX <= x)
        }

        if (hitShip != null) {
            result = ActionResult.HIT
            sendAnswerToOpponent(result)


            hitShip.hits.value += 1

            if (hitShip.isSunk()) {
                result = ActionResult.SUNK
                sendAnswerToOpponent(result)

            }
        } else {
            result = ActionResult.MISS
            sendAnswerToOpponent(result)

            viewModelScope.launch {
                SupabaseService.releaseTurn()
            }
        }

        val attackedCell = attackCellGrid.find { it.x == x && it.y == y }
        attackedCell?.let {
            attackResults[it] = result
        }

        checkForWin()
    }




    fun performAttack(x: Int, y: Int) {
        // check the attack
        println("Performing attack at ($x, $y)")


        CellL.value  = attackCellGrid.find { it.x == x && it.y == y }!!


        CellL.value!!.let {
            if (!(it.hit.value || it.miss.value || it.sunk.value)) {

                it.hit.value = true
                viewModelScope.launch {
                    SupabaseService.sendTurn(x, y)
                }
            }
        }
    }




//    private fun checkForWin() {
//        val totalHits = attackCellGrid.count { it.hit.value }
//
//        if (totalHits >= 13) {
//            viewModelScope.launch {
//                gameFinished.value= true
//                SupabaseService.gameFinish(GameResult.LOSE)
//            }
//        }
//    }
    private fun checkForWin(){
        if (_ships.all { it.isSunk() }) {
            viewModelScope.launch {
                gameFinished.value = true
                SupabaseService.gameFinish(GameResult.WIN)
            }
        }
    }



    override suspend fun playerReadyHandler() {
        opponentReady = true
        if(imReady.value&&opponentReady){
            gameOn.value=true
        }
    }


    override suspend fun releaseTurnHandler() {
        relaseTurn.value = true


    }


    override suspend fun actionHandler(x: Int, y: Int) {
        // check the action
        println("Received action: x=$x, y=$y")

        if (opponentReady && imReady.value) {
            val hitShip = _ships.find { ship ->
                    val shipX = ((ship.x.value + 700) / 140).toInt()
                    val shipY = ((ship.y.value + 700) / 140).toInt()


                    (shipX == x && shipY == y) || (ship.orientation.value == Orientation.VERTICAL && shipX == x && shipY + ship.size > y && shipY <= y) ||
                            (ship.orientation.value == Orientation.HORIZONTAL && shipY == y && shipX + ship.size > x && shipX <= x)
                }

                if (hitShip != null) {
                    updateGameOnHit(x, y)
                } else {
                    updateGameOnHit(x, y)

                }


            }
        }


    override suspend fun answerHandler(status: ActionResult) {
        // check the  answer i receive
        Log.d("SupabaseCallback", "Received answer: $status")
        when (status) {
            ActionResult.MISS -> {
                viewModelScope.launch {
                    SupabaseService.releaseTurn()
                }
                relaseTurn.value = true
                CellL?.value?.miss?.value = true

            }

            ActionResult.HIT -> {
                CellL?.value?.hit?.value = true


            }

            ActionResult.SUNK -> {
                CellL?.value?.sunk?.value = true
               checkForWin()


            }
        }
    }


    override suspend fun finishHandler(status: GameResult) {
        Log.d("SupabaseCallback", "Received answer: $status")
        when (status) {
            GameResult.WIN -> {
                gameFinished.value = true
//                gameResult = GameResult.WIN
                gameResultMessage = "Congratulations! You won!"

            }

            GameResult.LOSE -> {
                gameFinished.value = true
//                gameResult = GameResult.LOSE
                gameResultMessage = "Game over! You lost!"

            }

            else -> {
                gameResult = null

            }
        }


        }
    }

