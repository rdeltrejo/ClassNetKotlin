package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date
import java.util.*

class AulaAtribuida {
    var id_projeto: Long? = 0
    var id_aluno: Long? = 0
    var codigo_aula: String? = ""
    var codigo_aluno: String? = ""
    var status: Int? = 0
    var sequencia: Int? = 0
    var materia: String? = ""
    var tela: String? = ""
    var pasta: String? = ""
    var id_autor: Long? = 0
    var data_final_atribuicao: Date? = Date(Calendar.getInstance().timeInMillis)
    var enviar_email: Int? = 0
    var nota_limite: Double? = 0.toDouble()
    var enviar_email_fim: Int? = 0
    var enviar_aviso: Int? = 0
    var duracao: Int? = 0
    var inicio: Date? = Date(Calendar.getInstance().timeInMillis)
}