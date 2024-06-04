package se.ju.dimp2022.battleship.group21.OE.MyScreen


sealed class Screen(val route: String) {
    object MainScreen : Screen(route = "MainScreen")
    object LobbyScreen : Screen(route = "LobbyScreen")
    object GameScreen : Screen(route = "GameScreen")
    
    object MyAttackScreen : Screen(route = "player2Screen")


}
