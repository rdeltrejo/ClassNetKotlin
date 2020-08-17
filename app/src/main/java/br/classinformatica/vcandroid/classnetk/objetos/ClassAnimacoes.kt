package br.classinformatica.vcandroid.classnetk.objetos

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.AsyncTask
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.dialogs.DialogCaixaMensagem
import br.classinformatica.vcandroid.classnetk.entidades.*
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class ClassAnimacoes {

    private var animarTask: AnimarTask? = null
    private var dialog: DialogCaixaMensagem? = null
    private var listaIv = ArrayList<Bitmap>()
    private var imageView: ImageView? = null
    private var mediaPlayer = MediaPlayer()
    private var rl: RelativeLayout.LayoutParams? = null

    var aplicacao: Aplicacao? = null
    var clicouObjeto = false
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var animacoes: Animacoes? = null
    var objimagem = ArrayList<Objimagem>()
    var objsom: Objsom? = null
    var objenunciado: Objenunciado? = null
    var objlink: Objlink? = null

    private fun baixarImagens(indice: Int, act: ActivityApresentacao) {
        if (indice < objimagem.size) {
            var iv = ImageView(act)
            iv.setBackgroundColor(Color.TRANSPARENT)
            iv.scaleType = ImageView.ScaleType.FIT_XY
            iv.layoutParams = rl
            if (UtilClass.existeArquivoLocal(act, objimagem[indice].arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act, objimagem[indice].arquivo, object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        iv.setImageResource(R.drawable.olho)
                        listaIv.add((iv.drawable as BitmapDrawable).bitmap)
                        baixarImagens(indice + 1, act)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        if (animacoes!!.transparente == 1) {
                            var bmp = Bitmap.createBitmap(bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
                            try {
                                for (x in 0 until bitmap!!.width) {
                                    for (y in 0 until bitmap!!.height) {
                                        if (bitmap!!.getPixel(x, y) == animacoes!!.cor_transparente) {
                                            bmp.setPixel(x, y, Color.TRANSPARENT)
                                        } else {
                                            bmp.setPixel(x, y, bitmap!!.getPixel(x, y))
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                UtilClass.trataErro(e)
                            }
                            listaIv.add(bmp)
                        } else {
                            listaIv.add(bitmap!!)
                        }
                        baixarImagens(indice + 1, act)
                    }
                })
            } else {
                UtilClass.carregarImagemImageView(act,
                    aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagem[indice].arquivo,
                    true,
                    aplicacao!!.configuracoes!!,
                    object : Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            iv.setImageResource(R.drawable.olho)
                            listaIv.add((iv.drawable as BitmapDrawable).bitmap)
                            baixarImagens(indice + 1, act)
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            if (animacoes!!.transparente == 1) {
                                try {
                                    for (x in 0 until bitmap!!.width) {
                                        for (y in 0 until bitmap!!.height) {
                                            if (bitmap!!.getPixel(x, y) == animacoes!!.cor_transparente) {
                                                bitmap!!.setPixel(x, y, Color.TRANSPARENT)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    UtilClass.trataErro(e)
                                }
                            }
                            listaIv.add(bitmap!!)
                            baixarImagens(indice + 1, act)
                        }
                    })
            }
        } else {
            desenharAnimacao(act)
        }
    }

    fun desenhar(act: ActivityApresentacao) {
        rl = RelativeLayout.LayoutParams(Math.round(animacoes!!.largura * pesoX), Math.round(animacoes!!.altura * pesoY))
        rl!!.leftMargin = Math.round(animacoes!!.esquerdo * pesoX)
        rl!!.topMargin = Math.round(animacoes!!.topo * pesoY)

        if (objimagem.size > 0) {
            baixarImagens(0, act)
        } else {
            var iv = ImageView(act)
            iv.scaleType = ImageView.ScaleType.FIT_XY
            iv.layoutParams = rl
            iv.setImageResource(R.drawable.olho)
            listaIv.add((iv.drawable as BitmapDrawable).bitmap)
            desenharAnimacao(act)
        }
    }

    private fun desenharAnimacao(act: ActivityApresentacao) {
        imageView = ImageView(act)
        imageView!!.setBackgroundColor(Color.TRANSPARENT)
        imageView!!.scaleType = ImageView.ScaleType.FIT_XY
        imageView!!.layoutParams = rl
        imageView!!.setImageBitmap(listaIv[0])
        rlTela!!.addView(imageView!!)
        imageView!!.postDelayed({
            desenhado = true
        }, 50)

        if (animacoes!!.tempos_3 == 1) {
            imageView!!.setOnTouchListener { _, event ->
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    if (listaIv.size > 1) {
                        imageView!!.setImageBitmap(listaIv[1])
                    } else if (listaIv.size > 0) {
                        imageView!!.setImageBitmap(listaIv[0])
                    } else {
                        imageView!!.setImageResource(R.drawable.olho)
                    }
                    false
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    if (listaIv.size > 0) {
                        imageView!!.setImageBitmap(listaIv[0])
                    } else {
                        imageView!!.setImageResource(R.drawable.olho)
                    }
                    tratarCliqueObjeto()
                }
                true
                //v?.onTouchEvent(event) ?: false
            }
        } else {
            if (animacoes!!.som == 1 && objsom != null && animacoes!!.som_inicio_ani == 1) {
                if (UtilClass.existeArquivoLocal(act, objsom!!.arquivo)) {
                    UtilClass.reproduzirAudioLocal(act, objsom!!.arquivo, mediaPlayer)
                } else {
                    UtilClass.reproduzirAudioServidor(aplicacao!!.servidores!!.ser_link + "arquivos/" + objsom!!.arquivo, mediaPlayer)
                }
            }

            animarTask = AnimarTask()
            animarTask!!.animacoes = animacoes
            animarTask!!.listaIv = listaIv
            animarTask!!.imageView = imageView
            animarTask!!.mediaPlayer = mediaPlayer

            animarTask!!.execute()
        }

        rlTela!!.invalidate()
    }

    private fun tratarCliqueObjeto() {

        if (animacoes!!.som == 1 && objsom != null) {
            if (UtilClass.existeArquivoLocal(aplicacao!!.act!!, objsom!!.arquivo)) {
                UtilClass.reproduzirAudioLocal(aplicacao!!.act!!, objsom!!.arquivo, mediaPlayer)
            } else {
                UtilClass.reproduzirAudioServidor(aplicacao!!.servidores!!.ser_link + "arquivos/" + objsom!!.arquivo,
                    mediaPlayer)
            }
        }

        if (animacoes!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_AVANCAR) {
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
        } else if (animacoes!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_VOLTAR) {
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
        } else if (animacoes!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_SAIR) {
            (aplicacao!!.act!! as ActivityApresentacao).atualizarAtribuicaoTela(1, aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela, true)
        } else if (animacoes!!.imagem_avancar == UtilClass.IMAGEM_AVANCAR_ORIGEM) {
            if (aplicacao!!.posicaoTelaLink > 0) {//serve apenas para link
                aplicacao!!.aulaIncompleta = ArrayList()
                aplicacao!!.posicaoTelaLink = 0
                (aplicacao!!.act!! as ActivityApresentacao).carregarTela(aplicacao!!.aulaTela[aplicacao!!.posicaoAulaTela].id_tela)
            }
        } else if (animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ESCAPAR) {
            //verificar depois como deve funcionar
        } else if (animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
            if (UtilClass.retJanelaFinalizar(aplicacao!!.descricao!!) == UtilClass.AVALIAR && !clicouObjeto) {
                dialog = DialogCaixaMensagem(aplicacao!!.act!!, aplicacao!!.act!!.getString(R.string.acertou), aplicacao!!.act!!.getString(R.string.parabens), View.OnClickListener {
                    dialog!!.dismiss()
                })
                dialog!!.show()
            }
        } else if (animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO) {
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
        }
        if (animacoes!!.link == 1 && objlink != null) {
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

    private class AnimarTask : AsyncTask<Void, Void, Long>() {

        var animacoes: Animacoes? = null
        var ciclo = 0
        var finalizarAnimacao = false
        var imageView: ImageView? = null
        var listaIv = ArrayList<Bitmap>()
        var mediaPlayer: MediaPlayer? = null
        var posicao = 0

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: Void?): Long {
            while (!finalizarAnimacao) {

                try {
                    Thread.sleep((animacoes!!.tempoframe * 1000).toLong())
                    /*imageView!!.setImageBitmap(listaIv[posicao])
                    imageView!!.invalidate()*/
                    publishProgress(null)
                } catch(e: Exception) {
                    UtilClass.trataErro(e)
                }
            }
            return 0
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            posicao++
            if (animacoes!!.som_fim_anima == 1 && !mediaPlayer!!.isPlaying) {//animação para quando acaba o som
                posicao = 0
                finalizarAnimacao = true
            } else {
                if (posicao >= listaIv.size) {
                    posicao = 0
                    ciclo++
                    if (animacoes!!.ciclos == ciclo) {
                        finalizarAnimacao = true
                    }
                }
            }

            imageView!!.setImageBitmap(listaIv[posicao])
            imageView!!.invalidate()
        }
    }
}