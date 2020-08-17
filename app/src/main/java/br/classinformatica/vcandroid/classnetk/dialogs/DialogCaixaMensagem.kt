package br.classinformatica.vcandroid.classnetk.dialogs

import android.app.Dialog
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.R

class DialogCaixaMensagem : Dialog {

    private var act: AppCompatActivity? = null

    constructor(act: AppCompatActivity, titulo: String, mensagem: String, onclick: View.OnClickListener?) : super(act) {
        this.act = act
        setContentView(R.layout.dialog_caixa_mensagem)

        var tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        tvTitulo.text = titulo

        var tvMensagem = findViewById<TextView>(R.id.tvMensagem)
        tvMensagem.text = mensagem

        var bOk = findViewById<Button>(R.id.bOk)
        bOk.setOnClickListener(onclick)
    }
}