package br.classinformatica.vcandroid.classnetk.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import br.classinformatica.vcandroid.classnetk.Aplicacao
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityProjetosAtribuidos
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ActivityProjetosAtribuidosAdapter : BaseAdapter {

    private var act: ActivityProjetosAtribuidos? = null
    private var aplicacao: Aplicacao? = null
    private var inflater: LayoutInflater

    constructor(act: ActivityProjetosAtribuidos) : super() {
        this.act = act
        this.aplicacao = act!!.application as Aplicacao
        this.inflater = LayoutInflater.from(act)
    }

    override fun getItem(position: Int): Any {
        return aplicacao!!.atribuidosDescricao!![position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return aplicacao!!.atribuidosDescricao!!.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View
        if (UtilClass.isRetrato(act!!)) {
            view = inflater.inflate(R.layout.activity_projetos_atribuidos_retrato_adapter, null)
        } else {
            view = inflater.inflate(R.layout.activity_projetos_atribuidos_adapter, null)
        }

        var ivIcone = view.findViewById<ImageView>(R.id.ivIcone)
        if (aplicacao!!.atribuidosAulaAtrbuida!![position].status == 2) {
            ivIcone!!.setImageResource(R.drawable.icone_projeto_concluido)
        } else if (aplicacao!!.atribuidosAulaAtrbuida!![position].status == 1) {
            ivIcone!!.setImageResource(R.drawable.icone_projeto_pendente)
        } else {
            ivIcone!!.setImageResource(R.drawable.icone_projeto)
        }
        ivIcone.tag = position
        ivIcone.setOnTouchListener { view, motionEvent ->
            if (view != null) {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    (view as ImageView).setBackgroundResource(R.drawable.borda_errado)
                } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                    (view as ImageView).setBackgroundResource(0)
                }
                var pos = view.tag as Int
                act!!.selecionouProjeto(aplicacao!!.atribuidosDescricao!![pos])
            }
            false
        }

        var tvCodigo = view.findViewById<TextView>(R.id.tvCodigo)
        tvCodigo.text = aplicacao!!.atribuidosDescricao!![position].codigo

        var tvTitulo = view.findViewById<TextView>(R.id.tvTitulo)
        tvTitulo.text = aplicacao!!.atribuidosDescricao!![position].nome

        return view
    }
}