package br.classinformatica.vcandroid.classnetk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.classinformatica.vcandroid.classnetk.R
import br.classinformatica.vcandroid.classnetk.activities.ActivityRelatorioNotas
import br.classinformatica.vcandroid.classnetk.entidades.AulaFeita
import br.classinformatica.vcandroid.classnetk.entidades.Descricao
import br.classinformatica.vcandroid.classnetk.util.UtilClass

class ActivityRelatorioNotasAdapter : BaseAdapter {

    private var act: ActivityRelatorioNotas? = null
    private var layoutInflater: LayoutInflater
    private var listAulaFeita: List<AulaFeita>
    private var listDescricao: List<Descricao>

    constructor(act: ActivityRelatorioNotas, listAulaFeita: List<AulaFeita>, listDescricao: List<Descricao>) : super() {
        this.listAulaFeita = listAulaFeita
        this.listDescricao = listDescricao
        this.act = act
        this.layoutInflater = LayoutInflater.from(act)
    }

    override fun getItem(p0: Int): Any {
        return listAulaFeita[p0]
    }

    override fun getCount(): Int {
        return listAulaFeita.size
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View
        if (UtilClass.isRetrato(act!!)) {
            view = layoutInflater.inflate(R.layout.activity_relatorio_notas_retrato_adapter, null)
        } else {
            view = layoutInflater.inflate(R.layout.activity_relatorio_notas_adapter, null)
        }

        var tvTitulo = view.findViewById<TextView>(R.id.tvTitulo)
        tvTitulo.text = listDescricao[position].nome

        var tvCodigo = view.findViewById<TextView>(R.id.tvCodigo)
        tvCodigo.text = listAulaFeita[position].codigo_aula

        var tvNota = view.findViewById<TextView>(R.id.tvNota)
        tvNota.text = listAulaFeita[position].nota.toString()

        var tvTentativas = view.findViewById<TextView>(R.id.tvTentativas)
        tvTentativas.text = listAulaFeita[position].tentativas.toString()

        var tvTempo = view.findViewById<TextView>(R.id.tvTempo)
        tvTempo.text = listAulaFeita[position].total_minutos.toString()

        return view
    }
}