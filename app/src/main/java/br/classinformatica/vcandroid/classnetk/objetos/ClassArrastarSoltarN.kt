package br.classinformatica.vcandroid.classnetk.objetos

import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.ArrastarN
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objimagem
import br.classinformatica.vcandroid.classnetk.entidades.SoltarN
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassArrastarSoltarN {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var indiceSoltar = ArrayList<Int>()
    var ligouCorreta = ArrayList<Boolean>()
    var ligouErrada = ArrayList<Boolean>()
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null
    var ivArrastarN = ArrayList<ImageView>()
    var ivSoltarN: ImageView? = null

    var arrastarN = ArrayList<ArrastarN>()
    var soltarN: SoltarN? = null
    var objimagemArrastarN = ArrayList<Objimagem>()
    var objimagemSoltarN: Objimagem? = null
    var objenunciado: Objenunciado? = null

    fun desenhar(act: ActivityApresentacao) {

        for (i in 0 until arrastarN.size) {
            ligouCorreta.add(false)
            ligouErrada.add(false)
            indiceSoltar.add(-1)
            var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastarN[i].largura * pesoX),
                Math.round(arrastarN[i].altura * pesoY))
            rlArrastar.leftMargin = Math.round(arrastarN[i].esquerdo * pesoX)
            rlArrastar.topMargin = Math.round(arrastarN[i].topo * pesoY)

            ivArrastarN.add(ImageView(act))
            ivArrastarN[i].tag = i
            ivArrastarN[i].layoutParams = rlArrastar
            ivArrastarN[i].setBackgroundColor(arrastarN[i].cor_fundo)
            ivArrastarN[i].scaleType = ImageView.ScaleType.FIT_XY
            if (objimagemArrastarN.size > i) {
                if (UtilClass.existeArquivoLocal(act, objimagemArrastarN[i].arquivo)) {
                    UtilClass.carregarImagemImageViewLocal(act, ivArrastarN[i], objimagemArrastarN[i].arquivo)
                } else {
                    UtilClass.carregarImagemImageView(act,
                        aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemArrastarN[i].arquivo,
                        R.drawable.olho,
                        true,
                        ivArrastarN[i],
                        aplicacao!!.configuracoes!!)
                }
            } else {
                ivArrastarN[i].setImageResource(R.drawable.olho)
            }
            ivArrastarN[i].setOnTouchListener { view, event ->
                var i = view.tag as Int
                if (!ligouCorreta[i] && !ligouErrada[i]) {
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        aplicacao!!.ivArrastarN = ivArrastarN[i]
                        aplicacao!!.rlTela = rlTela
                        false
                    } else if (event?.action == MotionEvent.ACTION_UP) {
                        var achou = false
                        var j = 0
                        while (!achou && j < aplicacao!!.classArrastarSoltarN.size) {
                            var rlSoltar = ivSoltarN!!.layoutParams as RelativeLayout.LayoutParams
                            var sXIni = rlSoltar.leftMargin
                            var sXFim = rlSoltar.leftMargin + rlSoltar.width
                            var sYIni = rlSoltar.topMargin
                            var sYFim = rlSoltar.topMargin + rlSoltar.height

                            var rlArrastar = ivArrastarN[i].layoutParams as RelativeLayout.LayoutParams
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
                                if (aplicacao!!.classArrastarSoltarN[j].soltarN!!.indice == arrastarN[i]!!.indice || aplicacao!!.descricao!!.teste_vest == 1) {
                                    var rlArrastar = RelativeLayout.LayoutParams(Math.round(soltarN!!.largura * pesoX),
                                        Math.round(soltarN!!.altura * pesoY))
                                    rlArrastar.leftMargin = Math.round(soltarN!!.esquerdo * pesoX)
                                    rlArrastar.topMargin = Math.round(soltarN!!.topo * pesoY)
                                    ivArrastarN[i].layoutParams = rlArrastar

                                    if (aplicacao!!.classArrastarSoltarN[j].soltarN!!.indice == arrastarN[i]!!.indice) {
                                        ligouCorreta[i] = true
                                    } else {
                                        ligouErrada[i] = true
                                    }
                                    indiceSoltar[i] = aplicacao!!.classArrastarSoltarN[j].soltarN!!.indice
                                    achou = true
                                } else {
                                    aplicacao!!.aulaFeita!!.erros_anterior++
                                }

                                j = aplicacao!!.classArrastarSoltarN.size
                            } else {
                                j++
                            }
                        }
                        if (!achou) {
                            var rlArrastar = RelativeLayout.LayoutParams(Math.round(arrastarN[i].largura * pesoX),
                                Math.round(arrastarN[i].altura * pesoY))
                            rlArrastar.leftMargin = Math.round(arrastarN[i].esquerdo * pesoX)
                            rlArrastar.topMargin = Math.round(arrastarN[i].topo * pesoY)
                            ivArrastarN[i].layoutParams = rlArrastar
                        }
                        aplicacao!!.ivArrastarN = null
                        aplicacao!!.rlTela = null
                        aplicacao!!.removeAlt = 0
                        aplicacao!!.removeLar = 0
                    }
                }
                true
            }
            rlTela!!.addView(ivArrastarN[i])
        }


        var rlSoltar = RelativeLayout.LayoutParams(Math.round(soltarN!!.largura * pesoX),
            Math.round(soltarN!!.altura * pesoY))
        rlSoltar.leftMargin = Math.round(soltarN!!.esquerdo * pesoX)
        rlSoltar.topMargin = Math.round(soltarN!!.topo * pesoY)

        ivSoltarN = ImageView(act)
        ivSoltarN!!.layoutParams = rlSoltar
        ivSoltarN!!.setBackgroundColor(soltarN!!.cor_fundo)
        ivSoltarN!!.scaleType = ImageView.ScaleType.FIT_XY
        if (objimagemSoltarN != null) {
            if (UtilClass.existeArquivoLocal(act, objimagemSoltarN!!.arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act, ivSoltarN!!, objimagemSoltarN!!.arquivo)
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemSoltarN!!.arquivo, R.drawable.olho,
                    true,
                    ivSoltarN!!,
                    aplicacao!!.configuracoes!!)
            }
        } else {
            ivSoltarN!!.setImageResource(R.drawable.olho)
        }

        rlTela!!.addView(ivSoltarN!!)

        rlTela!!.invalidate()

        ivSoltarN!!.postDelayed({
            desenhado = true
        }, 50)
    }
}