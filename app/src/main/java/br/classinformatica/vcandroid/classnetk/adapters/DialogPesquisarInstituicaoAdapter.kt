package br.classinformatica.vcandroid.classnetk.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import br.classinformatica.vcandroid.classnetk.MainActivity
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.dialogs.DialogPesquisarInstituicao
import br.classinformatica.vcandroid.classnetk.entidades.Instituicao
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class DialogPesquisarInstituicaoAdapter : BaseAdapter {

    private var act: MainActivity
    private var dialog: DialogPesquisarInstituicao? = null
    private var instituicoes: List<Instituicao>
    private var layoutInflater: LayoutInflater


    constructor(act: MainActivity, dialog: DialogPesquisarInstituicao, instituicoes: List<Instituicao>) : super() {
        this.instituicoes = instituicoes
        this.act = act
        this.layoutInflater = LayoutInflater.from(act)
        this.dialog = dialog
    }

    override fun getItem(p0: Int): Any {
        return instituicoes[p0]
    }

    override fun getCount(): Int {
        return instituicoes.size
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = layoutInflater.inflate(R.layout.dialog_pesquisar_instituicao_adapter, null)

        var ivInstituicao = view.findViewById<ImageView>(R.id.ivInstituicao)
        ivInstituicao.tag = position
        ivInstituicao.setOnTouchListener { view, motionEvent ->
            if (view != null && motionEvent != null) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    //(view as ImageView).setBackgroundResource(R.drawable.borda_errado)
                    var pos = view.tag as Int
                    if (instituicoes[pos].bloqueado == 0) {
                        act.selecionouInstituicao(instituicoes[pos])
                        dialog!!.dismiss()
                    } else {
                        UtilClass.msgOk(act, R.string.aviso, R.string.instituicao_bloqueada, R.string.ok, null)
                    }
                }
            }
            false
        }

        var tvCodigo = view.findViewById<TextView>(R.id.tvCodigo)
        tvCodigo.text = instituicoes[position].codigo

        var tvTitulo = view.findViewById<TextView>(R.id.tvTitulo)
        tvTitulo.text = instituicoes[position].titulo

        return view
    }
}