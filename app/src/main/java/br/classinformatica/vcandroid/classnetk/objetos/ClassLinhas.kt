package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Linhas

class ClassLinhas {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var linhas: Linhas? = null

    fun desenhar(act: ActivityApresentacao) {
        var largura = 0
        var altura = 0
        var x1 = 0
        var y1 = 0

        if (linhas!!.x1 < linhas!!.x2) {
            largura = linhas!!.x2 - linhas!!.x1
            x1 = linhas!!.x1
        } else {
            largura = linhas!!.x1 - linhas!!.x2
            x1 = linhas!!.x2
        }
        if (linhas!!.y1 < linhas!!.y2) {
            altura = linhas!!.y2 - linhas!!.y1
            y1 = linhas!!.y1
        } else {
            altura = linhas!!.y1 - linhas!!.y2
            y1 = linhas!!.y2
        }

        var bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        var paint = Paint()
        paint.color = linhas!!.cor
        paint.style = Paint.Style.STROKE
        paint .strokeWidth = linhas!!.espessura.toFloat()
        paint.isAntiAlias = true
        canvas.drawLine((linhas!!.x1 - x1).toFloat(), (linhas!!.y1 - y1).toFloat(), (linhas!!.x2 - x1).toFloat(), (linhas!!.y2 - y1).toFloat(), paint)

        var rl = RelativeLayout.LayoutParams(Math.round(largura * pesoX), Math.round(altura * pesoY))
        rl.leftMargin = Math.round(x1 * pesoX)
        rl.topMargin = Math.round(y1 * pesoY)

        var imageView = ImageView(act)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imageView.setBackgroundColor(act.resources.getColor(android.R.color.transparent, act.theme))
        } else {
            imageView.setBackgroundColor(act.resources.getColor(android.R.color.transparent))
        }
        imageView.layoutParams = rl
        imageView.setImageBitmap(bitmap)

        rlTela!!.addView(imageView)

        rlTela!!.invalidate()

        imageView.postDelayed({
            desenhado = true
        }, 50)
    }
}