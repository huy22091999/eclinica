package im.vector.app.ext.conference.customlayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TableLayout
import android.widget.TableRow
import im.vector.app.R


class TableLayoutEx : TableLayout {
    private var linePaint: Paint? = null
    private var tableLayoutRect: Rect? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val strokeWidth = this.context.resources.displayMetrics.scaledDensity * 1
        linePaint = Paint(0)
        linePaint!!.color = resources.getColor(R.color.line,null)
        linePaint!!.strokeWidth = strokeWidth
        linePaint!!.style = Paint.Style.STROKE
        val rect = Rect()
        val paddingTop = paddingTop
        getDrawingRect(rect)
        tableLayoutRect = Rect(rect.left, rect.top + paddingTop, rect.right, rect.bottom)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val rect = Rect()
        if (linePaint != null) {
            canvas.drawRect(tableLayoutRect!!, linePaint!!)
            var y = tableLayoutRect!!.top.toFloat()
            for (i in 0 until childCount - 1) {
                if (getChildAt(i) is TableRow) {
                    val tableRow = getChildAt(i) as TableRow
                    tableRow.getDrawingRect(rect)
                    y += rect.height().toFloat()
                    canvas.drawLine(
                        tableLayoutRect!!.left.toFloat(), y, tableLayoutRect!!.right.toFloat(), y,
                        linePaint!!
                    )
                    var x = tableLayoutRect!!.left.toFloat()
                    for (j in 0 until tableRow.childCount - 1) {
                        val view = tableRow.getChildAt(j)
                        if (view != null) {
                            view.getDrawingRect(rect)
                            x += rect.width().toFloat()
                            canvas.drawLine(
                                x,
                                tableLayoutRect!!.top.toFloat(),
                                x,
                                tableLayoutRect!!.bottom.toFloat(),
                                linePaint!!
                            )
                        }
                    }
                }
            }
        }
    }
}
