package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date
import java.util.*

class Descricao {
    var id_projeto: Long = 0
    var id_instituicao: Long = 0
    var id_autor: Long = 0
    var codigo: String = ""
    var nome: String = ""
    var autor: String = ""
    var data: Date = Date(Calendar.getInstance().timeInMillis)
    var grava_saida: Int = 0
    var n_telas: Long = 0
    var omite_avaliacao: Long = 0
    var oculta_avaliacao: Long = 0
    var grava_teste_detalhe: Int = 0
    var porcentagem_acertos: Long = 0
    var omite_lista_alunos: Int = 0
    var voto_entrada: Int = 0
    var voto_tempoconfirmacao: Double = 0.toDouble()
    var voto_cor_borda: Long = 0
    var voto_avancar: Int = 0
    var voto_senhainicio: Int = 0
    var voto_alterar: Int = 0
    var assunto: String = ""
    var materia: String = ""
    var alvo: String = ""
    var email: String = ""
    var grava_nota: String = ""
    var grava_notaurl: String = ""
    var exportar_alunos: String = ""
    var versao: String = ""
    var voto_tituloentrada: String = ""
    var voto_tela_origem: String = ""
    var zona: String = ""
    var nomezona: String = ""
    var solicitaident: Int = 0
    var clipcontrols: Int = 0
    var somduplo: Int = 0
    var versao_class: String = ""
    var telas_sorteio: Int = 0
    var navega_speech: Int = 0
    var teste_vest: Int = 0
    var senha_gerencia: String = ""
    var gestos: Int = 0
    var grava_dissert_ext: Int = 0
    var publico: Int = 0
    var redim_telas: Int = 0
    var permite_baixar: Int = 0
    var permite_baixar_proj: Int = 1
    var codigo_original: String = ""
    var orientacao: Int = 0
    var tentativas: Int = 1
    var url_vcnet_geo: String = ""
    var relatorio_pb: Int = 0
    var senha: String = ""
    var nivel_seguranca: Int = 0
    var path_arqproj: String = ""
    var data_apagamento: Date = Date(Calendar.getInstance().timeInMillis)
    var dataatualiza: Date = Date(Calendar.getInstance().timeInMillis)
    var automatricula: Int = 0
    var pago: Int = 0
    var senha_auto: String = ""
    var data_apagamento_cfg: Date = Date(Calendar.getInstance().timeInMillis)
    var redimensionar_textos: Int = 1
    var tipoprojeto: Int = 0
    var id_tipoprova: Long = 0
    var barra_navegacao: Int = 0
}