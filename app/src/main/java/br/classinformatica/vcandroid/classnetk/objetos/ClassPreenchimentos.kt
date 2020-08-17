package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Color
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objtexto
import br.classinformatica.vcandroid.classnetk.entidades.Preenchimentos
import br.classinformatica.vcandroid.classnetk.util.EditTextPersonalizado

class ClassPreenchimentos {

    var editText: EditTextPersonalizado? = null

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var preenchimentos: Preenchimentos? = null
    var objenunciado: Objenunciado? = null
    var objtexto = ArrayList<Objtexto>()

    fun desenhar(act: ActivityApresentacao) {
        var rl = RelativeLayout.LayoutParams(Math.round(preenchimentos!!.largura * pesoX), Math.round(preenchimentos!!.altura * pesoY))
        rl.leftMargin = Math.round(preenchimentos!!.esquerdo * pesoX)
        rl.topMargin = Math.round(preenchimentos!!.topo * pesoY)

        editText = EditTextPersonalizado(act)

        var numerico = false
        try {
            var i = 0
            var sai = false
            while (!sai && i < objtexto.size) {
                if (objtexto[i].tela.toDoubleOrNull() == null) {
                    sai = true
                }
                i++
            }
            if (!sai) {
                numerico = true
            }
        } catch (e: Exception) {
            numerico = false
        }
        if (numerico) {
            editText!!.inputType = InputType.TYPE_CLASS_NUMBER
        }

        editText!!.xdpi = aplicacao!!.metrics!!.density
        editText!!.largura = Math.round(preenchimentos!!.largura * pesoX)
        editText!!.altura = Math.round(preenchimentos!!.altura * pesoY)
        editText!!.layoutParams = rl
        editText!!.gravity = Gravity.TOP or Gravity.LEFT
        editText!!.setLayerType(RelativeLayout.LAYER_TYPE_SOFTWARE, null)
        editText!!.isSingleLine = false
        editText!!.setPadding(editText!!.paddingLeft, 0, editText!!.paddingRight, 0)

        editText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, preenchimentos!!.tamanho.toFloat() * pesoX)

        if (preenchimentos!!.cor_fundo != Color.TRANSPARENT) {
            editText!!.setBackgroundColor(preenchimentos!!.cor_fundo)
        } else {
            editText!!.setBackgroundColor(Color.WHITE)
        }
        if (preenchimentos!!.cor != Color.TRANSPARENT) {
            editText!!.setTextColor(preenchimentos!!.cor)
        } else {
            editText!!.setTextColor(Color.BLACK)
        }

        if (preenchimentos!!.maximo > 0) {
            editText!!.maxHeight = preenchimentos!!.maximo
        }

        if (preenchimentos!!.multilinha == 1) {
            editText!!.isSingleLine = false
        }

        rlTela!!.addView(editText)

        rlTela!!.invalidate()

        editText!!.postDelayed({
            desenhado = true
        }, 50)
    }
}