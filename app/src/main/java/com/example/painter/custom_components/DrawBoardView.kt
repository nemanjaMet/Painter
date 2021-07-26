package com.example.painter.custom_components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnPreDraw
import com.example.painter.constants.Constants
import com.example.painter.constants.DrawingMode
import com.example.painter.models.DrawPath

class DrawBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_PEN_SIZE_WIDTH_PERCENT = 0.05f
    }

    private var undoPaths = arrayListOf<DrawPath>()
    private var drawing = arrayListOf<DrawPath>() // cuvamo listu path-va za crtanje
    private var path = Path() // path koji nam sluzi za crtanje
    private var paint = Paint()
    private var penSize = 1f // velicina olovke za crtanje
    //private var isRubberSelected = false // fleg koji nam oznacava da li je gumica trenutno selektovana
    private var boardColor = Constants.DEFAULT_BOARD_COLOR
    private var penColor = Constants.DEFAULT_PEN_COLOR
    private var drawingMode = DrawingMode.PEN
    private var canvasSize = Size(0,0)
    private var isDrawingEnabled = true
    //private var isOrientationLandscape = false
    //private var boardCanvas: Canvas? = null

    init {
        setPaint()
        setBoardColor()
        initPenSize()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //canvas?.drawPath(path, paint)
        //boardCanvas = canvas
        drawBoard(canvas)
    }

    // vrsimo kompletno iscrtavanje na tabli
    private fun drawBoard(canvas: Canvas?) {

        // skaliramo tablu
        if (!isDrawingEnabled) {
            scaleBoard(canvas)
        }



        for (drawPath in drawing) {

            // ako je ovo path za brisanje onda setujemo boju table za paint
            if (drawPath.isDeletePath)
                drawPath.paint.color = boardColor

            canvas?.drawPath(drawPath.path, drawPath.paint)
        }

        canvas?.drawPath(path, paint)
    }

    private fun scaleBoard(canvas: Canvas?) {
        val matrix = Matrix()

        val wRatio = width.toFloat() / canvasSize.width
        val hRatio = height.toFloat() / canvasSize.height

        //Log.d("drawBoard", "wRatio: $wRatio, hRatio: $hRatio")

        matrix.setScale(wRatio, hRatio)
        canvas?.setMatrix(matrix)
    }

    // setujemo podeavanja za cetkicu
    private fun setPaint(drawMode: DrawingMode = drawingMode) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND

        when (drawMode) {

            DrawingMode.PEN -> {
                paint.strokeWidth = penSize
                paint.color = penColor
                paint.alpha = 0xFF
            }

            DrawingMode.BRUSH -> {
                paint.strokeWidth = penSize
                paint.color = penColor
                paint.alpha = 0x80
            }

            DrawingMode.RUBBER -> {
                paint.color = boardColor
                paint.strokeWidth = width * 0.05f
                paint.alpha = 0xFF
            }

        }


    }

    // setujemo boju table
    private fun setBoardColor() {
        setBackgroundColor(boardColor)
    }

    // setujemo pocetnu velicinu olovke
    private fun initPenSize() {
        doOnPreDraw {
            setPenSize(Constants.DEFAULT_PEN_SIZE_PERCENT)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (!isDrawingEnabled)
            return false

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStart(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp(event.x, event.y)
            }
        }

        //return super.onTouchEvent(event)
        return true
    }

    private fun onTouchStart(x: Float, y: Float) {

        // ukoliko postoji lista path-ova unda je klirujemo (ponistavamo undo mogucnost, zato sto je doslo do promena)
        if (undoPaths.isNotEmpty())
            undoPaths.clear()

        path.moveTo(x, y)
    }

    private fun onTouchMove(x: Float, y: Float) {
        path.lineTo(x, y)

        invalidate()
    }

    private fun onTouchUp(x: Float, y: Float) {
        path.moveTo(x, y)

        saveCurrentPath()
    }

    // menjamo boju table
    fun changeBoardColor(color: Int) {
        boardColor = color

        setBoardColor()

        invalidate()
    }

    // setujemo fleg da je gumica selektovana
//    private fun enableRubber() {
//        drawingMode = DrawingMode.RUBBER
//    }

    // setujemo fleg da je gumica deselektovana
//    private fun disableRubber() {
//
//    }

    private fun isRubberSelected(): Boolean {
        return drawingMode == DrawingMode.RUBBER
    }

    // setujemo podesavanja za gumicu
    private fun onRubberSelected() {
        drawingMode = DrawingMode.RUBBER

//        if (isOrientationLandscape) {
//            paint.strokeWidth = width * 0.1f
//        } else {
//            paint.strokeWidth = width * 0.1f
//        }

        setPaint()
    }

    // selektujemo olovku
    fun selectPen() {
        //saveCurrentPath()

//        // ukoliko je gumica selektovana onda je deselektujemo
//        if (isRubberSelected()) {
//            disableRubber()
//        }

        drawingMode = DrawingMode.PEN
        setPaint()
    }

    // selektujemo cetkicu
    fun selectBrush() {
        //saveCurrentPath()

//        // ukoliko je gumica selektovana onda je deselektujemo
//        if (isRubberSelected()) {
//            disableRubber()
//        }

        drawingMode = DrawingMode.BRUSH
        setPaint()
    }

    // selektujemo gumicu
    fun selectRubber() {
        //saveCurrentPath()

        onRubberSelected()
    }

    // setujemo boju za olovku i cetkicu
    fun setPenColor(color: Int) {
        //saveCurrentPath()

        // ukoliko je gumica selektovana onda je deselektujemo
//        if (isRubberSelected()) {
//            disableRubber()
//        }

        penColor = color
        setPaint()
    }

    fun getPenColor(): Int {
        return penColor
    }

    // cuvamo trenutni path za crtanje u listu
    private fun saveCurrentPath() {

        if (!path.isEmpty) {
            drawing.add(DrawPath(Path(path), Paint(paint), isRubberSelected()))

            //path = Path()
            path.reset()
        }

    }

    // setujemo velicinu olovke i cetkice
    fun setPenSize(percent: Float) {
        //saveCurrentPath()

        penSize = (width * MAX_PEN_SIZE_WIDTH_PERCENT) * (percent * 0.01f)

        setPaint()
    }

    fun undo() {
        //saveCurrentPath()

        if (drawing.isNotEmpty()) {
            val lastPath = drawing.removeLast()

            undoPaths.add(lastPath)

            invalidate()
        }
    }

    fun redo() {
        if (undoPaths.isNotEmpty()) {
            val lastPath = undoPaths.removeLast()

            drawing.add(lastPath)

            invalidate()
        }
    }

    fun clearBoard() {
        drawing.clear()
        path.reset()
        invalidate()
    }

    fun getDrawing(): MutableList<DrawPath> {
        return drawing
    }

    fun setDrawing(drawingToDraw: MutableList<DrawPath>) {
        drawing.clear()
        path.reset()

        drawing.addAll(drawingToDraw)

        invalidate()
    }

    fun setIsDrawingEnabled(isEnabled: Boolean) {
        isDrawingEnabled = isEnabled
    }

    fun setCanvasSize(size: Size) {
        canvasSize = size
    }

}