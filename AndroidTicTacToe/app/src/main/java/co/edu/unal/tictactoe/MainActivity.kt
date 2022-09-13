package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
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


class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame
    private lateinit var mInfoTextView: TextView
    private lateinit var mDifficultyTextView: TextView
    private lateinit var mVictoryHumanTextView: TextView
    private lateinit var mTieTextView: TextView
    private lateinit var mVictoryCpuTextView: TextView
    private lateinit var mBoardView: BoardView
    private var mGameOver: Boolean = false
    private var humanTurn: Boolean = true
    private var victories: ArrayList<Int> = arrayListOf(0,0,0)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharArray("board", mGame.getBoardState())
        outState.putBoolean("mGameOver",mGameOver)
        //outState.putIntegerArrayList("victories", victories)
        outState.putCharSequence("difficult", mDifficultyTextView.text)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putInt("startPlay", startPlay)
        outState.putBoolean("winLine", mBoardView.getWinLineBool())
        outState.putInt("winLineNum", mBoardView.getWinLineInt())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        victories[0] = mPrefs!!.getInt("mHumanWins", 0);
        victories[2] = mPrefs!!.getInt("mComputerWins", 0);
        victories[1] = mPrefs!!.getInt("mTies", 0);

        mGame = TicTacToeGame()

        when(mPrefs!!.getInt("difficulty", 0)){
            0 -> mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
            1 -> mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
            2 -> mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
        }

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
            moves++
            if (!mGameOver && humanTurn) {
                if(setMove(TicTacToeGame().HUMAN_PLAYER, pos)){
                    try{
                        mHumanMediaPlayer?.start()
                    }catch (e: IllegalStateException){
                        println("Fix it")
                    }
                    var winner: Int = mGame.checkForWinner()
                    if (winner == 0) {
                        humanTurn = false
                        mInfoTextView.text = "It's Android's turn."
                        handler.postDelayed({
                            playComputer()
                        }, 2000)
                    }
                    if(humanTurn){
                        verifyWin(winner)
                    }

                }
            }
            false
        }
        if(savedInstanceState == null){
            startNewGame()
        }else{
            if(!handlerflag){
                playComputer()
            }
        }
        showResults()
    }

    private fun playComputer(){
        val move = mGame.getComputerMove()
        try {
            mComputerMediaPlayer?.start()
        }catch (e: IllegalStateException){
            println("Fix it")
        }
        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
        mBoardView.invalidate()
        val winner = mGame.checkForWinner()
        verifyWin(winner)
        humanTurn = true
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
        showResults()
        mBoardView.invalidate()
    }

    private fun verifyWin(winner : Int){
        if (winner == 0) {
            mInfoTextView.text = "It's your turn."
        } else if (winner == 1) {
            mInfoTextView.text = "It's a tie!"
            victories[1]++
            mGameOver = true
            mTieMediaPlayer?.start()
        } else if (winner == 2) {
            mInfoTextView.text = "You won!"
            victories[0]++
            mGameOver = true
            mHumanVictoryMediaPlayer?.start()
        } else {
            mInfoTextView.text = "Android won!"
            victories[2]++
            mGameOver = true
            mComputerVictoryMediaPlayer?.start()
        }
        if(mGameOver){
            startPlay++
            showResults()
            mBoardView.drawWinnerLine(mGame.getwinnerLine())
            mBoardView.invalidate()
            startGameAlert(winner)
        }
    }

    private fun startNewGame() {
        if(!mGameOver && moves > 0){
            victories[1]++
        }
        moves = 0
        mInfoTextView.text = "You go first."
        mGameOver = false
        mGame.clearBoard()
        mBoardView.resetWinnerLine()
        mBoardView.invalidate()
        handler.removeCallbacksAndMessages(null)

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
        if(startPlay % 2 != 0){
            humanTurn = false
            mInfoTextView.text = "It's Android's turn."
            playComputer()
        }
    }

    private fun showResults(){
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
            R.id.reset -> {
                resetScores()
                return true
            }
        }
        return false
    }

    private fun difficultyAlert() {
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
    }

    fun resetScores() {
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
            when(win){
                1 ->{setTitle("Empate")}
                2 ->{setTitle("Ganaste")}
                3 ->{setTitle("Perdiste")}
            }
            setMessage("Iniciar nuevo juego?")
            setPositiveButton("Si", startgameButtonClick )
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
        // Save the current scores
        val ed : SharedPreferences.Editor = mPrefs!!.edit()
        ed.putInt("mHumanWins", victories[0])
        ed.putInt("mComputerWins", victories[2])
        ed.putInt("mTies", victories[1])
        when(mGame.getDifficultyLevel()){
            TicTacToeGame.DifficultyLevel.Easy -> ed.putInt("difficulty", 0)
            TicTacToeGame.DifficultyLevel.Harder -> ed.putInt("difficulty", 1)
            TicTacToeGame.DifficultyLevel.Expert -> ed.putInt("difficulty", 2)
        }
        ed.commit()
    }
}