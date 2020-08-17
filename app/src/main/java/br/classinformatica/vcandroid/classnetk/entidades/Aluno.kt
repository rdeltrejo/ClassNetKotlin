package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date
import java.util.*

class Aluno {
    var id_aluno: Long = 0
    var id_instituicao: Long = 0
    var id_serie: Long = 0
    var id_classe_inf: Long = 0
    var id_classe: Long = 0
    var codigo: String = ""
    var serie: String = ""
    var escola: String = ""
    var nome: String = ""
    var data_de_nascimento: Date = Date(Calendar.getInstance().timeInMillis)
    var classe_colegio: String = ""
    var codigo_classe_informatica: String = ""
    var telefone: String = ""
    var endereco: String = ""
    var cidade: String = ""
    var estado: String = ""
    var pais: String = ""
    var bairro: String = ""
    var cep: String = ""
    var foto: String = ""
    var email: String = ""
    var tela_aluno: String = ""
    var site: String = ""
    var pasta_aluno: String = ""
    var senha: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var sexo: String = ""
    var autocadastro: Int = 0
    var ddd: String = ""
    var celular: String = ""
}