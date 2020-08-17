package br.classinformatica.vcandroid.classnetk.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.entidades.Configuracoes
import br.classinformatica.vcandroid.classnetk.entidades.Descricao
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import com.androidnetworking.common.Priority
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import okhttp3.Authenticator
import java.io.*
import java.math.BigInteger
import java.net.*
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.Exception
import kotlin.collections.ArrayList


class UtilClass {

    companion object {

        const val VERSION_CODE = 1
        const val DATA_ATUALIZACAO = "02/03/2020"//atenção ao compo abaixo para sempre mandar duras as consultas a data da versão
        const val DATA_VERSAO = "20200203_5_3_10"//a data ao inverso

        const val ERRO_SERVIDOR_TAG = "ERRO_SERVIDOR"

        const val ORIGEM_APLICATIVO = "Class Net Android"

        //caminhos pastas
        const val CAMINHO = "classnet/"
        const val CAMINHO_TMP = CAMINHO + "tmp/"
        const val CAMINHO_ARQUIVOS = CAMINHO + "arquivos/"

        //caminhos arquivos
        const val CAMINHO_DESCRITOR = CAMINHO_TMP + "descritor.txt"
        const val CAMINHO_ARQUIVO_ZIP = CAMINHO_TMP + "arquivo.zip"

        //retornos de activity
        const val RETORNO_CONFIGURACOES = 1000
        const val RETORNO_FONTES_DESCONHECIDAS = 1001
        const val RETORNO_ATUALIZAR_CADASTRO = 1002
        const val RETORNO_APRESENTACAO = 1003

        //propriedades
        const val REDIM_TELAS_TAMANHO_ORIGINAL = 0
        const val REDIM_TELAS_TODA_TELA = 1
        const val REDIM_TELAS_LARGURA_TELA = 2
        const val REDIM_TELAS_MAXIMO_PROPORCIONAL = 3

        //servlets usados
        const val SERVLET = "servlet/"
        const val SERVLET_BAIXARARQUIVOSMULTIPLOS = SERVLET + "BaixarArquivosMultiplos?android=" + DATA_VERSAO
        const val SERVLET_GRAVAARQUIVOPROJ = SERVLET + "GravaArquivoProj?android=" + DATA_VERSAO
        const val SERVLET_SQLCONSULTA = SERVLET + "SqlConsulta?android=" + DATA_VERSAO
        const val SERVLET_SQLCONSULTAMULTIPLA = SERVLET + "SqlConsultaMultipla?android=" + DATA_VERSAO
        const val SERVLET_SQLEXECUTA = SERVLET + "SqlExecuta?android=" + DATA_VERSAO

        //tentativas para conectar com o servidor
        const val TENTATIVAS_CONECTAR = 3

        //timeouts as conexões com o servidor
        const val TIMEOUT_COM_FOTO = 60.toLong()
        const val TIMEOUT_SEM_FOTO = 15.toLong()

        //tipos objetos
        const val TIPO_ANIMACAO = 14L
        const val TIPO_ARRASTAR = 8L
        const val TIPO_ARRASTAR_IMAGEM = 10L
        const val TIPO_ARRASTAR_N = 23L
        const val TIPO_DISSERTATIVA = 20L
        const val TIPO_FILMES_TELA = 6L
        const val TIPO_GIRA = 13L
        const val TIPO_IMAGEM_EFEITO = 2L
        const val TIPO_LIGA1 = 15L
        const val TIPO_LIGA2 = 16L
        const val TIPO_PREENCHIMENTO = 4L
        const val TIPO_ROTULO = 3L
        const val TIPO_SOLTAR = 9L
        const val TIPO_SOLTAR_IMAGEM = 11L
        const val TIPO_SOLTAR_N = 24L
        const val TIPO_TELA = 90L
        const val TIPO_TESTE_VEST = 28L
        const val TIPO_TEXTO = 18L

        //urls padrões
        const val URL_AMAZON = "http://amazon.class.com.br/classnet/"
        const val URL_GEO = "http://geo.class.com.br/classnet/"

        //funções dos objetos
        const val IMAGEM_AVANCAR_AVANCAR = 1
        const val IMAGEM_AVANCAR_ORIGEM = 200
        const val IMAGEM_AVANCAR_SAIR = 100
        const val IMAGEM_AVANCAR_VOLTAR = -1
        const val REGISTRA_IMAGEM_CERTO = 2
        const val REGISTRA_IMAGEM_ERRADO = 1
        const val REGISTRA_IMAGEM_ESCAPAR = 3

        //tipos de janela para finalizar
        const val AVALIAR = 0
        const val OCULTAR = 1
        const val OCULTAR_MOSTRAR_NOTA = 2
        const val EXCLUIR = 3

        fun byteToHex(hash: ByteArray): String {
            var formatter = Formatter()
            for (b in hash) {
                formatter.format("%02x", b)
            }
            var result = formatter.toString()
            formatter.close()
            return result
        }

        fun changeProxy(configuracoes: Configuracoes) {
            if (configuracoes.con_proxy_link.isEmpty()) {
                System.setProperty("http.proxyHost", "")
                System.setProperty("http.proxyPort", "")
                System.setProperty("https.proxyHost", "")
                System.setProperty("https.proxyPort", "")
            } else {
                System.setProperty("http.proxyHost", configuracoes.con_proxy_link)
                System.setProperty("http.proxyPort", configuracoes.con_proxy_porta.toString())
                System.setProperty("https.proxyHost", configuracoes.con_proxy_link)
                System.setProperty("https.proxyPort", configuracoes.con_proxy_porta.toString())
            }
        }

        fun conectaHttp(
            url: String,
            jsonObject: JSONObject,
            httpClient: OkHttpClient?,
            configuracoes: Configuracoes,
            response: JSONObjectRequestListener
        ) {
            val okHttpClient: OkHttpClient

            if (httpClient == null) {

                if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {

                    val proxy = Proxy(
                        Proxy.Type.HTTP,
                        InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                    )

                    if (configuracoes.con_proxy_usu.isNotEmpty()) {

                        /*
                        val authenticator = object : Authenticator {
                            @Throws(IOException::class)
                            override fun authenticate(route: Route, response: Response): Request {
                                val credential = Credentials.basic(usuProxy, senProxy)
                                return response.request().newBuilder().header("Proxy-Authorization", credential).build()
                            }
                        }
                        * */
                        //este é o formato lambda, igual o trecho acima
                        val authenticator = Authenticator { _, response ->
                            val credential =
                                Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                            response.request().newBuilder().header("Proxy-Authorization", credential).build()
                        }

                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .proxyAuthenticator(authenticator)
                                .proxy(proxy)
                                .build()
                    } else {
                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                                .proxy(proxy)
                                .build()
                    }
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .build()
                }
            } else {
                okHttpClient = httpClient
            }

            AndroidNetworking.post(url)
                .setContentType("application/json")
                .addJSONObjectBody(jsonObject)
                .responseOnlyFromNetwork
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(response)
        }

        fun carregarImagemImageView(context: Context, url: String, possuiCache: Boolean, configuracoes: Configuracoes, target: Target) {
            var okHttpClient: OkHttpClient
            if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                val proxy = Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                )

                if (configuracoes.con_proxy_link.isNotEmpty()) {
                    val authenticator = Authenticator { _, response ->
                        val credential =
                            Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                        response.request().newBuilder().header("Proxy-Authorization", credential).build()
                    }

                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxyAuthenticator(authenticator)
                            .proxy(proxy)
                            .build()
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxy(proxy)
                            .build()
                }
            } else {
                okHttpClient =
                    OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                        .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .build()
            }

