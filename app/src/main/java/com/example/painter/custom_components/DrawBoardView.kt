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
import com.example.painter.models.CanvasSize
import com.example.painter.models.CustomPaint
import com.example.painter.models.CustomPath
import com.example.painter.models.Drawing

/**
 *  Custom view table koji nam sluzi za crtanje
 */
class DrawBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private var MAX_PEN_SIZE_WIDTH_PERCENT = 0.05f // maksimalna velicina koja olovka/cetkica moze da ima
        private var MAX_SIZE_OF_BUFFER_UNDO_MOVES = 20 // koliko poslednjih poteza mozemo da vratimo (podatak vezan za undo)
    }

    // lista poteza koju cuvamo kada se koristi undo (iz ove liste vadimo poteze za redo)
    private var undoDrawings = arrayListOf<Drawing>()

    private var paint = Paint() // trenutna boja ;; koristimo je za crtanje
    private var penSize = 1f // velicina olovke za crtanje

    private var boardColor = Constants.DEFAULT_BOARD_COLOR // boja table
    private var penColor = Constants.DEFAULT_PEN_COLOR // boja olove ili cetkice
    private var drawingMode = DrawingMode.PEN // trenutni mode (Pen, Brush ili Rubber)
    private var canvasSize = CanvasSize(0,0) // velicina canvasa
    private var isDrawingEnabled = true // fleg koji nam pokazuje da li je omoguceno crtanje na tabli
    private var numberOfPaintedPathsOnBitmap = 0 // broj poteza koji je iscrtana na bitmap-i (treba nam za undo)

    private var path = CustomPath() // path je jedan potez koji se crta (ovde cuvamo trenutni potez)
    private var painterDrawing = mutableListOf<Drawing>() // lista poteza za ceo crtez
    private val oldPaint = CustomPaint() // pomocni objekat za cuvanje podesavanja za paint
    private var painterCanvas: Canvas? = null // canvas za bitmap-u koju crtamo
    private var bitmap: Bitmap? = null // bitmapa na kojoj se crtaju potezi
    private var boardMatrix: Matrix = Matrix() // default-ni objekat matrice za board
    private var boardPaint: Paint = Paint() // default-ni objekat paint-a za board

    init {
        // setujemo inicijalne podatke
        setPaint()
        setBoardColor()
        initPenSize()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("onSizeChanged", "newSize: ${Size(w,h)}, oldSize: ${Size(oldw,oldh)}")

        // kreiramo bitmapu odredjene veclicine i kreiramo canvas za nju
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            this.eraseColor(boardColor)
            painterCanvas = Canvas(this)
        }

        // ukoliko je velicina canvasa 0, setujemo trenutnu velicinu view-a
        if (canvasSize.width == 0 || canvasSize.height == 0) {
            canvasSize.width = w
            canvasSize.height = h
        }

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmap?.let {
            // crtamo bitmapu
            canvas?.drawBitmap(it, boardMatrix, boardPaint)

            canvas?.save()
            // skaliramo tablu (ukoliko je promenjena velicina canvasa)
            scaleBoard(canvas)
            // crtamo poslednjih nekoliko poteza (te poteze crtamo zbog undo funkcionalosti)
            drawPathsFromBuffer(canvas) /** ovo moze da pravi problem ukoliko je canvas veci a telefoni hardverski slabi **/
            // trenutni path koji crtamo
            canvas?.drawPath(path.getPath(), paint)
            canvas?.restore()
        }

    }

    /**
     * Crtamo poslednjih nekoliko poteza ;; Njih iscrtavamo ovako zbog undo funkcionalnosti
     */
    private fun drawPathsFromBuffer(canvas: Canvas?) {
        for (i in numberOfPaintedPathsOnBitmap until painterDrawing.size) {

            val drawing = painterDrawing[i]

            oldPaint.savePaint(paint)

            drawing.paint.setPaint(paint)

            if (drawing.isDeletePath)
                paint.color = boardColor

            canvas?.drawPath(drawing.path.getPath(), paint)

            oldPaint.setPaint(paint)

        }
    }

    // vrsimo kompletno iscrtavanje na tabli
