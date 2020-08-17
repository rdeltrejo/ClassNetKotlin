package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Color
import android.media.MediaPlayer
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.dialogs.DialogCaixaMensagem
import br.classinformatica.vcandroid.classnetk.entidades.*
import br.classinformatica.vcandroid.classnetk.util.TextViewPersonalizado
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ClassRotulos {

    var aplicacao: Aplicacao? = null
    var clicouObjeto = false
    var desenhado = false
    var dialog: DialogCaixaMensagem? = null
    private var mediaPlayer = MediaPlayer()
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var rotulos: Rotulos? = null
    var objtexto: Objtexto? = null
    var objsom: Objsom? = null
    var objenunciado: Objenunciado? = null
    var objlink: Objlink? = null

    fun desenhar(act: ActivityApresentacao) {
        var rl =
            RelativeLayout.LayoutParams(Math.round(rotulos!!.largura * pesoX), Math.round(rotulos!!.altura * pesoY))
        rl.leftMargin = Math.round(rotulos!!.esquerdo * pesoX)
        rl.topMargin = Math.round(rotulos!!.topo * pesoY)

        var textView = TextViewPersonalizado(act)
        textView.xdpi = aplicacao!!.metrics!!.density
        textView.largura = Math.round(rotulos!!.largura * pesoX)
        textView.altura = Math.round(rotulos!!.altura * pesoY)
        textView.espessutaBorda = rotulos!!.espessura_borda_externa + rotulos!!.espessura_borda_interna
        textView.borda = rotulos!!.borda

        textView.layoutParams = rl
        textView.gravity = Gravity.TOP or Gravity.LEFT
        textView.setLayerType(RelativeLayout.LAYER_TYPE_SOFTWARE, null)
        textView.isSingleLine = false
        textView.setHorizontallyScrolling(false)
        textView.setPadding(textView.paddingLeft, 0, textView.paddingRight, 0)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rotulos!!.tamanho.toFloat() * pesoX)
        textView.setBackgroundColor(Color.TRANSPARENT)
        /*if (rotulos!!.invisivel == 1) {
            textView.setBackgroundColor(Color.TRANSPARENT)
        } else {
            textView.setBackgroundColor(rotulos!!.cor_fundo)
        }*/
        textView.setTextColor(rotulos!!.cor)

        textView.text = objtexto!!.texto

        textView.setOnClickListener {
            tratarCliqueObjeto()
        }

        rlTela!!.addView(textView)

        rlTela!!.invalidate()

        textView.postDelayed({
            desenhado = true
        }, 50)
    }

    private fun tratarCliqueObjeto() {

        if (rotulos!!.som == 1 && objsom != null) {
            if (UtilClass.existeArquivoLocal(aplicacao!!.act!!, objsom!!.arquivo)) {
                UtilClass.reproduzirAudioLocal(aplicacao!!.act!!, objsom!!.arquivo, mediaPlayer)
            } else {
                UtilClass.reproduzirAudioServidor(aplicacao!!.servidores!!.ser_link + "arquivos/" + objsom!!.arquivo,
                    mediaPlayer)
            }
        }

        if (rotulos!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_AVANCAR) {
            if (aplicacao!!.posicaoTelaLink == 0) {
                var msg = aplicacao!!.computarNotaTela()
                if (msg.isEmpty()) {
                    if (aplicacao!!.posicaoAulaTela + 1 == aplicacao!!.aulaTela.size) {//finalizar projeto
                        aplicacao!!.finalizarProjeto()
                    } else {
                        if (aplicacao!!.possuiExercicios() && UtilClass.retJanelaFinalizar(aplicacao!!.descricao!!) == UtilClass.AVALIAR) {
                            dialog = DialogCaixaMensagem(aplicacao!!.act!!,
                                aplicacao!!.act!!.getString(R.string.acertou),
                                aplicacao!!.act!!.getString(R.string.muito_bem),
                                View.OnClickListener {
                                    aplicacao!!.posicaoAulaTela++
                                    (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1,
                                        aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela,
                                        false)
                                    dialog!!.dismiss()
                                })
                            dialog!!.show()
                        } else {
                            aplicacao!!.posicaoAulaTela++
                            (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1,
                                aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela,
                                false)
                        }
                    }
                } else {
                    dialog = DialogCaixaMensagem(aplicacao!!.act!!, aplicacao!!.act!!.getString(R.string.reveja), msg,
                        View.OnClickListener {
                            dialog!!.dismiss()
                        })
                    dialog!!.show()
                }
            }// se estiver navegando pelos links, deve ignorar
        } else if (rotulos!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_VOLTAR) {
            if (aplicacao!!.posicaoTelaLink > 0) {
                aplicacao!!.aulaIncompleta.removeAt(aplicacao!!.posicaoTelaLink)
                aplicacao!!.posicaoTelaLink--
                if (aplicacao!!.posicaoTelaLink == 0) {
                    aplicacao!!.aulaIncompleta.removeAt(aplicacao!!.posicaoTelaLink)
                    (aplicacao!!.act!! as ActivityApresentacao).carregarTela(aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela)
                } else {
                    (aplicacao!!.act!! as ActivityApresentacao).carregarTela(aplicacao!!.aulaIncompleta[aplicacao!!.posicaoTelaLink].id_tela)
                }
            } else {
                aplicacao!!.posicaoAulaTela--
                (aplicacao!!.act!! as ActivityApresentacao).carregarTela(aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela)
            }
        } else if (rotulos!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_SAIR) {
            (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1, aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela, true)
        } else if (rotulos!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_ORIGEM) {
            if (aplicacao!!.posicaoTelaLink > 0) {//serve apenas para link
                aplicacao!!.aulaIncompleta = ArrayList()
                aplicacao!!.posicaoTelaLink = 0
                (aplicacao!!.act!! as ActivityApresentacao).carregarTela(aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela)
            }
        } else if (rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ESCAPAR) {
            //verificar depois como deve funcionar
        } else if (rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
            if (UtilClass.retJanelaFinalizar(aplicacao!!.descricao!!) == UtilClass.AVALIAR && !clicouObjeto) {
                dialog = DialogCaixaMensagem(aplicacao!!.act!!, aplicacao!!.act!!.getString(R.string.acertou), aplicacao!!.act!!.getString(R.string.parabens), View.OnClickListener {
                    dialog!!.dismiss()
                })
                dialog!!.show()
            }
        } else if (rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO) {
            if (!clicouObjeto) {
                if (UtilClass.retJanelaFinalizar(aplicacao!!.descricao!!) == UtilClass.AVALIAR) {
                    dialog = DialogCaixaMensagem(aplicacao!!.act!!,
                        aplicacao!!.act!!.getString(R.string.errou),
                        aplicacao!!.act!!.getString(R.string.pense_respeito),
                        View.OnClickListener {
                            dialog!!.dismiss()
                        })
                    dialog!!.show()
                }
            }
        } else if (rotulos!!.link == 1 && objlink != null) {
            var msg = aplicacao!!.computarNotaTela()
            if (msg.isEmpty()) {
                if (aplicacao!!.possuiExercicios() && UtilClass.retJanelaFinalizar(aplicacao!!.descricao!!) == UtilClass.AVALIAR) {
                    dialog = DialogCaixaMensagem(aplicacao!!.act!!,
                        aplicacao!!.act!!.getString(R.string.acertou),
                        aplicacao!!.act!!.getString(R.string.muito_bem),
                        View.OnClickListener {
                            var aulaIncompleta: AulaIncompleta
                            if (aplicacao!!.aulaIncompleta.size == 0) {
                                aulaIncompleta = AulaIncompleta()
                                aulaIncompleta.id_projeto = aplicacao!!.descricao!!.id_projeto
                                aulaIncompleta.id_aluno = aplicacao!!.aluno!!.id_aluno
                                aulaIncompleta.codigo_aula = aplicacao!!.descricao!!.codigo
                                aulaIncompleta.codigo_aluno = aplicacao!!.aluno!!.codigo
                                aulaIncompleta.sequencia = aplicacao!!.aulaIncompleta.size
                                aulaIncompleta.tela = aplicacao!!.classTela!!.tela!!.codigo
                                aulaIncompleta.id_tela = aplicacao!!.classTela!!.tela!!.id_tela
                                aplicacao!!.aulaIncompleta.add(aulaIncompleta)
                            }
                            aulaIncompleta = AulaIncompleta()
                            aulaIncompleta.id_projeto = aplicacao!!.descricao!!.id_projeto
                            aulaIncompleta.id_aluno = aplicacao!!.aluno!!.id_aluno
                            aulaIncompleta.codigo_aula = aplicacao!!.descricao!!.codigo
                            aulaIncompleta.codigo_aluno = aplicacao!!.aluno!!.codigo
                            aulaIncompleta.sequencia = aplicacao!!.aulaIncompleta.size
                            aulaIncompleta.tela = objlink!!.link
                            aulaIncompleta.id_tela = objlink!!.id_tela_link
                            aplicacao!!.aulaIncompleta.add(aulaIncompleta)
                            aplicacao!!.posicaoTelaLink = aplicacao!!.aulaIncompleta.size - 1

                            (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1,
                                aplicacao!!.aulaIncompleta[aplicacao!!.posicaoTelaLink].id_tela,
                                false)
                            dialog!!.dismiss()
                        })
                    dialog!!.show()
                } else {
                    var aulaIncompleta: AulaIncompleta
                    if (aplicacao!!.aulaIncompleta.size == 0) {
                        aulaIncompleta = AulaIncompleta()
                        aulaIncompleta.id_projeto = aplicacao!!.descricao!!.id_projeto
                        aulaIncompleta.id_aluno = aplicacao!!.aluno!!.id_aluno
                        aulaIncompleta.codigo_aula = aplicacao!!.descricao!!.codigo
                        aulaIncompleta.codigo_aluno = aplicacao!!.aluno!!.codigo
                        aulaIncompleta.sequencia = aplicacao!!.aulaIncompleta.size
                        aulaIncompleta.tela = aplicacao!!.classTela!!.tela!!.codigo
                        aulaIncompleta.id_tela = aplicacao!!.classTela!!.tela!!.id_tela
                        aplicacao!!.aulaIncompleta.add(aulaIncompleta)
                    }
                    aulaIncompleta = AulaIncompleta()
                    aulaIncompleta.id_projeto = aplicacao!!.descricao!!.id_projeto
                    aulaIncompleta.id_aluno = aplicacao!!.aluno!!.id_aluno
                    aulaIncompleta.codigo_aula = aplicacao!!.descricao!!.codigo
                    aulaIncompleta.codigo_aluno = aplicacao!!.aluno!!.codigo
                    aulaIncompleta.sequencia = aplicacao!!.aulaIncompleta.size
                    aulaIncompleta.tela = objlink!!.link
                    aulaIncompleta.id_tela = objlink!!.id_tela_link
                    aplicacao!!.aulaIncompleta.add(aulaIncompleta)
                    aplicacao!!.posicaoTelaLink = aplicacao!!.aulaIncompleta.size - 1

                    (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1,
                        aplicacao!!.aulaIncompleta[aplicacao!!.posicaoTelaLink].id_tela,
                        false)
                }
            } else {
                dialog = DialogCaixaMensagem(aplicacao!!.act!!, aplicacao!!.act!!.getString(R.string.reveja), msg,
                    View.OnClickListener {
                        dialog!!.dismiss()
                    })
                dialog!!.show()
            }
        }
        clicouObjeto = true
    }
}