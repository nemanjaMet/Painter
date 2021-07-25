package com.example.painter.custom_components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnPreDraw
import com.example.painter.constants.Constants
import com.example.painter.models.DrawPath

class DrawBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_PEN_SIZE_WIDTH_PERCENT = 0.05f
    }

    private var drawing = arrayListOf<DrawPath>() // cuvamo listu path-va za crtanje
    private var path = Path() // path koji nam sluzi za crtanje
    private var paint = Paint()
    private var penSize = 1f // velicina olovke za crtanje
    private var isRubberSelected = false // fleg koji nam oznacava da li je gumica trenutno selektovana
    private var boardColor = Constants.DEFAULT_BOARD_COLOR
    private var penColor = Constants.DEFAULT_PEN_COLOR
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

        for (drawPath in drawing) {

            // ako je ovo path za brisanje onda setujemo boju table za paint
            if (drawPath.isDeletePath)
                drawPath.paint.color = boardColor

            canvas?.drawPath(drawPath.path, drawPath.paint)
        }

        canvas?.drawPath(path, paint)
    }

    // setujemo podeavanja za cetkicu
    private fun setPaint() {
        paint.isAntiAlias = true
        paint.color = penColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = penSize
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
        path.moveTo(x, y)
    }

    private fun onTouchMove(x: Float, y: Float) {
        path.lineTo(x, y)

        invalidate()
    }

    private fun onTouchUp(x: Float, y: Float) {
        path.moveTo(x, y)
    }

    // menjamo boju table
    fun changeBoardColor(color: Int) {
        boardColor = color

        setBoardColor()

        invalidate()
    }

    // setujemo fleg da je gumica selektovana
    private fun enableRubber() {
        isRubberSelected = true
    }

    // setujemo fleg da je gumica deselektovana
    private fun disableRubber() {
        isRubberSelected = false
    }

    // setujemo podesavanja za gumicu
    private fun onRubberSelected() {
        enableRubber()

        paint.color = boardColor
        paint.strokeWidth = width * 0.05f

//        if (isOrientationLandscape) {
//            paint.strokeWidth = width * 0.1f
//        } else {
//            paint.strokeWidth = width * 0.1f
//        }

    }

    // selektujemo olovku
    fun selectPen() {
        saveCurrentPath()

        // ukoliko je gumica selektovana onda je deselektujemo
        if (isRubberSelected) {
            disableRubber()
            setPaint()
        }


    }

    // selektujemo cetkicu
    fun selectBrush() {
        saveCurrentPath()

        // ukoliko je gumica selektovana onda je deselektujemo
        if (isRubberSelected) {
            disableRubber()
            setPaint()
        }

    }

    // selektujemo gumicu
    fun selectRubber() {
        saveCurrentPath()

        onRubberSelected()
    }

    // setujemo boju za olovku i cetkicu
    fun setPenColor(color: Int) {
        saveCurrentPath()

        // ukoliko je gumica selektovana onda je deselektujemo
        if (isRubberSelected) {
            disableRubber()
        }

        penColor = color
        setPaint()
    }

    fun getPenColor(): Int {
        return penColor
    }

    // cuvamo trenutni path za crtanje u listu
    private fun saveCurrentPath() {

        if (!path.isEmpty) {
            drawing.add(DrawPath(path, Paint(paint), isRubberSelected))

            path = Path()
        }

    }

    // setujemo velicinu olovke i cetkice
    fun setPenSize(percent: Float) {
        saveCurrentPath()

        penSize = (width * MAX_PEN_SIZE_WIDTH_PERCENT) * (percent * 0.01f)

        setPaint()
    }

}