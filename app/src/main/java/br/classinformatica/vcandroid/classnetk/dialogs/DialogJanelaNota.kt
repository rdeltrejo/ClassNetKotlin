package br.classinformatica.vcandroid.classnetk.dialogs

import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DialogJanelaNota : Dialog {

    private var act: AppCompatActivity? = null
    private var aplicacao: Aplicacao? = null

    constructor(act: AppCompatActivity) : super(act) {
        this.act = act
        setContentView(R.layout.dialog_janela_nota)
        aplicacao = act.application as Aplicacao

        var tempoFinal = Calendar.getInstance()

        if (aplicacao!!.aulaFeita != null && aplicacao!!.aulaFeita!!.tempo_tmp != null && aplicacao!!.aulaFeita!!.tempo_tmp!! > 0) {
            //somar o tempoFinal com o tempo_tmp
        }

        tempoFinal.timeInMillis = tempoFinal.timeInMillis - aplicacao!!.tempoInicial.timeInMillis
        var erros = aplicacao!!.aulaFeita!!.erros_anterior
        var acertos = 0
        for (i in 0 until aplicacao!!.exerciciosTela.size) {
            if (aplicacao!!.exerciciosTela[i].aulaTestefeito!!.certoerrado == 1.toDouble()) {
                acertos++
            } else {
                erros++
            }
        }

        var tvNumAcertos = findViewById<TextView>(R.id.tvNumAcertos)
        tvNumAcertos.text = act.getString(R.string.numero_acertos_) + " " + acertos

        var tvNumErros = findViewById<TextView>(R.id.tvNumErros)
        tvNumErros.text = act.getString(R.string.numero_erros_) + " " + erros

        var tvTempoMinutos = findViewById<TextView>(R.id.tvTempoMinutos)

        var minutos = TimeUnit.MILLISECONDS.toMinutes(tempoFinal.timeInMillis)
        tvTempoMinutos.text = act.getString(R.string.tempo_minutos_) + " " + minutos

        var tvNotaFinal = findViewById<TextView>(R.id.tvNotaFinal)
        tvNotaFinal.text = act.getString(R.string.nota_final_) + " " + Math.round((acertos.toFloat() * 100) / (acertos.toFloat() + erros.toFloat()))

        var bOk = findViewById<Button>(R.id.bOk)
        bOk.setOnClickListener {
            if (aplicacao!!.aulaAtribuida!!.status != 2 || (aplicacao!!.aulaAtribuida!!.status == 2 && aplicacao!!.aulaFeita!!.tentativas < aplicacao!!.descricao!!.tentativas)) {
                if (aplicacao!!.descricao!!.grava_teste_detalhe == 1 || aplicacao!!.descricao!!.teste_vest == 1) {
                    gravarFinalExercicios(1)
                } else {
                    gravarFinal(1)
                }
            } else {
                (act!! as ActivityApresentacao).sairApresentacao()
            }
        }

        aplicacao!!.aulaAtribuida!!.status = 2

        aplicacao!!.aulaFeita!!.erros_anterior = erros
        aplicacao!!.aulaFeita!!.acertos_anterior = acertos
    }

    private fun gravarFinal(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.aguarde))
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
            aux = JSONArray()
            aux.put(UtilClass.toHex("delete"))
            aux.put(UtilClass.toHex("aula_testefeito"))
            aux.put(JSONArray())
            aux.put(JSONArray())
            aux.put(JSONArray())
            aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
            cmdparam.put(aux)

            //terceiro comando
            aux = JSONArray()
            aux.put(UtilClass.toHex("delete"))
            aux.put(UtilClass.toHex("aula_feita"))
            aux.put(JSONArray())
            aux.put(JSONArray())
            aux.put(JSONArray())
            aux.put(UtilClass.toHex("id_projeto = " + aplicacao!!.descricao!!.id_projeto + " and id_aluno = " + aplicacao!!.aluno!!.id_aluno))
            cmdparam.put(aux)

            //quarto comando
            aux = JSONArray()
            aux.put(UtilClass.toHex("insert"))
            aux.put(UtilClass.toHex("aula_feita"))

            var aux2 = JSONArray()
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
            
            
            //quinto comando
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
                            (act!! as ActivityApresentacao).sairApresentacao()
                        } else {
                            UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("mensagem"))
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                gravarFinal(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            gravarFinal(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    UtilClass.trataErro(anError!!)
                    aplicacao!!.finalizarProgress()
                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                        gravarFinal(nTentativa + 1)
                    } else {
                        UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                    }
                }
            })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                gravarFinal(nTentativa + 1)
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }

    private fun gravarFinalExercicios(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.aguarde))
        try {
            var sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            //var sdf = SimpleDateFormat("yyyy-MM-dd")

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
                            (act!! as ActivityApresentacao).sairApresentacao()
                        } else {
                            UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("mensagem"))
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                gravarFinalExercicios(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            gravarFinalExercicios(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    UtilClass.trataErro(anError!!)
                    aplicacao!!.finalizarProgress()
                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                        gravarFinalExercicios(nTentativa + 1)
                    } else {
                        UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                    }
                }
            })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                gravarFinalExercicios(nTentativa + 1)
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }
}