package br.classinformatica.vcandroid.classnetk.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.widget.TextView

class TextViewPersonalizado(context: Context?) : TextView(context) {

    var altura = 0
    var borda = 1
    var espessutaBorda = 0
    var largura = 0
    var pBorda: Paint? = null
    var xdpi = 1F

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (borda == 1 && canvas != null) {
            if (pBorda == null) {
                pBorda = Paint()
                pBorda!!.style = Paint.Style.STROKE
                pBorda!!.strokeWidth = espessutaBorda.toFloat()
                pBorda!!.color = Color.BLACK
            }
            setPadding((4 * xdpi).toInt(), 0, 0, 0)
            var r = Rect()
            this.getDrawingRect(r)
            //canvas!!.drawRect(1F, (1 + r.top).toFloat(), (largura - 2).toFloat(), (altura - 2 + r.top).toFloat(), pBorda!!)
            canvas!!.drawRect(0F, 0F, (largura - espessutaBorda).toFloat(), (altura - espessutaBorda).toFloat(), pBorda!!)
        }
    }
}