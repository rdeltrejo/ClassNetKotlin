package br.classinformatica.vcandroid.classnetk

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import br.classinformatica.vcandroid.classnetk.dialogs.DialogPesquisarInstituicao
import br.classinformatica.vcandroid.classnetk.entidades.Instituicao
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import br.classinformatica.vcandroid.classnetk.activities.ActivityConfiguracoes
import br.classinformatica.vcandroid.classnetk.activities.ActivityProjetosAtribuidos
import br.classinformatica.vcandroid.classnetk.entidades.Aluno
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import org.json.JSONArray
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private var abriuLogin = false
    private var abriuMenu = false
    private var act = this
    private var aplicacao: Aplicacao? = null
    private var bCadastro: Button? = null
    private var etCodigo: EditText? = null
    private var etInstituicao: EditText? = null
    private var etSenha: EditText? = null
    private var ivFundo: ImageView? = null
    private var ivOpcoes: ImageView? = null
    private var llLogin: LinearLayout? = null
    private var llMenu: LinearLayout? = null
    private var llOpcoes: LinearLayout? = null
    private var tvLogin: TextView? = null
    private var tvServidor: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UtilClass.isRetrato(act)) {
            setContentView(R.layout.activity_main_retrato)
        } else {
            setContentView(R.layout.activity_main)
        }

        iniciarActivity(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == UtilClass.RETORNO_CONFIGURACOES) {
                carregarInstituicaoTela()
            } else if (requestCode == UtilClass.RETORNO_FONTES_DESCONHECIDAS) {
                try {
                    if (Settings.Secure.getInt(contentResolver,
                            Settings.Secure.INSTALL_NON_MARKET_APPS) == 1) {//está com fontes desconhecidas
                        instalarClassNetServidor()
                    }
                } catch (e: Exception) {
                    UtilClass.trataErro(e)
                }
            }
        }
        aplicacao!!.act = act
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("abriuLogin", abriuLogin)
        outState.putBoolean("abriuMenu", abriuMenu)
    }

    @SuppressLint("ResourceAsColor")
    private fun abrirJanelaHelp() {
        try {
            var pack = act.packageManager.getPackageInfo(act.packageName, 0)
            var versao = ""
            try {
                versao = pack.versionName
            } catch (ex: Exception) {
                versao = ""
            }

            var context = UtilClass.getTemaBlack(act)

            var txtInfoSoft = TextView(context)
            var txtInfoDisp = TextView(context)
            var txtSiteClass = TextView(context)
            var txtPolitica = TextView(context)
            var lay = LinearLayout(context)
            var scroll = ScrollView(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtInfoSoft.setTextAppearance(android.R.style.TextAppearance_Medium)
                txtInfoDisp.setTextAppearance(android.R.style.TextAppearance_Medium)
                txtSiteClass.setTextAppearance(android.R.style.TextAppearance_Medium)
                txtPolitica.setTextAppearance(android.R.style.TextAppearance_Medium)
            }
            txtInfoSoft.setTextColor(Color.WHITE)
            txtInfoDisp.setTextColor(Color.WHITE)
            lay.orientation = LinearLayout.VERTICAL
            lay.setPadding(10, 10, 10, 10)

            var txt = act.getString(R.string.info_titulo) +
                    "\n" + act.getString(R.string.app_name) + " " + versao +
                    "\n" + act.getString(R.string.data_atualizacao_) + " " + UtilClass.DATA_ATUALIZACAO +
                    "\n" + act.getString(R.string.fabricante)
            txtInfoSoft.text = txt

            txt = "www.class.com.br"
            txtSiteClass.text = txt
            txtSiteClass.setTextColor(Color.rgb(40, 60, 186))
            txtSiteClass.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("http://www.class.com.br")
                startActivity(i)
            }

            txt = "\n" + act.getString(R.string.info_dispositivo) +
                    "\n" + act.getString(R.string.android_) + " " + Build.VERSION.RELEASE +
                    "\n" + act.getString(R.string.resolucao_) + " " + aplicacao!!.metrics!!.widthPixels + "x" + aplicacao!!.metrics!!.heightPixels +
                    "\n" + act.getString(R.string.densidade_) + " " + aplicacao!!.metrics!!.density
            txtInfoDisp.text = txt

            txt = act.getString(R.string.politica_privacidade)
            txtPolitica.text = txt
            txtPolitica.setTextColor(Color.rgb(40, 60, 186))
            txtPolitica.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("http://class.com.br/privacy-policy.txt")
                startActivity(i)
            }

            lay.addView(txtInfoSoft)
            lay.addView(txtSiteClass)
            lay.addView(txtInfoDisp)
            lay.addView(txtPolitica)

            scroll.addView(lay)

            UtilClass.msgViewOk(context, R.string.informacoes_min, scroll, R.string.ok, null)
        } catch (ex: Exception) {
            UtilClass.trataErro(ex)
        }
    }

    private fun abrirJanelaPesuisarInstituicao() {
        var dialog = DialogPesquisarInstituicao(act)
        dialog.show()
    }

    private fun abrirSite() {
        if (aplicacao!!.instituicao == null || aplicacao!!.instituicao!!.site == "") {
            UtilClass.msgOk(act, R.string.aviso, R.string.site_nao_definido, R.string.ok, null)
        } else if (aplicacao!!.instituicao != null && aplicacao!!.instituicao!!.site != "") {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(aplicacao!!.instituicao!!.site)
            startActivity(i)
        }
    }

    private fun abrirTelaUsuario(json: JSONArray) {
        if (json == null || json.length() == 0) {//não exite este código
            UtilClass.msgOk(act, R.string.aviso, R.string.usuario_nao_cadastrado, R.string.ok, null)
        } else {
            var aluno = Aluno()
            aluno!!.id_aluno = json.getJSONArray(0).getLong(0)
            aluno!!.id_instituicao = json.getJSONArray(0).getLong(1)
            aluno!!.id_serie = json.getJSONArray(0).getLong(2)
            aluno!!.id_classe_inf = json.getJSONArray(0).getLong(3)
            aluno!!.id_classe = json.getJSONArray(0).getLong(4)
            aluno!!.codigo = json.getJSONArray(0).getString(5)
            aluno!!.serie = json.getJSONArray(0).getString(6)
            aluno!!.escola = json.getJSONArray(0).getString(7)
            aluno!!.nome = json.getJSONArray(0).getString(8)
            if (json.getJSONArray(0).getString(9).trim().isNotEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                aluno!!.data_de_nascimento = Date(sdf.parse(json.getJSONArray(0).getString(9)).time)
            }
            aluno!!.classe_colegio = json.getJSONArray(0).getString(10)
            aluno!!.codigo_classe_informatica = json.getJSONArray(0).getString(11)
            aluno!!.telefone = json.getJSONArray(0).getString(12)
            aluno!!.endereco = json.getJSONArray(0).getString(13)
            aluno!!.cidade = json.getJSONArray(0).getString(14)
            aluno!!.estado = json.getJSONArray(0).getString(15)
            aluno!!.pais = json.getJSONArray(0).getString(16)
            aluno!!.bairro = json.getJSONArray(0).getString(17)
            aluno!!.cep = json.getJSONArray(0).getString(18)
            aluno!!.foto = json.getJSONArray(0).getString(19)
            aluno!!.email = json.getJSONArray(0).getString(20)
            aluno!!.tela_aluno = json.getJSONArray(0).getString(21)
            aluno!!.site = json.getJSONArray(0).getString(22)
            aluno!!.pasta_aluno = json.getJSONArray(0).getString(23)
            aluno!!.senha = json.getJSONArray(0).getString(24)
            aluno!!.latitude = json.getJSONArray(0).getString(25)
            aluno!!.longitude = json.getJSONArray(0).getString(26)
            aluno!!.sexo = json.getJSONArray(0).getString(27)
            aluno!!.autocadastro = json.getJSONArray(0).getInt(28)
            aluno!!.ddd = json.getJSONArray(0).getString(29)
            aluno!!.celular = json.getJSONArray(0).getString(30)

            var pwd = UtilClass.toSHA1(etSenha!!.text.toString())
            if (pwd != aluno!!.senha) {
                UtilClass.msgOk(act, R.string.aviso, R.string.senha_incorreta, R.string.ok, null)
            } else {
                aplicacao!!.aluno = aluno
                var intent = Intent(act, ActivityProjetosAtribuidos::class.java).apply {
                }
                act.startActivity(intent)
            }
        }
    }

    private fun carregarInstituicaoTela() {
        UtilClass.changeProxy(aplicacao!!.configuracoes!!)
        etInstituicao!!.setText("")
        bCadastro!!.visibility = RelativeLayout.INVISIBLE
        ivFundo!!.setImageResource(R.drawable.abertur)
        aplicacao!!.configuracoes!!.con_id_instituicao = 0
        if (aplicacao!!.instituicao != null) {
            aplicacao!!.configuracoes!!.con_id_instituicao = aplicacao!!.instituicao!!.id_instituicao!!
            if (aplicacao!!.instituicao!!.autocadastro == 1) {
                bCadastro!!.visibility = RelativeLayout.VISIBLE
            }
            etInstituicao!!.setText(aplicacao!!.instituicao!!.titulo)
            if (aplicacao!!.instituicao!!.fundo!!.trim().isNotEmpty()) {
                UtilClass.carregarImagemImageView(act, aplicacao!!.servidores!!.ser_link + "arquivos/" + aplicacao!!.instituicao!!.fundo, R.drawable.abertur, true, ivFundo!!, aplicacao!!.configuracoes!!)
            }
        }
        tvServidor!!.text = aplicacao!!.servidores!!.ser_link
        var banco = Banco(act)
        banco.atualizarInstituicaoConfiguracoes(aplicacao!!.configuracoes!!.con_id_instituicao)
        banco.close()
        if (aplicacao!!.instituicao == null) {
            abriuMenu = false
            mudarMenu()
            abrirJanelaPesuisarInstituicao()
        }
    }

    private fun fazerLogin(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            val pesquisa = JSONArray()
            pesquisa.put("SELECT versao FROM versao WHERE origem = 'vclassnetandroid'")
            pesquisa.put("SELECT id_aluno, id_instituicao, id_serie, id_classe_inf, id_classe, codigo, serie, escola, nome, data_de_nascimento, classe_colegio, codigo_classe_informatica, telefone, endereco, cidade, estado, pais, bairro, cep, foto, email, tela_aluno, site, pasta_aluno, senha, latitude, longitude, sexo, autocadastro, ddd, celular FROM aluno WHERE id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND codigo = '" + etCodigo!!.text.toString() + "'")

            val json = JSONObject()
            json.put("usu", "visitante")
            json.put("pwd", "visitante")
            json.put("pesquisa", pesquisa)

            UtilClass.conectaHttp(
                aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTAMULTIPLA,
                json,
                null,
                aplicacao!!.configuracoes!!,
                object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        aplicacao!!.finalizarProgress()
                        try {
                            if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                                var continua = true
                                var aux = response!!.getJSONArray("consultas").getJSONObject(0).getJSONArray("linhas")
                                if (aux.length() > 0) {
                                    var versao = aux.getJSONArray(0).getString(0)
                                    var vServidor = Array(3) { 0 }
                                    var vLocal = Array(3) { 0 }

                                    var vet = versao.split(".")
                                    vServidor[0] = vet[0].toInt()
                                    vServidor[1] = vet[1].toInt()
                                    if (vet.size == 3) {
                                        vServidor[2] = vet[2].toInt()
                                    }

                                    vet = aplicacao!!.versao.split(".")
                                    vLocal[0] = vet[0].toInt()
                                    vLocal[1] = vet[1].toInt()
                                    if (vet.size == 3) {
                                        vLocal[2] = vet[2].toInt()
                                    }

                                    if (vLocal[0] < vServidor[0] || (vLocal[0] == vServidor[0] && vLocal[1] < vServidor[1])) {//aplicativo desatualizados e é obrigado a atualizar
                                        continua = false
                                        UtilClass.msgSimNao(
                                            act,
                                            R.string.aviso,
                                            R.string.classnetlocal_antigo,
                                            R.string.sim,
                                            R.string.nao,
                                            DialogInterface.OnClickListener { _, _ ->
                                                instalarClassNetServidor()
                                            },
                                            DialogInterface.OnClickListener { _, _ ->
                                                act.finish()
                                            })
                                    } else if (vLocal[0] > vServidor[0] || (vLocal[0] == vServidor[0] && vLocal[1] > vServidor[1])) {//servidor desatualizado e é obrigado a instalar versão compatível com o servidor
                                        continua = false
                                        UtilClass.msgSimNao(act,
                                            R.string.aviso,
                                            R.string.classnetserver_antigo,
                                            R.string.sim,
                                            R.string.nao,
                                            DialogInterface.OnClickListener { _, _ ->
                                                instalarClassNetServidor()
                                            },
                                            DialogInterface.OnClickListener { _, _ ->
                                                act.finish()
                                            })
                                    } else if (vLocal[0] == vServidor[0] && vLocal[1] == vServidor[1] && vLocal[2] < vServidor[2]) {//aplicativo desatualizado mas não é obrigado a atualizar
                                        continua = false
                                        UtilClass.msgSimNao(act, R.string.aviso, R.string.classnetlocal_antigo, R.string.sim, R.string.nao, DialogInterface.OnClickListener { _, _ ->
                                            instalarClassNetServidor()
                                        }, DialogInterface.OnClickListener { _, _ ->
                                            abrirTelaUsuario(response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas"))
                                        })
                                    } else if (vLocal[0] == vServidor[0] && vLocal[1] == vServidor[1] && vLocal[2] > vServidor[2]) {//servidor desatualizado mas não é obrigado a instalar versão do servidor
                                        continua = false
                                        UtilClass.msgSimNao(act, R.string.aviso, R.string.classnetserver_antigo, R.string.sim, R.string.nao, DialogInterface.OnClickListener { _, _ ->
                                            instalarClassNetServidor()
                                        }, DialogInterface.OnClickListener { _, _ ->
                                            abrirTelaUsuario(response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas"))
                                        })
                                    }
                                }

                                if (continua) {
                                    abrirTelaUsuario(response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas"))
                                }
                            } else {
                                UtilClass.trataErro(
                                    UtilClass.ERRO_SERVIDOR_TAG,
                                    response!!.getJSONObject("status").getString("mensagem")
                                )
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    fazerLogin(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(
                                        act,
                                        R.string.aviso,
                                        R.string.servidor_inacessivel,
                                        R.string.ok,
                                        null
                                    )
                                    aplicacao!!.aluno = null
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                fazerLogin(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                aplicacao!!.aluno = null
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            fazerLogin(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            aplicacao!!.aluno = null
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                fazerLogin(nTentativa + 1)
            } else {
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                aplicacao!!.aluno = null
            }
        }
    }

    private fun iniciarActivity(savedInstanceState: Bundle?) {
        aplicacao = application as Aplicacao
        aplicacao!!.act = act
        UtilClass.esconderBarraTitulo(act)
        aplicacao!!.atualizarMetricas()
        aplicacao!!.finalizarApresentacao()

        var pack = act.packageManager.getPackageInfo(act.packageName, 0)
        try {
            aplicacao!!.versao = pack.versionName;
        } catch (ex: Exception) {
            aplicacao!!.versao = ""
        }

        var banco = Banco(act)
        aplicacao!!.configuracoes = banco.getConfiguracoes()

        UtilClass.changeProxy(aplicacao!!.configuracoes!!)

        if (aplicacao!!.configuracoes!!.ser_id == 0) {//não tem servidor
            aplicacao!!.servidores = banco.iniciarServidores()
        } else {
            var servidores = banco.getListaServidores()
            if (servidores.isNotEmpty()) {
                if (aplicacao!!.configuracoes!!.ser_id == 0) {
                    aplicacao!!.servidores = banco.getServidores(UtilClass.URL_AMAZON)
                    if (aplicacao!!.servidores!!.ser_id == 0) {
                        aplicacao!!.servidores!!.ser_link = UtilClass.URL_AMAZON
                        aplicacao!!.servidores = banco.gravarServidores(aplicacao!!.servidores!!)
                    }
                } else {
                    aplicacao!!.servidores = banco.getServidores(aplicacao!!.configuracoes!!.ser_id)
                }
            } else {
                aplicacao!!.servidores = banco.iniciarServidores()
            }
        }
        aplicacao!!.configuracoes!!.ser_id = aplicacao!!.servidores!!.ser_id
        banco.gravarConfiguracoes(aplicacao!!.configuracoes!!)
        banco.close()

        if (abriuLogin || aplicacao!!.configuracoes!!.con_inicia_login) {
            abriuLogin = true
        }


        //inicializando layout
        llOpcoes = findViewById(R.id.llOpcoes)
        tvLogin = findViewById(R.id.tvLogin)
        ivOpcoes = findViewById(R.id.ivOpcoes)
        tvServidor = findViewById(R.id.tvServidor)
        ivFundo = findViewById(R.id.ivFundo)
        llLogin = findViewById(R.id.llLogin)
        var ivLupa = findViewById<ImageView>(R.id.ivLupa)
        etInstituicao = findViewById(R.id.etInstituicao)
        bCadastro = findViewById(R.id.bCadastro)
        etCodigo = findViewById(R.id.etCodigo)
        etSenha = findViewById(R.id.etSenha)
        var bCancelar = findViewById<Button>(R.id.bCancelar)
        var bConfiguracoes = findViewById<Button>(R.id.bConfiguracoes)
        var bOk = findViewById<Button>(R.id.bOk)
        var llInformacoes = findViewById<LinearLayout>(R.id.llInformacoes)
        var llConfiguracoes = findViewById<LinearLayout>(R.id.llConfiguracoes)
        var llSite = findViewById<LinearLayout>(R.id.llSite)
        llMenu = findViewById(R.id.llMenu)

        //eventos de clique
        tvLogin!!.setOnClickListener {
            abriuLogin = !abriuLogin
            abriuMenu = false
            mudarLogin()
            mudarMenu()
        }
        ivOpcoes!!.setOnClickListener {
            abriuMenu = !abriuMenu
            mudarMenu()
        }
        ivLupa.setOnClickListener {
            abriuMenu = false
            mudarMenu()
            abrirJanelaPesuisarInstituicao()
        }
        bCadastro!!.setOnClickListener {
            abriuMenu = false
            mudarMenu()
        }
        bCancelar.setOnClickListener {
            act.finish()
        }
        bConfiguracoes.setOnClickListener {
            abriuMenu = false
            mudarMenu()
            var intent = Intent(act, ActivityConfiguracoes::class.java).apply {
            }
            act.startActivityForResult(intent, UtilClass.RETORNO_CONFIGURACOES)
        }
        bOk.setOnClickListener {
            abriuMenu = false
            mudarMenu()

            if (aplicacao!!.servidores == null) {
                UtilClass.msgOk(act, R.string.aviso, R.string.selecione_instituicao, R.string.ok, null)
            } else if (etCodigo!!.text.toString().trim().isEmpty() || etSenha!!.text.toString().isEmpty()) {
                UtilClass.msgOk(act, R.string.aviso, R.string.digite_usuario_senha, R.string.ok, null)
            } else {
                fazerLogin(1)
            }
        }
        llInformacoes.setOnClickListener {
            abriuMenu = false
            mudarMenu()
            abrirJanelaHelp()
        }
        llConfiguracoes.setOnClickListener {
            abriuMenu = false
            mudarMenu()
            var intent = Intent(act, ActivityConfiguracoes::class.java).apply {
            }
            act.startActivityForResult(intent, UtilClass.RETORNO_CONFIGURACOES)
        }
        llSite.setOnClickListener {
            abriuMenu = false
            mudarMenu()
            abrirSite()
        }



        tvServidor!!.setText(R.string.app_name)
        if (aplicacao!!.servidores!!.ser_id > 0) {
            tvServidor!!.text = aplicacao!!.servidores!!.ser_link
        }

        if (aplicacao!!.configuracoes!!.con_id_instituicao > 0L) {//tem uma instituição selecionada, verificar as propriedades de login
            pesquisarInstituicao(aplicacao!!.configuracoes!!.con_id_instituicao, 1)
        } else {//não selecionou instituição, carregar a lista de instituições
            abrirJanelaPesuisarInstituicao()
        }
        if (savedInstanceState != null) {
            abriuLogin = savedInstanceState.getBoolean("abriuLogin", false)
            abriuMenu = savedInstanceState.getBoolean("abriuMenu", false)
        }
        mudarLogin()
        mudarMenu()
    }

    private fun mudarLogin() {
        if (abriuLogin) {
            tvLogin!!.setText(R.string.sair)
            llLogin!!.visibility = RelativeLayout.VISIBLE
        } else {
            tvLogin!!.setText(R.string.login)
            llLogin!!.visibility = RelativeLayout.INVISIBLE
        }
    }

    private fun instalarClassNetServidor() {
        var continua = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            try {
                if (Settings.Secure.getInt(
                        contentResolver,
                        Settings.Secure.INSTALL_NON_MARKET_APPS
                    ) != 1
                ) {//não está com fontes desconhecidas
                    continua = false
                    startActivityForResult(
                        Intent(Settings.ACTION_SECURITY_SETTINGS),
                        UtilClass.RETORNO_FONTES_DESCONHECIDAS
                    )
                }
            } catch (e: Exception) {
                UtilClass.trataErro(e)
                fazerLogin(1)
            }
        }
        if (continua) {
            var file = File(UtilClass.getCaminhoRaiz(act) + UtilClass.CAMINHO_TMP + "classnet.apk")
            file.delete()
            try {
                file.createNewFile()
            } catch (e: Exception) {
                UtilClass.trataErro(e)
            }
            aplicacao!!.iniciarProgress(act, act.getString(R.string.baixando))
            try {
                UtilClass.download(
                    aplicacao!!.servidores!!.ser_link + "arquivos/execl5/classnet.apk",
                    file,
                    UtilClass.TIMEOUT_COM_FOTO,
                    aplicacao!!.configuracoes!!,
                    object : DownloadProgressListener {
                        override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                            aplicacao!!.setMessageProgress(act.getString(R.string.baixando) + ": " + Math.round(((bytesDownloaded * 100) / totalBytes).toDouble()) + "/100")
                        }
                    },
                    object : DownloadListener {
                        override fun onDownloadComplete() {
                            aplicacao!!.finalizarProgress()
                            var file = File(UtilClass.getCaminhoRaiz(act) + UtilClass.CAMINHO_TMP + "classnet.apk")
                            if (file.exists()) {
                                var intent = Intent(Intent.ACTION_VIEW)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                var apkURI: Uri
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    apkURI = FileProvider.getUriForFile(
                                        act,
                                        act.applicationContext.packageName + ".provider",
                                        file
                                    )
                                } else {
                                    apkURI = Uri.fromFile(file)
                                }
                                intent.setDataAndType(apkURI, "application/vnd.android.package-archive")
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                act.startActivity(intent)
                            } else {
                                UtilClass.msgOk(
                                    act,
                                    R.string.aviso,
                                    R.string.servidor_inacessivel,
                                    R.string.ok,
                                    null
                                )
                            }
                        }

                        override fun onError(anError: ANError?) {
                            UtilClass.trataErro(anError!!)
                            aplicacao!!.finalizarProgress()
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    })
            } catch (e: Exception) {
                UtilClass.trataErro(e)
                aplicacao!!.finalizarProgress()
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    private fun mudarMenu() {
        if (abriuMenu) {
            var rl =
                RelativeLayout.LayoutParams((200 * aplicacao!!.metrics!!.density).toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
            rl.addRule(RelativeLayout.ALIGN_PARENT_END)
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            llMenu!!.layoutParams = rl
            ivOpcoes!!.setImageResource(R.drawable.opcoes2)
        } else {
            ivOpcoes!!.setImageResource(R.drawable.opcoes)
            var rl = RelativeLayout.LayoutParams(0, 0)
            llMenu!!.layoutParams = rl
        }
    }

    private fun pesquisarInstituicao(id_instituicao: Long, nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            val json = JSONObject()
            json.put("usu", "visitante")
            json.put("pwd", "visitante")
            json.put(
                "pesquisa",
                "SELECT id_instituicao, codigo, titulo, site, fundo, atualizacao_automatica, bloqueado, autocadastro FROM instituicao WHERE data_apagamento IS NULL AND id_instituicao = $id_instituicao"
            )
            UtilClass.conectaHttp(
                aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTA,
                json,
                null,
                aplicacao!!.configuracoes!!,
                object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        try {
                            aplicacao!!.finalizarProgress()
                            if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                                val array = response.getJSONArray("linhas")
                                var instituicao: Instituicao
                                if (array.length() > 0) {
                                    instituicao = Instituicao()
                                    instituicao.id_instituicao = array.getJSONArray(0).getLong(0)
                                    instituicao.codigo = array.getJSONArray(0).getString(1)
                                    instituicao.titulo = array.getJSONArray(0).getString(2)
                                    instituicao.site = array.getJSONArray(0).getString(3)
                                    instituicao.fundo = array.getJSONArray(0).getString(4)
                                    instituicao.atualizacao_automatica = array.getJSONArray(0).getInt(5)
                                    instituicao.bloqueado = array.getJSONArray(0).getInt(6)
                                    instituicao.autocadastro = array.getJSONArray(0).getInt(7)
                                    if (instituicao.bloqueado == 0) {
                                        aplicacao!!.instituicao = instituicao
                                    } else {
                                        aplicacao!!.instituicao = null
                                        UtilClass.msgOk(
                                            act,
                                            R.string.aviso,
                                            R.string.instituicao_bloqueada,
                                            R.string.ok,
                                            null
                                        )
                                    }
                                } else {
                                    UtilClass.msgOk(
                                        act,
                                        R.string.aviso,
                                        R.string.servidor_inacessivel,
                                        R.string.ok,
                                        null
                                    )
                                    aplicacao!!.instituicao = null
                                }
                                carregarInstituicaoTela()
                            } else {
                                UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response.getJSONObject("status").getString("msg"))
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    pesquisarInstituicao(id_instituicao, nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(
                                        act,
                                        R.string.aviso,
                                        R.string.servidor_inacessivel,
                                        R.string.ok,
                                        null
                                    )
                                    aplicacao!!.instituicao = null
                                    carregarInstituicaoTela()
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                pesquisarInstituicao(id_instituicao, nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                aplicacao!!.instituicao = null
                                carregarInstituicaoTela()
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            pesquisarInstituicao(id_instituicao, nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            aplicacao!!.instituicao = null
                            carregarInstituicaoTela()
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                pesquisarInstituicao(id_instituicao, nTentativa + 1)
            } else {
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                aplicacao!!.instituicao = null
                carregarInstituicaoTela()
            }
        }
    }

    fun selecionouInstituicao(instituicao: Instituicao) {
        aplicacao!!.instituicao = instituicao
        carregarInstituicaoTela()
    }
}