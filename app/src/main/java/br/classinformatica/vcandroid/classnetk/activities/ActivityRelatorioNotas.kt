package br.classinformatica.vcandroid.classnetk.activities

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.adapters.ActivityRelatorioNotasAdapter
import br.classinformatica.vcandroid.classnetk.entidades.AulaFeita
import br.classinformatica.vcandroid.classnetk.entidades.Descricao
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import java.sql.Date
import java.text.SimpleDateFormat

class ActivityRelatorioNotas : AppCompatActivity() {

    var act: ActivityRelatorioNotas = this
    var aplicacao: Aplicacao? = null
    var listAulaFeita: ArrayList<AulaFeita>? = null
    var listDescricao: ArrayList<Descricao>? = null
    var llCabecalho: LinearLayout? = null
    var lvNotas: ListView? = null
    var tvMedia: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio_notas)

        iniciarActivity(savedInstanceState)
    }

    private fun carregarNotasServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.aguarde))
        try {
            val json = JSONObject()
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put(
                "pesquisa",
                "SELECT af.id_projeto, af.id_aluno, af.codigo_aluno, af.codigo_aula, af.data, af.nota, af.tentativas, af.erros_anterior, af.acertos_anterior, af.total_minutos, af.ultima_tela_feita, af.nome, af.autor, af.assunto, af.materia, af.alvo, af.pendente, af.status, af.data_inicio, af.cod_certificacao, af.autor_atribuicao, af.origem, af.tempo_tmp, af.id_ultima_tela_feita, d.nome FROM aula_feita AS af, descricao AS d WHERE id_aluno = " + aplicacao!!.aluno!!.id_aluno + " AND af.id_projeto = d.id_projeto ORDER BY d.nome"
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
                                listAulaFeita = ArrayList()
                                listDescricao = ArrayList()
                                var aula_feita: AulaFeita
                                var descricao: Descricao
                                val sdf = SimpleDateFormat("yyyy-MM-dd")
                                var nota_geral: Double
                                nota_geral = 0.toDouble()
                                for (i in 0 until array.length()) {
                                    aula_feita = AulaFeita()
                                    aula_feita.id_projeto = array.getJSONArray(i).getLong(0)
                                    aula_feita.id_aluno = array.getJSONArray(i).getLong(1)
                                    aula_feita.codigo_aluno = array.getJSONArray(i).getString(2)
                                    aula_feita.codigo_aula = array.getJSONArray(i).getString(3)
                                    if (array.getJSONArray(i).getString(4).trim().isNotEmpty()) {
                                        aula_feita.data = Date(sdf.parse(array.getJSONArray(i).getString(4)).time)
                                    }
                                    aula_feita.nota = array.getJSONArray(i).getDouble(5)
                                    aula_feita.tentativas = array.getJSONArray(i).getInt(6)
                                    aula_feita.erros_anterior = array.getJSONArray(i).getInt(7)
                                    aula_feita.acertos_anterior = array.getJSONArray(i).getInt(8)
                                    aula_feita.total_minutos = array.getJSONArray(i).getInt(9)
                                    aula_feita.ultima_tela_feita = array.getJSONArray(i).getString(10)
                                    aula_feita.nome = array.getJSONArray(i).getString(11)
                                    aula_feita.autor = array.getJSONArray(i).getString(12)
                                    aula_feita.assunto = array.getJSONArray(i).getString(13)
                                    aula_feita.materia = array.getJSONArray(i).getString(14)
                                    aula_feita.alvo = array.getJSONArray(i).getString(15)
                                    aula_feita.pendente = array.getJSONArray(i).getInt(16)
                                    aula_feita.status = array.getJSONArray(i).getInt(17)
                                    if (array.getJSONArray(i).getString(18).trim().isNotEmpty()) {
                                        aula_feita.data_inicio =
                                            Date(sdf.parse(array.getJSONArray(i).getString(18)).time)
                                    }
                                    aula_feita.cod_certificacao = array.getJSONArray(i).getInt(19)
                                    aula_feita.autor_atribuicao = array.getJSONArray(i).getString(20)
                                    aula_feita.origem = array.getJSONArray(i).getString(21)
                                    aula_feita.tempo_tmp = array.getJSONArray(i).getDouble(22)
                                    aula_feita.id_ultima_tela_feita = array.getJSONArray(i).getLong(23)
                                    listAulaFeita!!.add(aula_feita)

                                    descricao = Descricao()
                                    descricao.nome = array.getJSONArray(i).getString(24)
                                    listDescricao!!.add(descricao)

                                    nota_geral += aula_feita!!.nota!!
                                }

                                tvMedia!!.text = act.getString(R.string.media_geral) + " " + Math.round(nota_geral)

                                montarLista()
                            } else {
                                UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("msg"))
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    carregarNotasServidor(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                carregarNotasServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            carregarNotasServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                carregarNotasServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    private fun iniciarActivity(savedInstanceState: Bundle?) {
        aplicacao = application as Aplicacao
        aplicacao!!.act = act
        UtilClass.esconderBarraTitulo(act)
        aplicacao!!.atualizarMetricas()

        tvMedia = findViewById(R.id.tvMedia)
        var bOk = findViewById<Button>(R.id.bOk)
        lvNotas = findViewById(R.id.lvNotas)
        llCabecalho = findViewById(R.id.llCabecalho)

        bOk.setOnClickListener {
            act.finish()
        }

        if (UtilClass.isRetrato(act)) {
            llCabecalho!!.layoutParams = LinearLayout.LayoutParams(0, 0)
        } else {
            llCabecalho!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        carregarNotasServidor(1)

        /*if (savedInstanceState == null) {
            carregarNotasServidor(1)
        } else {
            montarLista()
        }*/
    }

    private fun montarLista() {
        if (listAulaFeita != null) {
            var adapter = ActivityRelatorioNotasAdapter(act, listAulaFeita!!, listDescricao!!)
            lvNotas!!.adapter = adapter
        }
    }
}