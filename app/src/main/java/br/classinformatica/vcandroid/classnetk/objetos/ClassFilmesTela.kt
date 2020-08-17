package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Color
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.FilmesTela
import br.classinformatica.vcandroid.classnetk.entidades.Objfilme
import br.classinformatica.vcandroid.classnetk.util.StretchVideoView
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassFilmesTela {

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var filmesTela: FilmesTela? = null
    var objfilme: Objfilme? = null

    fun desenhar(act: ActivityApresentacao) {
        var rl =
            RelativeLayout.LayoutParams(Math.round(filmesTela!!.largura * pesoX), Math.round(filmesTela!!.altura * pesoY))
        rl.leftMargin = Math.round(filmesTela!!.esquerdo * pesoX)
        rl.topMargin = Math.round(filmesTela!!.topo * pesoY)

        if (filmesTela!!.link_url.trim().isNotEmpty()) {
            var webView = WebView(act)
            webView.layoutParams = rl
            webView.setBackgroundColor(Color.BLACK)
            UtilClass.msgToast(act, R.string.youtube_demora)

            webView.settings.javaScriptEnabled = true
            //webView.getSettings().setPluginState(PluginState.ON)
            webView.webChromeClient = object : WebChromeClient() {}
            var html = getHTML(filmesTela!!.link_url, Math.round(filmesTela!!.largura * pesoX), Math.round(filmesTela!!.altura * pesoY))
            webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "")
            //videoW.setOnTouchListener(clickApres)
            
            rlTela!!.addView(webView)
            webView.postDelayed({
                desenhado = true
            }, 50)
        } else if (objfilme != null) {
            var video = StretchVideoView(act)
            video.largura = Math.round(filmesTela!!.largura * pesoX)
            video.altura = Math.round(filmesTela!!.altura * pesoY)
            video.requestLayout()
            video.isFocusable = true
            video.isFocusableInTouchMode = true
            video.keepScreenOn = true
            video.requestFocus() // necessario para eventos touch
            video.isClickable = true
            
            video.layoutParams = rl
            video.holder.setFixedSize(Math.round(filmesTela!!.largura * pesoX), Math.round(filmesTela!!.altura * pesoY))

            if (UtilClass.existeArquivoLocal(act, objfilme!!.arquivo)) {
                UtilClass.reproduzirVideoLocal(act, objfilme!!.arquivo, filmesTela!!.barra_progresso == 1, video)
            } else {
                UtilClass.reproduzirVideoServidor(aplicacao!!.servidores!!.ser_link + "arquivos/" + objfilme!!.arquivo,
                    filmesTela!!.barra_progresso == 1,
                    video,
                    aplicacao!!)
            }
            video.setOnCompletionListener {
            }
            //video.setOnTouchListener(clickApres)

            rlTela!!.addView(video)

            rlTela!!.invalidate()

            video.postDelayed({
                desenhado = true
            }, 50)
        }
    }

    private fun getHTML(link_url: String, largura: Int, altura: Int): String {
        var tipo = "https"
        var aux = link_url
        if (link_url.contains("http:")) {
            tipo = "http"
        }
        var pos = link_url.indexOf("watch?v=")
        var html = ""
        if (pos > -1) {
            pos += 8
            aux = link_url.substring(pos)
            pos = aux.indexOf("/")
            if (pos > -1) {
                aux = aux.substring(0, pos)
            } else {
                pos = aux.indexOf("&")
                if (pos > -1) {
                    aux = aux.substring(0, pos)
                }
            }
            var lar = Math.round((largura / aplicacao!!.metrics!!.density))
            var alt = Math.round((altura / aplicacao!!.metrics!!.density))
            html = "<body style=\"margin:0 0 0 0 padding:0 0 0 0\"> <iframe width=\"$lar\" height=\"$alt\" src=\"$tipo://www.youtube.com/embed/$aux\" frameborder=\"0\" allowfullscreen></iframe></body>"
        }
        return html
    }
}