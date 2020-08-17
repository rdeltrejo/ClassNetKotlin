package br.classinformatica.vcandroid.classnetk.activities

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.Banco
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ActivityConfiguracoes : AppCompatActivity() {

    private var act = this
    private var aplicacao: Aplicacao? = null
    private var cbIniciarLogin: CheckBox? = null
    private var cbProxy: CheckBox? = null
    private var etPortaProxy: EditText? = null
    private var etSenhaProxy: EditText? = null
    private var etSenhaProxyRep: EditText? = null
    private var etServidor: EditText? = null
    private var etServidorProxy: EditText? = null
    private var etUsuarioProxy: EditText? = null
    private var llProxy: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UtilClass.isRetrato(act)) {
            setContentView(R.layout.activity_configuracoes_retrato)
        } else {
            setContentView(R.layout.activity_configuracoes)
        }

        iniciarActivity(savedInstanceState)
    }

    private fun iniciarActivity(savedInstanceState: Bundle?) {
        aplicacao = application as Aplicacao
        aplicacao!!.act = act
        UtilClass.esconderBarraTitulo(act)
        aplicacao!!.atualizarMetricas()

        //instanciando objetos
        etServidor = findViewById(R.id.etServidor)
        var bListar = findViewById<Button>(R.id.bListar)
        cbIniciarLogin = findViewById(R.id.cbIniciarLogin)
        cbProxy = findViewById(R.id.cbProxy)
        llProxy = findViewById(R.id.llProxy)
        etServidorProxy = findViewById(R.id.etServidorProxy)
        etPortaProxy = findViewById(R.id.etPortaProxy)
        etUsuarioProxy = findViewById(R.id.etUsuarioProxy)
        etSenhaProxy = findViewById(R.id.etSenhaProxy)
        etSenhaProxyRep = findViewById(R.id.etSenhaProxyRep)
        var bCancelar = findViewById<Button>(R.id.bCancelar)
        var bApagar = findViewById<Button>(R.id.bApagar)
        var bOk = findViewById<Button>(R.id.bOk)


        //carregar dados ojetos
        if (savedInstanceState != null) {
            cbProxy!!.isChecked = savedInstanceState.getString("con_proxy_link")!!.isNotEmpty()
            if (cbProxy!!.isChecked) {
                etServidorProxy!!.setText(savedInstanceState.getString("con_proxy_link"))
                etPortaProxy!!.setText(savedInstanceState.getInt("con_proxy_porta").toString())
                etUsuarioProxy!!.setText(savedInstanceState.getString("con_proxy_usu"))
                etSenhaProxy!!.setText(savedInstanceState.getString("con_proxy_pwd"))
                etSenhaProxyRep!!.setText(savedInstanceState.getString("con_proxy_pwd"))
            } else {
                etServidorProxy!!.setText("")
                etPortaProxy!!.setText("")
                etUsuarioProxy!!.setText("")
                etSenhaProxy!!.setText("")
                etSenhaProxyRep!!.setText("")
            }
            cbIniciarLogin!!.isChecked = savedInstanceState.getBoolean("con_inicia_login")
            etServidor!!.setText(savedInstanceState.getString("ser_link"))
        } else {
            cbProxy!!.isChecked = aplicacao!!.configuracoes!!.con_proxy_link.isNotEmpty()
            if (cbProxy!!.isChecked) {
                etServidorProxy!!.setText(aplicacao!!.configuracoes!!.con_proxy_link)
                etPortaProxy!!.setText(aplicacao!!.configuracoes!!.con_proxy_porta.toString())
                etUsuarioProxy!!.setText(aplicacao!!.configuracoes!!.con_proxy_usu)
                etSenhaProxy!!.setText(aplicacao!!.configuracoes!!.con_proxy_pwd)
                etSenhaProxyRep!!.setText(aplicacao!!.configuracoes!!.con_proxy_pwd)
            } else {
                etServidorProxy!!.setText("")
                etPortaProxy!!.setText("")
                etUsuarioProxy!!.setText("")
                etSenhaProxy!!.setText("")
                etSenhaProxyRep!!.setText("")
            }
            cbIniciarLogin!!.isChecked = aplicacao!!.configuracoes!!.con_inicia_login
            etServidor!!.setText(aplicacao!!.servidores!!.ser_link)
        }


        // listeners objetos
        bListar.setOnClickListener {
            var banco = Banco(act)
            var servidores = banco.getListaServidores()
            banco.close()
            if (servidores.isNotEmpty()) {
                var items = Array(servidores.size) { "" }
                for (i in 0 until servidores.size) {
                    items[i] = servidores[i].ser_link
                }
                UtilClass.msgItems(act, R.string.servidores, items, DialogInterface.OnClickListener { _, choice ->
                    etServidor!!.setText(servidores[choice].ser_link)
                })
            }
        }
        cbProxy!!.setOnCheckedChangeListener { _, _ ->
            mudarProxy()
        }
        bCancelar.setOnClickListener {
            if (aplicacao!!.configuracoes!!.ser_id == 0) {
                UtilClass.msgOk(act, R.string.aviso, R.string.selecione_servidor_sair, R.string.ok, null)
            } else {
                act.finish()
            }
        }
        bApagar.setOnClickListener {
            UtilClass.msgSimNao(act, R.string.aviso, R.string.deseja_apagar_servidor, R.string.sim, R.string.nao, DialogInterface.OnClickListener { _, _ ->
                var banco = Banco(act)
                banco.apagarServidor(aplicacao!!.configuracoes!!.ser_id)
                aplicacao!!.servidores = null
                aplicacao!!.configuracoes!!.ser_id = 0
                etServidor!!.setText("")
                banco.close()
            }, null)
        }
        bOk.setOnClickListener {
            if (etServidor!!.text.toString().trim().isEmpty()) {
                UtilClass.msgOk(act, R.string.aviso, R.string.selecione_servidor_sair, R.string.ok, null)
            } else {
                if (cbProxy!!.isChecked && etSenhaProxy!!.text.toString() != etSenhaProxyRep!!.text.toString()) {
                    UtilClass.msgOk(act, R.string.aviso, R.string.senhas_diferem, R.string.ok, null)
                } else {
                    if (cbProxy!!.isChecked && etServidorProxy!!.text.toString().trim().isNotEmpty() && etPortaProxy!!.text.toString().trim().isNotEmpty()) {
                        aplicacao!!.configuracoes!!.con_proxy_link = etServidorProxy!!.text.toString()
                        aplicacao!!.configuracoes!!.con_proxy_porta = etPortaProxy!!.text.toString().toInt()
                        aplicacao!!.configuracoes!!.con_proxy_usu = etUsuarioProxy!!.text.toString()
                        aplicacao!!.configuracoes!!.con_proxy_pwd = etSenhaProxy!!.text.toString()
                    } else {
                        aplicacao!!.configuracoes!!.con_proxy_link = ""
                        aplicacao!!.configuracoes!!.con_proxy_porta = 0
                        aplicacao!!.configuracoes!!.con_proxy_usu = ""
                        aplicacao!!.configuracoes!!.con_proxy_pwd = ""
                    }

                    if (!etServidor!!.text.toString().endsWith("/")) {
                        etServidor!!.setText(etServidor!!.text.toString().trim() + "/")
                    }
                    var banco = Banco(act)
                    var servidor = banco.getServidores(etServidor!!.text.toString())
                    if (servidor.ser_id == 0) {//tem que gravar
                        servidor.ser_link = etServidor!!.text.toString()
                        servidor = banco.gravarServidores(servidor)
                    }
                    aplicacao!!.configuracoes!!.ser_id = servidor.ser_id
                    aplicacao!!.configuracoes!!.con_inicia_login = cbIniciarLogin!!.isChecked
                    aplicacao!!.configuracoes!!.con_id_instituicao = 0
                    aplicacao!!.instituicao = null
                    aplicacao!!.servidores = servidor
                    banco.gravarConfiguracoes(aplicacao!!.configuracoes!!)
                    banco.close()

                    setResult(RESULT_OK)
                    act.finish()
                }
            }
        }

        mudarProxy()
    }

    override fun onBackPressed() {
        if (aplicacao!!.configuracoes!!.ser_id == 0) {
            UtilClass.msgOk(act, R.string.aviso, R.string.selecione_servidor_sair, R.string.ok, null)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("con_proxy_link", etServidorProxy!!.text.toString())
        outState.putInt("con_proxy_porta", etPortaProxy!!.text.toString().toInt())
        outState.putString("con_proxy_usu", etUsuarioProxy!!.text.toString())
        outState.putString("con_proxy_pwd", etSenhaProxy!!.text.toString())
        outState.putBoolean("con_inicia_login", cbIniciarLogin!!.isChecked)
        outState.putString("ser_link", etServidor!!.text.toString())
        super.onSaveInstanceState(outState)
    }

    private fun mudarProxy() {
        if (cbProxy!!.isChecked) {
            var lay = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            llProxy!!.layoutParams = lay
        } else {
            var lay = LinearLayout.LayoutParams(0, 0)
            llProxy!!.layoutParams = lay
        }
    }
}