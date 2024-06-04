package se.ju.dimp2022.battleship.group21.OE.MyScreen




import LobbyScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.garrit.android.demo.memoryexample.screens.GameScreen
import se.ju.dimp2022.battleship.group21.OE.ViewModels.GameViewModel

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val gameViewModel : GameViewModel = viewModel()


    NavHost(navController=navController,startDestination = Screen.MainScreen.route){

        composable(route = Screen.MainScreen.route) {
            MainScreen(lobbyViewModel = viewModel(),navController = navController)

        }
        composable(route = Screen.LobbyScreen.route) {
            LobbyScreen(lobbyViewModel = viewModel(), navController = navController)
        }
        composable(route = Screen.GameScreen.route){
            GameScreen(gameViewModel,navController = navController)
        }
        composable(route = Screen.MyAttackScreen.route){
            MyAttackScreen(gameViewModel,navController = navController)
        }

    }
}