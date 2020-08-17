package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date

class Instituicao {
    var id_instituicao: Long? = 0
    var id_classificacao: Long? = 0
    var codigo: String? = ""
    var titulo: String? = ""
    var estado: String? = ""
    var pais: String? = ""
    var cidade: String? = ""
    var endereco: String? = ""
    var bairro: String? = ""
    var cep: String? = ""
    var telefone: String? = ""
    var email: String? = ""
    var site: String? = ""
    var fundo: String? = ""
    var data_apagamento: Date? = null
    var latitude: String? = ""
    var longitude: String? = ""
    var maximo_projeto: Long? = 0
    var maximo_disco: Long? = 0
    var horas_del_proj: Long? = 0
    var horas_del_inst: Long? = 0
    var envia_email_usu: Int? = 0
    var envia_email_prof: Int? = 0
    var controle_online: Int? = 0
    var atualizacao_automatica: Int? = 0
    var envia_email_smtp: String? = ""
    var envia_email_endereco: String? = ""
    var envia_email_pwd: String? = ""
    var envia_email_ssl: Int? = 0
    var envia_email_porta: Int? = 0
    var envia_projeto_senha: Int? = 0
    var id_projeto: Long? = 0
    var logo: String? = ""
    var bloqueado: Int? = 0
    var autocadastro: Int? = 0
    var maximo_projeto_cp: Long? = 0
    var maximo_usuarios: Long? = 0
}