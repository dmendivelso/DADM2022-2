package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder



class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var mInfoTextView: TextView
    private lateinit var mDifficultyTextView: TextView
    private lateinit var mBoardView: BoardView
    private var mGameOver: Boolean = false
    private var humanTurn: Boolean = true

    private lateinit var mHumanMediaPlayer: MediaPlayer
    private lateinit var mComputerMediaPlayer: MediaPlayer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGame = TicTacToeGame()
        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mBoardView = findViewById<View>(R.id.board) as BoardView
        mBoardView.setGame(mGame)

        mBoardView.setOnTouchListener { v, event ->
            val col = event.x.toInt() / mBoardView.getBoardCellWidth()
            val row = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col
            if (!mGameOver && humanTurn && setMove(TicTacToeGame().HUMAN_PLAYER, pos)) {
                mHumanMediaPlayer.start()
                var winner: Int = mGame.checkForWinner()
                if (winner == 0) {
                    humanTurn = false
                    mInfoTextView.text = "It's Android's turn."
                    Handler(Looper.getMainLooper()).postDelayed({
                        val move = mGame.getComputerMove()
                        mComputerMediaPlayer.start()
                        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
                        mBoardView.invalidate()
                        winner = mGame.checkForWinner()
                        verifyWin(winner)
                        humanTurn = true
                    }, 2000)
                }
                if(humanTurn){
                    verifyWin(winner)
                }

            }
            false
        }
        startNewGame()
    }

    private fun verifyWin(winner : Int){
        if (winner == 0) {
            mInfoTextView.text = "It's your turn."
        } else if (winner == 1) {
            mInfoTextView.text = "It's a tie!"
        } else if (winner == 2) {
            mInfoTextView.text = "You won!"
            mGameOver = true
        } else {
            mInfoTextView.text = "Android won!"
            mGameOver = true
        }
    }


    private fun startNewGame() {
        mGameOver = false
        mGame.clearBoard()
        mBoardView.invalidate()
        when (mGame.getDifficultyLevel()) {
            TicTacToeGame.DifficultyLevel.Easy -> {
                mDifficultyTextView.text = "The difficulty is easy"
            }
            TicTacToeGame.DifficultyLevel.Harder -> {
                mDifficultyTextView.text = "The difficulty is hard"
            }
            TicTacToeGame.DifficultyLevel.Expert -> {
                mDifficultyTextView.text = "The difficulty is expert"
            }
        }
        mInfoTextView.setText("You go first.");
    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate() // Redraw the board
            return true;
        }
        return false;
    }

    @Suppress("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu is MenuBuilder) {
            (menu).setOptionalIconsVisible(true)
        }
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_game -> {
                startNewGame()
                return true
            }
            R.id.ai_difficulty -> {
                difficultyAlert()
                return true
            }
            R.id.quit -> {
                quitGameAlert()
                return true
            }
        }
        return false
    }

    fun difficultyAlert() {
        var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        val levels = arrayOf<CharSequence>(
            resources.getString(R.string.difficulty_easy),
            resources.getString(R.string.difficulty_harder),
            resources.getString(R.string.difficulty_expert)
        )
        with(builder)
        {
            setTitle(R.string.difficulty_choose);
            setSingleChoiceItems(levels, -1) { dialogInterface, item ->
                when (item) {
                    0 -> {
                        var difficulty = TicTacToeGame.DifficultyLevel.Easy
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    1 -> {
                        var difficulty = TicTacToeGame.DifficultyLevel.Harder
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                    2 -> {
                        var difficulty = TicTacToeGame.DifficultyLevel.Expert
                        mGame.setDifficultyLevel(difficulty)
                        startNewGame()
                    }
                }
                // Display the selected difficulty level
                Toast.makeText(applicationContext, levels[item], Toast.LENGTH_SHORT).show()
                dialogInterface.dismiss()
            }
        }
        dialog = builder.create()
        // Finally, display the alert dialog
        dialog.show()
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        finish()
    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext, "Continuemos jugando", Toast.LENGTH_SHORT).show()
    }

    fun quitGameAlert() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Cerrar juego")
            setMessage("Desea cerrar el juego?")
            setPositiveButton("Si", positiveButtonClick)
            setNegativeButton("No", negativeButtonClick)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer.release()
        mComputerMediaPlayer.release()
    }

}