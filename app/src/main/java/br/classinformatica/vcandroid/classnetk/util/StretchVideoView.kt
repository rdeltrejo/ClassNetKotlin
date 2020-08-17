package br.classinformatica.vcandroid.classnetk.util

import android.content.Context
import android.widget.VideoView

class StretchVideoView(context: Context?) : VideoView(context) {

    var altura: Int = 0
    var largura: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(largura, altura)
    }
}