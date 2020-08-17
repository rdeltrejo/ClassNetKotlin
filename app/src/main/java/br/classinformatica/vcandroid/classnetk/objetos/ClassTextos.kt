package br.classinformatica.vcandroid.classnetk.objetos

import android.webkit.WebView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Objtexto
import br.classinformatica.vcandroid.classnetk.entidades.Textos
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassTextos {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var textos: Textos? = null
    var objtexto: Objtexto? = null

    fun desenhar(act: ActivityApresentacao) {
        var rl = RelativeLayout.LayoutParams(Math.round(textos!!.largura * pesoX), Math.round(textos!!.altura * pesoY))
        rl.leftMargin = Math.round(textos!!.esquerdo * pesoX)
        rl.topMargin = Math.round(textos!!.topo * pesoY)

        var webView = WebView(act)
        webView.layoutParams = rl
        webView.setBackgroundColor(textos!!.cor_fundo)

        if (aplicacao!!.metrics!!.density > 1 && pesoY > 0 && pesoX > 0) {
            var tira = 100 - (pesoY * 100)
            if (pesoX < pesoY) {
                tira = 100 - (pesoX * 100)
            }
            var scale = Math.round(((aplicacao!!.metrics!!.density * 100) - 100 - tira))
            webView.setInitialScale(scale)
        }

        if (UtilClass.existeArquivoLocal(act, objtexto!!.texto)) {
            UtilClass.loadHtmlLocal(act, objtexto!!.texto, webView)
        } else {
            UtilClass.loadHtml(aplicacao!!.servidores!!.ser_link + "arquivos/" + objtexto!!.texto,
                objtexto!!.texto,
                webView,
                aplicacao!!)
        }

        rlTela!!.addView(webView)

        rlTela!!.invalidate()

        webView.postDelayed({
            desenhado = true
        }, 50)
    }
}