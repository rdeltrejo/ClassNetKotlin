package br.classinformatica.vcandroid.classnetk.dialogs

import android.app.Dialog
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.MainActivity
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.adapters.DialogPesquisarInstituicaoAdapter
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import java.lang.Exception
import br.classinformatica.vcandroid.classnetk.entidades.Instituicao
import kotlinx.android.synthetic.main.dialog_pesquisar_instituicao.*


class DialogPesquisarInstituicao : Dialog {

    var act : MainActivity? = null
    var aplicacao : Aplicacao? = null
    var dialog: DialogPesquisarInstituicao? = null
    var etFiltro : EditText? = null

    constructor(act: MainActivity) : super(act) {
        this.act = act
        setContentView(R.layout.dialog_pesquisar_instituicao)
        setTitle(R.string.selecione_instituicao)
        iniciarDialog()
    }

    fun iniciarDialog() {
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        aplicacao = act!!.application as Aplicacao
        etFiltro = findViewById(R.id.etFiltro)
        dialog = this

        var ivLupa = findViewById<ImageView>(R.id.ivLupa)
        ivLupa.setOnClickListener {
            pesquisarInstituicao(etFiltro!!.text.toString(), 1)
        }

        var ivBancoDados = findViewById<ImageView>(R.id.ivBancoDados)
        ivBancoDados.setOnClickListener {
            pesquisarInstituicao("", 1)
        }

        pesquisarInstituicao("", 1)
    }

    private fun pesquisarInstituicao(filtro: String, nTentativa: Int) {
        aplicacao!!.iniciarProgress(act!!, act!!.getString(R.string.aguarde))
        try {
            val json = JSONObject()
            json.put("usu", "visitante")
            json.put("pwd", "visitante")
            if (filtro.trim().isNotEmpty()) {
                json.put(
                    "pesquisa",
                    "SELECT id_instituicao, codigo, titulo, site, fundo, atualizacao_automatica, bloqueado, autocadastro FROM instituicao WHERE data_apagamento IS NULL AND (codigo LIKE '%$filtro%' OR titulo LIKE '%$filtro%') ORDER BY codigo"
                )
            } else {
                json.put(
                    "pesquisa",
                    "SELECT id_instituicao, codigo, titulo, site, fundo, atualizacao_automatica, bloqueado, autocadastro FROM instituicao WHERE data_apagamento IS NULL ORDER BY codigo"
                )
            }
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
                                var instituicoes = ArrayList<Instituicao>()
                                var instituicao: Instituicao
                                for (i in 0 until array.length()) {
                                    instituicao = Instituicao()
                                    instituicao.id_instituicao = array.getJSONArray(i).getLong(0)
                                    instituicao.codigo = array.getJSONArray(i).getString(1)
                                    instituicao.titulo = array.getJSONArray(i).getString(2)
                                    instituicao.site = array.getJSONArray(i).getString(3)
                                    instituicao.fundo = array.getJSONArray(i).getString(4)
                                    instituicao.atualizacao_automatica = array.getJSONArray(i).getInt(5)
                                    instituicao.bloqueado = array.getJSONArray(i).getInt(6)
                                    instituicao.autocadastro = array.getJSONArray(i).getInt(7)
                                    instituicoes.add(instituicao)
                                }

                                var adapter = DialogPesquisarInstituicaoAdapter(act!!, dialog!!, instituicoes)
                                lvInstituicoes.adapter = adapter
                            } else {
                                UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("msg"))
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    pesquisarInstituicao(filtro, nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                }
                            }
                        } catch (e: Exception) {
                            UtilClass.trataErro(e)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                pesquisarInstituicao(filtro, nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        UtilClass.trataErro(anError!!)
                        aplicacao!!.finalizarProgress()
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            pesquisarInstituicao(filtro, nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                pesquisarInstituicao(filtro, nTentativa + 1)
            } else {
                UtilClass.msgOk(act!!, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }
}