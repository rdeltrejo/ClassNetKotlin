package br.classinformatica.vcandroid.classnetk.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.zip.ZipOutputStream



class ActivityAlterarCadastro : AppCompatActivity() {

    private var act = this
    private var aplicacao: Aplicacao? = null
    private var etBairro: EditText? = null
    private var etCep: EditText? = null
    private var etCidade: EditText? = null
    private var etCodigo: EditText? = null
    private var etEmail: EditText? = null
    private var etEndereco: EditText? = null
    private var etInstituicao: EditText? = null
    private var etNome: EditText? = null
    private var etNovaSenha: EditText? = null
    private var etRedigiteSenha: EditText? = null
    private var etSenha: EditText? = null
    private var etTelefone: EditText? = null
    private var foto: String? = ""
    private var ivFoto: ImageView? = null
    private var mudouFoto = false
    private var rbFeminino: RadioButton? = null
    private var rbMasculino: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UtilClass.isRetrato(act)) {
            setContentView(R.layout.activity_alterar_cadastro_retrato)
        } else {
            setContentView(R.layout.activity_alterar_cadastro)
        }

        iniciarActivity(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("etInstituicao", etInstituicao!!.text.toString())
        outState.putString("etCodigo", etCodigo!!.text.toString())
        outState.putString("etNome", etNome!!.text.toString())
        outState.putString("etSenha", etSenha!!.text.toString())
        outState.putString("etNovaSenha", etNovaSenha!!.text.toString())
        outState.putString("etRedigiteSenha", etRedigiteSenha!!.text.toString())
        outState.putString("etEmail", etEmail!!.text.toString())
        outState.putString("etTelefone", etTelefone!!.text.toString())
        outState.putString("etEndereco", etEndereco!!.text.toString())
        outState.putString("etCidade", etCidade!!.text.toString())
        outState.putString("etBairro", etBairro!!.text.toString())
        outState.putString("etCep", etCep!!.text.toString())
        outState.putBoolean("rbFeminino", rbFeminino!!.isChecked)
        outState.putString("foto", foto)
        outState.putBoolean("mudouFoto", mudouFoto)
    }

    private fun gravarAlunoServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            var cmdparam = JSONArray()
            var comando = JSONArray()

            comando.put("update")
            comando.put("aluno")

            var sexo = "F"
            if (rbMasculino!!.isChecked) {
                sexo = "M"
            }

            var aux = JSONArray()
            aux.put(UtilClass.toHex("nome"))
            aux.put(UtilClass.toHex("email"))
            aux.put(UtilClass.toHex("telefone"))
            aux.put(UtilClass.toHex("endereco"))
            aux.put(UtilClass.toHex("cidade"))
            aux.put(UtilClass.toHex("bairro"))
            aux.put(UtilClass.toHex("cep"))
            aux.put(UtilClass.toHex("sexo"))
            if (etNovaSenha!!.text.toString().trim().isNotEmpty()) {
                aux.put(UtilClass.toHex("senha"))
            }
            comando.put(aux)

            aux = JSONArray()
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            aux.put(UtilClass.toHex("string"))
            if (etNovaSenha!!.text.toString().trim().isNotEmpty()) {
                aux.put(UtilClass.toHex("string"))
            }
            comando.put(aux)

            aux = JSONArray()
            aux.put(UtilClass.toHex(etNome!!.text.toString()))
            aux.put(UtilClass.toHex(etEmail!!.text.toString()))
            aux.put(UtilClass.toHex(etTelefone!!.text.toString()))
            aux.put(UtilClass.toHex(etEndereco!!.text.toString()))
            aux.put(UtilClass.toHex(etCidade!!.text.toString()))
            aux.put(UtilClass.toHex(etBairro!!.text.toString()))
            aux.put(UtilClass.toHex(etCep!!.text.toString()))
            aux.put(UtilClass.toHex(sexo))
            if (etNovaSenha!!.text.toString().trim().isNotEmpty()) {
                aux.put(UtilClass.toHex(UtilClass.toSHA1(etNovaSenha!!.text.toString())))
            }
            comando.put(aux)

            comando.put(UtilClass.toHex("id_aluno = " + aplicacao!!.aluno!!.id_aluno))
            cmdparam.put(comando)

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
                            UtilClass.msgToast(act, R.string.cadastro_alterado)
                            setResult(Activity.RESULT_OK)
                            act.finish()
                        } else {
                            UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response!!.getJSONObject("status").getString("mensagem"))
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                gravarAlunoServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                        if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                            gravarAlunoServidor(nTentativa + 1)
                        } else {
                            UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    UtilClass.trataErro(anError!!)
                    aplicacao!!.finalizarProgress()
                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                        gravarAlunoServidor(nTentativa + 1)
                    } else {
                        UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                    }
                }
            })
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                gravarAlunoServidor(nTentativa + 1)
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

        etInstituicao = findViewById(R.id.etInstituicao)
        etCodigo = findViewById(R.id.etCodigo)
        etNome = findViewById(R.id.etNome)
        etSenha = findViewById(R.id.etSenha)
        etNovaSenha = findViewById(R.id.etNovaSenha)
        etRedigiteSenha = findViewById(R.id.etRedigiteSenha)
        etEmail = findViewById(R.id.etEmail)
        etTelefone = findViewById(R.id.etTelefone)
        etEndereco = findViewById(R.id.etEndereco)
        etCidade = findViewById(R.id.etCidade)
        etBairro = findViewById(R.id.etBairro)
        etCep = findViewById(R.id.etCep)
        rbMasculino = findViewById(R.id.rbMasculino)
        rbFeminino = findViewById(R.id.rbFeminino)
        ivFoto = findViewById(R.id.ivFoto)
        var bCancelar = findViewById<Button>(R.id.bCancelar)
        var bGravar = findViewById<Button>(R.id.bGravar)

        bCancelar.setOnClickListener {
            act.finish()
        }
        bGravar.setOnClickListener {
            if (etNovaSenha!!.text.toString() != etRedigiteSenha!!.text.toString()) {
                UtilClass.msgOk(act, R.string.aviso, R.string.erro_nova_senha_redigite_senha, R.string.ok, null)
            } else if(etSenha!!.text.toString().isEmpty() && etNovaSenha!!.text.toString().isNotEmpty()) {
                UtilClass.msgOk(act, R.string.aviso, R.string.erro_senha_atual_vazio, R.string.ok, null)
            } else if (etNome!!.text.toString().trim().isNotEmpty()) {
                UtilClass.msgOk(act, R.string.aviso, R.string.erro_campos_obrigatorios, R.string.ok, null)
            } else if (etSenha!!.text.toString().isNotEmpty() && etNovaSenha!!.text.toString().isNotEmpty() && etRedigiteSenha!!.text.toString().isNotEmpty() && UtilClass.toSHA1(etSenha!!.text.toString()) != aplicacao!!.aluno!!.senha) {//na condição anterior já viu se a confimarção da senha est´correta
                UtilClass.msgOk(act, R.string.aviso, R.string.erro_senha_atual_direfente, R.string.ok, null)
            } else if (mudouFoto) {
                uploadFotoServidor(1)
            } else {
                gravarAlunoServidor(1)
            }
        }

        if (savedInstanceState == null) {
            etInstituicao!!.setText(aplicacao!!.instituicao!!.titulo)
            etCodigo!!.setText(aplicacao!!.aluno!!.codigo)
            etNome!!.setText(aplicacao!!.aluno!!.nome)
            etEmail!!.setText(aplicacao!!.aluno!!.email)
            etTelefone!!.setText(aplicacao!!.aluno!!.telefone)
            etEndereco!!.setText(aplicacao!!.aluno!!.endereco)
            etCidade!!.setText(aplicacao!!.aluno!!.cidade)
            etBairro!!.setText(aplicacao!!.aluno!!.bairro)
            etCep!!.setText(aplicacao!!.aluno!!.cep)
            if (aplicacao!!.aluno!!.sexo!!.toLowerCase() == "f") {
                rbFeminino!!.isChecked = true
            } else {
                rbMasculino!!.isChecked = true
            }
            if (aplicacao!!.aluno!!.foto!!.trim().isNotEmpty()) {
                foto = aplicacao!!.servidores!!.ser_link + "arquivos/" + aplicacao!!.aluno!!.foto!!.trim()
            }
        } else {
            etInstituicao!!.setText(savedInstanceState.getString("etInstituicao"))
            etCodigo!!.setText(savedInstanceState.getString("etCodigo"))
            etNome!!.setText(savedInstanceState.getString("etNome"))
            etSenha!!.setText(savedInstanceState.getString("etSenha"))
            etNovaSenha!!.setText(savedInstanceState.getString("etNovaSenha"))
            etRedigiteSenha!!.setText(savedInstanceState.getString("etRedigiteSenha"))
            etEmail!!.setText(savedInstanceState.getString("etEmail"))
            etTelefone!!.setText(savedInstanceState.getString("etTelefone"))
            etEndereco!!.setText(savedInstanceState.getString("etEndereco"))
            etCidade!!.setText(savedInstanceState.getString("etCidade"))
            etBairro!!.setText(savedInstanceState.getString("etBairro"))
            etCep!!.setText(savedInstanceState.getString("etCep"))
            if (savedInstanceState.getBoolean("rbFeminino", false)) {
                rbFeminino!!.isChecked = true
            } else {
                rbMasculino!!.isChecked = true
            }
            foto = savedInstanceState.getString("foto")
            mudouFoto = savedInstanceState.getBoolean("mudouFoto")
        }
        if (foto!!.trim().isNotEmpty()) {
            UtilClass.carregarImagemImageView(act, foto!!, R.drawable.aluno, false, ivFoto!!, aplicacao!!.configuracoes!!)
        }
    }

    private fun uploadFotoServidor(nTentativa: Int) {
        aplicacao!!.iniciarProgress(act, act.getString(R.string.aguarde))
        try {
            var arqUpl = File(foto)
            var arqDes = File(UtilClass.CAMINHO_DESCRITOR)
            var arqZip = File(UtilClass.CAMINHO_ARQUIVO_ZIP)
            arqDes.delete()
            arqZip.delete()

            var json = JSONObject()
            json.put("id_instituicao", aplicacao!!.instituicao!!.id_instituicao)
            json.put("usu", aplicacao!!.aluno!!.codigo)
            json.put("pwd", aplicacao!!.aluno!!.senha)
            json.put("tipousu", 2)
            json.put("path", "arqproj/" + aplicacao!!.instituicao!!.codigo + "/")

            var bw = BufferedWriter(OutputStreamWriter(FileOutputStream(arqDes.absolutePath), "UTF-8"))
            bw.write(json.toString())
            bw.close()

            var out = FileOutputStream(arqZip)
            var zipoutputstream = ZipOutputStream(out)
            zipoutputstream.setMethod(ZipOutputStream.DEFLATED)
            UtilClass.zipArquivo(zipoutputstream, arqDes.absolutePath, "")
            UtilClass.zipArquivo(zipoutputstream, arqUpl.absolutePath, "")
            zipoutputstream.close()
            out.close()

            if (UtilClass.possuiProxy(aplicacao!!.configuracoes!!)) {
                UtilClass.upload(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_GRAVAARQUIVOPROJ + "&android=1",
                    arqZip,
                    UtilClass.TIMEOUT_COM_FOTO,
                    aplicacao!!.configuracoes!!,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            UtilClass.trataErro(e)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                uploadFotoServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }

                        override fun onResponse(call: Call, resp: Response) {
                            var response = JSONObject(UtilClass.convert(resp.body()!!.byteStream(), Charsets.UTF_8))
                            aplicacao!!.finalizarProgress()
                            try {
                                if (response.getJSONObject("status").getString("codigo").toLowerCase() == "ok") {
                                    gravarAlunoServidor(1)
                                } else {
                                    UtilClass.trataErro(UtilClass.ERRO_SERVIDOR_TAG, response.getJSONObject("status").getString("codigo"))
                                    if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                        uploadFotoServidor(nTentativa + 1)
                                    } else {
                                        UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                    }
                                }
                            } catch (e: Exception) {
                                UtilClass.trataErro(e)
                                if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                    uploadFotoServidor(nTentativa + 1)
                                } else {
                                    UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                                }
                            }
                        }
                    })
            } else {
                UtilClass.upload(aplicacao!!.servidores!!.ser_link + UtilClass.SERVLET_GRAVAARQUIVOPROJ + "&android=1",
                    arqZip,
                    UtilClass.TIMEOUT_COM_FOTO,
                    object : UploadProgressListener {
                        override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
                        }
                    },
                    object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject?) {
                        }

                        override fun onError(anError: ANError?) {
                            UtilClass.trataErro(anError!!)
                            aplicacao!!.finalizarProgress()
                            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                                uploadFotoServidor(nTentativa + 1)
                            } else {
                                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
                            }
                        }
                    })
            }
        } catch (e: Exception) {
            UtilClass.trataErro(e)
            aplicacao!!.finalizarProgress()
            if (nTentativa < UtilClass.TENTATIVAS_CONECTAR) {
                uploadFotoServidor(nTentativa + 1)
            } else {
                UtilClass.msgOk(act, R.string.aviso, R.string.servidor_inacessivel, R.string.ok, null)
            }
        }
    }
}