package br.classinformatica.vcandroid.classnetk.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.widget.EditText

class EditTextPersonalizado(context: Context?) : EditText(context) {

    var altura = 0
    var errado = false
    var largura = 0
    var pBorda: Paint? = null
    var pErrado: Paint? = null
    var xdpi = 1F

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        if (pErrado == null || pBorda == null) {
            pErrado = Paint()
            pErrado!!.style = Paint.Style.STROKE
            pErrado!!.strokeWidth = 3F
            pErrado!!.color = Color.RED

            pBorda = Paint()
            pBorda!!.style = Paint.Style.STROKE
            pBorda!!.strokeWidth = 3F
            pBorda!!.color = Color.BLACK
        }
        
        setPadding((4 * xdpi).toInt(), 0, 0, 0)
        var r = Rect()
        this.getDrawingRect(r)
        if (errado) {
            canvas!!.drawRect(1F, (1 + r.top).toFloat(), (largura - 2).toFloat(), (altura - 2 + r.top).toFloat(), pErrado!!)
        } else {
            canvas!!.drawRect(1F, (1 + r.top).toFloat(), (largura - 2).toFloat(), (altura - 2 + r.top).toFloat(), pBorda!!)
        }
    }
}