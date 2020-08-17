package br.classinformatica.vcandroid.classnetk.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.entidades.*
import br.classinformatica.vcandroid.classnetk.objetos.*
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList
import java.net.*


class ActivityApresentacao : AppCompatActivity() {

    private var act: ActivityApresentacao = this
    private var altImgFundo: Int = 0
    private var aplicacao: Aplicacao? = null
    private var inicio = true
    private var larImgFundo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_apresentacao)

        inicio = intent.getBooleanExtra("inicio", false)
        //intent.putExtra("inicio", false)

        iniciarActivity(savedInstanceState)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_MOVE) {
            aplicacao!!.moveuApresentacao(ev)
            return true
        } else if (ev!!.action == MotionEvent.ACTION_UP) {
            aplicacao!!.parouApresentacao(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        aplicacao!!.act = act
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        sairApresentacao()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (aplicacao!!.classTela != null) {
            outState.putInt("altImgFundo", aplicacao!!.classTela!!.altImgFundo)
            outState.putInt("larImgFundo", aplicacao!!.classTela!!.larImgFundo)
        }
        super.onSaveInstanceState(outState)
    }
//métodos -------------------------------------------------------------------------------------------------

    fun atualizarAtribuicaoTela(nTentativa: Int, idTela: Long, sairProjeto: Boolean) {
        if ((aplicacao!!.descricao!!.grava_teste_detalhe == 1 || aplicacao!!.descricao!!.teste_vest == 1) && (aplicacao!!.aulaAtribuida!!.status != 2 || (aplicacao!!.aulaAtribuida!!.status == 2 && aplicacao!!.aulaFeita!!.tentativas < aplicacao!!.descricao!!.tentativas))) {
            aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
            try {
                var sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

                var cmdparam = JSONArray()

                //primeiro comando
                var aux = JSONArray()
                aux.put(UtilClass.toHex("delete"))
                aux.put(UtilClass.toHex("aula_incompleta"))
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
                cmdparam.put(aux)

                //segundo comando
                var aux2: JSONArray
                for (i in 0 until aplicacao!!.aulaIncompleta.size) {
                    aux = JSONArray()
                    aux.put(UtilClass.toHex("insert"))
                    aux.put(UtilClass.toHex("aula_incompleta"))

                    aux2 = JSONArray()
                    aux2.put(UtilClass.toHex("id_projeto"))
                    aux2.put(UtilClass.toHex("id_aluno"))
                    aux2.put(UtilClass.toHex("codigo_aula"))
                    aux2.put(UtilClass.toHex("codigo_aluno"))
                    aux2.put(UtilClass.toHex("sequencia"))
                    aux2.put(UtilClass.toHex("tela"))
                    aux2.put(UtilClass.toHex("id_tela"))
                    aux.put(aux2)

                    aux2 = JSONArray()
                    aux2.put(UtilClass.toHex("long"))
                    aux2.put(UtilClass.toHex("long"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("long"))
                    aux.put(aux2)

                    aux2 = JSONArray()
                    aux2.put(aplicacao!!.aulaIncompleta[i].id_projeto)
                    aux2.put(aplicacao!!.aulaIncompleta[i].id_aluno)
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaIncompleta[i].codigo_aula))
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaIncompleta[i].codigo_aluno))
                    aux2.put(aplicacao!!.aulaIncompleta[i].sequencia)
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaIncompleta[i].tela))
                    aux2.put(aplicacao!!.aulaIncompleta[i].id_tela)
                    aux.put(aux2)

                    aux.put("")
                    cmdparam.put(aux)
                }

                //terceiro comando
                aux = JSONArray()
                aux.put(UtilClass.toHex("delete"))
                aux.put(UtilClass.toHex("aula_testefeito"))
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
                cmdparam.put(aux)

                //quarto comando
                for (i in 0 until aplicacao!!.aulaIncompleta.size) {
                    aux = JSONArray()
                    aux.put(UtilClass.toHex("insert"))
                    aux.put(UtilClass.toHex("aula_testefeito"))

                    aux2 = JSONArray()
                    aux2.put(UtilClass.toHex("id_projeto"))
                    aux2.put(UtilClass.toHex("id_aluno"))
                    aux2.put(UtilClass.toHex("codigo_aluno"))
                    aux2.put(UtilClass.toHex("codigo_aula"))
                    aux2.put(UtilClass.toHex("tela"))
                    aux2.put(UtilClass.toHex("tipoobj"))
                    aux2.put(UtilClass.toHex("indice"))
                    aux2.put(UtilClass.toHex("indice_par"))
                    aux2.put(UtilClass.toHex("indice_obj"))
                    aux2.put(UtilClass.toHex("peso"))
                    aux2.put(UtilClass.toHex("certoerrado"))
                    aux2.put(UtilClass.toHex("indice_soltar"))
                    aux2.put(UtilClass.toHex("pendente"))
                    aux2.put(UtilClass.toHex("resposta"))
                    aux2.put(UtilClass.toHex("textopreenchido"))
                    aux2.put(UtilClass.toHex("alternativa_selecionada"))
                    aux2.put(UtilClass.toHex("preenchimento"))
                    aux2.put(UtilClass.toHex("id_tela"))
                    aux.put(aux2)

                    aux2 = JSONArray()
                    aux2.put(UtilClass.toHex("long"))
                    aux2.put(UtilClass.toHex("long"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("long"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("double"))
                    aux2.put(UtilClass.toHex("double"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("int"))
                    aux2.put(UtilClass.toHex("string"))
                    aux2.put(UtilClass.toHex("long"))
                    aux.put(aux2)

                    aux2 = JSONArray()
                    aux2.put(aplicacao!!.aulaTestefeito[i].id_projeto)
                    aux2.put(aplicacao!!.aulaTestefeito[i].id_aluno)
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].codigo_aluno))
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].codigo_aula))
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].tela))
                    aux2.put(aplicacao!!.aulaTestefeito[i].tipoobj)
                    aux2.put(aplicacao!!.aulaTestefeito[i].indice)
                    aux2.put(aplicacao!!.aulaTestefeito[i].indice_par)
                    aux2.put(aplicacao!!.aulaTestefeito[i].indice_obj)
                    aux2.put(aplicacao!!.aulaTestefeito[i].peso)
                    aux2.put(aplicacao!!.aulaTestefeito[i].certoerrado)
                    aux2.put(aplicacao!!.aulaTestefeito[i].indice_soltar)
                    aux2.put(aplicacao!!.aulaTestefeito[i].pendente)
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].resposta))
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].textopreenchido))
                    aux2.put(aplicacao!!.aulaTestefeito[i].alternativa_selecionada)
                    aux2.put(UtilClass.toHex(aplicacao!!.aulaTestefeito[i].preenchimento))
                    aux2.put(aplicacao!!.aulaTestefeito[i].id_tela)
                    aux.put(aux2)

                    aux.put("")
                    cmdparam.put(aux)
                }

                //quinto comando
                aux = JSONArray()
                aux.put(UtilClass.toHex("delete"))
                aux.put(UtilClass.toHex("aula_feita"))
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(JSONArray())
                aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
                cmdparam.put(aux)

                //sexto comando
                aux = JSONArray()
                aux.put(UtilClass.toHex("insert"))
                aux.put(UtilClass.toHex("aula_feita"))

                aux2 = JSONArray()
                aux2.put(UtilClass.toHex("id_projeto"))
                aux2.put(UtilClass.toHex("id_aluno"))
                aux2.put(UtilClass.toHex("codigo_aluno"))
                aux2.put(UtilClass.toHex("codigo_aula"))
                aux2.put(UtilClass.toHex("data"))
                aux2.put(UtilClass.toHex("nota"))
                aux2.put(UtilClass.toHex("tentativas"))
                aux2.put(UtilClass.toHex("erros_anterior"))
                aux2.put(UtilClass.toHex("acertos_anterior"))
                aux2.put(UtilClass.toHex("total_minutos"))
                aux2.put(UtilClass.toHex("ultima_tela_feita"))
                aux2.put(UtilClass.toHex("nome"))
                aux2.put(UtilClass.toHex("autor"))
                aux2.put(UtilClass.toHex("assunto"))
                aux2.put(UtilClass.toHex("materia"))
                aux2.put(UtilClass.toHex("alvo"))
                aux2.put(UtilClass.toHex("pendente"))
                aux2.put(UtilClass.toHex("status"))
                aux2.put(UtilClass.toHex("data_inicio"))
                aux2.put(UtilClass.toHex("cod_certificacao"))
                aux2.put(UtilClass.toHex("autor_atribuicao"))
                aux2.put(UtilClass.toHex("origem"))
                aux2.put(UtilClass.toHex("tempo_tmp"))
                aux2.put(UtilClass.toHex("id_ultima_tela_feita"))
                aux.put(aux2)

                aux2 = JSONArray()
                aux2.put(UtilClass.toHex("long"))
                aux2.put(UtilClass.toHex("long"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("date"))
                aux2.put(UtilClass.toHex("double"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("long"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("date"))
                aux2.put(UtilClass.toHex("int"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("string"))
                aux2.put(UtilClass.toHex("double"))
                aux2.put(UtilClass.toHex("long"))
                aux.put(aux2)

                aux2 = JSONArray()
                aux2.put(aplicacao!!.aulaFeita!!.id_projeto)
                aux2.put(aplicacao!!.aulaFeita!!.id_aluno)
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.codigo_aluno))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.codigo_aula))
                aux2.put(UtilClass.toHex(sdf.format(aplicacao!!.aulaFeita!!.data)))
                aux2.put(aplicacao!!.aulaFeita!!.nota)
                aux2.put(aplicacao!!.aulaFeita!!.tentativas)
                aux2.put(aplicacao!!.aulaFeita!!.erros_anterior)
                aux2.put(aplicacao!!.aulaFeita!!.acertos_anterior)
                aux2.put(aplicacao!!.aulaFeita!!.total_minutos)
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.ultima_tela_feita))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.nome))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.autor))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.assunto))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.materia))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.alvo))
                aux2.put(aplicacao!!.aulaFeita!!.pendente)
                aux2.put(aplicacao!!.aulaFeita!!.status)
                aux2.put(UtilClass.toHex(sdf.format(aplicacao!!.aulaFeita!!.data_inicio)))
                aux2.put(aplicacao!!.aulaFeita!!.cod_certificacao)
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.autor_atribuicao))
                aux2.put(UtilClass.toHex(aplicacao!!.aulaFeita!!.origem))
                aux2.put(aplicacao!!.aulaFeita!!.tempo_tmp)
                aux2.put(aplicacao!!.aulaFeita!!.id_ultima_tela_feita)
                aux.put(aux2)

                aux.put("")
                cmdparam.put(aux)


                //sétimo comando
                aux = JSONArray()
                aux.put(UtilClass.toHex("update"))
                aux.put(UtilClass.toHex("aula_atribuida"))

                aux2 = JSONArray()
                aux2.put(UtilClass.toHex("status"))
                aux.put(aux2)

                aux2 = JSONArray()
                aux2.put(UtilClass.toHex("int"))
                aux.put(aux2)

                aux2 = JSONArray()
                aux2.put(aplicacao!!.aulaAtribuida!!.status)
                aux.put(aux2)

                aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
                cmdparam.put(aux)



                val json = JSONObject()
                json.put("id_instituicao", aplicacao!!.instituicao!!.id_instituicao)
                json.put("usu", aplicacao!!.aluno!!.codigo)
                json.put("pwd", aplicacao!!.aluno!!.senha)
                json.put("tipousu", 2)
                json.put("comandos", JSONArray())
                json.put("cmdparam", cmdparam)
                json.put("formato", 1)
                json.put("retorna_id", 1)

                UtilClass.conectaHttp(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLEXECUTA, json, null, aplicacao!!.configuracoes!!, object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        aplicacao!!.finalizarProgress()
                        try {
                            if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                                if (sairProjeto) {
                                    sairApresentacao()
                                } else {
                                    carregarTela(idTela)
                                }
                            } else {
                                UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("mensagem"))
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    atualizarAtribuicaoTela(nTentativa + 1, idTela, sairProjeto)
                                } else {
                                    UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                atualizarAtribuicaoTela(nTentativa + 1, idTela, sairProjeto)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            atualizarAtribuicaoTela(nTentativa + 1, idTela, sairProjeto)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                })
            } catch (e: Exception) {
                UtilClass.trataErro(e)
                aplicacao!!.finalizarProgress()
                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                    atualizarAtribuicaoTela(nTentativa + 1, idTela, sairProjeto)
                } else {
                    UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                }
            }
        } else {
            if (sairProjeto) {
                sairApresentacao()
            } else {
                carregarTela(idTela)
            }
        }
    }

    private fun carregarProjetoServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        aplicacao!!.iniciarTela()
        try {
            var pesquisa = JSONArray()
            pesquisa.put("SELECT at.id_projeto, at.codigo_tela, at.sequencia, at.codigo_aula, t.id_tela FROM aula_tela AS at INNER JOIN tela AS t ON t.codigo = at.codigo_tela AND t.id_projeto = at.id_projeto WHERE at.id_projeto = " + aplicacao!!.descricao!!.id_projeto + " ORDER BY at.sequencia")
            pesquisa.put("SELECT id_projeto, id_aluno, codigo_aula, codigo_aluno, status, sequencia, materia, tela, pasta, id_autor, data_final_atribuicao, enviar_email, nota_limite, enviar_email_fim, enviar_aviso, duracao, inicio FROM aula_atribuida WHERE id_projeto = " + aplicacao!!.descricao!!.id_projeto)
            pesquisa.put("SELECT id_projeto, id_aluno, codigo_aluno, codigo_aula, data, nota, tentativas, erros_anterior, acertos_anterior, total_minutos, ultima_tela_feita, nome, autor, assunto, materia, alvo, pendente, status, data_inicio, cod_certificacao, autor_atribuicao, origem, tempo_tmp, id_ultima_tela_feita FROM aula_feita WHERE id_projeto = " + aplicacao!!.descricao!!.id_projeto)
            pesquisa.put("SELECT id_projeto, id_aluno, codigo_aluno, codigo_aula, tela, tipoobj, indice, indice_par, indice_obj, peso, certoerrado, indice_soltar, pendente, resposta, textopreenchido, alternativa_selecionada, preenchimento, unicode, id_tela FROM aula_testefeito WHERE id_projeto = " + aplicacao!!.descricao!!.id_projeto)
            pesquisa.put("SELECT id_projeto, id_aluno, codigo_aula, codigo_aluno, sequencia, tela, id_tela FROM aula_incompleta WHERE id_projeto = " + aplicacao!!.descricao!!.id_projeto)

            var json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put("pesquisa", pesquisa)

            UtilClass.conectaHttp(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTAMULTIPLA, json, null, aplicacao!!.configuracoes!!, object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    aplicacao!!.finalizarProgress()
                    try {
                        if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                            var idTela = 0L
                            val sdf = SimpleDateFormat("yyyy-MM-dd")

                            var jAulaTela = response.getJSONArray("consultas").getJSONObject(0).getJSONArray("linhas")
                            var jAulaAtribuida =
                                response.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas")
                            var jAulaFeita =
                                response.getJSONArray("consultas").getJSONObject(2).getJSONArray("linhas")
                            var jAulaTestefeito =
                                response.getJSONArray("consultas").getJSONObject(3).getJSONArray("linhas")
                            var jAulaIncompleta =
                                response.getJSONArray("consultas").getJSONObject(4).getJSONArray("linhas")

                            if (jAulaTela.length() > 0) {
                                var aulaTela: AulaTela
                                for (i in 0 until jAulaTela.length()) {
                                    aulaTela = AulaTela()
                                    aulaTela.id_projeto = jAulaTela.getJSONArray(i).getLong(0)
                                    aulaTela.codigo_tela = jAulaTela.getJSONArray(i).getString(1)
                                    aulaTela.sequencia = jAulaTela.getJSONArray(i).getInt(2)
                                    aulaTela.codigo_aula = jAulaTela.getJSONArray(i).getString(3)
                                    aulaTela.id_tela = jAulaTela.getJSONArray(i).getLong(4)
                                    if (aulaTela.sequencia == 1) {
                                        idTela = aulaTela.id_tela
                                    }
                                    aplicacao!!.aulaTela.add(aulaTela)
                                }
                            }

                            if (idTela == 0L) {
                                UtilClass.msgOk(act, R.string.aviso, R.string.erro_carregar_tela, R.string.ok, DialogInterface.OnClickListener { _, _ ->
                                    act.finish()
                                })
                            } else {

                                if (jAulaAtribuida.length() > 0) {
                                    var aulaAtribuida = AulaAtribuida()
                                    aulaAtribuida.id_projeto = jAulaAtribuida.getJSONArray(0).getLong(0)
                                    aulaAtribuida.id_aluno = jAulaAtribuida.getJSONArray(0).getLong(1)
                                    aulaAtribuida.codigo_aula = jAulaAtribuida.getJSONArray(0).getString(2)
                                    aulaAtribuida.codigo_aluno = jAulaAtribuida.getJSONArray(0).getString(3)
                                    aulaAtribuida.status = jAulaAtribuida.getJSONArray(0).getInt(4)
                                    aulaAtribuida.sequencia = jAulaAtribuida.getJSONArray(0).getInt(5)
                                    aulaAtribuida.materia = jAulaAtribuida.getJSONArray(0).getString(6)
                                    aulaAtribuida.tela = jAulaAtribuida.getJSONArray(0).getString(7)
                                    aulaAtribuida.pasta = jAulaAtribuida.getJSONArray(0).getString(8)
                                    aulaAtribuida.id_autor = jAulaAtribuida.getJSONArray(0).getLong(9)
                                    if (jAulaAtribuida.getJSONArray(0).getString(10).trim().isNotEmpty()) {
                                        aulaAtribuida.data_final_atribuicao =
                                            Date(sdf.parse(jAulaAtribuida.getJSONArray(0).getString(10).trim()).time)
                                    }
                                    aulaAtribuida.enviar_email = jAulaAtribuida.getJSONArray(0).getInt(11)
                                    aulaAtribuida.nota_limite = jAulaAtribuida.getJSONArray(0).getDouble(12)
                                    aulaAtribuida.enviar_email_fim = jAulaAtribuida.getJSONArray(0).getInt(13)
                                    aulaAtribuida.enviar_aviso = jAulaAtribuida.getJSONArray(0).getInt(14)
                                    aulaAtribuida.duracao = jAulaAtribuida.getJSONArray(0).getInt(15)
                                    if (jAulaAtribuida.getJSONArray(0).getString(16).trim().isNotEmpty()) {
                                        aulaAtribuida.inicio =
                                            Date(sdf.parse(jAulaAtribuida.getJSONArray(0).getString(16).trim()).time)
                                    }
                                    aplicacao!!.aulaAtribuida = aulaAtribuida
                                }

                                if (jAulaFeita.length() > 0) {
                                    var aulaFeita = AulaFeita()
                                    aulaFeita.id_projeto = jAulaFeita.getJSONArray(0).getLong(0)
                                    aulaFeita.id_aluno = jAulaFeita.getJSONArray(0).getLong(1)
                                    aulaFeita.codigo_aluno = jAulaFeita.getJSONArray(0).getString(2)
                                    aulaFeita.codigo_aula = jAulaFeita.getJSONArray(0).getString(3)
                                    if (jAulaFeita.getJSONArray(0).getString(4).trim().isNotEmpty()) {
                                        aulaFeita.data =
                                            Date(sdf.parse(jAulaFeita.getJSONArray(0).getString(4).trim()).time)
                                    }
                                    aulaFeita.nota = jAulaFeita.getJSONArray(0).getDouble(5)
                                    aulaFeita.tentativas = jAulaFeita.getJSONArray(0).getInt(6)
                                    aulaFeita.erros_anterior = jAulaFeita.getJSONArray(0).getInt(7)
                                    aulaFeita.acertos_anterior = jAulaFeita.getJSONArray(0).getInt(8)
                                    aulaFeita.total_minutos = jAulaFeita.getJSONArray(0).getInt(9)
                                    aulaFeita.ultima_tela_feita = jAulaFeita.getJSONArray(0).getString(10)
                                    aulaFeita.nome = jAulaFeita.getJSONArray(0).getString(11)
                                    aulaFeita.autor = jAulaFeita.getJSONArray(0).getString(12)
                                    aulaFeita.assunto = jAulaFeita.getJSONArray(0).getString(13)
                                    aulaFeita.materia = jAulaFeita.getJSONArray(0).getString(14)
                                    aulaFeita.alvo = jAulaFeita.getJSONArray(0).getString(15)
                                    aulaFeita.pendente = jAulaFeita.getJSONArray(0).getInt(16)
                                    aulaFeita.status = jAulaFeita.getJSONArray(0).getInt(17)
                                    if (jAulaFeita.getJSONArray(0).getString(18).trim().isNotEmpty()) {
                                        aulaFeita.data_inicio =
                                            Date(sdf.parse(jAulaFeita.getJSONArray(0).getString(18).trim()).time)
                                    }
                                    aulaFeita.cod_certificacao = jAulaFeita.getJSONArray(0).getInt(19)
                                    aulaFeita.autor_atribuicao = jAulaFeita.getJSONArray(0).getString(20)
                                    aulaFeita.origem = jAulaFeita.getJSONArray(0).getString(21)
                                    aulaFeita.tempo_tmp = jAulaFeita.getJSONArray(0).getDouble(22)
                                    aulaFeita.id_ultima_tela_feita = jAulaFeita.getJSONArray(0).getLong(23)
                                    aplicacao!!.aulaFeita = aulaFeita
                                }

                                if (jAulaTestefeito.length() > 0) {
                                    var aulaTestefeito: AulaTestefeito
                                    for (i in 0 until jAulaTestefeito.length()) {
                                        aulaTestefeito = AulaTestefeito()
                                        aulaTestefeito.id_projeto = jAulaTestefeito.getJSONArray(i).getLong(0)
                                        aulaTestefeito.id_aluno = jAulaTestefeito.getJSONArray(i).getLong(1)
                                        aulaTestefeito.codigo_aluno = jAulaTestefeito.getJSONArray(i).getString(2)
                                        aulaTestefeito.codigo_aula = jAulaTestefeito.getJSONArray(i).getString(3)
                                        aulaTestefeito.tela = jAulaTestefeito.getJSONArray(i).getString(4)
                                        aulaTestefeito.tipoobj = jAulaTestefeito.getJSONArray(i).getLong(5)
                                        aulaTestefeito.indice = jAulaTestefeito.getJSONArray(i).getInt(6)
                                        aulaTestefeito.indice_par = jAulaTestefeito.getJSONArray(i).getInt(7)
                                        aulaTestefeito.indice_obj = jAulaTestefeito.getJSONArray(i).getInt(8)
                                        aulaTestefeito.peso = jAulaTestefeito.getJSONArray(i).getDouble(9)
                                        aulaTestefeito.certoerrado = jAulaTestefeito.getJSONArray(i).getDouble(10)
                                        aulaTestefeito.indice_soltar = jAulaTestefeito.getJSONArray(i).getInt(11)
                                        aulaTestefeito.pendente = jAulaTestefeito.getJSONArray(i).getInt(12)
                                        aulaTestefeito.resposta = jAulaTestefeito.getJSONArray(i).getString(13)
                                        aulaTestefeito.textopreenchido = jAulaTestefeito.getJSONArray(i).getString(14)
                                        aulaTestefeito.alternativa_selecionada = jAulaTestefeito.getJSONArray(i).getInt(15)
                                        aulaTestefeito.preenchimento = jAulaTestefeito.getJSONArray(i).getString(16)
                                        aulaTestefeito.unicode = jAulaTestefeito.getJSONArray(i).getInt(17)
                                        aulaTestefeito.id_tela = jAulaTestefeito.getJSONArray(i).getLong(18)
                                        aplicacao!!.aulaTestefeito.add(aulaTestefeito)
                                    }
                                }

                                if (jAulaIncompleta.length() > 0) {
                                    var aulaIncompleta: AulaIncompleta
                                    for (i in 0 until jAulaIncompleta.length()) {
                                        aulaIncompleta = AulaIncompleta()
                                        aulaIncompleta.id_projeto = jAulaIncompleta.getJSONArray(i).getLong(0)
                                        aulaIncompleta.id_aluno = jAulaIncompleta.getJSONArray(i).getLong(1)
                                        aulaIncompleta.codigo_aula = jAulaIncompleta.getJSONArray(i).getString(2)
                                        aulaIncompleta.codigo_aluno = jAulaIncompleta.getJSONArray(i).getString(3)
                                        aulaIncompleta.sequencia = jAulaIncompleta.getJSONArray(i).getInt(4)
                                        aulaIncompleta.tela = jAulaIncompleta.getJSONArray(i).getString(5)
                                        aulaIncompleta.id_tela = jAulaIncompleta.getJSONArray(i).getLong(6)
                                        aplicacao!!.aulaIncompleta.add(aulaIncompleta)
                                    }
                                }

                                if (aplicacao!!.aulaAtribuida!!.status == 2 && aplicacao!!.descricao!!.tentativas == aplicacao!!.aulaFeita!!.tentativas) {
                                    aplicacao!!.aulaIncompleta = ArrayList()
                                    aplicacao!!.aulaTestefeito = ArrayList()
                                    aplicacao!!.aulaFeita = AulaFeita()
                                    aplicacao!!.aulaFeita!!.status = 2
                                    aplicacao!!.aulaFeita!!.tentativas = aplicacao!!.descricao!!.tentativas
                                }
                                atualizarAtribuicaoTela(1, idTela, false)
                            }
                        } else {
                            UtilClass.trataErro(
                                UtilClass.ERRO_SERVIDOR_TAG,
                                response!!.getJSONObject("status").getString("mensagem")
                            )
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarProjetoServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarProjetoServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    UtilClass.trataErro(anError!!)
                    aplicacao!!.finalizarProgress()
                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                        carregarProjetoServidor(nTentativa + 1)
                    } else {
                        UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                    }
                }
            })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarProjetoServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    fun carregarTela(idTela: Long) {
        if (inicio) {//entrou agora, primeira tela e exercícios
            inicio = false
            carregarProjetoServidor(1)
        } else {//carregando apenas a tela
            carregarTelaServidor(1, idTela)
        }
    }

    private fun carregarTelaLocal() {//carregar com as variáveis em memória
        aplicacao!!.classTela!!.aplicacao = aplicacao
        aplicacao!!.classTela!!.instanciaLayout(act)
        aplicacao!!.classTela!!.calcularPesos(larImgFundo, altImgFundo)
        aplicacao!!.classTela!!.desenhar(act)
    }

    private fun carregarTelaServidor(nTentativa: Int, idTela: Long) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        aplicacao!!.iniciarTela()
        try {
            var pesquisa = JSONArray()
            pesquisa.put("SELECT id_tela, id_projeto, codigo, titulo, autor, data, assunto, materia, alvo, cor, porcentagem_acertos, tentativas_avancar, bloqueia_recuo, som_autoexec, menu_invisivel, titulo_visivel, som_antes, paletas, tempo_tela, cortar_som, apagar_antes, avalia_hiperlink, borda_selecao, cor_rotulo_hiperlink, cor_borda_selecao, cor_rotulo_movel, cor_fundo_rotulo_movel, largura, altura, esconde_mouse, desabilita_gravar_posicao, programa_autoexec, filme_autoexec, som, mensagem_parabens, som_parabens, mensagem_muitobem, mensagem_pense, mensagem_revejateste, mensagem_revejagira, mensagem_revejapreenche, imagem, som_fim, som_looping, consistencia_contagem, grava_voto, arquivo_contagem, mostrafundo, posicao, msgbarra, converteanima, padrao, posicionatopo, desabilita_sorteio, errado_nrespondido, mensagem_faltaselecionar, mensagem_faltaliga, mensagem_faltapreenche, mensagem_faltagira, mensagem_faltaarrastar, omiteindicacaoerro, sempreenchimentoigual, sequencia_objetos, tela_filha, sequencia_enter, tela_aleatoria, voto_tipourna, voto_idgrupo, id_nivel, id_provacurso, id_dificuldade FROM tela WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, borda, exportar_programa, topo, esquerdo, largura, altura, registra_imagem, imagem_avancar, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, cortar_som_programas, mouse, transparente, cor_transparente, autosize, link, imagem_mousemove, imagem, programa, rotulo_imagem, imagem_click, som, enunciado, mensagem_avalia, voto_funcao, programamodal, funcaoativa, jogomemoria, indice_speech, inicia_invisivel, transparente_xy, transparente_x, transparente_y, imagem_flash, imagem_flash_efeito, angulo, identificador, projeto_vcnet, localiza_anonimo FROM imagens_efeitos WHERE id_tela = $idTela ORDER BY indice")
            pesquisa.put("SELECT id_tela, tela, tipoobj, indice, arquivo FROM objsom WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, tipoobj, indice, posicao, arquivo FROM objimagem WHERE id_tela = $idTela ORDER BY tipoobj, indice, posicao")
            pesquisa.put("SELECT id_tela, tela, indice, texto, cor, fonte, tamanho, topo, esquerdo, largura, cor_fundo, altura, arquivo, tipotexto, estilofundo, barrarolamento, borda, inicia_invisivel, externo FROM textos WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, tipoobj, posicao, texto FROM objtexto WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, cor, cor_fundo, estilo, borda, tamanho, topo, esquerdo, largura, altura, link, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo_efeito, fonte3d, espessura_fonte3d, cor_contorno, cor_sombra, centralizado, imagem_visivel, imagem_letra, estilo_letra, estilo_fundo, granulacao, registra_imagem, imagem_avancar, mouse, aula, tempo, tempo_piscagem, italico, negrito, sublinhado, invisivel, sensivel, fonte, rotulo, rotulo_rotulo, som, enunciado, mensagem_avalia, robo_sensor, robo_tempo, robo_sequencia, robo_autoajusta, voto_funcao, desativado, funcaoativa, mensagem_desativado, programa, programamodal, cortar_som_programas, indice_speech, inicia_invisivel, robo_porta, rotulo_flash, rotulo_flash_efeito, angulo, identificador, projeto_vcnet, localiza_anonimo FROM rotulos WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, tipoobj, indice, arquivo FROM objfilme WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, borda, autoexecutar, repetir, topo, esquerdo, largura, altura, registra_filme, filme_avancar, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, paleta, mouse, silencio, tipo, sensivel_mouse, apagar_ao_encerrar, link, invisivel, filme_tela, rotulo_filme, enunciado, filme_anterior, mensagem_avalia, voto_funcao, funcaoativa, indice_speech, inicia_invisivel, filme_flash, link_url, barra_progresso FROM filmes_tela WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, borda, topo, esquerdo, altura, largura, exportar_programa, registra_imagem, imagem_avancar, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, cortar_som_programas, sensivel_mouse, transparente, cor_transparente, autosize, link, autoexecutar, tempos_3, programa, imagem, som, rotulo_imagem, enunciado, tempoframe, looping, contador, ativa_contador, nome_contador, mensagem_contador, mensagem_avalia, robo_servomotor, robo_direcao, robo_passo, robo_armazena, robo_acao, robo_intervalo, voto_funcao, duracao, temporefresh, ciclos, interrompe, programamodal, desativado, funcaoativa, mensagem_desativado, anima_anterior, invisivel, avancaraoencerrar, visivelaoexecutar, indice_speech, inicia_invisivel, robo_tempointer, som_posicao, anima_flash, rotaciona, som_inicio_ani, som_fim_anima, duracao_som, executar_apos, invisivel_apos, arquivo_externo, identificador, projeto_vcnet, localiza_anonimo, compartilha FROM animacoes WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, cor, estilo, espessura, x1, y1, x2, y2 FROM linhas WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, texto, cor, tamanho, topo, esquerdo, largura, altura, fonte, enunciado, selecionarclic, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, fonte3d, espessura_fonte3d, cor_contorno, cor_sombra, espessura_borda, negrito FROM arrastar WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, texto, dica, cor, fonte, tamanho, topo, esquerdo, largura, altura, som, colar_errado, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, fonte3d, espessura_fonte3d, cor_contorno, cor_sombra, espessura_borda, negrito FROM soltar WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, borda, topo, esquerdo, largura, altura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, imagem, enunciado, selecionarclic, angulo FROM arrastar_imagens WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, imagem, borda, dica, som, topo, esquerdo, largura, altura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, colar_errado, angulo FROM soltar_imagens WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, indice_obj, borda, topo, esquerdo, largura, altura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, imagem, enunciado, selecionarclic, imagem_soltar, orfa, angulo FROM arrastar_n WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, imagem, borda, dica, som, topo, esquerdo, largura, altura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, colar_errado, contador, angulo FROM soltar_n WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, resposta, topo, esquerdo, largura, altura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, imagem, enunciado, fig1, borda_central FROM giras_efeitos WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, borda, topo, esquerdo, altura, largura, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, liga_anterior, liga_primeiro, cor_linha, espessura_linha, estilo_linha, imagem, som, enunciado, liga_errado, clicar, associasom_obj, desabilita_liga2, angulo FROM liga1 WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, imagem, borda, topo, esquerdo, altura, largura, som, efeito, delay, cor_borda_interna1, cor_borda_interna2, estilo_borda_interna, espessura_borda_interna, cor_borda_central, cor_borda_externa1, cor_borda_externa2, estilo_borda_externa, espessura_borda_externa, cor_fundo, granulacao, transparente, cor_transparente, liga_anterior, liga_primeiro, cor_linha, espessura_linha, estilo_linha, associasom_obj, angulo FROM liga2 WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, cor, tamanho, topo, esquerdo, largura, altura, cor_fundo, estilo_borda, ignora_branco, maximo, caixa, preenchimento, enunciado, fonte, transparente, negrito, italico, multilinha, caixadig FROM preenchimentos WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, enunciado, topo, esquerdo, largura, altura, cor_fundo, borda, barrarolamento, transparente, fonte, tamanho, cor, caixadig FROM dissertativa WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, resposta, enunciado, imagem, distancia_h, unicode, centraliza_sel, alin_par, aumenta_par, selecao_x FROM testes_vest WHERE id_tela = $idTela")
            pesquisa.put("SELECT id_tela, tela, indice, indice_obj, topo, esquerdo, largura, altura, alternativa, cor_fundo, barrarolamento, borda, estilofundo, arquivo, cor, italico, negrito, tamanho, fonte, largura_img, altura_img, fundo_img FROM testes_vest_alt WHERE id_tela = $idTela ORDER BY indice, indice_obj")
            pesquisa.put("SELECT id_tela, tela, tipoobj, indice, peso, enunciado, descritivo FROM objenunciado WHERE id_tela = $idTela")
            pesquisa.put("SELECT ol.id_tela, ol.tela, ol.tipoobj, ol.indice, ol.link, ol.linkretorno, ol.condicao, t.id_tela as id_tela_link FROM objlink AS ol INNER JOIN tela AS t ON t.codigo = ol.link AND t.id_projeto = ${aplicacao!!.descricao!!.id_projeto} WHERE ol.id_tela = $idTela")

            var json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put("pesquisa", pesquisa)

            UtilClass.conectaHttp(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_SQLCONSULTAMULTIPLA, json, null, aplicacao!!.configuracoes!!, object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    aplicacao!!.finalizarProgress()
                    try {
                        if (response!!.getJSONObject("status").getString("codigo") == "ok") {
                            var jTela = response!!.getJSONArray("consultas").getJSONObject(0).getJSONArray("linhas")
                            var jImagensEfeitos = response!!.getJSONArray("consultas").getJSONObject(1).getJSONArray("linhas")
                            var jObjsom = response!!.getJSONArray("consultas").getJSONObject(2).getJSONArray("linhas")
                            var jObjimagem = response!!.getJSONArray("consultas").getJSONObject(3).getJSONArray("linhas")
                            var jTextos = response!!.getJSONArray("consultas").getJSONObject(4).getJSONArray("linhas")
                            var jObjtexto = response!!.getJSONArray("consultas").getJSONObject(5).getJSONArray("linhas")
                            var jRotulos = response!!.getJSONArray("consultas").getJSONObject(6).getJSONArray("linhas")
                            var jObjfilme = response!!.getJSONArray("consultas").getJSONObject(7).getJSONArray("linhas")
                            var jFilmesTela = response!!.getJSONArray("consultas").getJSONObject(8).getJSONArray("linhas")
                            var jAnimacoes = response!!.getJSONArray("consultas").getJSONObject(9).getJSONArray("linhas")
                            var jLinhas = response!!.getJSONArray("consultas").getJSONObject(10).getJSONArray("linhas")
                            var jArrastar = response!!.getJSONArray("consultas").getJSONObject(11).getJSONArray("linhas")
                            var jSoltar = response!!.getJSONArray("consultas").getJSONObject(12).getJSONArray("linhas")
                            var jArrastarImagens = response!!.getJSONArray("consultas").getJSONObject(13).getJSONArray("linhas")
                            var jSoltarImagens = response!!.getJSONArray("consultas").getJSONObject(14).getJSONArray("linhas")
                            var jArrastarN = response!!.getJSONArray("consultas").getJSONObject(15).getJSONArray("linhas")
                            var jSoltarN = response!!.getJSONArray("consultas").getJSONObject(16).getJSONArray("linhas")
                            var jGirasEfeitos = response!!.getJSONArray("consultas").getJSONObject(17).getJSONArray("linhas")
                            var jLiga1 = response!!.getJSONArray("consultas").getJSONObject(18).getJSONArray("linhas")
                            var jLiga2 = response!!.getJSONArray("consultas").getJSONObject(19).getJSONArray("linhas")
                            var jPreenchimentos = response!!.getJSONArray("consultas").getJSONObject(20).getJSONArray("linhas")
                            var jDissertativa = response!!.getJSONArray("consultas").getJSONObject(21).getJSONArray("linhas")
                            var jTestesVest = response!!.getJSONArray("consultas").getJSONObject(22).getJSONArray("linhas")
                            var jTestesVestAlt = response!!.getJSONArray("consultas").getJSONObject(23).getJSONArray("linhas")
                            var jObjenunciado = response!!.getJSONArray("consultas").getJSONObject(24).getJSONArray("linhas")
                            var jObjlink = response!!.getJSONArray("consultas").getJSONObject(25).getJSONArray("linhas")

                            if (jTela == null || jTela.length() == 0) {
                                UtilClass.msgOk(act, R.string.aviso, R.string.erro_carregar_tela, R.string.ok, DialogInterface.OnClickListener { _, _ ->
                                    act.finish()
                                })
                            } else {
                                val sdf = SimpleDateFormat("yyyy-MM-dd")
                                var tela = Tela()
                                tela.id_tela = jTela.getJSONArray(0).getLong(0)
                                tela.id_projeto = jTela.getJSONArray(0).getLong(1)
                                tela.codigo = jTela.getJSONArray(0).getString(2)
                                tela.titulo = jTela.getJSONArray(0).getString(3)
                                tela.autor = jTela.getJSONArray(0).getString(4)
                                if (jTela.getJSONArray(0).getString(5).trim().isNotEmpty()) {
                                    tela.data = Date(sdf.parse(jTela.getJSONArray(0).getString(5).trim()).time)
                                }
                                tela.assunto = jTela.getJSONArray(0).getString(6)
                                tela.materia = jTela.getJSONArray(0).getString(7)
                                tela.alvo = jTela.getJSONArray(0).getString(8)
                                tela.cor = jTela.getJSONArray(0).getInt(9)
                                tela.porcentagem_acertos = jTela.getJSONArray(0).getInt(10)
                                tela.tentativas_avancar = jTela.getJSONArray(0).getInt(11)
                                tela.bloqueia_recuo = jTela.getJSONArray(0).getInt(12)
                                tela.som_autoexec = jTela.getJSONArray(0).getInt(13)
                                tela.menu_invisivel = jTela.getJSONArray(0).getInt(14)
                                tela.titulo_visivel = jTela.getJSONArray(0).getInt(15)
                                tela.som_antes = jTela.getJSONArray(0).getInt(16)
                                tela.paletas = jTela.getJSONArray(0).getInt(17)
                                tela.tempo_tela = jTela.getJSONArray(0).getInt(18)
                                tela.cortar_som = jTela.getJSONArray(0).getInt(19)
                                tela.apagar_antes = jTela.getJSONArray(0).getInt(20)
                                tela.avalia_hiperlink = jTela.getJSONArray(0).getInt(21)
                                tela.borda_selecao = jTela.getJSONArray(0).getInt(22)
                                tela.cor_rotulo_hiperlink = jTela.getJSONArray(0).getInt(23)
                                tela.cor_borda_selecao = jTela.getJSONArray(0).getInt(24)
                                tela.cor_rotulo_movel = jTela.getJSONArray(0).getInt(25)
                                tela.cor_fundo_rotulo_movel = jTela.getJSONArray(0).getInt(26)
                                tela.largura = jTela.getJSONArray(0).getInt(27)
                                tela.altura = jTela.getJSONArray(0).getInt(28)
                                tela.esconde_mouse = jTela.getJSONArray(0).getInt(29)
                                tela.desabilita_gravar_posicao = jTela.getJSONArray(0).getInt(30)
                                tela.programa_autoexec = jTela.getJSONArray(0).getInt(31)
                                tela.filme_autoexec = jTela.getJSONArray(0).getInt(32)
                                tela.som = jTela.getJSONArray(0).getInt(33)
                                tela.mensagem_parabens = jTela.getJSONArray(0).getInt(34)
                                tela.som_parabens = jTela.getJSONArray(0).getInt(35)
                                tela.mensagem_muitobem = jTela.getJSONArray(0).getInt(36)
                                tela.mensagem_pense = jTela.getJSONArray(0).getInt(37)
                                tela.mensagem_revejateste = jTela.getJSONArray(0).getInt(38)
                                tela.mensagem_revejagira = jTela.getJSONArray(0).getInt(39)
                                tela.mensagem_revejapreenche = jTela.getJSONArray(0).getInt(40)
                                tela.imagem = jTela.getJSONArray(0).getInt(41)
                                tela.som_fim = jTela.getJSONArray(0).getInt(42)
                                tela.som_looping = jTela.getJSONArray(0).getInt(43)
                                tela.consistencia_contagem = jTela.getJSONArray(0).getInt(44)
                                tela.grava_voto = jTela.getJSONArray(0).getInt(45)
                                tela.arquivo_contagem = jTela.getJSONArray(0).getString(46)
                                tela.mostrafundo = jTela.getJSONArray(0).getInt(47)
                                tela.posicao = jTela.getJSONArray(0).getInt(48)
                                tela.msgbarra = jTela.getJSONArray(0).getInt(49)
                                tela.converteanima = jTela.getJSONArray(0).getInt(50)
                                tela.padrao = jTela.getJSONArray(0).getInt(51)
                                tela.posicionatopo = jTela.getJSONArray(0).getInt(52)
                                tela.desabilita_sorteio = jTela.getJSONArray(0).getInt(53)
                                tela.errado_nrespondido = jTela.getJSONArray(0).getInt(54)
                                tela.mensagem_faltaselecionar = jTela.getJSONArray(0).getInt(55)
                                tela.mensagem_faltaliga = jTela.getJSONArray(0).getInt(56)
                                tela.mensagem_faltapreenche = jTela.getJSONArray(0).getInt(57)
                                tela.mensagem_faltagira = jTela.getJSONArray(0).getInt(58)
                                tela.mensagem_faltaarrastar = jTela.getJSONArray(0).getInt(59)
                                tela.omiteindicacaoerro = jTela.getJSONArray(0).getInt(60)
                                tela.sempreenchimentoigual = jTela.getJSONArray(0).getInt(61)
                                tela.sequencia_objetos = jTela.getJSONArray(0).getInt(62)
                                tela.tela_filha = jTela.getJSONArray(0).getInt(63)
                                tela.sequencia_enter = jTela.getJSONArray(0).getInt(64)
                                tela.tela_aleatoria = jTela.getJSONArray(0).getInt(65)
                                tela.voto_tipourna = jTela.getJSONArray(0).getInt(66)
                                tela.voto_idgrupo = jTela.getJSONArray(0).getLong(67)
                                tela.id_nivel = jTela.getJSONArray(0).getLong(68)
                                tela.id_provacurso = jTela.getJSONArray(0).getLong(69)
                                tela.id_dificuldade = jTela.getJSONArray(0).getLong(70)

                                aplicacao!!.classTela = ClassTela()
                                aplicacao!!.classTela!!.tela = tela

                                if (jImagensEfeitos != null && jImagensEfeitos.length() > 0) {
                                    var imagensEfeitos: ImagensEfeitos
                                    for (i in 0 until jImagensEfeitos.length()) {
                                        imagensEfeitos = ImagensEfeitos()
                                        imagensEfeitos.id_tela = jImagensEfeitos.getJSONArray(i).getLong(0)
                                        imagensEfeitos.tela = jImagensEfeitos.getJSONArray(i).getString(1)
                                        imagensEfeitos.indice = jImagensEfeitos.getJSONArray(i).getInt(2)
                                        imagensEfeitos.borda = jImagensEfeitos.getJSONArray(i).getInt(3)
                                        imagensEfeitos.exportar_programa = jImagensEfeitos.getJSONArray(i).getInt(4)
                                        imagensEfeitos.topo = UtilClass.twipToPixel(jImagensEfeitos.getJSONArray(i).getInt(5))
                                        imagensEfeitos.esquerdo = UtilClass.twipToPixel(jImagensEfeitos.getJSONArray(i).getInt(6))
                                        imagensEfeitos.largura = UtilClass.twipToPixel(jImagensEfeitos.getJSONArray(i).getInt(7))
                                        imagensEfeitos.altura = UtilClass.twipToPixel(jImagensEfeitos.getJSONArray(i).getInt(8))
                                        imagensEfeitos.registra_imagem = jImagensEfeitos.getJSONArray(i).getInt(9)
                                        imagensEfeitos.imagem_avancar = jImagensEfeitos.getJSONArray(i).getInt(10)
                                        imagensEfeitos.efeito = jImagensEfeitos.getJSONArray(i).getInt(11)
                                        imagensEfeitos.delay = jImagensEfeitos.getJSONArray(i).getInt(12)
                                        imagensEfeitos.cor_borda_interna1 = jImagensEfeitos.getJSONArray(i).getInt(13)
                                        imagensEfeitos.cor_borda_interna2 = jImagensEfeitos.getJSONArray(i).getInt(14)
                                        imagensEfeitos.estilo_borda_interna = jImagensEfeitos.getJSONArray(i).getInt(15)
                                        imagensEfeitos.espessura_borda_interna = jImagensEfeitos.getJSONArray(i).getInt(16)
                                        imagensEfeitos.cor_borda_central = jImagensEfeitos.getJSONArray(i).getInt(17)
                                        imagensEfeitos.cor_borda_externa1 = jImagensEfeitos.getJSONArray(i).getInt(18)
                                        imagensEfeitos.cor_borda_externa2 = jImagensEfeitos.getJSONArray(i).getInt(19)
                                        imagensEfeitos.estilo_borda_externa = jImagensEfeitos.getJSONArray(i).getInt(20)
                                        imagensEfeitos.espessura_borda_externa = jImagensEfeitos.getJSONArray(i).getInt(21)
                                        imagensEfeitos.cor_fundo = jImagensEfeitos.getJSONArray(i).getInt(22)
                                        imagensEfeitos.granulacao = jImagensEfeitos.getJSONArray(i).getInt(23)
                                        imagensEfeitos.cortar_som_programas = jImagensEfeitos.getJSONArray(i).getInt(24)
                                        imagensEfeitos.mouse = jImagensEfeitos.getJSONArray(i).getInt(25)
                                        imagensEfeitos.transparente = jImagensEfeitos.getJSONArray(i).getInt(26)
                                        imagensEfeitos.cor_transparente = jImagensEfeitos.getJSONArray(i).getInt(27)
                                        imagensEfeitos.autosize = jImagensEfeitos.getJSONArray(i).getInt(28)
                                        imagensEfeitos.link = jImagensEfeitos.getJSONArray(i).getInt(29)
                                        imagensEfeitos.imagem_mousemove = jImagensEfeitos.getJSONArray(i).getInt(30)
                                        imagensEfeitos.imagem = jImagensEfeitos.getJSONArray(i).getInt(31)
                                        imagensEfeitos.programa = jImagensEfeitos.getJSONArray(i).getInt(32)
                                        imagensEfeitos.rotulo_imagem = jImagensEfeitos.getJSONArray(i).getInt(33)
                                        imagensEfeitos.imagem_click = jImagensEfeitos.getJSONArray(i).getInt(34)
                                        imagensEfeitos.som = jImagensEfeitos.getJSONArray(i).getInt(35)
                                        imagensEfeitos.enunciado = jImagensEfeitos.getJSONArray(i).getInt(36)
                                        imagensEfeitos.mensagem_avalia = jImagensEfeitos.getJSONArray(i).getInt(37)
                                        imagensEfeitos.voto_funcao = jImagensEfeitos.getJSONArray(i).getInt(38)
                                        imagensEfeitos.programamodal = jImagensEfeitos.getJSONArray(i).getInt(39)
                                        imagensEfeitos.funcaoativa = jImagensEfeitos.getJSONArray(i).getInt(40)
                                        imagensEfeitos.jogomemoria = jImagensEfeitos.getJSONArray(i).getInt(41)
                                        imagensEfeitos.indice_speech = jImagensEfeitos.getJSONArray(i).getInt(42)
                                        imagensEfeitos.inicia_invisivel = jImagensEfeitos.getJSONArray(i).getInt(43)
                                        imagensEfeitos.transparente_xy = jImagensEfeitos.getJSONArray(i).getInt(44)
                                        imagensEfeitos.transparente_x = jImagensEfeitos.getJSONArray(i).getInt(45)
                                        imagensEfeitos.transparente_y = jImagensEfeitos.getJSONArray(i).getInt(46)
                                        imagensEfeitos.imagem_flash = jImagensEfeitos.getJSONArray(i).getInt(47)
                                        imagensEfeitos.imagem_flash_efeito = jImagensEfeitos.getJSONArray(i).getInt(48)
                                        imagensEfeitos.angulo = jImagensEfeitos.getJSONArray(i).getInt(49)
                                        imagensEfeitos.identificador = jImagensEfeitos.getJSONArray(i).getString(50)
                                        imagensEfeitos.projeto_vcnet = jImagensEfeitos.getJSONArray(i).getString(51)
                                        imagensEfeitos.localiza_anonimo = jImagensEfeitos.getJSONArray(i).getInt(52)

                                        var classImagensEfeitos = ClassImagensEfeitos()
                                        classImagensEfeitos.imagensEfeitos = imagensEfeitos

                                        aplicacao!!.classImagensEfeitos.add(classImagensEfeitos)
                                    }
                                }

                                if (jTextos != null && jTextos.length() > 0) {
                                    var textos: Textos
                                    for (i in 0 until jTextos.length()) {
                                        textos = Textos()
                                        textos.id_tela = jTextos.getJSONArray(i).getLong(0)
                                        textos.tela = jTextos.getJSONArray(i).getString(1)
                                        textos.indice = jTextos.getJSONArray(i).getInt(2)
                                        textos.texto = jTextos.getJSONArray(i).getInt(3)
                                        textos.cor = jTextos.getJSONArray(i).getInt(4)
                                        textos.fonte = jTextos.getJSONArray(i).getString(5)
                                        textos.tamanho = jTextos.getJSONArray(i).getInt(6)
                                        textos.topo = UtilClass.twipToPixel(jTextos.getJSONArray(i).getInt(7))
                                        textos.esquerdo = UtilClass.twipToPixel(jTextos.getJSONArray(i).getInt(8))
                                        textos.largura = UtilClass.twipToPixel(jTextos.getJSONArray(i).getInt(9))
                                        textos.cor_fundo = jTextos.getJSONArray(i).getInt(10)
                                        textos.altura = UtilClass.twipToPixel(jTextos.getJSONArray(i).getInt(11))
                                        textos.arquivo = jTextos.getJSONArray(i).getInt(12)
                                        textos.tipotexto = jTextos.getJSONArray(i).getLong(13)
                                        textos.estilofundo = jTextos.getJSONArray(i).getInt(14)
                                        textos.barrarolamento = jTextos.getJSONArray(i).getInt(15)
                                        textos.borda = jTextos.getJSONArray(i).getInt(16)
                                        textos.inicia_invisivel = jTextos.getJSONArray(i).getInt(17)
                                        textos.externo = jTextos.getJSONArray(i).getInt(18)

                                        var classTextos = ClassTextos()
                                        classTextos.textos = textos

                                        aplicacao!!.classTextos.add(classTextos)
                                    }
                                }

                                if (jRotulos != null && jRotulos.length() > 0) {
                                    var rotulos: Rotulos
                                    for (i in 0 until jRotulos.length()) {
                                        rotulos = Rotulos()
                                        rotulos.id_tela = jRotulos.getJSONArray(i).getLong(0)
                                        rotulos.tela = jRotulos.getJSONArray(i).getString(1)
                                        rotulos.indice = jRotulos.getJSONArray(i).getInt(2)
                                        rotulos.cor = jRotulos.getJSONArray(i).getInt(3)
                                        rotulos.cor_fundo = jRotulos.getJSONArray(i).getInt(4)
                                        rotulos.estilo = jRotulos.getJSONArray(i).getInt(5)
                                        rotulos.borda = jRotulos.getJSONArray(i).getInt(6)
                                        rotulos.tamanho = jRotulos.getJSONArray(i).getDouble(7)
                                        rotulos.topo = UtilClass.twipToPixel(jRotulos.getJSONArray(i).getInt(8))
                                        rotulos.esquerdo = UtilClass.twipToPixel(jRotulos.getJSONArray(i).getInt(9))
                                        rotulos.largura = UtilClass.twipToPixel(jRotulos.getJSONArray(i).getInt(10))
                                        rotulos.altura = UtilClass.twipToPixel(jRotulos.getJSONArray(i).getInt(11))
                                        rotulos.link = jRotulos.getJSONArray(i).getInt(12)
                                        rotulos.efeito = jRotulos.getJSONArray(i).getInt(13)
                                        rotulos.delay = jRotulos.getJSONArray(i).getInt(14)
                                        rotulos.cor_borda_interna1 = jRotulos.getJSONArray(i).getInt(15)
                                        rotulos.cor_borda_interna2 = jRotulos.getJSONArray(i).getInt(16)
                                        rotulos.estilo_borda_interna = jRotulos.getJSONArray(i).getInt(17)
                                        rotulos.espessura_borda_interna = jRotulos.getJSONArray(i).getInt(18)
                                        rotulos.cor_borda_central = jRotulos.getJSONArray(i).getInt(19)
                                        rotulos.cor_borda_externa1 = jRotulos.getJSONArray(i).getInt(20)
                                        rotulos.cor_borda_externa2 = jRotulos.getJSONArray(i).getInt(21)
                                        rotulos.estilo_borda_externa = jRotulos.getJSONArray(i).getInt(22)
                                        rotulos.espessura_borda_externa = jRotulos.getJSONArray(i).getInt(23)
                                        rotulos.cor_fundo_efeito = jRotulos.getJSONArray(i).getInt(24)
                                        rotulos.fonte3d = jRotulos.getJSONArray(i).getInt(25)
                                        rotulos.espessura_fonte3d = jRotulos.getJSONArray(i).getInt(26)
                                        rotulos.cor_contorno = jRotulos.getJSONArray(i).getInt(27)
                                        rotulos.cor_sombra = jRotulos.getJSONArray(i).getInt(28)
                                        rotulos.centralizado = jRotulos.getJSONArray(i).getInt(29)
                                        rotulos.imagem_visivel = jRotulos.getJSONArray(i).getInt(30)
                                        rotulos.imagem_letra = jRotulos.getJSONArray(i).getInt(31)
                                        rotulos.estilo_letra = jRotulos.getJSONArray(i).getInt(32)
                                        rotulos.estilo_fundo = jRotulos.getJSONArray(i).getInt(33)
                                        rotulos.granulacao = jRotulos.getJSONArray(i).getInt(34)
                                        rotulos.registra_imagem = jRotulos.getJSONArray(i).getInt(35)
                                        rotulos.imagem_avancar = jRotulos.getJSONArray(i).getInt(36)
                                        rotulos.mouse = jRotulos.getJSONArray(i).getInt(37)
                                        rotulos.aula = jRotulos.getJSONArray(i).getString(38)
                                        rotulos.tempo = jRotulos.getJSONArray(i).getDouble(39)
                                        rotulos.tempo_piscagem = jRotulos.getJSONArray(i).getDouble(40)
                                        rotulos.italico = jRotulos.getJSONArray(i).getInt(41)
                                        rotulos.negrito = jRotulos.getJSONArray(i).getInt(42)
                                        rotulos.sublinhado = jRotulos.getJSONArray(i).getInt(43)
                                        rotulos.invisivel = jRotulos.getJSONArray(i).getInt(44)
                                        rotulos.sensivel = jRotulos.getJSONArray(i).getInt(45)
                                        rotulos.fonte = jRotulos.getJSONArray(i).getString(46)
                                        rotulos.rotulo = jRotulos.getJSONArray(i).getInt(47)
                                        rotulos.rotulo_rotulo = jRotulos.getJSONArray(i).getInt(48)
                                        rotulos.som = jRotulos.getJSONArray(i).getInt(49)
                                        rotulos.enunciado = jRotulos.getJSONArray(i).getInt(50)
                                        rotulos.mensagem_avalia = jRotulos.getJSONArray(i).getInt(51)
                                        rotulos.robo_sensor = jRotulos.getJSONArray(i).getInt(52)
                                        rotulos.robo_tempo = jRotulos.getJSONArray(i).getInt(53)
                                        rotulos.robo_sequencia = jRotulos.getJSONArray(i).getInt(54)
                                        rotulos.robo_autoajusta = jRotulos.getJSONArray(i).getInt(55)
                                        rotulos.voto_funcao = jRotulos.getJSONArray(i).getInt(56)
                                        rotulos.desativado = jRotulos.getJSONArray(i).getInt(57)
                                        rotulos.funcaoativa = jRotulos.getJSONArray(i).getInt(58)
                                        rotulos.mensagem_desativado = jRotulos.getJSONArray(i).getInt(59)
                                        rotulos.programa = jRotulos.getJSONArray(i).getInt(60)
                                        rotulos.programamodal = jRotulos.getJSONArray(i).getInt(61)
                                        rotulos.cortar_som_programas = jRotulos.getJSONArray(i).getInt(62)
                                        rotulos.indice_speech = jRotulos.getJSONArray(i).getInt(63)
                                        rotulos.inicia_invisivel = jRotulos.getJSONArray(i).getInt(64)
                                        rotulos.robo_porta = jRotulos.getJSONArray(i).getInt(65)
                                        rotulos.rotulo_flash = jRotulos.getJSONArray(i).getInt(66)
                                        rotulos.rotulo_flash_efeito = jRotulos.getJSONArray(i).getInt(67)
                                        rotulos.angulo = jRotulos.getJSONArray(i).getInt(68)
                                        rotulos.identificador = jRotulos.getJSONArray(i).getString(69)
                                        rotulos.projeto_vcnet = jRotulos.getJSONArray(i).getString(70)
                                        rotulos.localiza_anonimo = jRotulos.getJSONArray(i).getInt(71)

                                        var classRotulos = ClassRotulos()
                                        classRotulos.rotulos = rotulos

                                        aplicacao!!.classRotulos.add(classRotulos)
                                    }
                                }

                                if (jFilmesTela != null && jFilmesTela.length() > 0) {
                                    var filmesTela: FilmesTela
                                    for (i in 0 until jFilmesTela.length()) {
                                        filmesTela = FilmesTela()
                                        filmesTela.id_tela = jFilmesTela.getJSONArray(i).getLong(0)
                                        filmesTela.tela = jFilmesTela.getJSONArray(i).getString(1)
                                        filmesTela.indice = jFilmesTela.getJSONArray(i).getInt(2)
                                        filmesTela.borda = jFilmesTela.getJSONArray(i).getInt(3)
                                        filmesTela.autoexecutar = jFilmesTela.getJSONArray(i).getInt(4)
                                        filmesTela.repetir = jFilmesTela.getJSONArray(i).getInt(5)
                                        filmesTela.topo = UtilClass.twipToPixel(jFilmesTela.getJSONArray(i).getInt(6))
                                        filmesTela.esquerdo = UtilClass.twipToPixel(jFilmesTela.getJSONArray(i).getInt(7))
                                        filmesTela.largura = UtilClass.twipToPixel(jFilmesTela.getJSONArray(i).getInt(8))
                                        filmesTela.altura = UtilClass.twipToPixel(jFilmesTela.getJSONArray(i).getInt(9))
                                        filmesTela.registra_filme = jFilmesTela.getJSONArray(i).getInt(10)
                                        filmesTela.filme_avancar = jFilmesTela.getJSONArray(i).getInt(11)
                                        filmesTela.efeito = jFilmesTela.getJSONArray(i).getInt(12)
                                        filmesTela.delay = jFilmesTela.getJSONArray(i).getInt(13)
                                        filmesTela.cor_borda_interna1 = jFilmesTela.getJSONArray(i).getInt(14)
                                        filmesTela.cor_borda_interna2 = jFilmesTela.getJSONArray(i).getInt(15)
                                        filmesTela.estilo_borda_interna = jFilmesTela.getJSONArray(i).getInt(16)
                                        filmesTela.espessura_borda_interna = jFilmesTela.getJSONArray(i).getInt(17)
                                        filmesTela.cor_borda_central = jFilmesTela.getJSONArray(i).getInt(18)
                                        filmesTela.cor_borda_externa1 = jFilmesTela.getJSONArray(i).getInt(19)
                                        filmesTela.cor_borda_externa2 = jFilmesTela.getJSONArray(i).getInt(20)
                                        filmesTela.estilo_borda_externa = jFilmesTela.getJSONArray(i).getInt(21)
                                        filmesTela.espessura_borda_externa = jFilmesTela.getJSONArray(i).getInt(22)
                                        filmesTela.cor_fundo = jFilmesTela.getJSONArray(i).getInt(23)
                                        filmesTela.granulacao = jFilmesTela.getJSONArray(i).getInt(24)
                                        filmesTela.paleta = jFilmesTela.getJSONArray(i).getInt(25)
                                        filmesTela.mouse = jFilmesTela.getJSONArray(i).getInt(26)
                                        filmesTela.silencio = jFilmesTela.getJSONArray(i).getInt(27)
                                        filmesTela.tipo = jFilmesTela.getJSONArray(i).getInt(28)
                                        filmesTela.sensivel_mouse = jFilmesTela.getJSONArray(i).getInt(29)
                                        filmesTela.apagar_ao_encerrar = jFilmesTela.getJSONArray(i).getInt(30)
                                        filmesTela.link = jFilmesTela.getJSONArray(i).getInt(31)
                                        filmesTela.invisivel = jFilmesTela.getJSONArray(i).getInt(32)
                                        filmesTela.filme_tela = jFilmesTela.getJSONArray(i).getInt(33)
                                        filmesTela.rotulo_filme = jFilmesTela.getJSONArray(i).getInt(34)
                                        filmesTela.enunciado = jFilmesTela.getJSONArray(i).getInt(35)
                                        filmesTela.filme_anterior = jFilmesTela.getJSONArray(i).getInt(36)
                                        filmesTela.mensagem_avalia = jFilmesTela.getJSONArray(i).getInt(37)
                                        filmesTela.voto_funcao = jFilmesTela.getJSONArray(i).getInt(38)
                                        filmesTela.funcaoativa = jFilmesTela.getJSONArray(i).getInt(39)
                                        filmesTela.indice_speech = jFilmesTela.getJSONArray(i).getInt(40)
                                        filmesTela.inicia_invisivel = jFilmesTela.getJSONArray(i).getInt(41)
                                        filmesTela.filme_flash = jFilmesTela.getJSONArray(i).getInt(42)
                                        filmesTela.link_url = jFilmesTela.getJSONArray(i).getString(43)
                                        filmesTela.barra_progresso = jFilmesTela.getJSONArray(i).getInt(44)

                                        var classFilmesTela = ClassFilmesTela()
                                        classFilmesTela.filmesTela = filmesTela

                                        aplicacao!!.classFilmesTela.add(classFilmesTela)
                                    }
                                }

                                if (jAnimacoes != null && jAnimacoes.length() > 0) {
                                    var animacoes: Animacoes
                                    for (i in 0 until jAnimacoes.length()) {
                                        animacoes = Animacoes()
                                        animacoes.id_tela = jAnimacoes.getJSONArray(i).getLong(0)
                                        animacoes.tela = jAnimacoes.getJSONArray(i).getString(1)
                                        animacoes.indice = jAnimacoes.getJSONArray(i).getInt(2)
                                        animacoes.borda = jAnimacoes.getJSONArray(i).getInt(3)
                                        animacoes.topo = UtilClass.twipToPixel(jAnimacoes.getJSONArray(i).getInt(4))
                                        animacoes.esquerdo = UtilClass.twipToPixel(jAnimacoes.getJSONArray(i).getInt(5))
                                        animacoes.altura = UtilClass.twipToPixel(jAnimacoes.getJSONArray(i).getInt(6))
                                        animacoes.largura = UtilClass.twipToPixel(jAnimacoes.getJSONArray(i).getInt(7))
                                        animacoes.exportar_programa = jAnimacoes.getJSONArray(i).getInt(8)
                                        animacoes.registra_imagem = jAnimacoes.getJSONArray(i).getInt(9)
                                        animacoes.imagem_avancar = jAnimacoes.getJSONArray(i).getInt(10)
                                        animacoes.efeito = jAnimacoes.getJSONArray(i).getInt(11)
                                        animacoes.delay = jAnimacoes.getJSONArray(i).getInt(12)
                                        animacoes.cor_borda_interna1 = jAnimacoes.getJSONArray(i).getInt(13)
                                        animacoes.cor_borda_interna2 = jAnimacoes.getJSONArray(i).getInt(14)
                                        animacoes.estilo_borda_interna = jAnimacoes.getJSONArray(i).getInt(15)
                                        animacoes.espessura_borda_interna = jAnimacoes.getJSONArray(i).getInt(16)
                                        animacoes.cor_borda_central = jAnimacoes.getJSONArray(i).getInt(17)
                                        animacoes.cor_borda_externa1 = jAnimacoes.getJSONArray(i).getInt(18)
                                        animacoes.cor_borda_externa2 = jAnimacoes.getJSONArray(i).getInt(19)
                                        animacoes.estilo_borda_externa = jAnimacoes.getJSONArray(i).getInt(20)
                                        animacoes.espessura_borda_externa = jAnimacoes.getJSONArray(i).getInt(21)
                                        animacoes.cor_fundo = jAnimacoes.getJSONArray(i).getInt(22)
                                        animacoes.granulacao = jAnimacoes.getJSONArray(i).getInt(23)
                                        animacoes.cortar_som_programas = jAnimacoes.getJSONArray(i).getInt(24)
                                        animacoes.sensivel_mouse = jAnimacoes.getJSONArray(i).getInt(25)
                                        animacoes.transparente = jAnimacoes.getJSONArray(i).getInt(26)
                                        animacoes.cor_transparente = jAnimacoes.getJSONArray(i).getInt(27)
                                        animacoes.autosize = jAnimacoes.getJSONArray(i).getInt(28)
                                        animacoes.link = jAnimacoes.getJSONArray(i).getInt(29)
                                        animacoes.autoexecutar = jAnimacoes.getJSONArray(i).getInt(30)
                                        animacoes.tempos_3 = jAnimacoes.getJSONArray(i).getInt(31)
                                        animacoes.programa = jAnimacoes.getJSONArray(i).getInt(32)
                                        animacoes.imagem = jAnimacoes.getJSONArray(i).getInt(33)
                                        animacoes.som = jAnimacoes.getJSONArray(i).getInt(34)
                                        animacoes.rotulo_imagem = jAnimacoes.getJSONArray(i).getInt(35)
                                        animacoes.enunciado = jAnimacoes.getJSONArray(i).getInt(36)
                                        animacoes.tempoframe = jAnimacoes.getJSONArray(i).getDouble(37)
                                        animacoes.looping = jAnimacoes.getJSONArray(i).getInt(38)
                                        animacoes.contador = jAnimacoes.getJSONArray(i).getInt(39)
                                        animacoes.ativa_contador = jAnimacoes.getJSONArray(i).getInt(40)
                                        animacoes.nome_contador = jAnimacoes.getJSONArray(i).getInt(41)
                                        animacoes.mensagem_contador = jAnimacoes.getJSONArray(i).getInt(42)
                                        animacoes.mensagem_avalia = jAnimacoes.getJSONArray(i).getInt(43)
                                        animacoes.robo_servomotor = jAnimacoes.getJSONArray(i).getInt(44)
                                        animacoes.robo_direcao = jAnimacoes.getJSONArray(i).getInt(45)
                                        animacoes.robo_passo = jAnimacoes.getJSONArray(i).getInt(46)
                                        animacoes.robo_armazena = jAnimacoes.getJSONArray(i).getInt(47)
                                        animacoes.robo_acao = jAnimacoes.getJSONArray(i).getInt(48)
                                        animacoes.robo_intervalo = jAnimacoes.getJSONArray(i).getInt(49)
                                        animacoes.voto_funcao = jAnimacoes.getJSONArray(i).getInt(50)
                                        animacoes.duracao = jAnimacoes.getJSONArray(i).getDouble(51)
                                        animacoes.temporefresh = jAnimacoes.getJSONArray(i).getDouble(52)
                                        animacoes.ciclos = jAnimacoes.getJSONArray(i).getInt(53)
                                        animacoes.interrompe = jAnimacoes.getJSONArray(i).getInt(54)
                                        animacoes.programamodal = jAnimacoes.getJSONArray(i).getInt(55)
                                        animacoes.desativado = jAnimacoes.getJSONArray(i).getInt(56)
                                        animacoes.funcaoativa = jAnimacoes.getJSONArray(i).getInt(57)
                                        animacoes.mensagem_desativado = jAnimacoes.getJSONArray(i).getInt(58)
                                        animacoes.anima_anterior = jAnimacoes.getJSONArray(i).getInt(59)
                                        animacoes.invisivel = jAnimacoes.getJSONArray(i).getInt(60)
                                        animacoes.avancaraoencerrar = jAnimacoes.getJSONArray(i).getInt(61)
                                        animacoes.visivelaoexecutar = jAnimacoes.getJSONArray(i).getInt(62)
                                        animacoes.indice_speech = jAnimacoes.getJSONArray(i).getInt(63)
                                        animacoes.inicia_invisivel = jAnimacoes.getJSONArray(i).getInt(64)
                                        animacoes.robo_tempointer = jAnimacoes.getJSONArray(i).getInt(65)
                                        animacoes.som_posicao = jAnimacoes.getJSONArray(i).getInt(66)
                                        animacoes.anima_flash = jAnimacoes.getJSONArray(i).getInt(67)
                                        animacoes.rotaciona = jAnimacoes.getJSONArray(i).getInt(68)
                                        animacoes.som_inicio_ani = jAnimacoes.getJSONArray(i).getInt(69)
                                        animacoes.som_fim_anima = jAnimacoes.getJSONArray(i).getInt(70)
                                        animacoes.duracao_som = jAnimacoes.getJSONArray(i).getLong(71)
                                        animacoes.executar_apos = jAnimacoes.getJSONArray(i).getInt(72)
                                        animacoes.invisivel_apos = jAnimacoes.getJSONArray(i).getInt(73)
                                        animacoes.arquivo_externo = jAnimacoes.getJSONArray(i).getString(74)
                                        animacoes.identificador = jAnimacoes.getJSONArray(i).getString(75)
                                        animacoes.projeto_vcnet = jAnimacoes.getJSONArray(i).getString(76)
                                        animacoes.localiza_anonimo = jAnimacoes.getJSONArray(i).getInt(77)
                                        animacoes.compartilha = jAnimacoes.getJSONArray(i).getInt(78)

                                        var classAnimacoes = ClassAnimacoes()
                                        classAnimacoes.animacoes = animacoes

                                        aplicacao!!.classAnimacoes.add(classAnimacoes)
                                    }
                                }
                                
                                if (jLinhas != null && jLinhas.length() > 0) {
                                    var linhas: Linhas
                                    for (i in 0 until jLinhas.length()) {
                                        linhas = Linhas()
                                        linhas.id_tela = jLinhas.getJSONArray(i).getLong(0)
                                        linhas.tela = jLinhas.getJSONArray(i).getString(1)
                                        linhas.indice = jLinhas.getJSONArray(i).getInt(2)
                                        linhas.cor = jLinhas.getJSONArray(i).getInt(3)
                                        linhas.estilo = jLinhas.getJSONArray(i).getInt(4)
                                        linhas.espessura = jLinhas.getJSONArray(i).getInt(5)
                                        linhas.x1 = UtilClass.twipToPixel(jLinhas.getJSONArray(i).getInt(6))
                                        linhas.y1 = UtilClass.twipToPixel(jLinhas.getJSONArray(i).getInt(7))
                                        linhas.x2 = UtilClass.twipToPixel(jLinhas.getJSONArray(i).getInt(8))
                                        linhas.y2 = UtilClass.twipToPixel(jLinhas.getJSONArray(i).getInt(9))

                                        var classLinhas = ClassLinhas()
                                        classLinhas.linhas = linhas

                                        aplicacao!!.classLinhas.add(classLinhas)
                                    }
                                }

                                if (jArrastar != null && jLinhas.length() > 0 && jSoltar != null && jSoltar.length() > 0) {
                                    var arrastar: Arrastar
                                    for (i in 0 until jArrastar.length()) {
                                        arrastar = Arrastar()
                                        arrastar.id_tela = jArrastar.getJSONArray(i).getLong(0)
                                        arrastar.tela = jArrastar.getJSONArray(i).getString(1)
                                        arrastar.indice = jArrastar.getJSONArray(i).getInt(2)
                                        arrastar.texto = jArrastar.getJSONArray(i).getString(3)
                                        arrastar.cor = jArrastar.getJSONArray(i).getInt(4)
                                        arrastar.tamanho = jArrastar.getJSONArray(i).getInt(5)
                                        arrastar.topo = UtilClass.twipToPixel(jArrastar.getJSONArray(i).getInt(6))
                                        arrastar.esquerdo = UtilClass.twipToPixel(jArrastar.getJSONArray(i).getInt(7))
                                        arrastar.largura = UtilClass.twipToPixel(jArrastar.getJSONArray(i).getInt(8))
                                        arrastar.altura = UtilClass.twipToPixel(jArrastar.getJSONArray(i).getInt(9))
                                        arrastar.fonte = jArrastar.getJSONArray(i).getString(10)
                                        arrastar.enunciado = jArrastar.getJSONArray(i).getInt(11)
                                        arrastar.selecionarclic = jArrastar.getJSONArray(i).getInt(12)
                                        arrastar.cor_borda_interna1 = jArrastar.getJSONArray(i).getInt(13)
                                        arrastar.cor_borda_interna2 = jArrastar.getJSONArray(i).getInt(14)
                                        arrastar.estilo_borda_interna = jArrastar.getJSONArray(i).getInt(15)
                                        arrastar.espessura_borda_interna = jArrastar.getJSONArray(i).getInt(16)
                                        arrastar.cor_borda_central = jArrastar.getJSONArray(i).getInt(17)
                                        arrastar.cor_borda_externa1 = jArrastar.getJSONArray(i).getInt(18)
                                        arrastar.cor_borda_externa2 = jArrastar.getJSONArray(i).getInt(19)
                                        arrastar.estilo_borda_externa = jArrastar.getJSONArray(i).getInt(20)
                                        arrastar.espessura_borda_externa = jArrastar.getJSONArray(i).getInt(21)
                                        arrastar.cor_fundo = jArrastar.getJSONArray(i).getInt(22)
                                        arrastar.fonte3d = jArrastar.getJSONArray(i).getInt(23)
                                        arrastar.espessura_fonte3d = jArrastar.getJSONArray(i).getInt(24)
                                        arrastar.cor_contorno = jArrastar.getJSONArray(i).getInt(25)
                                        arrastar.cor_sombra = jArrastar.getJSONArray(i).getInt(26)
                                        arrastar.espessura_borda = jArrastar.getJSONArray(i).getInt(27)
                                        arrastar.negrito = jArrastar.getJSONArray(i).getInt(28)

                                        var classArrastarSoltar = ClassArrastarSoltar()
                                        classArrastarSoltar.arrastar = arrastar

                                        aplicacao!!.classArrastarSoltar.add(classArrastarSoltar)
                                    }

                                    var soltar: Soltar
                                    for (i in 0 until jSoltar.length()) {
                                        soltar = Soltar()
                                        soltar.id_tela = jSoltar.getJSONArray(i).getLong(0)
                                        soltar.tela = jSoltar.getJSONArray(i).getString(1)
                                        soltar.indice = jSoltar.getJSONArray(i).getInt(2)
                                        soltar.texto = jSoltar.getJSONArray(i).getString(3)
                                        soltar.dica = jSoltar.getJSONArray(i).getInt(4)
                                        soltar.cor = jSoltar.getJSONArray(i).getInt(5)
                                        soltar.fonte = jSoltar.getJSONArray(i).getString(6)
                                        soltar.tamanho = jSoltar.getJSONArray(i).getInt(7)
                                        soltar.topo = UtilClass.twipToPixel(jSoltar.getJSONArray(i).getInt(8))
                                        soltar.esquerdo = UtilClass.twipToPixel(jSoltar.getJSONArray(i).getInt(9))
                                        soltar.largura = UtilClass.twipToPixel(jSoltar.getJSONArray(i).getInt(10))
                                        soltar.altura = UtilClass.twipToPixel(jSoltar.getJSONArray(i).getInt(11))
                                        soltar.som = jSoltar.getJSONArray(i).getInt(12)
                                        soltar.colar_errado = jSoltar.getJSONArray(i).getInt(13)
                                        soltar.cor_borda_interna1 = jSoltar.getJSONArray(i).getInt(14)
                                        soltar.cor_borda_interna2 = jSoltar.getJSONArray(i).getInt(15)
                                        soltar.estilo_borda_interna = jSoltar.getJSONArray(i).getInt(16)
                                        soltar.espessura_borda_interna = jSoltar.getJSONArray(i).getInt(17)
                                        soltar.cor_borda_central = jSoltar.getJSONArray(i).getInt(18)
                                        soltar.cor_borda_externa1 = jSoltar.getJSONArray(i).getInt(19)
                                        soltar.cor_borda_externa2 = jSoltar.getJSONArray(i).getInt(20)
                                        soltar.estilo_borda_externa = jSoltar.getJSONArray(i).getInt(21)
                                        soltar.espessura_borda_externa = jSoltar.getJSONArray(i).getInt(22)
                                        soltar.cor_fundo = jSoltar.getJSONArray(i).getInt(23)
                                        soltar.fonte3d = jSoltar.getJSONArray(i).getInt(24)
                                        soltar.espessura_fonte3d = jSoltar.getJSONArray(i).getInt(25)
                                        soltar.cor_contorno = jSoltar.getJSONArray(i).getInt(26)
                                        soltar.cor_sombra = jSoltar.getJSONArray(i).getInt(27)
                                        soltar.espessura_borda = jSoltar.getJSONArray(i).getInt(28)
                                        soltar.negrito = jSoltar.getJSONArray(i).getInt(29)

                                        var j = 0
                                        while (j < aplicacao!!.classArrastarSoltar.size) {
                                            if (aplicacao!!.classArrastarSoltar[j].arrastar != null && aplicacao!!.classArrastarSoltar[j].arrastar!!.indice == soltar.indice) {
                                                aplicacao!!.classArrastarSoltar[j].soltar = soltar
                                                j = aplicacao!!.classArrastarSoltar.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (jArrastarImagens != null && jArrastarImagens.length() > 0 && jSoltarImagens != null && jSoltarImagens.length() > 0) {
                                    var arrastarImagens: ArrastarImagens
                                    for (i in 0 until jArrastarImagens.length()) {
                                        arrastarImagens = ArrastarImagens()
                                        arrastarImagens.id_tela = jArrastarImagens.getJSONArray(i).getLong(0)
                                        arrastarImagens.tela = jArrastarImagens.getJSONArray(i).getString(1)
                                        arrastarImagens.indice = jArrastarImagens.getJSONArray(i).getInt(2)
                                        arrastarImagens.borda = jArrastarImagens.getJSONArray(i).getInt(3)
                                        arrastarImagens.topo = UtilClass.twipToPixel(jArrastarImagens.getJSONArray(i).getInt(4))
                                        arrastarImagens.esquerdo = UtilClass.twipToPixel(jArrastarImagens.getJSONArray(i).getInt(5))
                                        arrastarImagens.largura = UtilClass.twipToPixel(jArrastarImagens.getJSONArray(i).getInt(6))
                                        arrastarImagens.altura = UtilClass.twipToPixel(jArrastarImagens.getJSONArray(i).getInt(7))
                                        arrastarImagens.efeito = jArrastarImagens.getJSONArray(i).getInt(8)
                                        arrastarImagens.delay = jArrastarImagens.getJSONArray(i).getInt(9)
                                        arrastarImagens.cor_borda_interna1 = jArrastarImagens.getJSONArray(i).getInt(10)
                                        arrastarImagens.cor_borda_interna2 = jArrastarImagens.getJSONArray(i).getInt(11)
                                        arrastarImagens.estilo_borda_interna = jArrastarImagens.getJSONArray(i).getInt(12)
                                        arrastarImagens.espessura_borda_interna = jArrastarImagens.getJSONArray(i).getInt(13)
                                        arrastarImagens.cor_borda_central = jArrastarImagens.getJSONArray(i).getInt(14)
                                        arrastarImagens.cor_borda_externa1 = jArrastarImagens.getJSONArray(i).getInt(15)
                                        arrastarImagens.cor_borda_externa2 = jArrastarImagens.getJSONArray(i).getInt(16)
                                        arrastarImagens.estilo_borda_externa = jArrastarImagens.getJSONArray(i).getInt(17)
                                        arrastarImagens.espessura_borda_externa = jArrastarImagens.getJSONArray(i).getInt(18)
                                        arrastarImagens.cor_fundo = jArrastarImagens.getJSONArray(i).getInt(19)
                                        arrastarImagens.granulacao = jArrastarImagens.getJSONArray(i).getInt(20)
                                        arrastarImagens.transparente = jArrastarImagens.getJSONArray(i).getInt(21)
                                        arrastarImagens.cor_transparente = jArrastarImagens.getJSONArray(i).getInt(22)
                                        arrastarImagens.imagem = jArrastarImagens.getJSONArray(i).getInt(23)
                                        arrastarImagens.enunciado = jArrastarImagens.getJSONArray(i).getInt(24)
                                        arrastarImagens.selecionarclic = jArrastarImagens.getJSONArray(i).getInt(25)
                                        arrastarImagens.angulo = jArrastarImagens.getJSONArray(i).getInt(26)

                                        var classArrastarSoltarImagens = ClassArrastarSoltarImagens()
                                        classArrastarSoltarImagens.arrastarImagens = arrastarImagens

                                        aplicacao!!.classArrastarSoltarImagens.add(classArrastarSoltarImagens)
                                    }

                                    var soltarImagens: SoltarImagens
                                    for (i in 0 until jSoltarImagens.length()) {
                                        soltarImagens = SoltarImagens()
                                        soltarImagens.id_tela = jSoltarImagens.getJSONArray(i).getLong(0)
                                        soltarImagens.tela = jSoltarImagens.getJSONArray(i).getString(1)
                                        soltarImagens.indice = jSoltarImagens.getJSONArray(i).getInt(2)
                                        soltarImagens.imagem = jSoltarImagens.getJSONArray(i).getInt(3)
                                        soltarImagens.borda = jSoltarImagens.getJSONArray(i).getInt(4)
                                        soltarImagens.dica = jSoltarImagens.getJSONArray(i).getInt(5)
                                        soltarImagens.som = jSoltarImagens.getJSONArray(i).getInt(6)
                                        soltarImagens.topo = UtilClass.twipToPixel(jSoltarImagens.getJSONArray(i).getInt(7))
                                        soltarImagens.esquerdo = UtilClass.twipToPixel(jSoltarImagens.getJSONArray(i).getInt(8))
                                        soltarImagens.largura = UtilClass.twipToPixel(jSoltarImagens.getJSONArray(i).getInt(9))
                                        soltarImagens.altura = UtilClass.twipToPixel(jSoltarImagens.getJSONArray(i).getInt(10))
                                        soltarImagens.efeito = jSoltarImagens.getJSONArray(i).getInt(11)
                                        soltarImagens.delay = jSoltarImagens.getJSONArray(i).getInt(12)
                                        soltarImagens.cor_borda_interna1 = jSoltarImagens.getJSONArray(i).getInt(13)
                                        soltarImagens.cor_borda_interna2 = jSoltarImagens.getJSONArray(i).getInt(14)
                                        soltarImagens.estilo_borda_interna = jSoltarImagens.getJSONArray(i).getInt(15)
                                        soltarImagens.espessura_borda_interna = jSoltarImagens.getJSONArray(i).getInt(16)
                                        soltarImagens.cor_borda_central = jSoltarImagens.getJSONArray(i).getInt(17)
                                        soltarImagens.cor_borda_externa1 = jSoltarImagens.getJSONArray(i).getInt(18)
                                        soltarImagens.cor_borda_externa2 = jSoltarImagens.getJSONArray(i).getInt(19)
                                        soltarImagens.estilo_borda_externa = jSoltarImagens.getJSONArray(i).getInt(20)
                                        soltarImagens.espessura_borda_externa = jSoltarImagens.getJSONArray(i).getInt(21)
                                        soltarImagens.cor_fundo = jSoltarImagens.getJSONArray(i).getInt(22)
                                        soltarImagens.granulacao = jSoltarImagens.getJSONArray(i).getInt(23)
                                        soltarImagens.transparente = jSoltarImagens.getJSONArray(i).getInt(24)
                                        soltarImagens.cor_transparente = jSoltarImagens.getJSONArray(i).getInt(25)
                                        soltarImagens.colar_errado = jSoltarImagens.getJSONArray(i).getInt(26)
                                        soltarImagens.angulo = jSoltarImagens.getJSONArray(i).getInt(27)

                                        var j = 0
                                        while (j < aplicacao!!.classArrastarSoltarImagens.size) {
                                            if (aplicacao!!.classArrastarSoltarImagens[j].arrastarImagens != null && aplicacao!!.classArrastarSoltarImagens[j].arrastarImagens!!.indice == soltarImagens.indice) {
                                                aplicacao!!.classArrastarSoltarImagens[j].soltarImagens = soltarImagens
                                                j = aplicacao!!.classArrastarSoltarImagens.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (jArrastarN != null && jArrastarN.length() > 0 && jSoltarN != null && jSoltarN.length() > 0) {
                                    var arrastarN: ArrastarN
                                    for (i in 0 until jArrastarN.length()) {
                                        arrastarN = ArrastarN()
                                        arrastarN.id_tela = jArrastarN.getJSONArray(i).getLong(0)
                                        arrastarN.tela = jArrastarN.getJSONArray(i).getString(1)
                                        arrastarN.indice = jArrastarN.getJSONArray(i).getInt(2)
                                        arrastarN.indice_obj = jArrastarN.getJSONArray(i).getInt(3)
                                        arrastarN.borda = jArrastarN.getJSONArray(i).getInt(4)
                                        arrastarN.topo = UtilClass.twipToPixel(jArrastarN.getJSONArray(i).getInt(5))
                                        arrastarN.esquerdo = UtilClass.twipToPixel(jArrastarN.getJSONArray(i).getInt(6))
                                        arrastarN.largura = UtilClass.twipToPixel(jArrastarN.getJSONArray(i).getInt(7))
                                        arrastarN.altura = UtilClass.twipToPixel(jArrastarN.getJSONArray(i).getInt(8))
                                        arrastarN.efeito = jArrastarN.getJSONArray(i).getInt(9)
                                        arrastarN.delay = jArrastarN.getJSONArray(i).getInt(10)
                                        arrastarN.cor_borda_interna1 = jArrastarN.getJSONArray(i).getInt(11)
                                        arrastarN.cor_borda_interna2 = jArrastarN.getJSONArray(i).getInt(12)
                                        arrastarN.estilo_borda_interna = jArrastarN.getJSONArray(i).getInt(13)
                                        arrastarN.espessura_borda_interna = jArrastarN.getJSONArray(i).getInt(14)
                                        arrastarN.cor_borda_central = jArrastarN.getJSONArray(i).getInt(15)
                                        arrastarN.cor_borda_externa1 = jArrastarN.getJSONArray(i).getInt(16)
                                        arrastarN.cor_borda_externa2 = jArrastarN.getJSONArray(i).getInt(17)
                                        arrastarN.estilo_borda_externa = jArrastarN.getJSONArray(i).getInt(18)
                                        arrastarN.espessura_borda_externa = jArrastarN.getJSONArray(i).getInt(19)
                                        arrastarN.cor_fundo = jArrastarN.getJSONArray(i).getInt(20)
                                        arrastarN.granulacao = jArrastarN.getJSONArray(i).getInt(21)
                                        arrastarN.transparente = jArrastarN.getJSONArray(i).getInt(22)
                                        arrastarN.cor_transparente = jArrastarN.getJSONArray(i).getInt(23)
                                        arrastarN.imagem = jArrastarN.getJSONArray(i).getInt(24)
                                        arrastarN.enunciado = jArrastarN.getJSONArray(i).getInt(25)
                                        arrastarN.selecionarclic = jArrastarN.getJSONArray(i).getInt(26)
                                        arrastarN.imagem_soltar = jArrastarN.getJSONArray(i).getInt(27)
                                        arrastarN.orfa = jArrastarN.getJSONArray(i).getInt(28)
                                        arrastarN.angulo = jArrastarN.getJSONArray(i).getInt(29)

                                        var achou = false

                                        for (j in 0 until aplicacao!!.classArrastarSoltarN.size) {
                                            if (aplicacao!!.classArrastarSoltarN[j].arrastarN.size > 0 && aplicacao!!.classArrastarSoltarN[j].arrastarN[0].indice == arrastarN.indice) {
                                                aplicacao!!.classArrastarSoltarN[j].arrastarN.add(arrastarN)
                                                achou = true
                                            }
                                        }

                                        if (!achou) {
                                            var classArrastarSoltarN = ClassArrastarSoltarN()
                                            classArrastarSoltarN.arrastarN.add(arrastarN)

                                            aplicacao!!.classArrastarSoltarN.add(classArrastarSoltarN)
                                        }
                                    }

                                    var soltarN: SoltarN
                                    for (i in 0 until jSoltar.length()) {
                                        soltarN = SoltarN()
                                        soltarN.id_tela = jSoltarN.getJSONArray(i).getLong(0)
                                        soltarN.tela = jSoltarN.getJSONArray(i).getString(1)
                                        soltarN.indice = jSoltarN.getJSONArray(i).getInt(2)
                                        soltarN.imagem = jSoltarN.getJSONArray(i).getInt(3)
                                        soltarN.borda = jSoltarN.getJSONArray(i).getInt(4)
                                        soltarN.dica = jSoltarN.getJSONArray(i).getInt(5)
                                        soltarN.som = jSoltarN.getJSONArray(i).getInt(6)
                                        soltarN.topo = UtilClass.twipToPixel(jSoltarN.getJSONArray(i).getInt(7))
                                        soltarN.esquerdo = UtilClass.twipToPixel(jSoltarN.getJSONArray(i).getInt(8))
                                        soltarN.largura = UtilClass.twipToPixel(jSoltarN.getJSONArray(i).getInt(9))
                                        soltarN.altura = UtilClass.twipToPixel(jSoltarN.getJSONArray(i).getInt(10))
                                        soltarN.efeito = jSoltarN.getJSONArray(i).getInt(11)
                                        soltarN.delay = jSoltarN.getJSONArray(i).getInt(12)
                                        soltarN.cor_borda_interna1 = jSoltarN.getJSONArray(i).getInt(13)
                                        soltarN.cor_borda_interna2 = jSoltarN.getJSONArray(i).getInt(14)
                                        soltarN.estilo_borda_interna = jSoltarN.getJSONArray(i).getInt(15)
                                        soltarN.espessura_borda_interna = jSoltarN.getJSONArray(i).getInt(16)
                                        soltarN.cor_borda_central = jSoltarN.getJSONArray(i).getInt(17)
                                        soltarN.cor_borda_externa1 = jSoltarN.getJSONArray(i).getInt(18)
                                        soltarN.cor_borda_externa2 = jSoltarN.getJSONArray(i).getInt(19)
                                        soltarN.estilo_borda_externa = jSoltarN.getJSONArray(i).getInt(20)
                                        soltarN.espessura_borda_externa = jSoltarN.getJSONArray(i).getInt(21)
                                        soltarN.cor_fundo = jSoltarN.getJSONArray(i).getInt(22)
                                        soltarN.granulacao = jSoltarN.getJSONArray(i).getInt(23)
                                        soltarN.transparente = jSoltarN.getJSONArray(i).getInt(24)
                                        soltarN.cor_transparente = jSoltarN.getJSONArray(i).getInt(25)
                                        soltarN.colar_errado = jSoltarN.getJSONArray(i).getInt(26)
                                        soltarN.contador = jSoltarN.getJSONArray(i).getInt(27)
                                        soltarN.angulo = jSoltarN.getJSONArray(i).getInt(28)

                                        var j = 0
                                        while (j < aplicacao!!.classArrastarSoltarN.size) {
                                            if (aplicacao!!.classArrastarSoltarN[j].arrastarN.size > 0 && aplicacao!!.classArrastarSoltarN[j].arrastarN[0].indice == soltarN.indice) {
                                                aplicacao!!.classArrastarSoltarN[j].soltarN = soltarN
                                                j = aplicacao!!.classArrastarSoltarN.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (jGirasEfeitos != null && jGirasEfeitos.length() > 0) {
                                    var girasEfeitos: GirasEfeitos
                                    for (i in 0 until jGirasEfeitos.length()) {
                                        girasEfeitos = GirasEfeitos()
                                        girasEfeitos.id_tela = jGirasEfeitos.getJSONArray(i).getLong(0)
                                        girasEfeitos.tela = jGirasEfeitos.getJSONArray(i).getString(1)
                                        girasEfeitos.indice = jGirasEfeitos.getJSONArray(i).getInt(2)
                                        girasEfeitos.resposta = jGirasEfeitos.getJSONArray(i).getInt(3)
                                        girasEfeitos.topo = UtilClass.twipToPixel(jGirasEfeitos.getJSONArray(i).getInt(4))
                                        girasEfeitos.esquerdo = UtilClass.twipToPixel(jGirasEfeitos.getJSONArray(i).getInt(5))
                                        girasEfeitos.largura = UtilClass.twipToPixel(jGirasEfeitos.getJSONArray(i).getInt(6))
                                        girasEfeitos.altura = UtilClass.twipToPixel(jGirasEfeitos.getJSONArray(i).getInt(7))
                                        girasEfeitos.efeito = jGirasEfeitos.getJSONArray(i).getInt(8)
                                        girasEfeitos.delay = jGirasEfeitos.getJSONArray(i).getInt(9)
                                        girasEfeitos.cor_borda_interna1 = jGirasEfeitos.getJSONArray(i).getInt(10)
                                        girasEfeitos.cor_borda_interna2 = jGirasEfeitos.getJSONArray(i).getInt(11)
                                        girasEfeitos.estilo_borda_interna = jGirasEfeitos.getJSONArray(i).getInt(12)
                                        girasEfeitos.espessura_borda_interna = jGirasEfeitos.getJSONArray(i).getInt(13)
                                        girasEfeitos.cor_borda_central = jGirasEfeitos.getJSONArray(i).getInt(14)
                                        girasEfeitos.cor_borda_externa1 = jGirasEfeitos.getJSONArray(i).getInt(15)
                                        girasEfeitos.cor_borda_externa2 = jGirasEfeitos.getJSONArray(i).getInt(16)
                                        girasEfeitos.estilo_borda_externa = jGirasEfeitos.getJSONArray(i).getInt(17)
                                        girasEfeitos.espessura_borda_externa = jGirasEfeitos.getJSONArray(i).getInt(18)
                                        girasEfeitos.cor_fundo = jGirasEfeitos.getJSONArray(i).getInt(19)
                                        girasEfeitos.granulacao = jGirasEfeitos.getJSONArray(i).getInt(20)
                                        girasEfeitos.transparente = jGirasEfeitos.getJSONArray(i).getInt(21)
                                        girasEfeitos.cor_transparente = jGirasEfeitos.getJSONArray(i).getInt(22)
                                        girasEfeitos.imagem = jGirasEfeitos.getJSONArray(i).getInt(23)
                                        girasEfeitos.enunciado = jGirasEfeitos.getJSONArray(i).getInt(24)
                                        girasEfeitos.fig1 = jGirasEfeitos.getJSONArray(i).getInt(25)
                                        girasEfeitos.borda_central = jGirasEfeitos.getJSONArray(i).getInt(26)

                                        var classGirasEfeitos = ClassGirasEfeitos()
                                        classGirasEfeitos.girasEfeitos = girasEfeitos

                                        aplicacao!!.classGirasEfeitos.add(classGirasEfeitos)
                                    }
                                }

                                if (jPreenchimentos != null && jPreenchimentos.length() > 0) {
                                    var preenchimentos: Preenchimentos
                                    for (i in 0 until jPreenchimentos.length()) {
                                        preenchimentos = Preenchimentos()
                                        preenchimentos.id_tela = jPreenchimentos.getJSONArray(i).getLong(0)
                                        preenchimentos.tela = jPreenchimentos.getJSONArray(i).getString(1)
                                        preenchimentos.indice = jPreenchimentos.getJSONArray(i).getInt(2)
                                        preenchimentos.cor = jPreenchimentos.getJSONArray(i).getInt(3)
                                        preenchimentos.tamanho = jPreenchimentos.getJSONArray(i).getInt(4)
                                        preenchimentos.topo = UtilClass.twipToPixel(jPreenchimentos.getJSONArray(i).getInt(5))
                                        preenchimentos.esquerdo = UtilClass.twipToPixel(jPreenchimentos.getJSONArray(i).getInt(6))
                                        preenchimentos.largura = UtilClass.twipToPixel(jPreenchimentos.getJSONArray(i).getInt(7))
                                        preenchimentos.altura = UtilClass.twipToPixel(jPreenchimentos.getJSONArray(i).getInt(8))
                                        preenchimentos.cor_fundo = jPreenchimentos.getJSONArray(i).getInt(9)
                                        preenchimentos.estilo_borda = jPreenchimentos.getJSONArray(i).getInt(10)
                                        preenchimentos.ignora_branco = jPreenchimentos.getJSONArray(i).getInt(11)
                                        preenchimentos.maximo = jPreenchimentos.getJSONArray(i).getInt(12)
                                        preenchimentos.caixa = jPreenchimentos.getJSONArray(i).getInt(13)
                                        preenchimentos.preenchimento = jPreenchimentos.getJSONArray(i).getInt(14)
                                        preenchimentos.enunciado = jPreenchimentos.getJSONArray(i).getInt(15)
                                        preenchimentos.fonte = jPreenchimentos.getJSONArray(i).getString(16)
                                        preenchimentos.transparente = jPreenchimentos.getJSONArray(i).getInt(17)
                                        preenchimentos.negrito = jPreenchimentos.getJSONArray(i).getInt(18)
                                        preenchimentos.italico = jPreenchimentos.getJSONArray(i).getInt(19)
                                        preenchimentos.multilinha = jPreenchimentos.getJSONArray(i).getInt(20)
                                        preenchimentos.caixadig = jPreenchimentos.getJSONArray(i).getInt(21)

                                        var classPreenchimentos = ClassPreenchimentos()
                                        classPreenchimentos.preenchimentos = preenchimentos

                                        aplicacao!!.classPreenchimentos.add(classPreenchimentos)
                                    }
                                }

                                if (jDissertativa != null && jDissertativa.length() > 0) {
                                    var dissertativa: Dissertativa
                                    for (i in 0 until jDissertativa.length()) {
                                        dissertativa = Dissertativa()
                                        dissertativa.id_tela = jDissertativa.getJSONArray(i).getLong(0)
                                        dissertativa.tela = jDissertativa.getJSONArray(i).getString(1)
                                        dissertativa.indice = jDissertativa.getJSONArray(i).getInt(2)
                                        dissertativa.enunciado = jDissertativa.getJSONArray(i).getInt(3)
                                        dissertativa.topo = UtilClass.twipToPixel(jDissertativa.getJSONArray(i).getInt(4))
                                        dissertativa.esquerdo = UtilClass.twipToPixel(jDissertativa.getJSONArray(i).getInt(5))
                                        dissertativa.largura = UtilClass.twipToPixel(jDissertativa.getJSONArray(i).getInt(6))
                                        dissertativa.altura = UtilClass.twipToPixel(jDissertativa.getJSONArray(i).getInt(7))
                                        dissertativa.cor_fundo = jDissertativa.getJSONArray(i).getInt(8)
                                        dissertativa.borda = jDissertativa.getJSONArray(i).getInt(9)
                                        dissertativa.barrarolamento = jDissertativa.getJSONArray(i).getInt(10)
                                        dissertativa.transparente = jDissertativa.getJSONArray(i).getInt(11)
                                        dissertativa.fonte = jDissertativa.getJSONArray(i).getString(12)
                                        dissertativa.tamanho = jDissertativa.getJSONArray(i).getDouble(13)
                                        dissertativa.cor = jDissertativa.getJSONArray(i).getInt(14)
                                        dissertativa.caixadig = jDissertativa.getJSONArray(i).getInt(15)

                                        var classDissertativa = ClassDissertativa()
                                        classDissertativa.dissertativa = dissertativa

                                        aplicacao!!.classDissertativa.add(classDissertativa)
                                    }
                                }

                                if (jTestesVest != null && jTestesVest.length() > 0) {
                                    var testesVest: TestesVest
                                    for (i in 0 until jTestesVest.length()) {
                                        testesVest = TestesVest()
                                        testesVest.id_tela = jTestesVest.getJSONArray(i).getLong(0)
                                        testesVest.tela = jTestesVest.getJSONArray(i).getString(1)
                                        testesVest.indice = jTestesVest.getJSONArray(i).getInt(2)
                                        testesVest.resposta = jTestesVest.getJSONArray(i).getInt(3)
                                        testesVest.enunciado = jTestesVest.getJSONArray(i).getInt(4)
                                        testesVest.imagem = jTestesVest.getJSONArray(i).getInt(5)
                                        testesVest.distancia_h = jTestesVest.getJSONArray(i).getInt(6)
                                        testesVest.unicode = jTestesVest.getJSONArray(i).getInt(7)
                                        testesVest.centraliza_sel = jTestesVest.getJSONArray(i).getInt(8)
                                        testesVest.alin_par = jTestesVest.getJSONArray(i).getInt(9)
                                        testesVest.aumenta_par = jTestesVest.getJSONArray(i).getInt(10)
                                        testesVest.selecao_x = jTestesVest.getJSONArray(i).getInt(11)

                                        var classTestesVest = ClassTestesVest()
                                        classTestesVest.testesVest = testesVest

                                        aplicacao!!.classTestesVest.add(classTestesVest)
                                    }
                                }

                                if (jTestesVestAlt != null && jTestesVestAlt.length() > 0) {
                                    var testesVestAlt: TestesVestAlt
                                    for (i in 0 until jTestesVestAlt.length()) {
                                        testesVestAlt = TestesVestAlt()
                                        testesVestAlt.id_tela = jTestesVestAlt.getJSONArray(i).getLong(0)
                                        testesVestAlt.tela = jTestesVestAlt.getJSONArray(i).getString(1)
                                        testesVestAlt.indice = jTestesVestAlt.getJSONArray(i).getInt(2)
                                        testesVestAlt.indice_obj = jTestesVestAlt.getJSONArray(i).getInt(3)
                                        testesVestAlt.topo = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(4))
                                        testesVestAlt.esquerdo = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(5))
                                        testesVestAlt.largura = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(6))
                                        testesVestAlt.altura = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(7))
                                        testesVestAlt.alternativa = jTestesVestAlt.getJSONArray(i).getString(8)
                                        testesVestAlt.cor_fundo = jTestesVestAlt.getJSONArray(i).getInt(9)
                                        testesVestAlt.barrarolamento = jTestesVestAlt.getJSONArray(i).getInt(10)
                                        testesVestAlt.borda = jTestesVestAlt.getJSONArray(i).getInt(11)
                                        testesVestAlt.estilofundo = jTestesVestAlt.getJSONArray(i).getInt(12)
                                        testesVestAlt.arquivo = jTestesVestAlt.getJSONArray(i).getString(13)
                                        testesVestAlt.cor = jTestesVestAlt.getJSONArray(i).getInt(14)
                                        testesVestAlt.italico = jTestesVestAlt.getJSONArray(i).getInt(15)
                                        testesVestAlt.negrito = jTestesVestAlt.getJSONArray(i).getInt(16)
                                        testesVestAlt.tamanho = jTestesVestAlt.getJSONArray(i).getInt(17)
                                        testesVestAlt.fonte = jTestesVestAlt.getJSONArray(i).getString(18)
                                        testesVestAlt.largura_img = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(19))
                                        testesVestAlt.altura_img = UtilClass.twipToPixel(jTestesVestAlt.getJSONArray(i).getInt(20))
                                        testesVestAlt.fundo_img = jTestesVestAlt.getJSONArray(i).getInt(21)

                                        var j = 0
                                        while (j < aplicacao!!.classTestesVest.size) {
                                            if (aplicacao!!.classTestesVest[j].testesVest != null && testesVestAlt.indice == aplicacao!!.classTestesVest[j].testesVest!!.indice) {
                                                aplicacao!!.classTestesVest[j].testesVestAlt.add(testesVestAlt)
                                                j = aplicacao!!.classTestesVest.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                //sempre no final
                                if (jLiga1 != null && jLiga1.length() > 0 && jLiga2 != null && jLiga2.length() > 0) {//por causa da linha
                                    var liga1: Liga1
                                    for (i in 0 until jLiga1.length()) {
                                        liga1 = Liga1()
                                        liga1.id_tela = jLiga1.getJSONArray(i).getLong(0)
                                        liga1.tela = jLiga1.getJSONArray(i).getString(1)
                                        liga1.indice = jLiga1.getJSONArray(i).getInt(2)
                                        liga1.borda = jLiga1.getJSONArray(i).getInt(3)
                                        liga1.topo = UtilClass.twipToPixel(jLiga1.getJSONArray(i).getInt(4))
                                        liga1.esquerdo = UtilClass.twipToPixel(jLiga1.getJSONArray(i).getInt(5))
                                        liga1.altura = UtilClass.twipToPixel(jLiga1.getJSONArray(i).getInt(6))
                                        liga1.largura = UtilClass.twipToPixel(jLiga1.getJSONArray(i).getInt(7))
                                        liga1.efeito = jLiga1.getJSONArray(i).getInt(8)
                                        liga1.delay = jLiga1.getJSONArray(i).getInt(9)
                                        liga1.cor_borda_interna1 = jLiga1.getJSONArray(i).getInt(10)
                                        liga1.cor_borda_interna2 = jLiga1.getJSONArray(i).getInt(11)
                                        liga1.estilo_borda_interna = jLiga1.getJSONArray(i).getInt(12)
                                        liga1.espessura_borda_interna = jLiga1.getJSONArray(i).getInt(13)
                                        liga1.cor_borda_central = jLiga1.getJSONArray(i).getInt(14)
                                        liga1.cor_borda_externa1 = jLiga1.getJSONArray(i).getInt(15)
                                        liga1.cor_borda_externa2 = jLiga1.getJSONArray(i).getInt(16)
                                        liga1.estilo_borda_externa = jLiga1.getJSONArray(i).getInt(17)
                                        liga1.espessura_borda_externa = jLiga1.getJSONArray(i).getInt(18)
                                        liga1.cor_fundo = jLiga1.getJSONArray(i).getInt(19)
                                        liga1.granulacao = jLiga1.getJSONArray(i).getInt(20)
                                        liga1.transparente = jLiga1.getJSONArray(i).getInt(21)
                                        liga1.cor_transparente = jLiga1.getJSONArray(i).getInt(22)
                                        liga1.liga_anterior = jLiga1.getJSONArray(i).getInt(23)
                                        liga1.liga_primeiro = jLiga1.getJSONArray(i).getInt(24)
                                        liga1.cor_linha = jLiga1.getJSONArray(i).getInt(25)
                                        liga1.espessura_linha = jLiga1.getJSONArray(i).getInt(26)
                                        liga1.estilo_linha = jLiga1.getJSONArray(i).getInt(27)
                                        liga1.imagem = jLiga1.getJSONArray(i).getInt(28)
                                        liga1.som = jLiga1.getJSONArray(i).getInt(29)
                                        liga1.enunciado = jLiga1.getJSONArray(i).getInt(30)
                                        liga1.liga_errado = jLiga1.getJSONArray(i).getInt(31)
                                        liga1.clicar = jLiga1.getJSONArray(i).getInt(32)
                                        liga1.associasom_obj = jLiga1.getJSONArray(i).getInt(33)
                                        liga1.desabilita_liga2 = jLiga1.getJSONArray(i).getInt(34)
                                        liga1.angulo = jLiga1.getJSONArray(i).getInt(35)

                                        var classLigas = ClassLigas()
                                        classLigas.liga1 = liga1

                                        aplicacao!!.classLigas.add(classLigas)
                                    }

                                    var liga2: Liga2
                                    for (i in 0 until jLiga2.length()) {
                                        liga2 = Liga2()
                                        liga2.id_tela = jLiga2.getJSONArray(i).getLong(0)
                                        liga2.tela = jLiga2.getJSONArray(i).getString(1)
                                        liga2.indice = jLiga2.getJSONArray(i).getInt(2)
                                        liga2.imagem = jLiga2.getJSONArray(i).getInt(3)
                                        liga2.borda = jLiga2.getJSONArray(i).getInt(4)
                                        liga2.topo = UtilClass.twipToPixel(jLiga2.getJSONArray(i).getInt(5))
                                        liga2.esquerdo = UtilClass.twipToPixel(jLiga2.getJSONArray(i).getInt(6))
                                        liga2.altura = UtilClass.twipToPixel(jLiga2.getJSONArray(i).getInt(7))
                                        liga2.largura = UtilClass.twipToPixel(jLiga2.getJSONArray(i).getInt(8))
                                        liga2.som = jLiga2.getJSONArray(i).getInt(9)
                                        liga2.efeito = jLiga2.getJSONArray(i).getInt(10)
                                        liga2.delay = jLiga2.getJSONArray(i).getInt(11)
                                        liga2.cor_borda_interna1 = jLiga2.getJSONArray(i).getInt(12)
                                        liga2.cor_borda_interna2 = jLiga2.getJSONArray(i).getInt(13)
                                        liga2.estilo_borda_interna = jLiga2.getJSONArray(i).getInt(14)
                                        liga2.espessura_borda_interna = jLiga2.getJSONArray(i).getInt(15)
                                        liga2.cor_borda_central = jLiga2.getJSONArray(i).getInt(16)
                                        liga2.cor_borda_externa1 = jLiga2.getJSONArray(i).getInt(17)
                                        liga2.cor_borda_externa2 = jLiga2.getJSONArray(i).getInt(18)
                                        liga2.estilo_borda_externa = jLiga2.getJSONArray(i).getInt(19)
                                        liga2.espessura_borda_externa = jLiga2.getJSONArray(i).getInt(20)
                                        liga2.cor_fundo = jLiga2.getJSONArray(i).getInt(21)
                                        liga2.granulacao = jLiga2.getJSONArray(i).getInt(22)
                                        liga2.transparente = jLiga2.getJSONArray(i).getInt(23)
                                        liga2.cor_transparente = jLiga2.getJSONArray(i).getInt(24)
                                        liga2.liga_anterior = jLiga2.getJSONArray(i).getInt(25)
                                        liga2.liga_primeiro = jLiga2.getJSONArray(i).getInt(26)
                                        liga2.cor_linha = jLiga2.getJSONArray(i).getInt(27)
                                        liga2.espessura_linha = jLiga2.getJSONArray(i).getInt(28)
                                        liga2.estilo_linha = jLiga2.getJSONArray(i).getInt(29)
                                        liga2.associasom_obj = jLiga2.getJSONArray(i).getInt(30)
                                        liga2.angulo = jLiga2.getJSONArray(i).getInt(31)

                                        var j = 0
                                        while (j < aplicacao!!.classLigas.size) {
                                            if (aplicacao!!.classLigas[j].liga1 != null && aplicacao!!.classLigas[j].liga1!!.indice == liga2.indice) {
                                                aplicacao!!.classLigas[j].liga2 = liga2
                                                j = aplicacao!!.classLigas.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                var arquivosBaixar = JSONArray()


                                if (jObjsom != null && jObjsom.length() > 0) {
                                    var objsom: Objsom
                                    for (i in 0 until jObjsom.length()) {
                                        objsom = Objsom()
                                        objsom.id_tela = jObjsom.getJSONArray(i).getLong(0)
                                        objsom.tela = jObjsom.getJSONArray(i).getString(1)
                                        objsom.tipoobj = jObjsom.getJSONArray(i).getLong(2)
                                        objsom.indice = jObjsom.getJSONArray(i).getInt(3)
                                        objsom.arquivo = jObjsom.getJSONArray(i).getString(4)

                                        if (!File(UtilClass.getCaminhoArquivos(act!!) + UtilClass.nomeArquivo(objsom.arquivo)).exists()) {
                                            arquivosBaixar.put(objsom.arquivo)
                                        }

                                        if (objsom.tipoobj == UtilClass.TIPO_TELA) {
                                            aplicacao!!.classTela!!.objsom = objsom
                                        } else {
                                            var achou = false

                                            var j = 0
                                            while (j < aplicacao!!.classImagensEfeitos.size) {
                                                if (objsom.tipoobj == UtilClass.TIPO_IMAGEM_EFEITO && aplicacao!!.classImagensEfeitos[j].imagensEfeitos != null && objsom.indice == aplicacao!!.classImagensEfeitos[j].imagensEfeitos!!.indice) {
                                                    aplicacao!!.classImagensEfeitos[j].objsom = objsom
                                                    achou = true
                                                    j = aplicacao!!.classImagensEfeitos.size
                                                } else {
                                                    j++
                                                }
                                            }

                                            if (!achou) {
                                                j = 0
                                                while (j < aplicacao!!.classRotulos.size) {
                                                    if (objsom.tipoobj == UtilClass.TIPO_ROTULO && aplicacao!!.classRotulos[j].rotulos != null && objsom.indice == aplicacao!!.classRotulos[j].rotulos!!.indice) {
                                                        aplicacao!!.classRotulos[j].objsom = objsom
                                                        achou = true
                                                        j = aplicacao!!.classRotulos.size
                                                    } else {
                                                        j++
                                                    }
                                                }

                                                if (!achou) {
                                                    j = 0
                                                    while (j < aplicacao!!.classAnimacoes.size) {
                                                        if (objsom.tipoobj == UtilClass.TIPO_ANIMACAO && aplicacao!!.classAnimacoes[j].animacoes != null && objsom.indice == aplicacao!!.classAnimacoes[j].animacoes!!.indice) {
                                                            aplicacao!!.classAnimacoes[j].objsom = objsom
                                                            achou = true
                                                            j = aplicacao!!.classAnimacoes.size
                                                        } else {
                                                            j++
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (jObjimagem != null && jObjimagem.length() > 0) {
                                    var objimagem: Objimagem
                                    for (i in 0 until jObjimagem.length()) {
                                        objimagem = Objimagem()
                                        objimagem.id_tela = jObjimagem.getJSONArray(i).getLong(0)
                                        objimagem.tela = jObjimagem.getJSONArray(i).getString(1)
                                        objimagem.tipoobj = jObjimagem.getJSONArray(i).getLong(2)
                                        objimagem.indice = jObjimagem.getJSONArray(i).getInt(3)
                                        objimagem.posicao = jObjimagem.getJSONArray(i).getInt(4)
                                        objimagem.arquivo = jObjimagem.getJSONArray(i).getString(5)

                                        if (!File(UtilClass.getCaminhoArquivos(act!!) + UtilClass.nomeArquivo(objimagem.arquivo)).exists()) {
                                            arquivosBaixar.put(objimagem.arquivo)
                                        }

                                        if (objimagem.tipoobj == UtilClass.TIPO_TELA) {
                                            aplicacao!!.classTela!!.objimagem = objimagem
                                        } else {
                                            var achou = false

                                            var j = 0
                                            while (j < aplicacao!!.classImagensEfeitos.size) {
                                                if (objimagem.tipoobj == UtilClass.TIPO_IMAGEM_EFEITO && aplicacao!!.classImagensEfeitos[j].imagensEfeitos != null && objimagem.indice == aplicacao!!.classImagensEfeitos[j].imagensEfeitos!!.indice) {
                                                    aplicacao!!.classImagensEfeitos[j].objimagem = objimagem
                                                    achou = true
                                                    j = aplicacao!!.classImagensEfeitos.size
                                                } else {
                                                    j++
                                                }
                                            }

                                            if (!achou) {
                                                j = 0
                                                while (j < aplicacao!!.classAnimacoes.size) {
                                                    if (objimagem.tipoobj == UtilClass.TIPO_ANIMACAO && aplicacao!!.classAnimacoes[j].animacoes != null && objimagem.indice == aplicacao!!.classAnimacoes[j].animacoes!!.indice) {
                                                        aplicacao!!.classAnimacoes[j].objimagem.add(objimagem)
                                                        achou = true
                                                        j = aplicacao!!.classAnimacoes.size
                                                    } else {
                                                        j++
                                                    }
                                                }

                                                if (!achou) {
                                                    j = 0
                                                    while (j < aplicacao!!.classArrastarSoltarImagens.size) {
                                                        if (objimagem.tipoobj == UtilClass.TIPO_ARRASTAR_IMAGEM && aplicacao!!.classArrastarSoltarImagens[j].arrastarImagens != null && objimagem.indice == aplicacao!!.classArrastarSoltarImagens[j].arrastarImagens!!.indice) {
                                                            aplicacao!!.classArrastarSoltarImagens[j].objimagemArrastar = objimagem
                                                            achou = true
                                                            j = aplicacao!!.classArrastarSoltarImagens.size
                                                        } else if (objimagem.tipoobj == UtilClass.TIPO_SOLTAR_IMAGEM && aplicacao!!.classArrastarSoltarImagens[j].soltarImagens != null && objimagem.indice == aplicacao!!.classArrastarSoltarImagens[j].soltarImagens!!.indice) {
                                                            aplicacao!!.classArrastarSoltarImagens[j].objimagemSoltar = objimagem
                                                            achou = true
                                                            j = aplicacao!!.classArrastarSoltarImagens.size
                                                        } else {
                                                            j++
                                                        }
                                                    }

                                                    if (!achou) {
                                                        j = 0
                                                        while (j < aplicacao!!.classArrastarSoltarN.size) {
                                                            if (objimagem.tipoobj == UtilClass.TIPO_ARRASTAR_N && aplicacao!!.classArrastarSoltarN[j].arrastarN.size > 0 && objimagem.indice == aplicacao!!.classArrastarSoltarN[j].arrastarN[0].indice) {
                                                                aplicacao!!.classArrastarSoltarN[j].objimagemArrastarN.add(objimagem)
                                                                achou = true
                                                                j = aplicacao!!.classArrastarSoltarN.size
                                                            } else if (objimagem.tipoobj == UtilClass.TIPO_SOLTAR_N && aplicacao!!.classArrastarSoltarN[j].soltarN != null && objimagem.indice == aplicacao!!.classArrastarSoltarN[j].soltarN!!.indice) {
                                                                aplicacao!!.classArrastarSoltarN[j].objimagemSoltarN = objimagem
                                                                achou = true
                                                                j = aplicacao!!.classArrastarSoltarN.size
                                                            } else {
                                                                j++
                                                            }
                                                        }

                                                        if (!achou) {
                                                            j = 0
                                                            while (j < aplicacao!!.classGirasEfeitos.size) {
                                                                if (objimagem.tipoobj == UtilClass.TIPO_GIRA && aplicacao!!.classGirasEfeitos[j].girasEfeitos != null && objimagem.indice == aplicacao!!.classGirasEfeitos[j].girasEfeitos!!.indice) {
                                                                    aplicacao!!.classGirasEfeitos[j].objimagem.add(objimagem)
                                                                    achou = true
                                                                    j = aplicacao!!.classGirasEfeitos.size
                                                                } else {
                                                                    j++
                                                                }
                                                            }

                                                            if (!achou) {
                                                                j = 0
                                                                while (j < aplicacao!!.classLigas.size) {
                                                                    if (objimagem.tipoobj == UtilClass.TIPO_LIGA1 && aplicacao!!.classLigas[j].liga1 != null && objimagem.indice == aplicacao!!.classLigas[j].liga1!!.indice) {
                                                                        aplicacao!!.classLigas[j].objimagemLiga1 = objimagem
                                                                        achou = true
                                                                        j = aplicacao!!.classLigas.size
                                                                    } else if (objimagem.tipoobj == UtilClass.TIPO_LIGA2 && aplicacao!!.classLigas[j].liga2 != null && objimagem.indice == aplicacao!!.classLigas[j].liga2!!.indice) {
                                                                        aplicacao!!.classLigas[j].objimagemLiga2 = objimagem
                                                                        achou = true
                                                                        j = aplicacao!!.classLigas.size
                                                                    } else {
                                                                        j++
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (jObjtexto != null && jObjtexto.length() > 0) {
                                    var objtexto: Objtexto
                                    for (i in 0 until jObjtexto.length()) {
                                        objtexto = Objtexto()
                                        objtexto.id_tela = jObjtexto.getJSONArray(i).getLong(0)
                                        objtexto.tela = jObjtexto.getJSONArray(i).getString(1)
                                        objtexto.indice = jObjtexto.getJSONArray(i).getInt(2)
                                        objtexto.tipoobj = jObjtexto.getJSONArray(i).getLong(3)
                                        objtexto.posicao = jObjtexto.getJSONArray(i).getInt(4)
                                        objtexto.texto = jObjtexto.getJSONArray(i).getString(5).replace("&#10;", "")

                                        var achou = false

                                        var j = 0
                                        while (j < aplicacao!!.classTextos.size) {
                                            if (objtexto.tipoobj == UtilClass.TIPO_TEXTO && aplicacao!!.classTextos[j].textos != null && objtexto.indice == aplicacao!!.classTextos[j].textos!!.indice) {
                                                aplicacao!!.classTextos[j].objtexto = objtexto
                                                achou = true
                                                j = aplicacao!!.classTextos.size

                                                if (aplicacao!!.configuracoes!!.con_proxy_link.isNotEmpty()) {
                                                    if (!File(UtilClass.getCaminhoArquivos(act!!) + UtilClass.nomeArquivo(objtexto.texto)).exists()) {
                                                        arquivosBaixar.put(objtexto.texto)
                                                    }
                                                }
                                            } else {
                                                j++
                                            }
                                        }

                                        if (!achou) {
                                            j = 0
                                            while (j < aplicacao!!.classRotulos.size) {
                                                if (objtexto.tipoobj == UtilClass.TIPO_ROTULO && aplicacao!!.classRotulos[j].rotulos != null && objtexto.indice == aplicacao!!.classRotulos[j].rotulos!!.indice) {
                                                    aplicacao!!.classRotulos[j].objtexto = objtexto
                                                    achou = true
                                                    j = aplicacao!!.classRotulos.size
                                                } else {
                                                    j++
                                                }
                                            }

                                            if (!achou) {
                                                j = 0
                                                while (j < aplicacao!!.classTestesVest.size) {
                                                    if (objtexto.tipoobj == UtilClass.TIPO_TESTE_VEST && aplicacao!!.classTestesVest[j].testesVest != null && objtexto.indice == aplicacao!!.classTestesVest[j].testesVest!!.indice) {
                                                        aplicacao!!.classTestesVest[j].objtexto.add(objtexto)
                                                        achou = true
                                                        j = aplicacao!!.classTestesVest.size
                                                    } else {
                                                        j++
                                                    }
                                                }
                                            }

                                            if (!achou) {
                                                j = 0
                                                while (j < aplicacao!!.classPreenchimentos.size) {
                                                    if (objtexto.tipoobj == UtilClass.TIPO_PREENCHIMENTO && aplicacao!!.classPreenchimentos[j].preenchimentos != null && objtexto.indice == aplicacao!!.classPreenchimentos[j].preenchimentos!!.indice) {
                                                        aplicacao!!.classPreenchimentos[j].objtexto.add(objtexto)
                                                        achou = true
                                                        j = aplicacao!!.classPreenchimentos.size
                                                    } else {
                                                        j++
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (jObjfilme != null && jObjfilme.length() > 0) {
                                    var objfilme: Objfilme
                                    for (i in 0 until jObjfilme.length()) {
                                        objfilme = Objfilme()
                                        objfilme.id_tela = jObjfilme.getJSONArray(i).getLong(0)
                                        objfilme.tela = jObjfilme.getJSONArray(i).getString(1)
                                        objfilme.tipoobj = jObjfilme.getJSONArray(i).getLong(2)
                                        objfilme.indice = jObjfilme.getJSONArray(i).getInt(3)
                                        objfilme.arquivo = jObjfilme.getJSONArray(i).getString(4)

                                        if (UtilClass.possuiProxy(aplicacao!!.configuracoes!!)) {
                                            if (!File(UtilClass.getCaminhoArquivos(act!!) + UtilClass.nomeArquivo(objfilme.arquivo)).exists()) {
                                                arquivosBaixar.put(objfilme.arquivo)
                                            }
                                        }

                                        var j = 0
                                        while (j < aplicacao!!.classFilmesTela.size) {
                                            if (objfilme.tipoobj == UtilClass.TIPO_FILMES_TELA && aplicacao!!.classFilmesTela[j].filmesTela != null && objfilme.indice == aplicacao!!.classFilmesTela[j].filmesTela!!.indice) {
                                                aplicacao!!.classFilmesTela[j].objfilme = objfilme
                                                j = aplicacao!!.classFilmesTela.size
                                            } else {
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (jObjlink != null && jObjlink.length() > 0) {
                                    var objlink: Objlink
                                    for (i in 0 until jObjlink.length()) {
                                        objlink = Objlink()
                                        objlink.id_tela = jObjlink.getJSONArray(i).getLong(0)
                                        objlink.tela = jObjlink.getJSONArray(i).getString(1)
                                        objlink.tipoobj = jObjlink.getJSONArray(i).getLong(2)
                                        objlink.indice = jObjlink.getJSONArray(i).getInt(3)
                                        objlink.link = jObjlink.getJSONArray(i).getString(4)
                                        objlink.linkretorno = jObjlink.getJSONArray(i).getLong(5)
                                        objlink.condicao = jObjlink.getJSONArray(i).getInt(6)
                                        objlink.id_tela_link = jObjlink.getJSONArray(i).getLong(7)

                                        var achou = false
                                        var j = 0
                                        if (objlink.tipoobj == UtilClass.TIPO_IMAGEM_EFEITO) {
                                            while (!achou && j < aplicacao!!.classImagensEfeitos.size) {
                                                if (objlink.indice == aplicacao!!.classImagensEfeitos[j].imagensEfeitos!!.indice) {
                                                    aplicacao!!.classImagensEfeitos[j].objlink = objlink
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objlink.tipoobj == UtilClass.TIPO_ROTULO) {
                                            while (!achou && j < aplicacao!!.classRotulos[j].rotulos!!.indice) {
                                                if (objlink.indice == aplicacao!!.classRotulos[j].rotulos!!.indice) {
                                                    aplicacao!!.classRotulos[j].objlink = objlink
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objlink.tipoobj == UtilClass.TIPO_ANIMACAO) {
                                            while (!achou && j < aplicacao!!.classAnimacoes[j].animacoes!!.indice) {
                                                if (objlink.indice == aplicacao!!.classAnimacoes[j].animacoes!!.indice) {
                                                    aplicacao!!.classAnimacoes[j].objlink = objlink
                                                    achou = true
                                                }
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (jObjenunciado != null && jObjenunciado.length() > 0) {
                                    var objenunciado: Objenunciado
                                    for (i in 0 until jObjenunciado.length()) {
                                        objenunciado = Objenunciado()
                                        objenunciado.id_tela = jObjenunciado.getJSONArray(i).getLong(0)
                                        objenunciado.tela = jObjenunciado.getJSONArray(i).getString(1)
                                        objenunciado.tipoobj = jObjenunciado.getJSONArray(i).getLong(2)
                                        objenunciado.indice = jObjenunciado.getJSONArray(i).getInt(3)
                                        objenunciado.peso = jObjenunciado.getJSONArray(i).getDouble(4)
                                        objenunciado.enunciado = jObjenunciado.getJSONArray(i).getString(5)
                                        objenunciado.descritivo = jObjenunciado.getJSONArray(i).getString(6)

                                        var achou = false
                                        var j = 0
                                        if (objenunciado.tipoobj == UtilClass.TIPO_IMAGEM_EFEITO) {
                                            while (!achou && j < aplicacao!!.classImagensEfeitos.size) {
                                                if (objenunciado.indice == aplicacao!!.classImagensEfeitos[j].imagensEfeitos!!.indice) {
                                                    aplicacao!!.classImagensEfeitos[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_ROTULO) {
                                            while (!achou && j < aplicacao!!.classRotulos.size) {
                                                if (objenunciado.indice == aplicacao!!.classRotulos[j].rotulos!!.indice) {
                                                    aplicacao!!.classRotulos[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_PREENCHIMENTO) {
                                            while (!achou && j < aplicacao!!.classPreenchimentos.size) {
                                                if (objenunciado.indice == aplicacao!!.classPreenchimentos[j].preenchimentos!!.indice) {
                                                    aplicacao!!.classPreenchimentos[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_ARRASTAR) {
                                            while (!achou && j < aplicacao!!.classArrastarSoltar.size) {
                                                if (objenunciado.indice == aplicacao!!.classArrastarSoltar[j].arrastar!!.indice) {
                                                    aplicacao!!.classArrastarSoltar[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_ARRASTAR_IMAGEM) {
                                            while (!achou && j < aplicacao!!.classArrastarSoltarImagens.size) {
                                                if (objenunciado.indice == aplicacao!!.classArrastarSoltarImagens[j].arrastarImagens!!.indice) {
                                                    aplicacao!!.classArrastarSoltarImagens[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_GIRA) {
                                            while (!achou && j < aplicacao!!.classGirasEfeitos.size) {
                                                if (objenunciado.indice == aplicacao!!.classGirasEfeitos[j].girasEfeitos!!.indice) {
                                                    aplicacao!!.classGirasEfeitos[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_ANIMACAO) {
                                            while (!achou && j < aplicacao!!.classAnimacoes.size) {
                                                if (objenunciado.indice == aplicacao!!.classAnimacoes[j].animacoes!!.indice) {
                                                    aplicacao!!.classAnimacoes[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_LIGA1) {
                                            while (!achou && j < aplicacao!!.classLigas.size) {
                                                if (objenunciado.indice == aplicacao!!.classLigas[j].liga1!!.indice) {
                                                    aplicacao!!.classLigas[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_DISSERTATIVA) {
                                            while (!achou && j < aplicacao!!.classDissertativa.size) {
                                                if (objenunciado.indice == aplicacao!!.classDissertativa[j].dissertativa!!.indice) {
                                                    aplicacao!!.classDissertativa[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_ARRASTAR_N) {
                                            while (!achou && j < aplicacao!!.classArrastarSoltarN.size) {
                                                var k = 0
                                                while (!achou && k < aplicacao!!.classArrastarSoltarN[j].arrastarN.size) {
                                                    if (objenunciado.indice == aplicacao!!.classArrastarSoltarN[j].arrastarN[k].indice) {
                                                        aplicacao!!.classArrastarSoltarN[j].objenunciado = objenunciado
                                                        achou = true
                                                    }
                                                    k++
                                                }
                                                j++
                                            }
                                        } else if (objenunciado.tipoobj == UtilClass.TIPO_TESTE_VEST) {
                                            while (!achou && j < aplicacao!!.classTestesVest.size) {
                                                if (objenunciado.indice == aplicacao!!.classTestesVest[j].testesVest!!.indice) {
                                                    aplicacao!!.classTestesVest[j].objenunciado = objenunciado
                                                    achou = true
                                                }
                                                j++
                                            }
                                        }
                                    }
                                }

                                if (arquivosBaixar.length() > 0) {
                                    var baixar = BaixarArquivosServidor()
                                    baixar.act = act
                                    baixar.arquivosBaixar = arquivosBaixar
                                    baixar.execute()
                                } else {
                                    carregarTelaLocal()
                                }
                            }
                        } else {
                            UtilClass.trataErro(
                                UtilClass.ERRO_SERVIDOR_TAG,
                                response!!.getJSONObject("status").getString("mensagem")
                            )
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarTelaServidor(nTentativa + 1, idTela)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarTelaServidor(nTentativa + 1, idTela)
                        } else {
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    UtilClass.trataErro(anError!!)
                    aplicacao!!.finalizarProgress()
                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                        carregarTelaServidor(nTentativa + 1, idTela)
                    } else {
                        UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                    }
                }
            })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarTelaServidor(nTentativa + 1, idTela)
            } else {
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    private fun iniciarActivity(savedInstanceState: Bundle?) {
        aplicacao = application as Aplicacao
        aplicacao!!.act = act
        UtilClass.esconderBarraTitulo(act)
        aplicacao!!.atualizarMetricas()

        if (savedInstanceState == null) {
            carregarTela(0)
        } else {
            altImgFundo = savedInstanceState.getInt("altImgFundo")
            larImgFundo = savedInstanceState.getInt("larImgFundo")
            carregarTelaLocal()
        }
    }

    fun sairApresentacao() {
        aplicacao!!.finalizarApresentacao()
        setResult(Activity.RESULT_OK)
        act!!.finish()
    }

    //async task --------------------------------------------------------------------------------------------------
    private class BaixarArquivosServidor : AsyncTask<Void, Void, Long>() {

        var act: ActivityApresentacao? = null
        var aplicacao: Aplicacao? = null
        var arquivosBaixar = JSONArray()
        var continua = true
        var zipFile: File? = null

        override fun onPreExecute() {
            aplicacao = act!!.application as Aplicacao

            zipFile = File(UtilClass.getCaminhoArquivos(act!!) + "arquivos.zip")

            aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.baixando_arquivos))
        }

        override fun doInBackground(vararg p0: Void?): Long {
            var nTentativa = 0
            while (nTentativa < UtilClass.TENTATIVAS_CONECTAR && continua) {
                try {
                    if (zipFile!!.exists()) {
                        zipFile!!.delete()
                    }
                    var url = URL(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_BAIXARARQUIVOSMULTIPLOS)

                    var con: HttpURLConnection
                    if (UtilClass.possuiProxy(aplicacao!!.configuracoes!!)) {

                        val authenticator = object : Authenticator() {

                            public override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(aplicacao!!.configuracoes!!.con_proxy_usu,
                                    aplicacao!!.configuracoes!!.con_proxy_pwd.toCharArray())
                            }
                        }

                        Authenticator.setDefault(authenticator)

                        val proxy = Proxy(Proxy.Type.HTTP,
                            InetSocketAddress(aplicacao!!.configuracoes!!.con_proxy_link,
                                aplicacao!!.configuracoes!!.con_proxy_porta))

                        con = url.openConnection(proxy) as HttpURLConnection
                    } else if (aplicacao!!.configuracoes!!.con_proxy_link.isNotEmpty()) {
                        val proxy = Proxy(Proxy.Type.HTTP,
                            InetSocketAddress(aplicacao!!.configuracoes!!.con_proxy_link,
                                aplicacao!!.configuracoes!!.con_proxy_porta))

                        con = url.openConnection(proxy) as HttpURLConnection
                    } else {
                        con = url.openConnection() as HttpURLConnection
                    }

                    con.doOutput = true
                    con.doInput = true
                    con.setRequestProperty("Content-Type", "application/zip")
                    con.connectTimeout = 60000

                    con.setRequestProperty("Content-type", "text/plain")
                    var request = ObjectOutputStream(BufferedOutputStream(con.outputStream))
                    request.writeObject(arquivosBaixar.toString())
                    request.flush()
                    request.close()

                    /*var request: ObjectOutputStream
                    request = ObjectOutputStream(BufferedOutputStream(con.outputStream))
                    request.writeChars(arquivosBaixar.toString())
                    request.flush()
                    request.close()*/
                    
                    
                    var inputStream = con.inputStream
                    var zipinputstream = ZipInputStream(inputStream)

                    var zipentry = zipinputstream.nextEntry
                    while (zipentry != null) {
                        var rgb = ByteArray(1000)
                        var fos = FileOutputStream(UtilClass.getCaminhoArquivos(act!!) + zipentry.name)
                        var n = zipinputstream.read(rgb)
                        while (n > -1) {
                            try {
                                fos.write(rgb, 0, n)
                            } catch (errZip: Exception) {
                                UtilClass.trataErro(errZip)
                            }
                            n = zipinputstream.read(rgb)
                        }
                        fos.close()
                        zipinputstream.closeEntry()

                        zipentry = zipinputstream.nextEntry
                    }
                    inputStream.close()
                    con.disconnect()

                    continua = false
                } catch (e: Exception) {
                    UtilClass.trataErro(e)
                }
                nTentativa++
            }
            return 0
        }

        override fun onCancelled() {
            aplicacao!!.finalizarProgress()
            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, DialogInterface.OnClickListener { _, _->
                act!!.sairApresentacao()
            })
        }

        override fun onPostExecute(result: Long?) {
            aplicacao!!.finalizarProgress()
            if (!continua) {
                act!!.carregarTelaLocal()
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, DialogInterface.OnClickListener { _, _->
                    act!!.sairApresentacao()
                })
            }
        }
    }
}