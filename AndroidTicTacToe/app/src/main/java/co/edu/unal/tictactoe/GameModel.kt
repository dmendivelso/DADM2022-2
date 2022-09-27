package co.edu.unal.tictactoe

data class GameModel(val gameId: Int = 0, val movement: Int = -1, val activePlayer: Int = 1, val gameFull: Boolean = false){
    override fun toString(): String = gameId.toString()
}

