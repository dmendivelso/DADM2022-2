package co.edu.unal.tictactoe

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View



class BoardView: View {

    val GRID_WIDTH = 10
    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap
    private lateinit var mPaint: Paint
    private lateinit var mGame: TicTacToeGame

    fun initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x_2)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o_2)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG);
    }

    constructor(context: Context?): super(context) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs) {
        initialize()
    }

    fun setGame(game: TicTacToeGame) {
        mGame = game
    }

    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Determine the width and height of the View
        val boardWidth = width
        val boardHeight = height
        // Make thick, light gray lines

        val colors : IntArray = intArrayOf(Color.CYAN, Color.rgb(107,52,165))

        val shader: Shader = LinearGradient(0f, 0f, boardWidth.toFloat(), boardHeight.toFloat(), colors, null, Shader.TileMode.CLAMP)

        mPaint!!.color = Color.LTGRAY
        mPaint!!.strokeWidth = GRID_WIDTH.toFloat()
        mPaint!!.setShader(shader)

        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3
        canvas.drawLine(cellWidth.toFloat(), 0F, cellWidth.toFloat(), boardHeight.toFloat(), mPaint!!)
        canvas.drawLine((cellWidth * 2).toFloat(), 0F, (cellWidth * 2).toFloat(), boardHeight.toFloat(), mPaint!!)
        val cellHeight = boardHeight / 3
        canvas.drawLine(0F, cellHeight.toFloat(), boardWidth.toFloat(), cellHeight.toFloat(), mPaint!!)
        canvas.drawLine(0F, (cellHeight * 2).toFloat(), boardWidth.toFloat(), (cellHeight * 2).toFloat(), mPaint!!)

        for (i in 0 until TicTacToeGame().BOARD_SIZE) {
            val col = i % 3;
            val row = i / 3;
            val pad = 20;
            // Define the boundaries of a destination rectangle for the image
            val left = (col * cellWidth) + pad
            val top = (row *  cellHeight) + pad
            val right = (cellWidth * (col + 1)) - (2 * pad)
            val bottom = (cellHeight * (row + 1)) - (2 * pad)

            if (mGame.getBoardOccupant(i) == TicTacToeGame().HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,null, Rect(left, top, right, bottom), null)
            }else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame().COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,null, Rect(left, top, right, bottom), null);
            }
        }
    }





}