//    private fun drawBoard(canvas: Canvas?) {
//
//        // skaliramo tablu
//        if (!isDrawingEnabled) {
//            scaleBoard(canvas)
//        }
//
//        oldPaint.savePaint(paint)
//
//        for (drawPath in painterDrawing) {
//
//            // ako je ovo path za brisanje onda setujemo boju table za paint
//            if (drawPath.isDeletePath)
//                drawPath.paint.color = boardColor
//
//            drawPath.paint.setPaint(paint)
//
//            painterDrawing.forEach {
//                it.paint.setPaint(paint)
//                canvas?.drawLines(it.points.toFloatArray(), paint)
//            }
//        }
//
//
//        oldPaint.setPaint(paint)
//        canvas?.drawLines(newDrawing.toFloatArray(), paint)
//    }

    private fun drawPathLine() {
        painterCanvas?.save()
        scaleBoard(painterCanvas)
        painterCanvas?.drawPath(path.getPath(), paint)
        painterCanvas?.restore()
        invalidate()
    }

    /**
     * Crtamo jedan path (potez) direktno na bitmap-u
     */
    private fun drawPathLine(drawing: Drawing, invalidateViewAfterDraw: Boolean = true) {
        oldPaint.savePaint(paint)

        painterCanvas?.save()
        scaleBoard(painterCanvas)
        drawing.paint.setPaint(paint)
        painterCanvas?.drawPath(drawing.path.getPath(), paint)
        painterCanvas?.restore()

        oldPaint.setPaint(paint)

        numberOfPaintedPathsOnBitmap++

        if (invalidateViewAfterDraw)
            invalidate()
    }

    /**
     *   F-ja preko koje izvrzava skaliranje matrice za canvas
     */
    private fun scaleBoard(canvas: Canvas?) {
        val matrix = Matrix()

        val wRatio = width.toFloat() / canvasSize.width
        val hRatio = height.toFloat() / canvasSize.height


        matrix.setScale(wRatio, hRatio)
        canvas?.setMatrix(matrix)

    }

    /**
     *  preko ove f-je setujemo podesavanja za paint u odnosu na selektovani mode (Pen, Brush, Rubber)
     */
    private fun setPaint(drawMode: DrawingMode = drawingMode) {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND

        when (drawMode) {

            DrawingMode.PEN -> {
                paint.strokeWidth = penSize
                paint.color = ColorUtils.setAlphaComponent(penColor, 0xFF)
            }

            DrawingMode.BRUSH -> {
                paint.strokeWidth = penSize
                paint.color = ColorUtils.setAlphaComponent(penColor, 0x80)
            }

            DrawingMode.RUBBER -> {
                paint.color = boardColor
                paint.strokeWidth = if (canvasSize.width < canvasSize.height) canvasSize.width * 0.05f else canvasSize.height * 0.05f
            }

        }


    }

    /**
     *  setujemo boju za board
     */
    private fun setBoardColor() {
        setBackgroundColor(boardColor)
    }

    /**
     *  setujemo inicijalnnu velicinu za velicinu olovke/cetkice
     */
    private fun initPenSize() {
        doOnPreDraw {
            setPenSize(Constants.DEFAULT_PEN_SIZE_PERCENT)
        }
    }

    // touch listener
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

        return true
    }

    /**
     *  skaliramo tacke u odnosu na velicinu canvasa
     */
    private fun getScaledPoint(x: Float, y: Float): PointF {
        val scaledX = x * (canvasSize.width / width.toFloat())
        val scaledY = y * (canvasSize.height / height.toFloat())

        return PointF(scaledX, scaledY)
    }

    // touch start ;; dodajemo startni point
    private fun onTouchStart(x: Float, y: Float) {

        // ukoliko postoji lista path-ova unda je klirujemo (ponistavamo undo mogucnost, zato sto je doslo do promena)
        if (undoDrawings.isNotEmpty())
            undoDrawings.clear()

        path.add(PointF(x,y), CustomPathMode.MOVE_TO)
    }

    // touch move ;; pamtimo listu tacaka
    private fun onTouchMove(x: Float, y: Float) {
        path.add(PointF(x,y), CustomPathMode.LINE_TO)

        invalidate()
    }

    // touch up ;; crtanje zavrseno i dodajemo poslednji point
    private fun onTouchUp(x: Float, y: Float) {

        path.add(PointF(x,y), CustomPathMode.MOVE_TO)

        // cuvamo trenutni path
        saveCurrentPath()
        // crtamo path na bitmapi
        drawPathOnBitmap()
    }

    /**
     *  crtamo path-ove (poteze) na bitmapi (ukoliko ima nesto da se crta)
     */
    private fun drawPathOnBitmap() {

        val maxIndexOfPathToDraw = painterDrawing.size - MAX_SIZE_OF_BUFFER_UNDO_MOVES

        // crtamo sve poteze bez onih koji su trenutno u baferu (zbog undo funkcionalnosti)
        while (numberOfPaintedPathsOnBitmap < maxIndexOfPathToDraw) {
            drawPathLine(painterDrawing[numberOfPaintedPathsOnBitmap])
        }

    }

    /**
     *  crtamo sve poteze na bitmapi
     */
    private fun drawAllPathsOnBitmap() {
        while (numberOfPaintedPathsOnBitmap < painterDrawing.size) {
            drawPathLine(painterDrawing[numberOfPaintedPathsOnBitmap], false)
        }
    }

    // menjamo boju table
    fun changeBoardColor(color: Int) {
        boardColor = color

        setBoardColor()

        invalidate()
    }

    // pomocna funkcija koja nam govori da li selektovana gumica
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

    // vracamo boju olovke/cetkice
    fun getPenColor(): Int {
        return penColor
    }

    /**
     *  cuvamo trenutni path za crtanje u listu
     */
    private fun saveCurrentPath() {
        if (!path.isEmpty()) {
            painterDrawing.add(Drawing(CustomPaint(paint.strokeWidth, paint.color, paint.alpha), isRubberSelected(), path.copy()))

            path.reset()
        }

    }

    // setujemo velicinu olovke i cetkice
    fun setPenSize(percent: Float) {
        penSize = (width * MAX_PEN_SIZE_WIDTH_PERCENT) * (percent * 0.01f)
        setPaint()
    }

    /**
     *  undo funkcionalnost
     */
    fun undo() {
        // proveravamo da li je undo dostupan
        if (painterDrawing.isNotEmpty() && undoDrawings.size < MAX_SIZE_OF_BUFFER_UNDO_MOVES) {
            val lastPath = painterDrawing.removeLast()

            undoDrawings.add(lastPath)

            invalidate()
        }
    }

    /**
     *  redo funkcionalnost
     */
    fun redo() {
        // proveravamo da li je redo dostupan
        if (undoDrawings.isNotEmpty()) {
            val lastPath = undoDrawings.removeLast()

            painterDrawing.add(lastPath)

            invalidate()
        }
    }

    /**
     *  brisemo celu tablu
     */
    fun clearBoard() {
        painterDrawing = mutableListOf()
        numberOfPaintedPathsOnBitmap = 0
        painterCanvas?.drawColor(boardColor)
        invalidate()
    }

    /**
     *  vracamo listu zapamcenih poteza
     */
    fun getDrawing(): MutableList<Drawing> {
        return painterDrawing
    }

    /**
     *  setujemo listu trenutnih crteza
     */
    fun setDrawing(drawingToDraw: MutableList<Drawing>) {

        if (drawingToDraw.isEmpty())
            return

        // brisemo celu tablu
        clearBoard()

        // setujemo listu poteza (path)
        painterDrawing.addAll(drawingToDraw)

        // crtamo sve to na bitmap-u
        drawPathOnBitmap()

        invalidate()
    }

    fun setPaint(newPaint: Paint) {
        paint = newPaint
    }

    fun setIsDrawingEnabled(isEnabled: Boolean) {
        isDrawingEnabled = isEnabled
    }

    /**
     *  setujemo velicinu canvasa
     */
    fun setCanvasSize(size: CanvasSize) {
        canvasSize = size

        //clearBoard()

        setCanvasAspectRatio()
    }

    /**
     *  vracamo velicinu trenutnog canvasa
     */
    fun getCanvasSize(): CanvasSize {
        return canvasSize
    }

    /**
     *  vracamo trenutnu bitmap-u
     */
    fun getBitmap(): Bitmap? {
        // crtamo path-ove koji su nam u baferu i vracamo kompletnu kopiju bitmape
        drawAllPathsOnBitmap()

        return bitmap?.copy(bitmap?.config, false)
    }

    /**
     *  setujemo aspect za canvas
     */
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