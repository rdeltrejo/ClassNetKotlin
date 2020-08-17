package br.classinformatica.vcandroid.classnetk.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.adapters.ActivityProjetosAtribuidosAdapter
import br.classinformatica.vcandroid.classnetk.entidades.Aluno
import br.classinformatica.vcandroid.classnetk.entidades.AulaAtribuida
import br.classinformatica.vcandroid.classnetk.entidades.Descricao
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_projetos_atribuidos.*
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Date
import java.text.SimpleDateFormat

class ActivityProjetosAtribuidos : AppCompatActivity() {

    private var abriuExpandir = false
    private var abriuOpcoes = false
    private var act = this
    private var aplicacao: Aplicacao? = null
    private var ivAvisos: ImageView? = null
    private var ivFoto: ImageView? = null
    private var ivOpcoes: ImageView? = null
    private var llMenu: LinearLayout? = null
    private var lvProjetos: ListView? = null
    private var rlCabecalho: RelativeLayout? = null
    private var tvAvisos: TextView? = null
    private var tvProjetosAtribuidosPara: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UtilClass.isRetrato(act)) {
            setContentView(R.layout.activity_projetos_atribuidos_retrato)
        } else {
            setContentView(R.layout.activity_projetos_atribuidos)
        }

        iniciarActivity(savedInstanceState)
    }

    override fun finish() {
        aplicacao!!.aluno = null
        aplicacao!!.atribuidosAulaAtrbuida = null
        aplicacao!!.atribuidosDescricao = null
        super.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UtilClass.RETORNO_ATUALIZAR_CADASTRO) {
                carregarAlunoServidor(1)
            } else if (requestCode == UtilClass.RETORNO_APRESENTACAO) {
                carregarProjetosAtribuidosServidor(1)
                //alterarExpandirContrair()
                //alterarOpcoes()
            }
        }
        aplicacao!!.act = act
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("abriuExpandir", abriuExpandir)
    }

    private fun abrirAlterarCadastro() {
        var intent = Intent(act, ActivityAlterarCadastro::class.java).apply {
        }
        act.startActivityForResult(intent, UtilClass.RETORNO_ATUALIZAR_CADASTRO)
    }

    private fun abrirCarregarNotas() {
        var intent = Intent(act, ActivityRelatorioNotas::class.java).apply {
        }
        act.startActivity(intent)
    }

    private fun alterarExpandirContrair() {
        var lay: LinearLayout.LayoutParams
        if (abriuExpandir) {
            ivExpandirContrair!!.setImageResource(R.drawable.contrair)
            lay = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } else {
            ivExpandirContrair!!.setImageResource(R.drawable.expandir)
            lay = LinearLayout.LayoutParams(0, 0)
        }
        rlCabecalho!!.layoutParams = lay
    }

    private fun alterarOpcoes() {
        var rl: RelativeLayout.LayoutParams
        if (abriuOpcoes) {
            rl = RelativeLayout.LayoutParams((200 * aplicacao!!.metrics!!.density).toInt(), RelativeLayout.LayoutParams.WRAP_CONTENT)
            rl.addRule(RelativeLayout.ALIGN_PARENT_END)
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            ivOpcoes!!.setImageResource(R.drawable.opcoes2)
        } else {
            ivOpcoes!!.setImageResource(R.drawable.opcoes)
            rl = RelativeLayout.LayoutParams(0, 0)
        }
        llMenu!!.layoutParams = rl
    }

    private fun carregarAlunoServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.aguarde))
        try {
            val json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put(
                "pesquisa",
                "SELECT id_aluno, id_instituicao, id_serie, id_classe_inf, id_classe, codigo, serie, escola, nome, data_de_nascimento, classe_colegio, codigo_classe_informatica, telefone, endereco, cidade, estado, pais, bairro, cep, foto, email, tela_aluno, site, pasta_aluno, senha, latitude, longitude, sexo, autocadastro, ddd, celular FROM aluno WHERE id_aluno = " + aplicacao!!.aluno!!.id_aluno
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
                                var aluno = Aluno()
                                if (array.length() > 0) {
                                    aluno!!.id_aluno = array.getJSONArray(0).getLong(0)
                                    aluno!!.id_instituicao = array.getJSONArray(0).getLong(1)
                                    aluno!!.id_serie = array.getJSONArray(0).getLong(2)
                                    aluno!!.id_classe_inf = array.getJSONArray(0).getLong(3)
                                    aluno!!.id_classe = array.getJSONArray(0).getLong(4)
                                    aluno!!.codigo = array.getJSONArray(0).getString(5)
                                    aluno!!.serie = array.getJSONArray(0).getString(6)
                                    aluno!!.escola = array.getJSONArray(0).getString(7)
                                    aluno!!.nome = array.getJSONArray(0).getString(8)
                                    if (array.getJSONArray(0).getString(9).trim().isNotEmpty()) {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                                        aluno!!.data_de_nascimento = Date(sdf.parse(array.getJSONArray(0).getString(9)).time)
                                    }
                                    aluno!!.classe_colegio = array.getJSONArray(0).getString(10)
                                    aluno!!.codigo_classe_informatica = array.getJSONArray(0).getString(11)
                                    aluno!!.telefone = array.getJSONArray(0).getString(12)
                                    aluno!!.endereco = array.getJSONArray(0).getString(13)
                                    aluno!!.cidade = array.getJSONArray(0).getString(14)
                                    aluno!!.estado = array.getJSONArray(0).getString(15)
                                    aluno!!.pais = array.getJSONArray(0).getString(16)
                                    aluno!!.bairro = array.getJSONArray(0).getString(17)
                                    aluno!!.cep = array.getJSONArray(0).getString(18)
                                    aluno!!.foto = array.getJSONArray(0).getString(19)
                                    aluno!!.email = array.getJSONArray(0).getString(20)
                                    aluno!!.tela_aluno = array.getJSONArray(0).getString(21)
                                    aluno!!.site = array.getJSONArray(0).getString(22)
                                    aluno!!.pasta_aluno = array.getJSONArray(0).getString(23)
                                    aluno!!.senha = array.getJSONArray(0).getString(24)
                                    aluno!!.latitude = array.getJSONArray(0).getString(25)
                                    aluno!!.longitude = array.getJSONArray(0).getString(26)
                                    aluno!!.sexo = array.getJSONArray(0).getString(27)
                                    aluno!!.autocadastro = array.getJSONArray(0).getInt(28)
                                    aluno!!.ddd = array.getJSONArray(0).getString(29)
                                    aluno!!.celular = array.getJSONArray(0).getString(30)

                                    aplicacao!!.aluno = aluno

                                    tvProjetosAtribuidosPara!!.text =
                                        act.getString(R.string.projetos_atribuidos_para) + " " + aplicacao!!.aluno!!.nome

                                    if (aplicacao!!.aluno!!.foto!!.isNotEmpty()) {
                                        UtilClass.carregarImagemImageView(act, aplicacao!!.servidores!!.ser_link + "arquivos/" + aplicacao!!.aluno!!.foto!!, R.drawable.aluno, false, ivFoto!!, aplicacao!!.configuracoes!!)
                                    } else {
                                        ivFoto!!.setImageResource(R.drawable.aluno)
                                    }
                                }
                            } else {
                                UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("msg"))
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    carregarAlunoServidor(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarAlunoServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarAlunoServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarAlunoServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    private fun carregarAvisosServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            val pesquisa = JSONArray()
            pesquisa.put("SELECT COUNT(*) AS n FROM aluno a, avisos b LEFT JOIN avisos_lidos c ON b.id_aviso = c.id_aviso AND b.tipousu_dest = c.tipousu_dest AND b.id_usu_dest = c.id_usu_dest WHERE a.id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND a.codigo = '" + aplicacao!!.aluno!!.codigo + "' AND b.tipousu_dest = 1 AND b.id_instituicao_dest = " + aplicacao!!.instituicao!!.id_instituicao + " AND b.id_usu_dest = a.id_aluno AND c.dataleitura IS NULL")
            pesquisa.put("SELECT COUNT(*) AS n FROM avisos b WHERE b.tipousu_dest = 1 AND b.id_instituicao_dest = " + aplicacao!!.instituicao!!.id_instituicao + " AND b.id_usu_dest = 0 AND b.id_usu_ori <> (SELECT id_aluno FROM aluno WHERE id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND codigo = '" + aplicacao!!.aluno!!.codigo + "') AND b.id_aviso NOT IN (SELECT c.id_aviso FROM aluno a, avisos_lidos c WHERE a.id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND a.codigo = '" + aplicacao!!.aluno!!.codigo + "' AND c.tipousu_dest = 1 AND c.id_usu_dest = a.id_aluno)")

            val json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put("pesquisa", pesquisa)

            UtilClass.conectaHttp(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTAMULTIPLA,
                json,
                null,
                aplicacao!!.configuracoes!!,
                object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        aplicacao!!.finalizarProgress()
                        try {
                            if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                                var qtde = 0
                                var msgEspecifica = response!!.getJSONArray("consultas").getJSONObject(0).getJSONArray("linhas")
                                if (msgEspecifica!!.length() > 0) {
                                    qtde += msgEspecifica.getJSONArray(0).getInt(0)
                                }
                                var msgGeral = response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas")
                                if (msgGeral!!.length() > 0) {
                                    qtde += msgGeral.getJSONArray(0).getInt(0)
                                }
                                tvAvisos!!.text = qtde.toString()
                                if (qtde == 0) {
                                    ivAvisos!!.setImageResource(R.drawable.aviso1)
                                    tvAvisos!!.visibility = RelativeLayout.INVISIBLE
                                } else {
                                    ivAvisos!!.setImageResource(R.drawable.aviso2)
                                    tvAvisos!!.visibility = RelativeLayout.VISIBLE
                                }
                            } else {
                                UtilClass.trataErro(
                                    UtilClass.ERRO_SERVIDOR_TAG,
                                    response!!.getJSONObject("status").getString("mensagem")
                                )
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    carregarAvisosServidor(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act,
                                        R.string.aviso,
                                        R.string.servidor_inacessivel,
                                        R.string.ok,
                                        null)
                                    ivAvisos!!.setImageResource(R.drawable.aviso1)
                                    tvAvisos!!.visibility = RelativeLayout.INVISIBLE
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarAvisosServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act,
                                    R.string.aviso,
                                    R.string.servidor_inacessivel,
                                    R.string.ok,
                                    null)
                                ivAvisos!!.setImageResource(R.drawable.aviso1)
                                tvAvisos!!.visibility = RelativeLayout.INVISIBLE
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarAvisosServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act,
                                R.string.aviso,
                                R.string.servidor_inacessivel,
                                R.string.ok,
                                null)
                            ivAvisos!!.setImageResource(R.drawable.aviso1)
                            tvAvisos!!.visibility = RelativeLayout.INVISIBLE
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarAvisosServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act,
                    R.string.aviso,
                    R.string.servidor_inacessivel,
                    R.string.ok,
                    null)
                ivAvisos!!.setImageResource(R.drawable.aviso1)
                tvAvisos!!.visibility = RelativeLayout.INVISIBLE
            }
        }
    }

    private fun carregarProjetosAtribuidosServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            val pesquisa = JSONArray()
            pesquisa.put("SELECT id_projeto, id_aluno, codigo_aula, codigo_aluno, status, sequencia, materia, tela, pasta, id_autor, data_final_atribuicao, enviar_email, nota_limite, enviar_email_fim, enviar_aviso, duracao, inicio FROM aula_atribuida WHERE id_aluno = " + aplicacao!!.aluno!!.id_aluno)
            pesquisa.put("SELECT d.id_projeto, d.id_instituicao, d.id_autor, d.codigo, d.nome, d.autor, d.data, d.grava_saida, d.n_telas, d.omite_avaliacao, d.oculta_avaliacao, d.grava_teste_detalhe, d.porcentagem_acertos, d.omite_lista_alunos, d.voto_entrada, d.voto_tempoconfirmacao, d.voto_cor_borda, d.voto_avancar, d.voto_senhainicio, d.voto_alterar, d.assunto, d.materia, d.alvo, d.email, d.grava_nota, d.grava_notaurl, d.exportar_alunos, d.versao, d.voto_tituloentrada, d.voto_tela_origem, d.zona, d.nomezona, d.solicitaident, d.clipcontrols, d.somduplo, d.versao_class, d.telas_sorteio, d.navega_speech, d.teste_vest, d.senha_gerencia, d.gestos, d.grava_dissert_ext, d.publico, d.redim_telas, d.permite_baixar, d.permite_baixar_proj, d.codigo_original, d.orientacao, d.tentativas, d.url_vcnet_geo, d.relatorio_pb, d.senha, d.nivel_seguranca, d.path_arqproj, d.data_apagamento, d.dataatualiza, d.automatricula, d.pago, d.senha_auto, d.data_apagamento_cfg, d.redimensionar_textos, d.tipoprojeto, d.id_tipoprova, d.barra_navegacao FROM descricao AS d INNER JOIN aula_atribuida AS aa ON d.id_projeto = aa.id_projeto AND aa.id_aluno = " + aplicacao!!.aluno!!.id_aluno + " ORDER BY d.codigo")
            pesquisa.put("SELECT COUNT(*) AS n FROM aluno a, avisos b LEFT JOIN avisos_lidos c ON b.id_aviso = c.id_aviso AND b.tipousu_dest = c.tipousu_dest AND b.id_usu_dest = c.id_usu_dest WHERE a.id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND a.codigo = '" + aplicacao!!.aluno!!.codigo + "' AND b.tipousu_dest = 1 AND b.id_instituicao_dest = " + aplicacao!!.instituicao!!.id_instituicao + " AND b.id_usu_dest = a.id_aluno AND c.dataleitura IS NULL")
            pesquisa.put("SELECT COUNT(*) AS n FROM avisos b WHERE b.tipousu_dest = 1 AND b.id_instituicao_dest = " + aplicacao!!.instituicao!!.id_instituicao + " AND b.id_usu_dest = 0 AND b.id_usu_ori <> (SELECT id_aluno FROM aluno WHERE id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND codigo = '" + aplicacao!!.aluno!!.codigo + "') AND b.id_aviso NOT IN (SELECT c.id_aviso FROM aluno a, avisos_lidos c WHERE a.id_instituicao = " + aplicacao!!.instituicao!!.id_instituicao + " AND a.codigo = '" + aplicacao!!.aluno!!.codigo + "' AND c.tipousu_dest = 1 AND c.id_usu_dest = a.id_aluno)")

            val json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put("pesquisa", pesquisa)

            UtilClass.conectaHttp(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTAMULTIPLA,
                json,
                null,
                aplicacao!!.configuracoes!!,
                object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        aplicacao!!.finalizarProgress()
                        try {
                            if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                                var projetosAtribuidos =
                                    response!!.getJSONArray("consultas").getJSONObject(0).getJSONArray("linhas")
                                var projetos =
                                    response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas")
                                if (projetosAtribuidos != null && projetos != null && projetosAtribuidos.length() > 0 && projetos.length() > 0 && projetosAtribuidos.length() == projetos.length()) {
                                    var aula_atribuida: AulaAtribuida
                                    var descricao: Descricao
                                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                                    aplicacao!!.atribuidosAulaAtrbuida = ArrayList()
                                    aplicacao!!.atribuidosDescricao = ArrayList()
                                    var i = 0
                                    var j: Int
                                    while (i < projetos.length()) {
                                        descricao = Descricao()
                                        descricao.id_projeto = projetos.getJSONArray(i).getLong(0)
                                        descricao.id_instituicao = projetos.getJSONArray(i).getLong(1)
                                        descricao.id_autor = projetos.getJSONArray(i).getLong(2)
                                        descricao.codigo = projetos.getJSONArray(i).getString(3)
                                        descricao.nome = projetos.getJSONArray(i).getString(4)
                                        descricao.autor = projetos.getJSONArray(i).getString(5)
                                        if (projetos.getJSONArray(i).getString(6).trim().isNotEmpty()) {
                                            descricao.data = Date(sdf.parse(projetos.getJSONArray(i).getString(6)).time)
                                        }
                                        descricao.grava_saida = projetos.getJSONArray(i).getInt(7)
                                        descricao.n_telas = projetos.getJSONArray(i).getLong(8)
                                        descricao.omite_avaliacao = projetos.getJSONArray(i).getLong(9)
                                        descricao.oculta_avaliacao = projetos.getJSONArray(i).getLong(10)
                                        descricao.grava_teste_detalhe = projetos.getJSONArray(i).getInt(11)
                                        descricao.porcentagem_acertos = projetos.getJSONArray(i).getLong(12)
                                        descricao.omite_lista_alunos = projetos.getJSONArray(i).getInt(13)
                                        descricao.voto_entrada = projetos.getJSONArray(i).getInt(14)
                                        descricao.voto_tempoconfirmacao = projetos.getJSONArray(i).getDouble(15)
                                        descricao.voto_cor_borda = projetos.getJSONArray(i).getLong(16)
                                        descricao.voto_avancar = projetos.getJSONArray(i).getInt(17)
                                        descricao.voto_senhainicio = projetos.getJSONArray(i).getInt(18)
                                        descricao.voto_alterar = projetos.getJSONArray(i).getInt(19)
                                        descricao.assunto = projetos.getJSONArray(i).getString(20)
                                        descricao.materia = projetos.getJSONArray(i).getString(21)
                                        descricao.alvo = projetos.getJSONArray(i).getString(22)
                                        descricao.email = projetos.getJSONArray(i).getString(23)
                                        descricao.grava_nota = projetos.getJSONArray(i).getString(24)
                                        descricao.grava_notaurl = projetos.getJSONArray(i).getString(25)
                                        descricao.exportar_alunos = projetos.getJSONArray(i).getString(26)
                                        descricao.versao = projetos.getJSONArray(i).getString(27)
                                        descricao.voto_tituloentrada = projetos.getJSONArray(i).getString(28)
                                        descricao.voto_tela_origem = projetos.getJSONArray(i).getString(29)
                                        descricao.zona = projetos.getJSONArray(i).getString(30)
                                        descricao.nomezona = projetos.getJSONArray(i).getString(31)
                                        descricao.solicitaident = projetos.getJSONArray(i).getInt(32)
                                        descricao.clipcontrols = projetos.getJSONArray(i).getInt(33)
                                        descricao.somduplo = projetos.getJSONArray(i).getInt(34)
                                        descricao.versao_class = projetos.getJSONArray(i).getString(35)
                                        descricao.telas_sorteio = projetos.getJSONArray(i).getInt(36)
                                        descricao.navega_speech = projetos.getJSONArray(i).getInt(37)
                                        descricao.teste_vest = projetos.getJSONArray(i).getInt(38)
                                        descricao.senha_gerencia = projetos.getJSONArray(i).getString(39)
                                        descricao.gestos = projetos.getJSONArray(i).getInt(40)
                                        descricao.grava_dissert_ext = projetos.getJSONArray(i).getInt(41)
                                        descricao.publico = projetos.getJSONArray(i).getInt(42)
                                        descricao.redim_telas = projetos.getJSONArray(i).getInt(43)
                                        descricao.permite_baixar = projetos.getJSONArray(i).getInt(44)
                                        descricao.permite_baixar_proj = projetos.getJSONArray(i).getInt(45)
                                        descricao.codigo_original = projetos.getJSONArray(i).getString(46)
                                        descricao.orientacao = projetos.getJSONArray(i).getInt(47)
                                        descricao.tentativas = projetos.getJSONArray(i).getInt(48)
                                        descricao.url_vcnet_geo = projetos.getJSONArray(i).getString(49)
                                        descricao.relatorio_pb = projetos.getJSONArray(i).getInt(50)
                                        descricao.senha = projetos.getJSONArray(i).getString(51)
                                        descricao.nivel_seguranca = projetos.getJSONArray(i).getInt(52)
                                        descricao.path_arqproj = projetos.getJSONArray(i).getString(53)
                                        if (projetos.getJSONArray(i).getString(54).trim().isNotEmpty()) {
                                            descricao.data_apagamento =
                                                Date(sdf.parse(projetos.getJSONArray(i).getString(54)).time)
                                        }
                                        if (projetos.getJSONArray(i).getString(55).trim().isNotEmpty()) {
                                            descricao.dataatualiza =
                                                Date(sdf.parse(projetos.getJSONArray(i).getString(55)).time)
                                        }
                                        descricao.automatricula = projetos.getJSONArray(i).getInt(56)
                                        descricao.pago = projetos.getJSONArray(i).getInt(57)
                                        descricao.senha_auto = projetos.getJSONArray(i).getString(58)
                                        if (projetos.getJSONArray(i).getString(59).trim().isNotEmpty()) {
                                            descricao.data_apagamento_cfg =
                                                Date(sdf.parse(projetos.getJSONArray(i).getString(59)).time)
                                        }
                                        descricao.redimensionar_textos = projetos.getJSONArray(i).getInt(60)
                                        descricao.tipoprojeto = projetos.getJSONArray(i).getInt(61)
                                        descricao.id_tipoprova = projetos.getJSONArray(i).getLong(62)
                                        descricao.barra_navegacao = projetos.getJSONArray(i).getInt(63)

                                        j = 0
                                        while (j < projetosAtribuidos.length()) {
                                            if (descricao.id_projeto == projetosAtribuidos.getJSONArray(j).getLong(0)) {
                                                aula_atribuida = AulaAtribuida()
                                                aula_atribuida.id_projeto =
                                                    projetosAtribuidos.getJSONArray(j).getLong(0)
                                                aula_atribuida.id_aluno = projetosAtribuidos.getJSONArray(j).getLong(1)
                                                aula_atribuida.codigo_aula =
                                                    projetosAtribuidos.getJSONArray(j).getString(2)
                                                aula_atribuida.codigo_aluno =
                                                    projetosAtribuidos.getJSONArray(j).getString(3)
                                                aula_atribuida.status = projetosAtribuidos.getJSONArray(j).getInt(4)
                                                aula_atribuida.sequencia = projetosAtribuidos.getJSONArray(j).getInt(5)
                                                aula_atribuida.materia = projetosAtribuidos.getJSONArray(j).getString(6)
                                                aula_atribuida.tela = projetosAtribuidos.getJSONArray(j).getString(7)
                                                aula_atribuida.pasta = projetosAtribuidos.getJSONArray(j).getString(8)
                                                aula_atribuida.id_autor = projetosAtribuidos.getJSONArray(j).getLong(9)
                                                if (projetosAtribuidos.getJSONArray(j).getString(10).trim().isNotEmpty()) {
                                                    aula_atribuida.data_final_atribuicao =
                                                        Date(sdf.parse(projetosAtribuidos.getJSONArray(j).getString(10)).time)
                                                }
                                                aula_atribuida.enviar_email =
                                                    projetosAtribuidos.getJSONArray(j).getInt(11)
                                                aula_atribuida.nota_limite =
                                                    projetosAtribuidos.getJSONArray(j).getDouble(12)
                                                aula_atribuida.enviar_email_fim =
                                                    projetosAtribuidos.getJSONArray(j).getInt(13)
                                                aula_atribuida.enviar_aviso =
                                                    projetosAtribuidos.getJSONArray(j).getInt(14)
                                                aula_atribuida.duracao = projetosAtribuidos.getJSONArray(j).getInt(15)
                                                if (projetosAtribuidos.getJSONArray(j).getString(16).trim().isNotEmpty()) {
                                                    aula_atribuida.inicio =
                                                        Date(sdf.parse(projetosAtribuidos.getJSONArray(j).getString(16)).time)
                                                }
                                                aplicacao!!.atribuidosAulaAtrbuida!!.add(aula_atribuida)
                                                aplicacao!!.atribuidosDescricao!!.add(descricao)

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    projetosAtribuidos.remove(j)
                                                    projetos.remove(i)
                                                    j--
                                                    i--
                                                }

                                                j = projetosAtribuidos.length()
                                            }
                                            j++
                                        }
                                        i++
                                    }

                                    montarLista()
                                }
                                var qtde = 0
                                var msgEspecifica = response!!.getJSONArray("consultas").getJSONObject(2).getJSONArray("linhas")
                                if (msgEspecifica!!.length() > 0) {
                                    qtde += msgEspecifica.getJSONArray(0).getInt(0)
                                }
                                var msgGeral = response!!.getJSONArray("consultas").getJSONObject(3).getJSONArray("linhas")
                                if (msgGeral!!.length() > 0) {
                                    qtde += msgGeral.getJSONArray(0).getInt(0)
                                }
                                tvAvisos!!.text = qtde.toString()
                                if (qtde == 0) {
                                    ivAvisos!!.setImageResource(R.drawable.aviso1)
                                    tvAvisos!!.visibility = RelativeLayout.INVISIBLE
                                } else {
                                    ivAvisos!!.setImageResource(R.drawable.aviso2)
                                    tvAvisos!!.visibility = RelativeLayout.VISIBLE
                                }
                            } else {
                                UtilClass.trataErro(
                                    UtilClass.ERRO_SERVIDOR_TAG,
                                    response!!.getJSONObject("status").getString("mensagem")
                                )
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    carregarProjetosAtribuidosServidor(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act,
                                        R.string.aviso,
                                        R.string.servidor_inacessivel,
                                        R.string.ok,
                                        DialogInterface.OnClickListener { _, _ ->
                                            act.finish()
                                        })
                                    aplicacao!!.atribuidosAulaAtrbuida = null
                                    aplicacao!!.atribuidosDescricao = null
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarProjetosAtribuidosServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act,
                                    R.string.aviso,
                                    R.string.servidor_inacessivel,
                                    R.string.ok,
                                    DialogInterface.OnClickListener { _, _ ->
                                        act.finish()
                                    })
                                aplicacao!!.atribuidosAulaAtrbuida = null
                                aplicacao!!.atribuidosDescricao = null
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarProjetosAtribuidosServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act,
                                R.string.aviso,
                                R.string.servidor_inacessivel,
                                R.string.ok,
                                DialogInterface.OnClickListener { _, _ ->
                                    act.finish()
                                })
                            aplicacao!!.atribuidosAulaAtrbuida = null
                            aplicacao!!.atribuidosDescricao = null
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarProjetosAtribuidosServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act,
                    R.string.aviso,
                    R.string.servidor_inacessivel,
                    R.string.ok,
                    DialogInterface.OnClickListener { _, _ ->
                        act.finish()
                    })
                aplicacao!!.atribuidosAulaAtrbuida = null
                aplicacao!!.atribuidosDescricao = null
            }
        }
    }

    private fun iniciarActivity(savedInstanceState: Bundle?) {
        aplicacao = application as Aplicacao
        aplicacao!!.act = act
        UtilClass.esconderBarraTitulo(act)
        aplicacao!!.atualizarMetricas()

        var ivExpandirContrair = findViewById<ImageView>(R.id.ivExpandirContrair)
        ivOpcoes = findViewById(R.id.ivOpcoes)
        rlCabecalho = findViewById(R.id.rlCabecalho)
        ivFoto = findViewById(R.id.ivFoto)
        tvProjetosAtribuidosPara = findViewById(R.id.tvProjetosAtribuidosPara)
        ivAvisos = findViewById(R.id.ivAvisos)
        tvAvisos = findViewById(R.id.tvAvisos)
        lvProjetos = findViewById(R.id.lvProjetos)
        var bAutomatricula = findViewById<Button>(R.id.bAutomatricula)
        llMenu = findViewById(R.id.llMenu)
        var llAlterarCadastro = findViewById<LinearLayout>(R.id.llAlterarCadastro)
        var llConsultarNotas = findViewById<LinearLayout>(R.id.llConsultarNotas)

        rlCabecalho!!.setOnClickListener {
            if (abriuOpcoes) {
                abriuOpcoes = false
                alterarOpcoes()
            }
        }
        ivExpandirContrair.setOnClickListener {
            if (abriuOpcoes) {
                abriuOpcoes = false
                alterarOpcoes()
            }
            abriuExpandir = !abriuExpandir
            alterarExpandirContrair()
        }
        ivOpcoes!!.setOnClickListener {
            abriuOpcoes = !abriuOpcoes
            alterarOpcoes()
        }
        ivAvisos!!.setOnClickListener {
            if (abriuOpcoes) {
                abriuOpcoes = false
                alterarOpcoes()
            }
        }
        lvProjetos!!.setOnTouchListener { _, _ ->
            if (abriuOpcoes) {
                abriuOpcoes = false
                alterarOpcoes()
            }
            false
        }
        bAutomatricula.setOnClickListener {
            if (abriuOpcoes) {
                abriuOpcoes = false
                alterarOpcoes()
            }
        }
        llAlterarCadastro.setOnClickListener {
            abriuOpcoes = !abriuOpcoes
            alterarOpcoes()
            abrirAlterarCadastro()
        }
        llConsultarNotas.setOnClickListener {
            abriuOpcoes = !abriuOpcoes
            alterarOpcoes()
            abrirCarregarNotas()
        }

        tvProjetosAtribuidosPara!!.text =
            act.getString(R.string.projetos_atribuidos_para) + " " + aplicacao!!.aluno!!.nome!!

        if (savedInstanceState == null) {
            carregarProjetosAtribuidosServidor(1)
        } else {
            abriuExpandir = savedInstanceState.getBoolean("abriuExpandir", true)
            montarLista()
            carregarAvisosServidor(1);
        }
        if (aplicacao!!.aluno!!.foto!!.isNotEmpty()) {
            UtilClass.carregarImagemImageView(act, aplicacao!!.servidores!!.ser_link + "arquivos/" + aplicacao!!.aluno!!.foto!!, R.drawable.aluno, false, ivFoto!!, aplicacao!!.configuracoes!!)
        }
        alterarExpandirContrair()
        alterarOpcoes()
    }

    private fun montarLista() {
        var adapter = ActivityProjetosAtribuidosAdapter(act!!)
        lvProjetos!!.adapter = adapter
    }

    fun selecionouProjeto(descricao: Descricao) {
        aplicacao!!.iniciarProjeto()
        aplicacao!!.descricao = descricao
        var intent = Intent(act, ActivityApresentacao::class.java).apply {
            putExtra("inicio", true)
        }
        startActivityForResult(intent, UtilClass.RETORNO_APRESENTACAO)
    }
}