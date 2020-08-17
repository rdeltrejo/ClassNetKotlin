package br.classinformatica.vcandroid.classnetk.entidades

import java.sql.Date
import java.util.Calendar

class Tela {
    var id_tela: Long = 0
    var id_projeto: Long = 0
    var codigo: String = ""
    var titulo: String = ""
    var autor: String = ""
    var data: Date = Date(Calendar.getInstance().timeInMillis)
    var assunto: String = ""
    var materia: String = ""
    var alvo: String = ""
    var cor: Int = 0
    var porcentagem_acertos: Int = 0
    var tentativas_avancar: Int = 0
    var bloqueia_recuo: Int = 0
    var som_autoexec: Int = 0
    var menu_invisivel: Int = 0
    var titulo_visivel: Int = 0
    var som_antes: Int = 0
    var paletas: Int = 0
    var tempo_tela: Int = 0
    var cortar_som: Int = 0
    var apagar_antes: Int = 0
    var avalia_hiperlink: Int = 0
    var borda_selecao: Int = 0
    var cor_rotulo_hiperlink: Int = 0
    var cor_borda_selecao: Int = 0
    var cor_rotulo_movel: Int = 0
    var cor_fundo_rotulo_movel: Int = 0
    var largura: Int = 0
    var altura: Int = 0
    var esconde_mouse: Int = 0
    var desabilita_gravar_posicao: Int = 0
    var programa_autoexec: Int = 0
    var filme_autoexec: Int = 0
    var som: Int = 0
    var mensagem_parabens: Int = 0
    var som_parabens: Int = 0
    var mensagem_muitobem: Int = 0
    var mensagem_pense: Int = 0
    var mensagem_revejateste: Int = 0
    var mensagem_revejagira: Int = 0
    var mensagem_revejapreenche: Int = 0
    var imagem: Int = 0
    var som_fim: Int = 0
    var som_looping: Int = 0
    var consistencia_contagem: Int = 0
    var grava_voto: Int = 0
    var arquivo_contagem: String = ""
    var mostrafundo: Int = 0
    var posicao: Int = 0
    var msgbarra: Int = 0
    var converteanima: Int = 0
    var padrao: Int = 0
    var posicionatopo: Int = 0
    var desabilita_sorteio: Int = 0
    var errado_nrespondido: Int = 0
    var mensagem_faltaselecionar: Int = 0
    var mensagem_faltaliga: Int = 0
    var mensagem_faltapreenche: Int = 0
    var mensagem_faltagira: Int = 0
    var mensagem_faltaarrastar: Int = 0
    var omiteindicacaoerro: Int = 0
    var sempreenchimentoigual: Int = 0
    var sequencia_objetos: Int = 0
    var tela_filha: Int = 0
    var sequencia_enter: Int = 0
    var tela_aleatoria: Int = 0
    var voto_tipourna: Int = 0
    var voto_idgrupo: Long = 0
    var id_nivel: Long = 0
    var id_provacurso: Long = 0
    var id_dificuldade: Long = 0
}