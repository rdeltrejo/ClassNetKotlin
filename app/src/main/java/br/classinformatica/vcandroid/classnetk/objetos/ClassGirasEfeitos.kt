package br.classinformatica.vcandroid.classnetk.objetos

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityApresentacao
import br.classinformatica.vcandroid.classnetk.entidades.GirasEfeitos
import br.classinformatica.vcandroid.classnetk.entidades.Objenunciado
import br.classinformatica.vcandroid.classnetk.entidades.Objimagem
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ClassGirasEfeitos {

    private var listaIv = ArrayList<Bitmap>()
    private var imageView: ImageView? = null
    var posicaoFigura = 0
    private var rl: RelativeLayout.LayoutParams? = null

    var aplicacao: Aplicacao? = null
    var desenhado = false
    var pesoX: Float = 1F
    var pesoY: Float = 1F
    var rlTela: RelativeLayout? = null

    var girasEfeitos: GirasEfeitos? = null
    var objimagem = ArrayList<Objimagem>()
    var objenunciado: Objenunciado? = null

    private fun baixarImagens(indice: Int, act: ActivityApresentacao) {
        if (indice < objimagem.size) {
            var iv = ImageView(act)
            iv.scaleType = ImageView.ScaleType.FIT_XY
            iv.layoutParams = rl
            if (UtilClass.existeArquivoLocal(act, objimagem[indice].arquivo)) {
                UtilClass.carregarImagemImageViewLocal(act, objimagem[indice].arquivo, object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                        iv.setImageResource(R.drawable.olho)
                        listaIv.add((iv.drawable as BitmapDrawable).bitmap)
                        baixarImagens(indice + 1, act)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        listaIv.add(bitmap!!)
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
        rl = RelativeLayout.LayoutParams(Math.round(girasEfeitos!!.largura * pesoX), Math.round(girasEfeitos!!.altura * pesoY))
        rl!!.leftMargin = Math.round(girasEfeitos!!.esquerdo * pesoX)
        rl!!.topMargin = Math.round(girasEfeitos!!.topo * pesoY)

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
        imageView!!.scaleType = ImageView.ScaleType.FIT_XY
        imageView!!.layoutParams = rl
        imageView!!.setImageBitmap(listaIv[0])
        imageView!!.setOnClickListener {
            posicaoFigura++
            if (posicaoFigura >= listaIv.size) {
                posicaoFigura = 0
            }
            imageView!!.setImageBitmap(listaIv[posicaoFigura])
        }
        rlTela!!.addView(imageView!!)

        rlTela!!.invalidate()

        imageView!!.postDelayed({
            desenhado = true
        }, 50)
    }
}