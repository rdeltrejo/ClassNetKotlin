package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date
import java.util.*

class AulaFeita {
    var id_projeto: Long = 0
    var id_aluno: Long = 0
    var codigo_aluno: String = ""
    var codigo_aula: String = ""
    var data: Date = Date(Calendar.getInstance().timeInMillis)
    var nota: Double = 0.toDouble()
    var tentativas: Int = 0
    var erros_anterior: Int = 0
    var acertos_anterior: Int = 0
    var total_minutos: Int = 0
    var ultima_tela_feita: String = ""
    var nome: String = ""
    var autor: String = ""
    var assunto: String = ""
    var materia: String = ""
    var alvo: String = ""
    var pendente: Int = 0
    var status: Int = 0
    var data_inicio: Date = Date(Calendar.getInstance().timeInMillis)
    var cod_certificacao: Int = 0
    var autor_atribuicao: String = ""
    var origem: String = ""
    var tempo_tmp: Double = 0.toDouble()
    var id_ultima_tela_feita: Long = 0
}