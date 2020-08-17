package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Dissertativa
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.util.EditTextPersonalizado

class ClassDissertativa {

    var editText: EditTextPersonalizado? = null

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var dissertativa: Dissertativa? = null
    var objenunciado: Objenunciado? = null

    fun desenhar(act: ActivityApresentacao) {
        var rl = RelativeLayout.LayoutParams(Math.round(dissertativa!!.largura * pesoX), Math.round(dissertativa!!.altura * pesoY))
        rl.leftMargin = Math.round(dissertativa!!.esquerdo * pesoX)
        rl.topMargin = Math.round(dissertativa!!.topo * pesoY)

        editText = EditTextPersonalizado(act)
        editText!!.isSingleLine = false
        editText!!.xdpi = aplicacao!!.metrics!!.density
        editText!!.largura = Math.round(dissertativa!!.largura * pesoX)
        editText!!.altura = Math.round(dissertativa!!.altura * pesoY)
        editText!!.layoutParams = rl
        editText!!.gravity = Gravity.TOP or Gravity.LEFT
        editText!!.setLayerType(RelativeLayout.LAYER_TYPE_SOFTWARE, null)
        editText!!.isSingleLine = false
        editText!!.setPadding(editText!!.paddingLeft, 0, editText!!.paddingRight, 0)

        editText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, dissertativa!!.tamanho.toFloat() * pesoX)

        if (dissertativa!!.cor_fundo != Color.TRANSPARENT) {
            editText!!.setBackgroundColor(dissertativa!!.cor_fundo)
        } else {
            editText!!.setBackgroundColor(Color.WHITE)
        }
        if (dissertativa!!.cor != Color.TRANSPARENT) {
            editText!!.setTextColor(dissertativa!!.cor)
        } else {
            editText!!.setTextColor(Color.BLACK)
        }

        rlTela!!.addView(editText)

        rlTela!!.invalidate()

        editText!!.postDelayed({
            desenhado = true
        }, 50)
    }
}