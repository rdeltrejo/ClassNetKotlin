package br.classinformatica.vcandroid.classnetk.objetos

import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.ArrastarImagens
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objimagem
import br.classinformatica.vcandroid.classnetk.entidades.SoltarImagens
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassArrastarSoltarImagens {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var indiceSoltar = -1
    var ligouCorreta = false
    var ligouErrada = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null
    var ivArrastarImagens: ImageView? = null
    var ivSoltarImagens: ImageView? = null

    var arrastarImagens: ArrastarImagens? = null
    var soltarImagens: SoltarImagens? = null
    var objimagemArrastar: Objimagem? = null
    var objimagemSoltar: Objimagem? = null
    var objenunciado: Objenunciado? = null

    fun desenhar(act: ActivityApresentacao) {
        var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastarImagens!!.largura * pesoX),
            Math.round(arrastarImagens!!.altura * pesoY))
        rlArrastar.leftMargin = Math.round(arrastarImagens!!.esquerdo * pesoX)
        rlArrastar.topMargin = Math.round(arrastarImagens!!.topo * pesoY)

        var rlSoltar = RelativeLayout.LayoutParams(Math.round(soltarImagens!!.largura * pesoX),
            Math.round(soltarImagens!!.altura * pesoY))
        rlSoltar.leftMargin = Math.round(soltarImagens!!.esquerdo * pesoX)
        rlSoltar.topMargin = Math.round(soltarImagens!!.topo * pesoY)

        ivArrastarImagens = ImageView(act)
        ivArrastarImagens!!.layoutParams = rlArrastar
        ivArrastarImagens!!.setBackgroundColor(arrastarImagens!!.cor_fundo)
        ivArrastarImagens!!.scaleType = ImageView.ScaleType.FIT_XY
        if (objimagemArrastar != null) {
            if (UtilClass.existeArquivoLocal(act!!, objimagemArrastar!!.arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act!!, ivArrastarImagens!!, objimagemArrastar!!.arquivo)
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemArrastar!!.arquivo, R.drawable.olho,
                    true,
                    ivArrastarImagens!!,
                    aplicacao!!.configuracoes!!)
            }
        } else {
            ivArrastarImagens!!.setImageResource(R.drawable.olho)
        }
        ivArrastarImagens!!.setOnTouchListener { _, event ->
            if (!ligouCorreta && !ligouErrada) {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    aplicacao!!.ivArrastarImagens = ivArrastarImagens
                    aplicacao!!.rlTela = rlTela
                    false
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    var achou = false
                    var i = 0
                    while (!achou && i < aplicacao!!.classArrastarSoltar.size) {
                        var rlSoltar = aplicacao!!.classArrastarSoltarImagens[i].ivSoltarImagens!!.layoutParams as RelativeLayout.LayoutParams
                        var sXIni = rlSoltar.leftMargin
                        var sXFim = rlSoltar.leftMargin + rlSoltar.width
                        var sYIni = rlSoltar.topMargin
                        var sYFim = rlSoltar.topMargin + rlSoltar.height

                        var rlArrastar = ivArrastarImagens!!.layoutParams as RelativeLayout.LayoutParams
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
                            if (aplicacao!!.classArrastarSoltarImagens[i].soltarImagens!!.indice == arrastarImagens!!.indice || aplicacao!!.descricao!!.teste_vest == 1) {
                                var rlArrastar =
                                    RelativeLayout.LayoutParams(Math.round(soltarImagens!!.largura * pesoX),
                                        Math.round(soltarImagens!!.altura * pesoY))
                                rlArrastar.leftMargin = Math.round(soltarImagens!!.esquerdo * pesoX)
                                rlArrastar.topMargin = Math.round(soltarImagens!!.topo * pesoY)
                                ivArrastarImagens!!.layoutParams = rlArrastar

                                if (aplicacao!!.classArrastarSoltarImagens[i].soltarImagens!!.indice == arrastarImagens!!.indice) {
                                    ligouCorreta = true
                                } else {
                                    ligouErrada = true
                                }
                                indiceSoltar = aplicacao!!.classArrastarSoltarImagens[i].soltarImagens!!.indice
                                achou = true
                            } else {
                                aplicacao!!.aulaFeita!!.erros_anterior++
                            }

                            i = aplicacao!!.classArrastarSoltarImagens.size
                        } else {
                            i++
                        }
                    }
                    if (!achou) {
                        var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastarImagens!!.largura * pesoX),
                            Math.round(arrastarImagens!!.altura * pesoY))
                        rlArrastar.leftMargin = Math.round(arrastarImagens!!.esquerdo * pesoX)
                        rlArrastar.topMargin = Math.round(arrastarImagens!!.topo * pesoY)
                        ivArrastarImagens!!.layoutParams = rlArrastar
                    }
                    aplicacao!!.ivArrastarImagens = null
                    aplicacao!!.rlTela = null
                    aplicacao!!.removeAlt = 0
                    aplicacao!!.removeLar = 0
                }
            }
            true
        }

        ivSoltarImagens = ImageView(act)
        ivSoltarImagens!!.layoutParams = rlSoltar
        ivSoltarImagens!!.setBackgroundColor(soltarImagens!!.cor_fundo)
        ivSoltarImagens!!.scaleType = ImageView.ScaleType.FIT_XY
        if (objimagemSoltar != null) {
            if (UtilClass.existeArquivoLocal(act!!, objimagemSoltar!!.arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act!!, ivSoltarImagens!!, objimagemSoltar!!.arquivo)
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemSoltar!!.arquivo, R.drawable.olho,
                    true,
                    ivSoltarImagens!!,
                    aplicacao!!.configuracoes!!)
            }
        } else {
            ivSoltarImagens!!.setImageResource(R.drawable.olho)
        }

        rlTela!!.addView(ivSoltarImagens!!)
        rlTela!!.addView(ivArrastarImagens!!)

        rlTela!!.invalidate()

        ivSoltarImagens!!.postDelayed({
            desenhado = true
        }, 50)
    }
}