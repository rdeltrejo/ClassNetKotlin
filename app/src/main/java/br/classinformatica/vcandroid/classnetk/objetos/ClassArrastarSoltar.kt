package br.classinformatica.vcandroid.classnetk.objetos

import android.view.Gravity
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.TextView
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Arrastar
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Soltar

class ClassArrastarSoltar {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var indiceSoltar = -1
    var ligouCorreta = false
    var ligouErrada = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null
    var tvArrastar: TextView? = null
    var tvSoltar: TextView? = null

    var arrastar: Arrastar? = null
    var soltar: Soltar? = null
    var objenunciado: Objenunciado? = null

    fun desenhar(act: ActivityApresentacao) {
        var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastar!!.largura * pesoX), Math.round(arrastar!!.altura * pesoY))
        rlArrastar.leftMargin = Math.round(arrastar!!.esquerdo * pesoX)
        rlArrastar.topMargin = Math.round(arrastar!!.topo * pesoY)

        var rlSoltar = RelativeLayout.LayoutParams(Math.round(soltar!!.largura * pesoX), Math.round(soltar!!.altura * pesoY))
        rlSoltar.leftMargin = Math.round(soltar!!.esquerdo * pesoX)
        rlSoltar.topMargin = Math.round(soltar!!.topo * pesoY)

        tvArrastar = TextView(act)
        tvArrastar!!.layoutParams = rlArrastar
        tvArrastar!!.gravity = Gravity.CENTER
        tvArrastar!!.setBackgroundColor(arrastar!!.cor_fundo)
        tvArrastar!!.setTextColor(arrastar!!.cor)
        tvArrastar!!.textSize = arrastar!!.tamanho.toFloat()
        tvArrastar!!.text = arrastar!!.texto
        tvArrastar!!.setOnTouchListener { _, event ->
            if (!ligouCorreta && !ligouErrada) {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    aplicacao!!.tvArrastar = tvArrastar
                    aplicacao!!.rlTela = rlTela
                    false
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    var achou = false
                    var i = 0
                    while (!achou && i < aplicacao!!.classArrastarSoltar.size) {
                        var rlSoltar = aplicacao!!.classArrastarSoltar[i].tvSoltar!!.layoutParams as RelativeLayout.LayoutParams
                        var sXIni = rlSoltar.leftMargin
                        var sXFim = rlSoltar.leftMargin + rlSoltar.width
                        var sYIni = rlSoltar.topMargin
                        var sYFim = rlSoltar.topMargin + rlSoltar.height

                        var rlArrastar = tvArrastar!!.layoutParams as RelativeLayout.LayoutParams
                        var aXIni = rlArrastar.leftMargin
                        var aXFim = rlArrastar.leftMargin + rlArrastar.width
                        var aYIni = rlArrastar.topMargin
                        var aYFim = rlArrastar.topMargin + rlArrastar.height

                        var posX = event!!.x.toInt()
                        var posY = event!!.y.toInt()

                        if ((aXIni >= sXIni && aXIni <= sXFim && aYIni >= sYIni && aYIni <= sYFim) ||
                            (aXIni >= sXIni && aXIni <= sXFim && aYFim >= sYIni && aYFim <= sYFim) ||
                            (aXFim >= sXIni && aXFim <= sXFim && aYIni >= sYIni && aYIni <= sYFim) ||
                            (aXFim >= sXIni && aXFim <= sXFim && aYFim >= sYIni && aYFim <= sYFim) ||
                            (posX >= sXIni && posX <= sXFim && posY >= sYIni && posY <= sYFim)) {//ligou certo
                            if (aplicacao!!.classArrastarSoltar[i].soltar!!.indice == arrastar!!.indice || aplicacao!!.descricao!!.teste_vest == 1) {
                                var rlArrastar = RelativeLayout.LayoutParams(Math.round(soltar!!.largura * pesoX),
                                    Math.round(soltar!!.altura * pesoY))
                                rlArrastar.leftMargin = Math.round(soltar!!.esquerdo * pesoX)
                                rlArrastar.topMargin = Math.round(soltar!!.topo * pesoY)
                                tvArrastar!!.layoutParams = rlArrastar

                                if (aplicacao!!.classArrastarSoltar[i].soltar!!.indice == arrastar!!.indice) {
                                    ligouCorreta = true
                                } else {
                                    ligouErrada = true
                                }
                                indiceSoltar = aplicacao!!.classArrastarSoltar[i].soltar!!.indice
                                achou = true
                            } else {
                                aplicacao!!.aulaFeita!!.erros_anterior++
                            }

                            i = aplicacao!!.classArrastarSoltar.size
                        } else {
                            i++
                        }
                    }
                    if (!achou) {
                        var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastar!!.largura * pesoX),
                            Math.round(arrastar!!.altura * pesoY))
                        rlArrastar.leftMargin = Math.round(arrastar!!.esquerdo * pesoX)
                        rlArrastar.topMargin = Math.round(arrastar!!.topo * pesoY)
                        tvArrastar!!.layoutParams = rlArrastar
                    }
                    aplicacao!!.tvArrastar = null
                    aplicacao!!.rlTela = null
                    aplicacao!!.removeAlt = 0
                    aplicacao!!.removeLar = 0
                }
            }
            true
        }

        tvSoltar = TextView(act)
        tvSoltar!!.layoutParams = rlSoltar
        tvSoltar!!.gravity = Gravity.CENTER
        tvSoltar!!.setBackgroundColor(soltar!!.cor_fundo)
        tvSoltar!!.setTextColor(soltar!!.cor)
        tvSoltar!!.textSize = soltar!!.tamanho.toFloat()
        tvSoltar!!.text = soltar!!.texto

        rlTela!!.addView(tvSoltar!!)
        rlTela!!.addView(tvArrastar!!)

        rlTela!!.invalidate()

        tvSoltar!!.postDelayed({
            desenhado = true
        }, 50)
    }
}