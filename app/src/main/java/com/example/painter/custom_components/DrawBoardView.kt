package com.example.painter.custom_components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnPreDraw
import com.example.painter.constants.Constants
import com.example.painter.constants.CustomPathMode
import com.example.painter.constants.DrawingMode
import com.example.painter.models.*

class DrawBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_PEN_SIZE_WIDTH_PERCENT = 0.05f
    }

    private var undoPaths = arrayListOf<Drawing>()

    private var paint = Paint()
    private var penSize = 1f // velicina olovke za crtanje

    private var boardColor = Constants.DEFAULT_BOARD_COLOR
    private var penColor = Constants.DEFAULT_PEN_COLOR
    private var drawingMode = DrawingMode.PEN
    private var canvasSize = CanvasSize(0,0)
    private var isDrawingEnabled = true

    private var path = CustomPath()
    private var newDrawing = mutableListOf<Float>()
    private var painterDrawing = mutableListOf<Drawing>()
    private var lastPoint = PointF(0f,0f)
    private val oldPaint = CustomPaint()
    private var painterCanvas: Canvas? = null
    private var bitmap: Bitmap? = null
    private var boardMatrix: Matrix = Matrix()
    private var boardPaint: Paint = Paint()

    init {
        setPaint()
        setBoardColor()
        initPenSize()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("onSizeChanged", "newSize: ${Size(w,h)}, oldSize: ${Size(oldw,oldh)}")

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            this.eraseColor(boardColor)
            painterCanvas = Canvas(this)
        }

        if (canvasSize.width == 0 || canvasSize.height == 0) {
            canvasSize.width = w
            canvasSize.height = h
        }

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //canvas?.drawPath(path, paint)
        //boardCanvas = canvas
        //scaleBoard(canvas)

        Log.d("drawBoardView", "onDraw")

        //drawBoard(canvas)
        bitmap?.let {
            canvas?.drawBitmap(it, boardMatrix, boardPaint)

            canvas?.save()
            scaleBoard(canvas)
            canvas?.drawPath(path.getPath(), paint)
            canvas?.restore()
        }

       //canvas?.drawPath(path.getPath(), paint)

    }

    // vrsimo kompletno iscrtavanje na tabli
    private fun drawBoard(canvas: Canvas?) {

        // skaliramo tablu
        if (!isDrawingEnabled) {
            scaleBoard(canvas)
        }

        oldPaint.savePaint(paint)

        for (drawPath in painterDrawing) {

            // ako je ovo path za brisanje onda setujemo boju table za paint
            if (drawPath.isDeletePath)
                drawPath.paint.color = boardColor

            drawPath.paint.setPaint(paint)

            painterDrawing.forEach {
                it.paint.setPaint(paint)
                canvas?.drawLines(it.points.toFloatArray(), paint)
            }
        }


        oldPaint.setPaint(paint)
        canvas?.drawLines(newDrawing.toFloatArray(), paint)
    }

    private fun drawPathLine() {
        painterCanvas?.save()
        scaleBoard(painterCanvas)
        painterCanvas?.drawPath(path.getPath(), paint)
        painterCanvas?.restore()
        invalidate()
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
                //paint.color = penColor
                //paint.alpha = 0xFF
                paint.color = ColorUtils.setAlphaComponent(penColor, 0xFF)
            }

            DrawingMode.BRUSH -> {
                paint.strokeWidth = penSize
                //paint.color = penColor
                //paint.alpha = 0x80
                paint.color = ColorUtils.setAlphaComponent(penColor, 0x80)
            }

            DrawingMode.RUBBER -> {
                paint.color = boardColor
                paint.strokeWidth = width * 0.05f
                //paint.alpha = 0xFF
                //ColorUtils.setAlphaComponent(penColor, 0xFF)
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
                val scaledPoint = getScaledPoint(event.x, event.y)
                onTouchStart(scaledPoint.x, scaledPoint.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val scaledPoint = getScaledPoint(event.x, event.y)
                onTouchMove(scaledPoint.x, scaledPoint.y)
            }
            MotionEvent.ACTION_UP -> {
                val scaledPoint = getScaledPoint(event.x, event.y)
                onTouchUp(scaledPoint.x, scaledPoint.y)
            }
        }

        //return super.onTouchEvent(event)
        return true
    }

    private fun getScaledPoint(x: Float, y: Float): PointF {
        val scaledX = x * (canvasSize.width / width.toFloat())
        val scaledY = y * (canvasSize.height / height.toFloat())

        return PointF(scaledX, scaledY)
    }

    private fun onTouchStart(x: Float, y: Float) {

        // ukoliko postoji lista path-ova unda je klirujemo (ponistavamo undo mogucnost, zato sto je doslo do promena)
        if (undoPaths.isNotEmpty())
            undoPaths.clear()

        newDrawing.add(x)
        newDrawing.add(y)

        path.add(PointF(x,y), CustomPathMode.MOVE_TO)
    }

    private fun onTouchMove(x: Float, y: Float) {
        if (lastPoint.x != 0f || lastPoint.y != 0f) {
            newDrawing.add(lastPoint.x)
            newDrawing.add(lastPoint.y)
        }

        newDrawing.add(x)
        newDrawing.add(y)

        lastPoint.set(x, y)

        //painterCanvas?.drawLines(newDrawing.toFloatArray(), paint)

        path.add(PointF(x,y), CustomPathMode.LINE_TO)

        //drawPathLine()

        invalidate()
    }

    private fun onTouchUp(x: Float, y: Float) {
        newDrawing.add(x)
        newDrawing.add(y)

        lastPoint.set(0f,0f)

        path.add(PointF(x,y), CustomPathMode.MOVE_TO)

        drawPathLine()
        saveCurrentPath()
    }

    // menjamo boju table
    fun changeBoardColor(color: Int) {
        boardColor = color

        setBoardColor()

        invalidate()
    }

    private fun isRubberSelected(): Boolean {
        return drawingMode == DrawingMode.RUBBER
    }

    // setujemo podesavanja za gumicu
    private fun onRubberSelected() {
        drawingMode = DrawingMode.RUBBER
        setPaint()
    }

    // selektujemo olovku
    fun selectPen() {
        drawingMode = DrawingMode.PEN
        setPaint()
    }

    // selektujemo cetkicu
    fun selectBrush() {
        drawingMode = DrawingMode.BRUSH
        setPaint()
    }

    // selektujemo gumicu
    fun selectRubber() {
        onRubberSelected()
    }

    // setujemo boju za olovku i cetkicu
    fun setPenColor(color: Int) {
        penColor = color
        setPaint()
    }

    fun getPenColor(): Int {
        return penColor
    }

    // cuvamo trenutni path za crtanje u listu
    private fun saveCurrentPath() {
        if (newDrawing.isNotEmpty()) {
            painterDrawing.add(Drawing(newDrawing, CustomPaint(paint.strokeWidth, paint.color, paint.alpha), isRubberSelected(), path.copy()))

            path.reset()
            newDrawing = mutableListOf()
        }

    }

    // setujemo velicinu olovke i cetkice
    fun setPenSize(percent: Float) {
        penSize = (width * MAX_PEN_SIZE_WIDTH_PERCENT) * (percent * 0.01f)
        setPaint()
    }

    fun undo() {

        if (painterDrawing.isNotEmpty()) {
            val lastPath = painterDrawing.removeLast()

            undoPaths.add(lastPath)

            invalidate()
        }
    }

    fun redo() {
        if (undoPaths.isNotEmpty()) {
            val lastPath = undoPaths.removeLast()

            painterDrawing.add(lastPath)

            invalidate()
        }
    }

    fun clearBoard() {
        painterDrawing = mutableListOf() // .clear()
        newDrawing.clear()
        invalidate()
    }

    fun getDrawing(): MutableList<Drawing> {
        return painterDrawing
    }

    fun setDrawing(drawingToDraw: MutableList<Drawing>) {
        painterDrawing.clear()
        newDrawing.clear()

        painterDrawing.addAll(drawingToDraw)

        invalidate()
    }


    fun setPaint(newPaint: Paint) {
        paint = newPaint
    }

    fun setIsDrawingEnabled(isEnabled: Boolean) {
        isDrawingEnabled = isEnabled
    }

    fun setCanvasSize(size: CanvasSize) {
        canvasSize = size

        setCanvasAspectRatio()
    }

    fun getCanvasSize(): CanvasSize {
        return canvasSize
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    private fun setCanvasAspectRatio() {

        val layoutParams = ConstraintLayout.LayoutParams(0, 0)
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        layoutParams.dimensionRatio = "${canvasSize.width}:${canvasSize.height}"

        this.layoutParams = layoutParams
    }

}