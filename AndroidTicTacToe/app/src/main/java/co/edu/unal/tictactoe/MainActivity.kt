package co.edu.unal.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame

    private lateinit var mBoardButtons: Array<Button?>
    private lateinit var mInfoTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardButtons = arrayOfNulls(TicTacToeGame().BOARD_SIZE)
        mBoardButtons[0] = findViewById<View>(R.id.one) as Button
        mBoardButtons[1] = findViewById<View>(R.id.two) as Button
        mBoardButtons[2] = findViewById<View>(R.id.three) as Button
        mBoardButtons[3] = findViewById<View>(R.id.four) as Button
        mBoardButtons[4] = findViewById<View>(R.id.five) as Button
        mBoardButtons[5] = findViewById<View>(R.id.six) as Button
        mBoardButtons[6] = findViewById<View>(R.id.seven) as Button
        mBoardButtons[7] = findViewById<View>(R.id.eight) as Button
        mBoardButtons[8] = findViewById<View>(R.id.nine) as Button
        mInfoTextView = findViewById<View>(R.id.information) as TextView

        mGame = TicTacToeGame()

        startNewGame()
    }

    private fun startNewGame() {
        mGame.clearBoard()

        // Reset all buttons
        for (i in mBoardButtons.indices) {
            mBoardButtons[i]!!.text = ""
            mBoardButtons[i]!!.isEnabled = true
            mBoardButtons[i]!!.setOnClickListener {
                if (mBoardButtons[i]!!.isEnabled) {
                    setMove(TicTacToeGame().HUMAN_PLAYER, i)
                    // If no winner yet, let the computer make a move
                    var winner : Int = mGame.checkForWinner()
                    if (winner == 0) {
                        mInfoTextView.text = "It's Android's turn."
                        val move = mGame.getComputerMove()
                        setMove(TicTacToeGame().COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                    }
                    if (winner == 0) {
                        mInfoTextView.text = "It's your turn."
                    } else if (winner == 1) {
                        mInfoTextView.text = "It's a tie!"
                    } else if (winner == 2) {
                        mInfoTextView.text = "You won!"
                    } else {
                        mInfoTextView.text = "Android won!"
                    }

                }
            }
        }

        mInfoTextView.setText("You go first.");
    }

    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false
        mBoardButtons[location]!!.text = player.toString()
        if (player == TicTacToeGame().HUMAN_PLAYER) {
            mBoardButtons[location]?.setTextColor(Color.rgb(0, 200, 0))
        } else {
            mBoardButtons[location]?.setTextColor(Color.rgb(200, 0, 0))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add("New Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startNewGame()
        return true
    }


}