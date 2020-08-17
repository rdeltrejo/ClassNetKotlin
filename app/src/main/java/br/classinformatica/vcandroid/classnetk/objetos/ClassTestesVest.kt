package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objtexto
import br.classinformatica.vcandroid.classnetk.entidades.TestesVest
import br.classinformatica.vcandroid.classnetk.entidades.TestesVestAlt
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.caverock.androidsvg.SVG
import java.lang.Exception

class ClassTestesVest {

    var ALTURA_ALTERNATIVA = 48
    var LARGURA_ALTERNATIVA = 48

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var testesVest: TestesVest? = null
    var testesVestAlt = ArrayList<TestesVestAlt>()
    var objenunciado: Objenunciado? = null
    var objtexto = ArrayList<Objtexto>()
    var listaIv = ArrayList<ImageView>()
    var posicaoSelecionada = -1

    private fun carregarImagem(ivAlternativa: ImageView, posicao: Int, act: ActivityApresentacao) {
        if (testesVestAlt[posicao].arquivo.isEmpty()) {
            var bmp: Bitmap
            if (testesVest!!.selecao_x == 1) {
                try {
                    var svg: SVG
                    if (posicaoSelecionada == posicao) {
                        svg = SVG.getFromAsset(act.assets, "testevest/opt1svgbranco.svg")
                    } else {
                        svg = SVG.getFromAsset(act.assets, "testevest/opt0svgbranco.svg")
                    }
                    bmp = Bitmap.createBitmap(LARGURA_ALTERNATIVA, ALTURA_ALTERNATIVA, Bitmap.Config.ARGB_8888)
                    var cv = Canvas(bmp)
                    cv.drawPicture(svg.renderToPicture(), Rect(0, 0, LARGURA_ALTERNATIVA, ALTURA_ALTERNATIVA))
                } catch (e: Exception) {
                    UtilClass.trataErro(e)
                    if (posicaoSelecionada == posicao) {
                        bmp = BitmapFactory.decodeResource(act.resources, R.drawable.opt1)
                    } else {
                        bmp = BitmapFactory.decodeResource(act.resources, R.drawable.opt0)
                    }
                }
            } else {
                try {
                    var svg: SVG
                    if (posicaoSelecionada == posicao) {
                        svg = SVG.getFromAsset(act.assets, "testevest/opt1svg.svg")
                    } else {
                        svg = SVG.getFromAsset(act.assets, "testevest/opt0svg.svg")
                    }
                    bmp = Bitmap.createBitmap(LARGURA_ALTERNATIVA, ALTURA_ALTERNATIVA, Bitmap.Config.ARGB_8888)
                    var cv = Canvas(bmp)
                    cv.drawPicture(svg.renderToPicture(), Rect(0, 0, LARGURA_ALTERNATIVA, ALTURA_ALTERNATIVA))
                } catch (e: Exception) {
                    UtilClass.trataErro(e)
                    if (posicaoSelecionada == posicao) {
                        bmp = BitmapFactory.decodeResource(act.resources, R.drawable.opt1)
                    } else {
                        bmp = BitmapFactory.decodeResource(act.resources, R.drawable.opt0)
                    }
                }
            }
            ivAlternativa.setImageBitmap(bmp)
        }
    }

    fun desenhar(act: ActivityApresentacao) {
        var llGeral: LinearLayout? = null
        for (i in 0 until testesVestAlt.size) {
            var j = 0
            while (j < objtexto.size) {
                if (objtexto[j].posicao == testesVestAlt[i].indice_obj) {
                    ALTURA_ALTERNATIVA = Math.round(testesVestAlt[i].altura_img * pesoY) + Math.round(testesVest!!.aumenta_par * pesoY)
                    LARGURA_ALTERNATIVA = Math.round(testesVestAlt[i].largura_img * pesoX) + Math.round(testesVest!!.aumenta_par * pesoX)

                    var lar = LARGURA_ALTERNATIVA + Math.round(testesVestAlt[i].largura * pesoX) + Math.round(testesVest!!.distancia_h * pesoX)
                    var alt = Math.round(testesVestAlt[i].altura * pesoY)
                    if (alt < ALTURA_ALTERNATIVA) {
                        alt = ALTURA_ALTERNATIVA
                    }
                    var rlGeral = RelativeLayout.LayoutParams(lar, alt)
                    rlGeral.leftMargin = Math.round(testesVestAlt[i].esquerdo * pesoX)
                    rlGeral.topMargin = Math.round(testesVestAlt[i].topo * pesoY)

                    llGeral = LinearLayout(act)
                    llGeral.layoutParams = rlGeral
                    llGeral.orientation = LinearLayout.HORIZONTAL
                    rlTela!!.addView(llGeral)

                    var ivAlternativa = ImageView(act)
                    ivAlternativa.tag = i
                    carregarImagem(ivAlternativa, i, act)
                    ivAlternativa.setOnClickListener {
                        var pos = it.tag as Int
                        if (posicaoSelecionada > -1) {
                            var ant = posicaoSelecionada
                            posicaoSelecionada = pos
                            carregarImagem(listaIv[ant], ant, act)
                        } else {
                            posicaoSelecionada = pos
                        }
                        carregarImagem(listaIv[posicaoSelecionada], posicaoSelecionada, act)
                    }
                    listaIv.add(ivAlternativa)
                    llGeral.addView(ivAlternativa)

                    var parTexto = LinearLayout.LayoutParams(Math.round(testesVestAlt[i].largura * pesoX), Math.round(testesVestAlt[i].altura * pesoY))
                    parTexto.leftMargin = Math.round(testesVest!!.distancia_h * pesoX)

                    var tvTexto = TextView(act)
                    tvTexto.layoutParams = parTexto

                    tvTexto.gravity = Gravity.TOP or Gravity.LEFT
                    tvTexto.setLayerType(RelativeLayout.LAYER_TYPE_SOFTWARE, null)
                    tvTexto.isSingleLine = false
                    tvTexto.setHorizontallyScrolling(false)
                    tvTexto.setPadding(tvTexto.paddingLeft, 0, tvTexto.paddingRight, 0)

                    tvTexto.setTextSize(TypedValue.COMPLEX_UNIT_PX, testesVestAlt[i].tamanho.toFloat() * pesoX)
                    tvTexto.setBackgroundColor(testesVestAlt[i].cor_fundo)
                    tvTexto.setTextColor(testesVestAlt[i].cor)

                    tvTexto.text = objtexto[j].texto

                    llGeral.addView(tvTexto)

                    j = objtexto.size
                } else {
                    j++
                }
            }

            rlTela!!.invalidate()
        }
        if (llGeral != null) {
            llGeral.postDelayed({
                desenhado = true
            }, 50)
        }
    }
}