            var picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()

            if (possuiCache) {
                picasso.load(url).into(target)
            } else {
                picasso.load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(target)
            }
        }

        fun carregarImagemImageView(context: Context, url: String, possuiCache: Boolean, iv: ImageView, configuracoes: Configuracoes) {
            var okHttpClient: OkHttpClient
            if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                val proxy = Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                )

                if (configuracoes.con_proxy_link.isNotEmpty()) {
                    val authenticator = Authenticator { _, response ->
                        val credential =
                            Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                        response.request().newBuilder().header("Proxy-Authorization", credential).build()
                    }

                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxyAuthenticator(authenticator)
                            .proxy(proxy)
                            .build()
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxy(proxy)
                            .build()
                }
            } else {
                okHttpClient =
                    OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                        .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .build()
            }

            /*if (possuiCache) {
                Picasso.get().load(url).error(erroImg).into(iv)
            } else {
                Picasso.get().load(url).error(erroImg).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv)
            }*/

            var picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()

            if (possuiCache) {
                picasso.load(url).into(iv)
            } else {
                picasso.load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv)
            }
        }

        fun carregarImagemImageView(context: Context, url: String, erroImg: Int, possuiCache: Boolean, iv: ImageView, configuracoes: Configuracoes) {
            var okHttpClient: OkHttpClient
            if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                val proxy = Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                )

                if (configuracoes.con_proxy_link.isNotEmpty()) {
                    val authenticator = Authenticator { _, response ->
                        val credential =
                            Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                        response.request().newBuilder().header("Proxy-Authorization", credential).build()
                    }

                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxyAuthenticator(authenticator)
                            .proxy(proxy)
                            .build()
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                            .proxy(proxy)
                            .build()
                }
            } else {
                okHttpClient =
                    OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                        .connectTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT_SEM_FOTO, TimeUnit.SECONDS)
                        .build()
            }

            /*if (possuiCache) {
                Picasso.get().load(url).error(erroImg).into(iv)
            } else {
                Picasso.get().load(url).error(erroImg).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv)
            }*/

            var picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()

            if (possuiCache) {
                picasso.load(url).error(erroImg).into(iv)
            } else {
                picasso.load(url).error(erroImg).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv)
            }
        }

        fun carregarImagemImageViewLocal(context: Context, iv: ImageView, arquivo: String): Bitmap {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            /*Picasso.get().load(file).error(R.drawable.olho).into(iv)*/
            var bitmap = BitmapFactory.decodeFile(file.absolutePath)
            iv.setImageBitmap(bitmap)
            return bitmap
        }

        fun carregarImagemImageViewLocal(context: Context, arquivo: String, target: Target) {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            Picasso.get().load(file).error(R.drawable.olho).into(target)
        }

        fun convert(inputStream: InputStream, charset: Charset): String {
            var builder = StringBuilder()
            try {
                var line: String
                var bufferedReader = BufferedReader(InputStreamReader(inputStream, charset))
                line = bufferedReader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = bufferedReader.readLine()
                }
                bufferedReader.close()
                return builder.toString()
            } catch (e: Exception) {
                trataErro(e)
            }
            return ""
        }

        /*fun download(
            url: String,
            fileDownload: File?,
            timeoutSeg: Long,
            configuracoes: Configuracoes,
            callback: Callback
        ) {
            if (fileDownload != null) {
                if (fileDownload.exists()) {
                    fileDownload.delete()
                }

                var okHttpClient: OkHttpClient? = null
                if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                    val proxy = Proxy(
                        Proxy.Type.HTTP,
                        InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                    )

                    if (configuracoes.con_proxy_usu.isNotEmpty()) {
                        val authenticator = Authenticator { _, response ->
                            val credential =
                                Credentials.basic(configuracoes!!.con_proxy_usu, configuracoes!!.con_proxy_pwd)
                            response.request().newBuilder().header("Proxy-Authorization", credential).build()
                        }

                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .proxyAuthenticator(authenticator)
                                .proxy(proxy)
                                .build()
                    } else {
                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .proxy(proxy)
                                .build()
                    }
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .build()
                }

                try {
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    okHttpClient!!.newCall(request).enqueue(callback)
                } catch (e: Exception) {
                    trataErro(e)
                }

            }
        }

        fun download(
            url: String,
            fileDownload: File?,
            timeoutSeg: Long,
            listenerProgresso: DownloadProgressListener,
            listenerDownload: DownloadListener
        ) {
            if (fileDownload != null) {
                if (fileDownload.exists()) {
                    fileDownload.delete()
                }

                var okHttpClient =
                    OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                        .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .build()

                AndroidNetworking.download(url, fileDownload.parent, nomeArquivo(fileDownload.absolutePath))
                    .setPriority(Priority.MEDIUM)
                    .setOkHttpClient(okHttpClient)
                    .responseOnlyFromNetwork
                    .build()
                    .setDownloadProgressListener(listenerProgresso)
                    .startDownload(listenerDownload)
            }
        }*/

        fun download(
            url: String,
            fileDownload: File?,
            timeoutSeg: Long,
            configuracoes: Configuracoes,
            listenerProgresso: DownloadProgressListener,
            listenerDownload: DownloadListener
        ) {
            if (fileDownload != null) {
                if (fileDownload.exists()) {
                    fileDownload.delete()
                }

                var okHttpClient: OkHttpClient
                if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                    val proxy = Proxy(
                        Proxy.Type.HTTP,
                        InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                    )

                    if (configuracoes.con_proxy_usu.isNotEmpty()) {
                        val authenticator = Authenticator { _, response ->
                            val credential =
                                Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                            response.request().newBuilder().header("Proxy-Authorization", credential).build()
                        }

                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .proxyAuthenticator(authenticator)
                                .proxy(proxy)
                                .build()
                    } else {
                        okHttpClient =
                            OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                                .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                                .proxy(proxy)
                                .build()
                    }
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .build()
                }

                AndroidNetworking.download(url, fileDownload.parent, nomeArquivo(fileDownload.absolutePath))
                    .setPriority(Priority.MEDIUM)
                    .setOkHttpClient(okHttpClient)
                    .responseOnlyFromNetwork
                    .build()
                    .setDownloadProgressListener(listenerProgresso)
                    .startDownload(listenerDownload)
            }
        }

        fun esconderBarraTitulo(act: AppCompatActivity) {
            act.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        fun existeArquivoLocal(context: Context, arquivo: String): Boolean {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            return file.exists()
        }

        fun getCaminhoArquivos(context: Context) : String {
            var file = File(getCaminhoRaiz(context) + CAMINHO_ARQUIVOS)
            if (!file.exists()) {
                file.mkdirs()
            }
            if (!file.absoluteFile.endsWith("/")) {
                return file.absolutePath + "/"
            }
            return file.absolutePath
        }

        fun getCaminhoRaiz(context: Context): String {
            return "/data/data/" + context.packageName + "/"
        }


        fun getNomeSemAcento(arquivo: String): String {
            var cSemAcento = arquivo
            val vAcentos = ArrayList<String>()
            val vUsos = ArrayList<String>()
            vAcentos.add("ç")
            vUsos.add("c")
            vAcentos.add("á")
            vUsos.add("a")
            vAcentos.add("à")
            vUsos.add("a")
            vAcentos.add("ã")
            vUsos.add("a")
            vAcentos.add("â")
            vUsos.add("a")
            vAcentos.add("ä")
            vUsos.add("a")
            vAcentos.add("é")
            vUsos.add("e")
            vAcentos.add("è")
            vUsos.add("e")
            vAcentos.add("ê")
            vUsos.add("e")
            vAcentos.add("ë")
            vUsos.add("e")
            vAcentos.add("í")
            vUsos.add("i")
            vAcentos.add("ì")
            vUsos.add("i")
            vAcentos.add("ï")
            vUsos.add("i")
            vAcentos.add("ó")
            vUsos.add("o")
            vAcentos.add("ò")
            vUsos.add("o")
            vAcentos.add("õ")
            vUsos.add("o")
            vAcentos.add("ô")
            vUsos.add("o")
            vAcentos.add("ö")
            vUsos.add("o")
            vAcentos.add("ú")
            vUsos.add("u")
            vAcentos.add("ù")
            vUsos.add("u")
            vAcentos.add("û")
            vUsos.add("u")
            vAcentos.add("ü")
            vUsos.add("u")
            vAcentos.add("ñ")
            vUsos.add("n")
            val n = vAcentos.size
            for (i in 1 until n) {
                vAcentos.add(vAcentos.elementAt(i).toUpperCase())
                vUsos.add(vUsos.elementAt(i).toUpperCase())
            }
            for (ch in 33..44) {
                vAcentos.add(ch.toString())
                vUsos.add("_")
            }
            for (ch in 47..47) {
                vAcentos.add(ch.toString())
                vUsos.add("_")
            }
            for (ch in 58..64) {
                vAcentos.add(ch.toString())
                vUsos.add("_")
            }
            for (ch in 91..94) {
                vAcentos.add(ch.toString())
                vUsos.add("_")
            }
            for (ch in 96..96) {
                vAcentos.add(ch.toString())
                vUsos.add("_")
            }
            for (i in 0 until vAcentos.size) {
                if (cSemAcento.indexOf(vAcentos.elementAt(i)) > -1) {
                    cSemAcento = cSemAcento.replace(vAcentos.elementAt(i), vUsos.elementAt(i))
                }
            }
            for (i in 0 until cSemAcento.length) {
                if (cSemAcento[i].toInt() > 122) {
                    cSemAcento = cSemAcento.replace(cSemAcento[i], '_')
                }
            }
            return cSemAcento
        }

        fun getTemaBlack(act: AppCompatActivity): Context {
            return ContextThemeWrapper(act, android.R.style.Theme_Black)
        }

        fun isRetrato(act: AppCompatActivity): Boolean {
            if (act.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            return false
        }

        fun loadHtml(url: String, nome: String, webView: WebView, aplicacao: Aplicacao) {
            if (possuiProxy(aplicacao.configuracoes!!)) {
                loadHtmlDownload(1, url, nome, webView, aplicacao)
            } else {
                webView.loadUrl(url)
            }
        }

        fun loadHtmlDownload(nTentativa: Int, url: String, nome: String, webView: WebView, aplicacao: Aplicacao) {
            var file = File(getCaminhoArquivos(aplicacao.act!!) + nomeArquivo(nome))
            if (file.exists()) {
                webView.loadUrl("file://" + file.absolutePath)
            } else {
                try {
                    //file.createNewFile()
                    //não precisa verificar se tem proxy, sei q tem
                    download(url, file, TIMEOUT_COM_FOTO, aplicacao.configuracoes!!, object : DownloadProgressListener {
                        override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                        }
                    }, object : DownloadListener {
                        override fun onDownloadComplete() {
                            try {
                                webView.loadUrl("file://" + file.absolutePath)
                            } catch (e: Exception) {
                                trataErro(e)
                                file.delete()
                                webView.loadDataWithBaseURL("",
                                    "<p>" + aplicacao.act!!.getString(R.string.erro_carregar_html) + "</p>",
                                    "text/html",
                                    "UTF-8",
                                    "")
                            }
                        }

                        override fun onError(anError: ANError?) {
                            trataErro(anError!!)
                            file.delete()
                            if (nTentativa < TENTATIVAS_CONECTAR) {
                                loadHtmlDownload(nTentativa + 1, url, nome, webView, aplicacao)
                            } else {
                                webView.loadDataWithBaseURL("",
                                    "<p>" + aplicacao.act!!.getString(R.string.erro_carregar_html) + "</p>",
                                    "text/html",
                                    "UTF-8",
                                    "")
                                file.delete()
                            }
                        }
                    })
                } catch (e: Exception) {
                    trataErro(e)
                    file.delete()
                    if (nTentativa < TENTATIVAS_CONECTAR) {
                        loadHtmlDownload(nTentativa + 1, url, nome, webView, aplicacao)
                    } else {
                        webView.loadDataWithBaseURL("",
                            "<p>" + aplicacao.act!!.getString(R.string.erro_carregar_html) + "</p>",
                            "text/html",
                            "UTF-8",
                            "")
                        file.delete()
                    }
                }
            }
        }

        fun loadHtmlLocal(context: Context, arquivo: String, webView: WebView) {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            if (file.exists()) {
                webView.loadUrl("file://" + file.absolutePath)
            }
        }

        fun msgItems(context: Context, titulo: Int, items: Array<String>, click: DialogInterface.OnClickListener?) {
            var alerta = AlertDialog.Builder(context)
            alerta.setTitle(titulo)
            alerta.setItems(items, click)
            alerta.create().show()
        }

        fun msgOk(context: Context, titulo: Int, msg: Int, botao: Int, click: DialogInterface.OnClickListener?) {
            var alerta = AlertDialog.Builder(context)
            alerta.setTitle(titulo)
            alerta.setMessage(msg)
            alerta.setPositiveButton(botao, click)
            alerta.create().show()
        }

        fun msgSimNao(
            context: Context,
            titulo: Int,
            msg: Int,
            botaoSim: Int,
            botaoNao: Int,
            clickSim: DialogInterface.OnClickListener?,
            clickNao: DialogInterface.OnClickListener?
        ) {
            var alerta = AlertDialog.Builder(context)
            alerta.setTitle(titulo)
            alerta.setMessage(msg)
            alerta.setNegativeButton(botaoSim, clickSim)
            alerta.setPositiveButton(botaoNao, clickNao)
            alerta.create().show()
        }

        fun msgToast(context: Context, msg: Int) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        fun msgViewOk(context: Context, titulo: Int, view: View, botao: Int, click: DialogInterface.OnClickListener?) {
            var alerta = AlertDialog.Builder(context)
            alerta.setTitle(titulo)
            alerta.setView(view)
            alerta.setPositiveButton(botao, click)
            alerta.create().show()
        }

        fun nomeArquivo(arquivo: String): String {
            var arq = arquivo
            arq = arq.replace('\\', '/')
            val f: File

            if (arq.startsWith("file:")) {
                f = File(arq.substring(5))
            } else {
                f = File(arq)
            }
            if (f.exists()) {
                var nome = f.name
                if (nome.lastIndexOf("/") > 0) {
                    nome = nome.substring(nome.lastIndexOf("/") + 1)
                }
                return nome
            } else {
                var nome = arq
                if (nome.lastIndexOf("/") > 0) {
                    nome = nome.substring(nome.lastIndexOf("/") + 1)
                }
                return nome
            }
        }

        fun possuiProxy(configuracoes: Configuracoes): Boolean {
            return configuracoes.con_proxy_link.isNotEmpty() and configuracoes.con_proxy_usu.isNotEmpty()
        }

        fun reproduzirAudioLocal(context: Context, arquivo: String, mediaPlayer: MediaPlayer) {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            if (file.exists()) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                if (mediaPlayer.duration > 0) {//já estanciado
                    mediaPlayer.start()
                } else {
                    mediaPlayer.apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource("file://" + file.absolutePath)
                        prepare() // might take long! (for buffering, etc)
                        start()
                    }
                }
            }
        }

        fun reproduzirAudioServidor(url: String, mediaPlayer: MediaPlayer) {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                if (mediaPlayer.duration > 0) {//já estanciado
                    mediaPlayer.start()
                } else {
                    mediaPlayer.apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(url)
                        prepare() // might take long! (for buffering, etc)
                        start()
                    }
                }
            } catch (e: Exception) {
                trataErro(e)
            }
        }

        /*fun reproduzirAudioLocal(context: Context, arquivo: String) {
            var file = File(getCaminhoArquivos(context) + nomeArquivo(arquivo))
            if (file.exists()) {
                var mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource("file://" + file.absolutePath)
                    prepare() // might take long! (for buffering, etc)
                    start()
                }
            }
        }

        fun reproduzirAudioServidor(url: String, nome: String, aplicacao: Aplicacao) {
            try {
                if (possuiProxy(aplicacao.configuracoes!!)) {
                    reproduzirAudioServidorDownload(1, url, nome, aplicacao)
                } else {
                    var mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(url)
                        prepare() // might take long! (for buffering, etc)
                        start()
                    }
                }
            } catch (e: Exception) {
                trataErro(e)
            }
        }

        fun reproduzirAudioServidorDownload(nTentativa: Int, url: String, nome: String, aplicacao: Aplicacao) {
            var file = File(getCaminhoArquivos(aplicacao.act!!) + nomeArquivo(nome))
            if (file.exists()) {
                var mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource("file://" + file.absolutePath)
                    prepare() // might take long! (for buffering, etc)
                    start()
                }
            } else {
                try {
                    //não precisa verificar se tem proxy, sei q tem
                    download(url, file, TIMEOUT_COM_FOTO, aplicacao.configuracoes!!, object : DownloadProgressListener {
                        override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                        }
                    }, object : DownloadListener {
                        override fun onDownloadComplete() {
                            try {
                                var mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                                    setDataSource("file://" + file.absolutePath)
                                    prepare() // might take long! (for buffering, etc)
                                    start()
                                }
                            } catch (e: Exception) {
                                trataErro(e)
                                file.delete()
                            }
                        }

                        override fun onError(anError: ANError?) {
                            trataErro(anError!!)
                            file.delete()
                            if (nTentativa < TENTATIVAS_CONECTAR) {
                                reproduzirAudioServidorDownload(nTentativa + 1, url, nome, aplicacao)
                            } else {
                                file.delete()
                            }
                        }
                    })
                } catch (e: Exception) {
                    trataErro(e)
                    file.delete()
                    if (nTentativa < TENTATIVAS_CONECTAR) {
                        reproduzirAudioServidorDownload(nTentativa + 1, url, nome, aplicacao)
                    } else {
                        file.delete()
                    }
                }
            }
        }*/

        fun reproduzirVideoLocal(context: Context, arquivo: String, barra_progresso: Boolean, stretchVideoView: StretchVideoView) {
            try {
                stretchVideoView.setVideoPath(getCaminhoArquivos(context) + nomeArquivo(arquivo))
                stretchVideoView.start()
                stretchVideoView.invalidate()

                if (barra_progresso) {
                    var mediaController = MediaController(context)
                    mediaController.setAnchorView(stretchVideoView)
                    stretchVideoView.setMediaController(mediaController)
                }

            } catch (e: Exception) {
                trataErro(e)
            }
        }

        fun reproduzirVideoServidor(url: String, barra_progresso: Boolean, stretchVideoView: StretchVideoView, aplicacao: Aplicacao) {
            if (url.trim().isNotEmpty()) {
                var path = Uri.parse(url)
                try {
                    stretchVideoView.setVideoURI(path)
                    stretchVideoView.start()
                    stretchVideoView.invalidate()

                    if (barra_progresso) {
                        var mediaController = MediaController(aplicacao.act!!)
                        mediaController.setAnchorView(stretchVideoView)
                        stretchVideoView.setMediaController(mediaController)
                    }

                } catch (e: Exception) {
                    trataErro(e)
                }
            }
        }

        fun retJanelaFinalizar(descricao: Descricao): Int {
            return AVALIAR
        }

        fun toHex(arg: String): String {
            if (arg.trim().isEmpty()) {
                return ""
            }
            return String.format("%x", BigInteger(1, arg.toByteArray(Charsets.ISO_8859_1)))
        }

        fun toSHA1(txt: String): String {
            var sha1 = ""
            try {
                var crypt = MessageDigest.getInstance("SHA-1")
                crypt.reset()
                crypt.update(txt.toByteArray(Charset.forName("UTF-8")))
                sha1 = byteToHex(crypt.digest())
            } catch (e: NoSuchAlgorithmException) {
                trataErro(e)
            } catch (e: UnsupportedEncodingException) {
                trataErro(e)
            }
            return sha1
        }

        fun trataErro(e: ANError) {
            e.printStackTrace()
        }

        fun trataErro(e: Exception) {
            e.printStackTrace()
        }

        fun trataErro(tag: String, msg: String) {
            Log.e(tag, msg)
        }

        fun twipToPixel(twip: Int): Int {
            var pixel = (66667095013177L).toDouble() / (1000000000000000L).toDouble()
            return Math.round(twip.toDouble() * pixel).toInt()
        }

        fun upload(url: String, file: File, timeoutSeg: Long, configuracoes: Configuracoes, callback: Callback) {
            var requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("file"), file))
                .addFormDataPart("some-field", "some-value")
                .build()

            var request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            var okHttpClient: OkHttpClient
            if (configuracoes.con_proxy_link.isNotEmpty() && configuracoes.con_proxy_porta != 0) {
                val proxy = Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(configuracoes.con_proxy_link, configuracoes.con_proxy_porta)
                )

                if (configuracoes.con_proxy_link.isNotEmpty()) {
                    val authenticator = Authenticator { _, response ->
                        val credential =
                            Credentials.basic(configuracoes.con_proxy_usu, configuracoes.con_proxy_pwd)
                        response.request().newBuilder().header("Proxy-Authorization", credential).build()
                    }

                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .proxyAuthenticator(authenticator)
                            .proxy(proxy)
                            .build()
                } else {
                    okHttpClient =
                        OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                            .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                            .proxy(proxy)
                            .build()
                }
            } else {
                okHttpClient =
                    OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                        .connectTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .readTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .writeTimeout(timeoutSeg, TimeUnit.SECONDS)
                        .build()
            }

            okHttpClient.newCall(request).enqueue(callback)
        }

        fun upload(url: String, file: File, timeoutSeg: Long, uploadListener: UploadProgressListener, respostaListener: JSONObjectRequestListener) {

            var okHttpClient = OkHttpClient().newBuilder()//estou configurando por requisicao, mas posso configurar global
                .connectTimeout(timeoutSeg, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutSeg, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutSeg, TimeUnit.MILLISECONDS)
                .build()

            AndroidNetworking.upload(url)
                .addMultipartFile("file", file)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .setUploadProgressListener(uploadListener)
                .getAsJSONObject(respostaListener)
        }

        fun verificaPastasIniciais(context: Context) {
            var file = File(getCaminhoRaiz(context) + CAMINHO)
            if (!file.exists()) {
                file.mkdirs()
            }

            file = File(getCaminhoRaiz(context) + CAMINHO_TMP)
            if (!file.exists()) {
                file.mkdirs()
            }

            file = File(getCaminhoArquivos(context))
            if (!file.exists()) {
                file.mkdirs()
            }
        }

        fun zipArquivo(zipoutputstream: ZipOutputStream, origem: String, path: String): Boolean {
            var lRet = true
            var file = File(origem)

            var rgb = ByteArray(24576)

            var n: Int

            var fileinputstream: FileInputStream

            // Calculate the CRC-32 value.  This isn't strictly necessary
            //   for DEFLATED entries, but it doesn't hurt.

            var crc32 = CRC32()

            try {
                fileinputstream = FileInputStream(file)

                n = fileinputstream.read(rgb)
                while (n > -1) {
                    crc32.update(rgb, 0, n)
                    n = fileinputstream.read(rgb)
                }

                fileinputstream.close()

                // Create a ZIP entry.

                var zipentry = ZipEntry(path + UtilClass.getNomeSemAcento(file.name.toLowerCase()))
                zipentry.size = file.length()
                zipentry.time = file.lastModified()
                zipentry.crc = crc32.getValue()


                // Add the ZIP entry and associated data.

                zipoutputstream.putNextEntry(zipentry)

                fileinputstream = FileInputStream(file)

                n = fileinputstream.read(rgb)
                while (n > -1) {
                    zipoutputstream.write(rgb, 0, n)
                    n = fileinputstream.read(rgb)
                }

                fileinputstream.close()

                zipoutputstream.closeEntry()
            } catch (eArq: Exception) {
                trataErro(eArq)
                lRet = false
            }

            return lRet
        }
    }

}