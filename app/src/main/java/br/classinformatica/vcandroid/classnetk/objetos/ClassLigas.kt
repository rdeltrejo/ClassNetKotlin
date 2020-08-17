package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Color
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Liga1
import br.classinformatica.vcandroid.classnetk.entidades.Liga2
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objimagem
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassLigas {

    var ivLiga1: ImageView? = null
    var ivLiga2: ImageView? = null
    var ivLinha: ImageView? = null
    var ligando1 = false
    var ligando2 = false
    var ligouCorreta1 = false
    var ligouErrada1 = false
    var ligouCorreta2 = false
    var ligouErrada2 = false

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var liga1: Liga1? = null
    var liga2: Liga2? = null
    var objimagemLiga1: Objimagem? = null
    var objimagemLiga2: Objimagem? = null
    var objenunciado: Objenunciado? = null

    fun desenhar(act: ActivityApresentacao) {
        ivLinha = ImageView(act)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ivLinha!!.setBackgroundColor(act.resources.getColor(android.R.color.transparent, act.theme))
        } else {
            ivLinha!!.setBackgroundColor(act.resources.getColor(android.R.color.transparent))
        }
        rlTela!!.addView(ivLinha)

        var rlLiga1 = RelativeLayout.LayoutParams(Math.round(liga1!!.largura * pesoX),
            Math.round(liga1!!.altura * pesoY))
        rlLiga1.leftMargin = Math.round(liga1!!.esquerdo * pesoX)
        rlLiga1.topMargin = Math.round(liga1!!.topo * pesoY)

        var rlLiga2 = RelativeLayout.LayoutParams(Math.round(liga2!!.largura * pesoX),
            Math.round(liga2!!.altura * pesoY))
        rlLiga2.leftMargin = Math.round(liga2!!.esquerdo * pesoX)
        rlLiga2.topMargin = Math.round(liga2!!.topo * pesoY)

        ivLiga1 = ImageView(act)
        ivLiga1!!.layoutParams = rlLiga1
        ivLiga1!!.setBackgroundColor(liga1!!.cor_fundo)
        ivLiga1!!.scaleType = ImageView.ScaleType.FIT_XY
        if (objimagemLiga1 != null) {
            if (UtilClass.existeArquivoLocal(act, objimagemLiga1!!.arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act, ivLiga1!!, objimagemLiga1!!.arquivo)
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemLiga1!!.arquivo, R.drawable.olho,
                    true,
                    ivLiga1!!,
                    aplicacao!!.configuracoes!!)
            }
        } else {
            ivLiga1!!.setImageResource(R.drawable.olho)
        }
        ivLiga1!!.setOnTouchListener(touchLiga1)

        ivLiga2 = ImageView(act)
        ivLiga2!!.layoutParams = rlLiga2
        ivLiga2!!.setBackgroundColor(liga2!!.cor_fundo)
        ivLiga2!!.scaleType = ImageView.ScaleType.FIT_XY
        if (objimagemLiga2 != null) {
            if (UtilClass.existeArquivoLocal(act, objimagemLiga2!!.arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act, ivLiga2!!, objimagemLiga2!!.arquivo)
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagemLiga2!!.arquivo, R.drawable.olho,
                    true,
                    ivLiga2!!,
                    aplicacao!!.configuracoes!!)
            }
        } else {
            ivLiga2!!.setImageResource(R.drawable.olho)
        }
        ivLiga2!!.setOnTouchListener(touchLiga2)

        rlTela!!.addView(ivLiga1!!)
        rlTela!!.addView(ivLiga2!!)

        rlTela!!.invalidate()

        ivLiga2!!.postDelayed({
            desenhado = true
        }, 50)
    }

    var touchLiga1 = View.OnTouchListener  { _, event ->
        if (!ligouCorreta1 && !ligouErrada1) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                aplicacao!!.classLigasUsando = this
                ligando1 = true
                ligando2 = false
                aplicacao!!.rlTela = rlTela
                false
            }
        }
        true
    }

    var touchLiga2 = View.OnTouchListener { _, event ->
        if (!ligouCorreta2 && !ligouErrada2) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                aplicacao!!.classLigasUsando = this
                ligando1 = false
                ligando2 = true
                aplicacao!!.rlTela = rlTela
                false
            }
        }
        true
    }
}