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
    private lateinit var mPaintLines: Paint
    private lateinit var mGame: TicTacToeGame
    private var winLine: Boolean = false
    private var winLineNum: Int = 0

    fun initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x_2)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o_2)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLines = Paint(Paint.ANTI_ALIAS_FLAG)
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

    fun drawWinnerLine(winNum: Int){
        winLineNum = winNum
        winLine = true
    }

    fun resetWinnerLine(){
        winLineNum = 0
        winLine = true
    }

    fun getWinLineBool(): Boolean{
        return winLine
    }

    fun setWinLineBool(win: Boolean){
        winLine = win
    }

    fun getWinLineInt(): Int{
        return winLineNum
    }

    fun setWinLineInt(win: Int){
        winLineNum = win
    }

    fun drawLine(canvas: Canvas){

        val boardWidth = width
        val boardHeight = height
        val cellHeight = boardHeight / 3
        val cellWidth = boardWidth / 3

        val colors : IntArray = intArrayOf(Color.YELLOW, Color.RED)
        val shader: Shader = LinearGradient(0f, 0f, boardWidth.toFloat(), boardHeight.toFloat(), colors, null, Shader.TileMode.CLAMP)
        mPaintLines!!.shader = shader
        mPaintLines!!.strokeWidth = GRID_WIDTH.toFloat() + 5

        when{
            winLineNum in 1..3 -> canvas.drawLine(0F, (cellHeight.toFloat() / 2) + (cellHeight.toFloat() * (winLineNum - 1)) , boardWidth.toFloat(), (cellHeight.toFloat() / 2) + (cellHeight.toFloat() * (winLineNum - 1)), mPaintLines!!)
            winLineNum in 4..6 -> canvas.drawLine((cellWidth.toFloat() / 2) + (cellWidth.toFloat() * (winLineNum - 4)), 0F, (cellWidth.toFloat() / 2) + (cellWidth.toFloat() * (winLineNum - 4)), boardHeight.toFloat(), mPaintLines!!)
            winLineNum == 7 -> canvas.drawLine(0F, 0F,boardWidth.toFloat() , boardHeight.toFloat(), mPaintLines!!)
            winLineNum == 8 -> canvas.drawLine(boardWidth.toFloat(), 0F, 0F, boardHeight.toFloat(), mPaintLines!!)
        }
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Determine the width and height of the View
        val boardWidth = width
        val boardHeight = height
        // Make thick, light gray lines

        val colors : IntArray = intArrayOf(Color.CYAN, Color.rgb(107,52,165))
        val shader: Shader = LinearGradient(0f, 0f, boardWidth.toFloat(), boardHeight.toFloat(), colors, null, Shader.TileMode.CLAMP)

        mPaint!!.strokeWidth = GRID_WIDTH.toFloat()
        mPaint!!.shader = shader

        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3
        canvas.drawLine(cellWidth.toFloat(), 0F, cellWidth.toFloat(), boardHeight.toFloat(), mPaint!!)
        canvas.drawLine((cellWidth * 2).toFloat(), 0F, (cellWidth * 2).toFloat(), boardHeight.toFloat(), mPaint!!)
        // Draw the two horizontal board lines
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
        if(winLine){
            drawLine(canvas)
        }
    }







}