package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.widget.*
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.Objimagem
import br.classinformatica.vcandroid.classnetk.entidades.Objsom
import br.classinformatica.vcandroid.classnetk.entidades.Tela
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File

class ClassTela {

    var altImgFundo: Int = 0
    var aplicacao: Aplicacao? = null
    var larImgFundo: Int = 0
    private var mediaPlayer = MediaPlayer()
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null
    var tipoLayout = 1 //1 = FrameLayout, 2 = RelativeLayout

    var tela: Tela? = null
    var objimagem: Objimagem? = null
    var objsom: Objsom? = null

    fun calcularPesos(larImgFundo: Int, altImgFundo: Int) {
        this.larImgFundo = larImgFundo
        this.altImgFundo = altImgFundo
        if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_TAMANHO_ORIGINAL) {
            pesoX = 1F
            pesoY = 1F
        } else {
            var disLarguraMaior = true
            var difLargura = aplicacao!!.metrics!!.widthPixels - tela!!.largura
            if (difLargura < 0) {//a largura do projeto é maior
                disLarguraMaior = false
                difLargura = tela!!.largura - aplicacao!!.metrics!!.widthPixels
            }
            var porcLargura = ((difLargura.toFloat() * 100) / tela!!.largura.toFloat()) / 100

            var disAlturaMaior = true
            var difAltura = aplicacao!!.metrics!!.heightPixels - tela!!.altura
            if (difAltura < 0) {//a altura do projeto é maior
                disAlturaMaior = false
                difAltura = tela!!.altura - aplicacao!!.metrics!!.heightPixels
            }
            var porcAltura = ((difAltura.toFloat() * 100) / tela!!.altura.toFloat()) / 100

            if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_TODA_TELA) {
                if (disLarguraMaior) {
                    pesoX = 1F + porcLargura
                } else {
                    pesoX = 1F - porcLargura
                }
                if (disAlturaMaior) {
                    pesoY = 1F + porcAltura
                } else {
                    pesoY = 1F - porcAltura
                }
            } else if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_LARGURA_TELA) {
                if (disLarguraMaior) {
                    pesoX = 1F + porcLargura
                    pesoY = 1F + porcLargura
                } else {
                    pesoX = 1F - porcLargura
                    pesoY = 1F - porcLargura
                }
            } else if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_MAXIMO_PROPORCIONAL) {
                if (porcLargura < porcAltura) {
                    if (disLarguraMaior) {
                        pesoX = 1F + porcLargura
                        pesoY = 1F + porcLargura
                    } else {
                        pesoX = 1F - porcLargura
                        pesoY = 1F - porcLargura
                    }
                } else {
                    if (disAlturaMaior) {
                        pesoX = 1F + porcAltura
                        pesoY = 1F + porcAltura
                    } else {
                        pesoX = 1F - porcAltura
                        pesoY = 1F - porcAltura
                    }
                }
            }
        }
    }

    fun desenhar(act: ActivityApresentacao) {
        try {
            rlTela!!.removeAllViews()

            //desenhando tela -------------------------------------------------------
            rlTela!!.setBackgroundColor(tela!!.cor)

            //cor de fundo
            if (tipoLayout == 1) {
                var rl = FrameLayout.LayoutParams(Math.round(tela!!.largura * pesoX),
                    Math.round(tela!!.altura * pesoY))
                rlTela!!.layoutParams = rl
            } else {
                var rl = RelativeLayout.LayoutParams(Math.round(tela!!.largura * pesoX),
                    Math.round(tela!!.altura * pesoY))
                rl.addRule(RelativeLayout.CENTER_IN_PARENT)
                rlTela!!.layoutParams = rl
            }

            //imagem de fundo
            if (tela!!.imagem == 1 && objimagem != null) {
                if (altImgFundo > 0 && larImgFundo > 0) {
                    var rlImagemFundo =
                        RelativeLayout.LayoutParams(Math.round(larImgFundo * pesoX), Math.round(altImgFundo * pesoY))
                    var ivImagemFundo = ImageView(act)
                    ivImagemFundo.layoutParams = rlImagemFundo

                    rlTela!!.addView(ivImagemFundo)

                    if (UtilClass.existeArquivoLocal(act, objimagem!!.arquivo)) {
                        UtilClass.carregarImagemImageViewLocal(act, ivImagemFundo, objimagem!!.arquivo)
                    } else {
                        UtilClass.carregarImagemImageView(act,
                            aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagem!!.arquivo,
                            true,
                            ivImagemFundo,
                            aplicacao!!.configuracoes!!)
                    }
                    desenharObjetos(act)
                } else {
                    if (UtilClass.existeArquivoLocal(act, objimagem!!.arquivo)) {
                        /*UtilClass.carregarImagemImageViewLocal(act, objimagem!!.arquivo, object : Target {
                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            }

                            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                                desenharObjetos(act)
                            }

                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                var rlImagemFundo =
                                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT)

                                var ivImagemFundo = ImageView(act)
                                ivImagemFundo.scaleType = ImageView.ScaleType.FIT_XY
                                ivImagemFundo.layoutParams = rlImagemFundo

                                ivImagemFundo.setImageBitmap(bitmap!!)
                                altImgFundo = bitmap.height
                                larImgFundo = bitmap.width
                                rlImagemFundo = RelativeLayout.LayoutParams(Math.round(bitmap.width * pesoX),
                                    Math.round(bitmap.height * pesoY))
                                ivImagemFundo.layoutParams = rlImagemFundo

                                rlTela!!.addView(ivImagemFundo)

                                rlTela!!.invalidate()

                                desenharObjetos(act)
                            }
                        })*/

                        var rlImagemFundo =
                            RelativeLayout.LayoutParams(Math.round(larImgFundo * pesoX), Math.round(altImgFundo * pesoY))
                        var iv = ImageView(act)
                        iv.scaleType = ImageView.ScaleType.FIT_XY
                        iv.layoutParams = rlImagemFundo

                        var bitmap = UtilClass.carregarImagemImageViewLocal(act, iv, objimagem!!.arquivo)

                        iv.postDelayed({
                            larImgFundo = bitmap.width
                            altImgFundo = bitmap.height

                            var rlImagemFundo =
                                RelativeLayout.LayoutParams(Math.round(larImgFundo * pesoX),
                                    Math.round(altImgFundo * pesoY))
                            var ivImagemFundo = ImageView(act)
                            ivImagemFundo.scaleType = ImageView.ScaleType.FIT_XY
                            ivImagemFundo.setImageBitmap(bitmap)
                            ivImagemFundo.layoutParams = rlImagemFundo

                            rlTela!!.addView(ivImagemFundo)

                            desenharObjetos(act)
                        }, 100)
                    } else {
                        UtilClass.carregarImagemImageView(act,
                            aplicacao!!.servidores!!.ser_link + "arquivos/" + objimagem!!.arquivo,
                            false,
                            aplicacao!!.configuracoes!!,
                            object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {UtilClass.trataErro("PASSO", "PASSO_tela")
                                    var rlImagemFundo =
                                        RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT)
                                    var ivImagemFundo = ImageView(act)
                                    ivImagemFundo.scaleType = ImageView.ScaleType.FIT_XY
                                    ivImagemFundo.layoutParams = rlImagemFundo

                                    ivImagemFundo.setImageBitmap(bitmap!!)
                                    altImgFundo = bitmap.height
                                    larImgFundo = bitmap.width
                                    rlImagemFundo = RelativeLayout.LayoutParams(Math.round(bitmap.width * pesoX),
                                        Math.round(bitmap.height * pesoY))
                                    ivImagemFundo.layoutParams = rlImagemFundo
                                    rlTela!!.addView(ivImagemFundo)
                                    rlTela!!.invalidate()
                                    desenharObjetos(act)
                                }

                                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                    UtilClass.trataErro(e!!)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                }
                            })
                    }
                }
            } else {
                desenharObjetos(act)
            }

            rlTela!!.invalidate()
        } catch (e: Exception) {
            UtilClass.trataErro(e)
        }

        //desenhando tela -------------------------------------------------------
    }

    private fun desenharObjetos(act: ActivityApresentacao) {
        var qtde: Int

        //desenhar objetos imagens
        for (i in 0 until aplicacao!!.classImagensEfeitos.size) {UtilClass.trataErro("PASSO", "PASSO_imagem")
            aplicacao!!.classImagensEfeitos[i].aplicacao = aplicacao
            aplicacao!!.classImagensEfeitos[i].pesoX = pesoX
            aplicacao!!.classImagensEfeitos[i].pesoY = pesoY
            aplicacao!!.classImagensEfeitos[i].rlTela = rlTela
            aplicacao!!.classImagensEfeitos[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classImagensEfeitos[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar objetos textos
        for (i in 0 until aplicacao!!.classTextos.size) {UtilClass.trataErro("PASSO", "PASSO_texto")
            aplicacao!!.classTextos[i].aplicacao = aplicacao
            aplicacao!!.classTextos[i].pesoX = pesoX
            aplicacao!!.classTextos[i].pesoY = pesoY
            aplicacao!!.classTextos[i].rlTela = rlTela
            aplicacao!!.classTextos[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classTextos[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar objetos rótulos
        for (i in 0 until aplicacao!!.classRotulos.size) {UtilClass.trataErro("PASSO", "PASSO_rotulo")
            aplicacao!!.classRotulos[i].aplicacao = aplicacao
            aplicacao!!.classRotulos[i].pesoX = pesoX
            aplicacao!!.classRotulos[i].pesoY = pesoY
            aplicacao!!.classRotulos[i].rlTela = rlTela
            aplicacao!!.classRotulos[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classRotulos[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar objetos filmes
        for (i in 0 until aplicacao!!.classFilmesTela.size) {UtilClass.trataErro("PASSO", "PASSO_filme")
            aplicacao!!.classFilmesTela[i].aplicacao = aplicacao
            aplicacao!!.classFilmesTela[i].pesoX = pesoX
            aplicacao!!.classFilmesTela[i].pesoY = pesoY
            aplicacao!!.classFilmesTela[i].rlTela = rlTela
            aplicacao!!.classFilmesTela[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classFilmesTela[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar objetos animações
        for (i in 0 until aplicacao!!.classAnimacoes.size) {UtilClass.trataErro("PASSO", "PASSO_animacao")
            aplicacao!!.classAnimacoes[i].aplicacao = aplicacao
            aplicacao!!.classAnimacoes[i].pesoX = pesoX
            aplicacao!!.classAnimacoes[i].pesoY = pesoY
            aplicacao!!.classAnimacoes[i].rlTela = rlTela
            aplicacao!!.classAnimacoes[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classAnimacoes[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar arrastar/soltar
        for (i in 0 until aplicacao!!.classArrastarSoltar.size) {UtilClass.trataErro("PASSO", "PASSO_arr_sol")
            aplicacao!!.classArrastarSoltar[i].aplicacao = aplicacao
            aplicacao!!.classArrastarSoltar[i].pesoX = pesoX
            aplicacao!!.classArrastarSoltar[i].pesoY = pesoY
            aplicacao!!.classArrastarSoltar[i].rlTela = rlTela
            aplicacao!!.classArrastarSoltar[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classArrastarSoltar[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar arrastar/soltar imagens
        for (i in 0 until aplicacao!!.classArrastarSoltarImagens.size) {UtilClass.trataErro("PASSO", "PASSO_arr_sol_img")
            aplicacao!!.classArrastarSoltarImagens[i].aplicacao = aplicacao
            aplicacao!!.classArrastarSoltarImagens[i].pesoX = pesoX
            aplicacao!!.classArrastarSoltarImagens[i].pesoY = pesoY
            aplicacao!!.classArrastarSoltarImagens[i].rlTela = rlTela
            aplicacao!!.classArrastarSoltarImagens[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classArrastarSoltarImagens[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar arrastar/soltar n
        for (i in 0 until aplicacao!!.classArrastarSoltarN.size) {UtilClass.trataErro("PASSO", "PASSO_arr_sol_n")
            aplicacao!!.classArrastarSoltarN[i].aplicacao = aplicacao
            aplicacao!!.classArrastarSoltarN[i].pesoX = pesoX
            aplicacao!!.classArrastarSoltarN[i].pesoY = pesoY
            aplicacao!!.classArrastarSoltarN[i].rlTela = rlTela
            aplicacao!!.classArrastarSoltarN[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classArrastarSoltarN[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar gira figuras
        for (i in 0 until aplicacao!!.classGirasEfeitos.size) {UtilClass.trataErro("PASSO", "PASSO_gira")
            aplicacao!!.classGirasEfeitos[i].aplicacao = aplicacao
            aplicacao!!.classGirasEfeitos[i].pesoX = pesoX
            aplicacao!!.classGirasEfeitos[i].pesoY = pesoY
            aplicacao!!.classGirasEfeitos[i].rlTela = rlTela
            aplicacao!!.classGirasEfeitos[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classGirasEfeitos[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar preenchimentos
        for (i in 0 until aplicacao!!.classPreenchimentos.size) {UtilClass.trataErro("PASSO", "PASSO_preenchimento")
            aplicacao!!.classPreenchimentos[i].aplicacao = aplicacao
            aplicacao!!.classPreenchimentos[i].pesoX = pesoX
            aplicacao!!.classPreenchimentos[i].pesoY = pesoY
            aplicacao!!.classPreenchimentos[i].rlTela = rlTela
            aplicacao!!.classPreenchimentos[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classPreenchimentos[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar dissertativas
        for (i in 0 until aplicacao!!.classDissertativa.size) {UtilClass.trataErro("PASSO", "PASSO_dissertativa")
            aplicacao!!.classDissertativa[i].aplicacao = aplicacao
            aplicacao!!.classDissertativa[i].pesoX = pesoX
            aplicacao!!.classDissertativa[i].pesoY = pesoY
            aplicacao!!.classDissertativa[i].rlTela = rlTela
            aplicacao!!.classDissertativa[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classDissertativa[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        //desenhar testes de vestibulares
        for (i in 0 until aplicacao!!.classTestesVest.size) {UtilClass.trataErro("PASSO", "PASSO_teste_vest")
            aplicacao!!.classTestesVest[i].aplicacao = aplicacao
            aplicacao!!.classTestesVest[i].pesoX = pesoX
            aplicacao!!.classTestesVest[i].pesoY = pesoY
            aplicacao!!.classTestesVest[i].rlTela = rlTela
            aplicacao!!.classTestesVest[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classTestesVest[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }



        //desenhar objetos linhas SEMPRE NO FINAL

        //desenhar liga pontos
        for (i in 0 until aplicacao!!.classLigas.size) {
            aplicacao!!.classLigas[i].aplicacao = aplicacao
            aplicacao!!.classLigas[i].pesoX = pesoX
            aplicacao!!.classLigas[i].pesoY = pesoY
            aplicacao!!.classLigas[i].rlTela = rlTela
            aplicacao!!.classLigas[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classLigas[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        for (i in 0 until aplicacao!!.classLinhas.size) {
            aplicacao!!.classLinhas[i].aplicacao = aplicacao
            aplicacao!!.classLinhas[i].pesoX = pesoX
            aplicacao!!.classLinhas[i].pesoY = pesoY
            aplicacao!!.classLinhas[i].rlTela = rlTela
            aplicacao!!.classLinhas[i].desenhar(act)

            qtde = 50
            while (qtde < 50 && !aplicacao!!.classLinhas[i].desenhado) {
                try {
                    Thread.sleep(100)
                } catch (e: Exception) {}
                qtde++
            }
        }

        rlTela!!.invalidate()

        //reproduzir som de fundo após carregar tudo
        if (tela!!.som_autoexec == 1 && objsom != null) {
            if (UtilClass.existeArquivoLocal(act, objsom!!.arquivo)) {
                UtilClass.reproduzirAudioLocal(act, objsom!!.arquivo, mediaPlayer)
            } else {
                UtilClass.reproduzirAudioServidor(aplicacao!!.servidores!!.ser_link + "arquivos/" + objsom!!.arquivo,
                    mediaPlayer)
            }
        }
    }

    fun instanciaLayout(act: ActivityApresentacao) {
        tipoLayout = 1
        var lVazio = RelativeLayout.LayoutParams(0, 0)
        var lHsvHorizontalVertical = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        var lHsvHorizontal = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        var lSvVertical = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        //não é necessário fazer o rlTela4 pois na hora de desenhar ele faz

        lHsvHorizontal.addRule(RelativeLayout.CENTER_IN_PARENT)
        lSvVertical.addRule(RelativeLayout.CENTER_IN_PARENT)

        var hsvHorizontalVertical = act.findViewById<HorizontalScrollView>(R.id.hsvHorizontalVertical)
        var hsvHorizontal = act.findViewById<HorizontalScrollView>(R.id.hsvHorizontal)
        var svVertical = act.findViewById<ScrollView>(R.id.svVertical)
        var rlTela4 = act.findViewById<RelativeLayout>(R.id.rlTela4)

        if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_TAMANHO_ORIGINAL) {
            if (tela!!.largura > aplicacao!!.metrics!!.widthPixels && tela!!.altura > aplicacao!!.metrics!!.heightPixels) {//scroll na largura e altura
                hsvHorizontalVertical.layoutParams = lHsvHorizontalVertical
                hsvHorizontal.layoutParams = lVazio
                svVertical.layoutParams = lVazio
                rlTela4.layoutParams = lVazio

                rlTela = act.findViewById(R.id.rlTela1)
            } else if (tela!!.largura > aplicacao!!.metrics!!.widthPixels && tela!!.altura <= aplicacao!!.metrics!!.heightPixels) {//scroll na largura
                hsvHorizontalVertical.layoutParams = lVazio
                hsvHorizontal.layoutParams = lHsvHorizontal
                svVertical.layoutParams = lVazio
                rlTela4.layoutParams = lVazio

                rlTela = act.findViewById(R.id.rlTela2)
            } else if (tela!!.largura <= aplicacao!!.metrics!!.widthPixels && tela!!.altura > aplicacao!!.metrics!!.heightPixels) {//scroll na altura
                hsvHorizontalVertical.layoutParams = lVazio
                hsvHorizontal.layoutParams = lVazio
                svVertical.layoutParams = lSvVertical
                rlTela4.layoutParams = lVazio

                rlTela = act.findViewById(R.id.rlTela3)
            } else {//sem scroll
                hsvHorizontalVertical.layoutParams = lVazio
                hsvHorizontal.layoutParams = lVazio
                svVertical.layoutParams = lVazio

                rlTela = act.findViewById(R.id.rlTela4)
                tipoLayout = 2
            }
        } else if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_TODA_TELA) {//sem scroll
            hsvHorizontalVertical.layoutParams = lVazio
            hsvHorizontal.layoutParams = lVazio
            svVertical.layoutParams = lVazio

            rlTela = act.findViewById(R.id.rlTela4)
            tipoLayout = 2
        } else if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_LARGURA_TELA) {//scroll na altura
            hsvHorizontalVertical.layoutParams = lVazio
            hsvHorizontal.layoutParams = lVazio
            svVertical.layoutParams = lSvVertical
            rlTela4.layoutParams = lVazio

            rlTela = act.findViewById(R.id.rlTela3)
        } else if (aplicacao!!.descricao!!.redim_telas == UtilClass.REDIM_TELAS_MAXIMO_PROPORCIONAL) {//sem scroll
            hsvHorizontalVertical.layoutParams = lVazio
            hsvHorizontal.layoutParams = lVazio
            svVertical.layoutParams = lVazio

            rlTela = act.findViewById(R.id.rlTela4)
            tipoLayout = 2
        }
    }
}