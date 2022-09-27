package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var mGame: TicTacToeGame
    private lateinit var mInfoTextView: TextView
    private lateinit var mDifficultyTextView: TextView
    private lateinit var mVictoryHumanTextView: TextView
    private lateinit var mTieTextView: TextView
    private lateinit var mVictoryCpuTextView: TextView
    private lateinit var mBoardView: BoardView
    private var mGameOver: Boolean = false
    private var humanTurn: Boolean = true
    private var victories: ArrayList<Int> = arrayListOf(0, 0, 0)
    private var moves: Int = 0
    private var startPlay: Int = 0

    private var mHumanMediaPlayer: MediaPlayer? = null
    private var mHumanVictoryMediaPlayer: MediaPlayer? = null
    private var mComputerMediaPlayer: MediaPlayer? = null
    private var mComputerVictoryMediaPlayer: MediaPlayer? = null
    private var mTieMediaPlayer: MediaPlayer? = null

    private var mPrefs: SharedPreferences? = null

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var handlerflag: Boolean = false

    private var userUID: String? = ""
    private var gameId: Int? = 0
    private var numberPlayer: Int? = 0
    private var lastMovement: Int = -1

    private lateinit var database: DatabaseReference
    private lateinit var games: DatabaseReference

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharArray("board", mGame.getBoardState())
        outState.putBoolean("mGameOver", mGameOver)
        outState.putCharSequence("difficult", mDifficultyTextView.text)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putInt("startPlay", startPlay)
        outState.putBoolean("winLine", mBoardView.getWinLineBool())
        outState.putInt("winLineNum", mBoardView.getWinLineInt())
        outState.putBoolean("humanTurn", humanTurn)
        outState.putBoolean("mGameOver", mGameOver)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundle = intent.extras
        userUID = bundle?.getString("user")
        gameId = bundle?.getInt("gameId")
        numberPlayer = bundle?.getInt("numberPlayer")

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        database = Firebase.database.reference
        games = Firebase.database.getReference("games")

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        victories[0] = 0
        victories[2] = 0
        victories[1] = 0

        mGame = TicTacToeGame()

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mDifficultyTextView = findViewById<View>(R.id.difficulty) as TextView
        mVictoryHumanTextView = findViewById<View>(R.id.resultHuman) as TextView
        mTieTextView = findViewById<View>(R.id.resultTie) as TextView
        mVictoryCpuTextView = findViewById<View>(R.id.resultCpu) as TextView
        mBoardView = findViewById<View>(R.id.board) as BoardView
        mBoardView.setGame(mGame)

        mBoardView.setOnTouchListener { v, event ->
            val col = event.x.toInt() / mBoardView.getBoardCellWidth()
            val row = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col
            games.child(gameId.toString()).get().addOnSuccessListener {
                var game = it.getValue(GameModel::class.java)
                moves++
                if (!mGameOver && humanTurn && game!!.gameFull && numberPlayer == game!!.activePlayer) {
                    if (setMove(TicTacToeGame().HUMAN_PLAYER, pos)) {
                        try {
                            mHumanMediaPlayer?.start()
                        } catch (e: IllegalStateException) {
                            println("Fix it")
                        }
                        var winner: Int = mGame.checkForWinner()
                        games.child(gameId.toString()).child("movement").setValue(pos)
                        lastMovement = pos
                        if (game!!.activePlayer == 1) {
                            games.child(gameId.toString()).child("activePlayer").setValue(2)
                        } else {
                            games.child(gameId.toString()).child("activePlayer").setValue(1)
                        }
                        verifyWin(winner)
                    }
                }
            }
            false
        }

        games.child(gameId.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val game = dataSnapshot.getValue<GameModel>()
                    val moveActual = game?.movement!!
                    if (moveActual != lastMovement) {
                        if (lastMovement != -1 && moveActual == -1) {
                            startNewGame()
                        } else {
                            playRival(moveActual)
                        }
                    }
                    if (game?.gameFull == true) {
                        if (game?.activePlayer == numberPlayer && game?.activePlayer != 0) {
                            mInfoTextView.text = "It's your turn"
                        } else {
                            mInfoTextView.text = "Opponent turn"
                        }
                    } else {
                        mInfoTextView.text = "Waiting rival"
                    }
                }else{
                    if(numberPlayer == 2){
                        onBackPressed()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        if (savedInstanceState == null) {
            startNewGame()
        }
        showResults()
    }

    private fun playRival(move: Int) {
        try {
            mComputerMediaPlayer?.start()
        } catch (e: IllegalStateException) {
            println("Fix it")
        }
        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
        lastMovement = move
        mBoardView.invalidate()
        val winner = mGame.checkForWinner()
        verifyWin(winner)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mGame.setBoardState(savedInstanceState.getCharArray("board"))
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.text = savedInstanceState.getCharSequence("info")
        mDifficultyTextView.text = savedInstanceState.getCharSequence("difficult")
        startPlay = savedInstanceState.getInt("startPlay")
        mBoardView.setWinLineBool(savedInstanceState.getBoolean("winLine"))
        mBoardView.setWinLineInt(savedInstanceState.getInt("winLineNum"))
        humanTurn = savedInstanceState.getBoolean("humanTurn")
        mGameOver = savedInstanceState.getBoolean("mGameOver")
        showResults()
        mBoardView.invalidate()
    }

    private fun verifyWin(winner: Int) {
        if (winner == 0) {
            mInfoTextView.text = "It's your turn."
        } else if (winner == 1) {
            games.child(gameId.toString()).child("activePlayer").setValue(0)
            victories[1]++
            mGameOver = true
            mTieMediaPlayer?.start()
            mInfoTextView.text = "It's a tie!"
        } else if (winner == 2) {
            games.child(gameId.toString()).child("activePlayer").setValue(0)
            victories[0]++
            mGameOver = true
            mHumanVictoryMediaPlayer?.start()
            mInfoTextView.text = "You won!"
        } else {
            games.child(gameId.toString()).child("activePlayer").setValue(0)
            victories[2]++
            mGameOver = true
            mComputerVictoryMediaPlayer?.start()
            mInfoTextView.text = "Opponent won!"
        }
        if (mGameOver) {
            startPlay++
            showResults()
            mBoardView.drawWinnerLine(mGame.getwinnerLine())
            mBoardView.invalidate()
            if (numberPlayer == 1) {
                startGameAlert(winner)
            }
        }
    }

    private fun startNewGame() {
        moves = 0
        mInfoTextView.text = "You go first."
        mGameOver = false
        mGame.clearBoard()
        mBoardView.resetWinnerLine()
        mBoardView.invalidate()
    }

    private fun startNewGameDb() {
        games.child(gameId.toString()).child("movement").setValue(-1)
        lastMovement = 0
        if (startPlay % 2 != 0) {
            games.child(gameId.toString()).child("activePlayer").setValue(2)
        } else {
            games.child(gameId.toString()).child("activePlayer").setValue(1)
        }
    }

    private fun showResults() {
        mVictoryHumanTextView.text = "Human: " + victories[0]
        mTieTextView.text = "Tie: " + victories[1]
        mVictoryCpuTextView.text = "Cpu: " + victories[2]
    }


    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate() // Redraw the board
            return true
        }
        return false
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
            R.id.quit -> {
                quitGameAlert()
                return true
            }
            R.id.reset -> {
                resetScores()
                return true
            }
        }
        return false
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        finish()
    }

    val restartButtonClick = { dialog: DialogInterface, which: Int ->
        victories[0] = 0
        victories[1] = 0
        victories[2] = 0
        showResults()
    }

    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext, "Continuemos jugando", Toast.LENGTH_SHORT).show()
        mGameOver = true
        humanTurn = false
    }

    val startgameButtonClick = { dialog: DialogInterface, which: Int ->
        startNewGame()
        startNewGameDb()
    }

    fun resetScores() {
        database.child("users").child("Daniel").child("age").setValue(20)
        database.child("users").child("Daniel").child("blood").setValue("B+")
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Resetear puntajes")
            setMessage("Desea resetear los puntajes?")
            setPositiveButton("Si", restartButtonClick)
            setNegativeButton("No", negativeButtonClick)
            show()
        }
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

    fun startGameAlert(win: Int) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            when (win) {
                1 -> {
                    setTitle("Empate")
                }
                2 -> {
                    setTitle("Ganaste")
                }
                3 -> {
                    setTitle("Perdiste")
                }
            }
            setMessage("Iniciar nuevo juego?")
            setPositiveButton("Si", startgameButtonClick)
            setNegativeButton("No", negativeButtonClick)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.human)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)
        mHumanVictoryMediaPlayer = MediaPlayer.create(applicationContext, R.raw.victory_h)
        mComputerVictoryMediaPlayer = MediaPlayer.create(applicationContext, R.raw.victory_c)
        mTieMediaPlayer = MediaPlayer.create(applicationContext, R.raw.tie)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer?.release()
        mComputerMediaPlayer?.release()
        mHumanVictoryMediaPlayer?.release()
        mComputerVictoryMediaPlayer?.release()
        mTieMediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
        handlerflag = humanTurn
    }

    override fun onStop() {
        super.onStop()
        games.child(gameId.toString()).removeValue()
        // Save the current scores
        val ed: SharedPreferences.Editor = mPrefs!!.edit()
        ed.putString("user", userUID)
        ed.commit()
    }


}