package br.classinformatica.vcandroid.classnetk

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.classinformatica.vcandroid.classnetk.dialogs.DialogJanelaNota
import br.classinformatica.vcandroid.classnetk.entidades.*
import br.classinformatica.vcandroid.classnetk.objetos.*
import br.classinformatica.vcandroid.classnetk.util.ExerciciosTela
import br.classinformatica.vcandroid.classnetk.util.UtilClass
import java.io.File
import java.sql.Date
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

class Aplicacao : Application() {

    //variáveis ----------------------------------------------------------------------------

    var act: AppCompatActivity? = null
    var aluno: Aluno? = null
    var atribuidosAulaAtrbuida: ArrayList<AulaAtribuida>? = null
    var atribuidosDescricao: ArrayList<Descricao>? = null
    var configuracoes: Configuracoes? = null
    var instituicao: Instituicao? = null
    var metrics: DisplayMetrics? = null
    var progress: AlertDialog? = null
    var removeAlt = 0
    var removeLar = 0
    var rlTela: RelativeLayout? = null
    var servidores: Servidores? = null
    var tempoInicial = Calendar.getInstance()
    var versao = ""

    //para objetos que arrastam pela tela --------------------------------------------------
    var classLigasUsando: ClassLigas? = null
    var ivArrastarImagens: ImageView? = null
    var ivArrastarN: ImageView? = null
    var tvArrastar: TextView? = null

    //projeto selecionado ------------------------------------------------------------------
    var aulaAtribuida: AulaAtribuida? = null
    var aulaFeita: AulaFeita? = null
    var aulaTestefeito = ArrayList<AulaTestefeito>()
    var aulaIncompleta = ArrayList<AulaIncompleta>()
    var exerciciosTela = ArrayList<ExerciciosTela>()

    var aulaTela = ArrayList<AulaTela>()
    var posicaoAulaTela = 0
    var posicaoTelaLink = 0

    var descricao: Descricao? = null
    var classTela: ClassTela? = null

    var classAnimacoes = ArrayList<ClassAnimacoes>()
    var classArrastarSoltar = ArrayList<ClassArrastarSoltar>()
    var classArrastarSoltarImagens = ArrayList<ClassArrastarSoltarImagens>()
    var classArrastarSoltarN = ArrayList<ClassArrastarSoltarN>()
    var classDissertativa = ArrayList<ClassDissertativa>()
    var classFilmesTela = ArrayList<ClassFilmesTela>()
    var classGirasEfeitos = ArrayList<ClassGirasEfeitos>()
    var classImagensEfeitos = ArrayList<ClassImagensEfeitos>()
    var classLigas = ArrayList<ClassLigas>()
    var classLinhas = ArrayList<ClassLinhas>()
    var classPreenchimentos = ArrayList<ClassPreenchimentos>()
    var classRotulos = ArrayList<ClassRotulos>()
    var classTestesVest = ArrayList<ClassTestesVest>()
    var classTextos = ArrayList<ClassTextos>()
    //projeto selecionado ------------------------------------------------------------------

    //métodos ------------------------------------------------------------------------------

    fun atualizarMetricas() {
        metrics = DisplayMetrics()
        act!!.windowManager.defaultDisplay.getMetrics(metrics)
    }

    fun computarNotaFinal(): Int {
        var posicaoExercicio = -1
        var i = 0
        while (posicaoExercicio == -1 && i < exerciciosTela.size) {
            if (!exerciciosTela[i].feito) {
                posicaoExercicio = i
            }
            i++
        }
        return posicaoExercicio
    }

    fun computarNotaTela(): String {
        var retorno = ""
        var posiExerciciosTela = -1
        var aulaTestefeito: AulaTestefeito
        var i: Int

        //verificar as animações
        i = 0
        while (retorno.isEmpty() && i < classAnimacoes.size) {
            if (classAnimacoes[i].animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classAnimacoes[i].clicouObjeto) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ANIMACAO, classAnimacoes[i].animacoes!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    aulaTestefeito = AulaTestefeito()
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_ANIMACAO
                    aulaTestefeito.indice = classAnimacoes[i].animacoes!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classAnimacoes[i].objenunciado != null) {
                        aulaTestefeito.peso = classAnimacoes[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    aulaTestefeito.certoerrado = 0.toDouble()
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    var exer = ExerciciosTela()
                    exer.aulaTestefeito = aulaTestefeito
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.feito = true
                    exerciciosTela.add(exer)
                } else {
                    i = classAnimacoes.size
                }
            } else if (classAnimacoes[i].animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ANIMACAO, classAnimacoes[i].animacoes!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    if (classAnimacoes[i].clicouObjeto) {//clicou certo
                        aulaTestefeito = AulaTestefeito()
                        aulaTestefeito.id_projeto = descricao!!.id_projeto
                        aulaTestefeito.id_aluno = aluno!!.id_aluno
                        aulaTestefeito.codigo_aluno = aluno!!.codigo
                        aulaTestefeito.codigo_aula = descricao!!.codigo
                        aulaTestefeito.tela = classTela!!.tela!!.codigo
                        aulaTestefeito.tipoobj = UtilClass.TIPO_ANIMACAO
                        aulaTestefeito.indice = classAnimacoes[i].animacoes!!.indice
                        aulaTestefeito.indice_par = 0
                        aulaTestefeito.indice_obj = 0
                        if (classAnimacoes[i].objenunciado != null) {
                            aulaTestefeito.peso = classAnimacoes[i].objenunciado!!.peso
                        } else {
                            aulaTestefeito.peso = 1.toDouble()
                        }
                        aulaTestefeito.certoerrado = 1.toDouble()
                        aulaTestefeito.indice_soltar = 0
                        aulaTestefeito.pendente = 0
                        aulaTestefeito.resposta = ""
                        aulaTestefeito.textopreenchido = ""
                        aulaTestefeito.alternativa_selecionada = 0
                        aulaTestefeito.preenchimento = ""
                        aulaTestefeito.unicode = 0
                        aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                        var exer = ExerciciosTela()
                        exer.aulaTestefeito = aulaTestefeito
                        exer.posicaoAulaTela = posicaoAulaTela
                        exer.posicaoTelaLink = posicaoTelaLink
                        exer.feito = true
                        exerciciosTela.add(exer)
                    } else {
                        if (classTela!!.tela!!.errado_nrespondido == 1) {//não clicou mas é para considerar errado
                            aulaTestefeito = AulaTestefeito()
                            aulaTestefeito.id_projeto = descricao!!.id_projeto
                            aulaTestefeito.id_aluno = aluno!!.id_aluno
                            aulaTestefeito.codigo_aluno = aluno!!.codigo
                            aulaTestefeito.codigo_aula = descricao!!.codigo
                            aulaTestefeito.tela = classTela!!.tela!!.codigo
                            aulaTestefeito.tipoobj = UtilClass.TIPO_ANIMACAO
                            aulaTestefeito.indice = classAnimacoes[i].animacoes!!.indice
                            aulaTestefeito.indice_par = 0
                            aulaTestefeito.indice_obj = 0
                            if (classAnimacoes[i].objenunciado != null) {
                                aulaTestefeito.peso = classAnimacoes[i].objenunciado!!.peso
                            } else {
                                aulaTestefeito.peso = 1.toDouble()
                            }
                            aulaTestefeito.certoerrado = 0.toDouble()
                            aulaTestefeito.indice_soltar = 0
                            aulaTestefeito.pendente = 0
                            aulaTestefeito.resposta = ""
                            aulaTestefeito.textopreenchido = ""
                            aulaTestefeito.alternativa_selecionada = 0
                            aulaTestefeito.preenchimento = ""
                            aulaTestefeito.unicode = 0
                            aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                            var exer = ExerciciosTela()
                            exer.aulaTestefeito = aulaTestefeito
                            exer.posicaoAulaTela = posicaoAulaTela
                            exer.posicaoTelaLink = posicaoTelaLink
                            exer.feito = true
                            exerciciosTela.add(exer)
                        } else {
                            if (descricao!!.teste_vest == 1) {//não fez mas é para avaliar apenas ao encerrar
                                aulaTestefeito = AulaTestefeito()
                                aulaTestefeito.id_projeto = descricao!!.id_projeto
                                aulaTestefeito.id_aluno = aluno!!.id_aluno
                                aulaTestefeito.codigo_aluno = aluno!!.codigo
                                aulaTestefeito.codigo_aula = descricao!!.codigo
                                aulaTestefeito.tela = classTela!!.tela!!.codigo
                                aulaTestefeito.tipoobj = UtilClass.TIPO_ANIMACAO
                                aulaTestefeito.indice = classAnimacoes[i].animacoes!!.indice
                                aulaTestefeito.indice_par = 0
                                aulaTestefeito.indice_obj = 0
                                if (classAnimacoes[i].objenunciado != null) {
                                    aulaTestefeito.peso = classAnimacoes[i].objenunciado!!.peso
                                } else {
                                    aulaTestefeito.peso = 1.toDouble()
                                }
                                aulaTestefeito.certoerrado = 0.toDouble()
                                aulaTestefeito.indice_soltar = 0
                                aulaTestefeito.pendente = 0
                                aulaTestefeito.resposta = ""
                                aulaTestefeito.textopreenchido = ""
                                aulaTestefeito.alternativa_selecionada = 0
                                aulaTestefeito.preenchimento = ""
                                aulaTestefeito.unicode = 0
                                aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                                var exer = ExerciciosTela()
                                exer.aulaTestefeito = aulaTestefeito
                                exer.posicaoAulaTela = posicaoAulaTela
                                exer.posicaoTelaLink = posicaoTelaLink
                                exer.feito = false
                                exerciciosTela.add(exer)
                            } else {
                                //verificar as mensagens aqui
                                retorno = act!!.getString(R.string.msg_falta_selecionar)
                            }
                        }
                    }
                } else {
                    i = classAnimacoes.size
                }
            }
            i++
        }

        //verificar as imagens
        i = 0
        while (retorno.isEmpty() && i < classImagensEfeitos.size) {
            if (classImagensEfeitos[i].imagensEfeitos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classImagensEfeitos[i].clicouObjeto) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_IMAGEM_EFEITO, classImagensEfeitos[i].imagensEfeitos!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    aulaTestefeito = AulaTestefeito()
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_IMAGEM_EFEITO
                    aulaTestefeito.indice = classImagensEfeitos[i].imagensEfeitos!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classImagensEfeitos[i].objenunciado != null) {
                        aulaTestefeito.peso = classImagensEfeitos[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    aulaTestefeito.certoerrado = 0.toDouble()
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    var exer = ExerciciosTela()
                    exer.aulaTestefeito = aulaTestefeito
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.feito = true
                    exerciciosTela.add(exer)
                } else {
                    i = classImagensEfeitos.size
                }
            } else if (classImagensEfeitos[i].imagensEfeitos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_IMAGEM_EFEITO, classImagensEfeitos[i].imagensEfeitos!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    if (classImagensEfeitos[i].clicouObjeto) {//clicou certo
                        aulaTestefeito = AulaTestefeito()
                        aulaTestefeito.id_projeto = descricao!!.id_projeto
                        aulaTestefeito.id_aluno = aluno!!.id_aluno
                        aulaTestefeito.codigo_aluno = aluno!!.codigo
                        aulaTestefeito.codigo_aula = descricao!!.codigo
                        aulaTestefeito.tela = classTela!!.tela!!.codigo
                        aulaTestefeito.tipoobj = UtilClass.TIPO_IMAGEM_EFEITO
                        aulaTestefeito.indice = classImagensEfeitos[i].imagensEfeitos!!.indice
                        aulaTestefeito.indice_par = 0
                        aulaTestefeito.indice_obj = 0
                        if (classImagensEfeitos[i].objenunciado != null) {
                            aulaTestefeito.peso = classImagensEfeitos[i].objenunciado!!.peso
                        } else {
                            aulaTestefeito.peso = 1.toDouble()
                        }
                        aulaTestefeito.certoerrado = 1.toDouble()
                        aulaTestefeito.indice_soltar = 0
                        aulaTestefeito.pendente = 0
                        aulaTestefeito.resposta = ""
                        aulaTestefeito.textopreenchido = ""
                        aulaTestefeito.alternativa_selecionada = 0
                        aulaTestefeito.preenchimento = ""
                        aulaTestefeito.unicode = 0
                        aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                        var exer = ExerciciosTela()
                        exer.aulaTestefeito = aulaTestefeito
                        exer.posicaoAulaTela = posicaoAulaTela
                        exer.posicaoTelaLink = posicaoTelaLink
                        exer.feito = true
                        exerciciosTela.add(exer)
                    } else {
                        if (classTela!!.tela!!.errado_nrespondido == 1) {//não clicou mas é para considerar errado
                            aulaTestefeito = AulaTestefeito()
                            aulaTestefeito.id_projeto = descricao!!.id_projeto
                            aulaTestefeito.id_aluno = aluno!!.id_aluno
                            aulaTestefeito.codigo_aluno = aluno!!.codigo
                            aulaTestefeito.codigo_aula = descricao!!.codigo
                            aulaTestefeito.tela = classTela!!.tela!!.codigo
                            aulaTestefeito.tipoobj = UtilClass.TIPO_IMAGEM_EFEITO
                            aulaTestefeito.indice = classImagensEfeitos[i].imagensEfeitos!!.indice
                            aulaTestefeito.indice_par = 0
                            aulaTestefeito.indice_obj = 0
                            if (classImagensEfeitos[i].objenunciado != null) {
                                aulaTestefeito.peso = classImagensEfeitos[i].objenunciado!!.peso
                            } else {
                                aulaTestefeito.peso = 1.toDouble()
                            }
                            aulaTestefeito.certoerrado = 0.toDouble()
                            aulaTestefeito.indice_soltar = 0
                            aulaTestefeito.pendente = 0
                            aulaTestefeito.resposta = ""
                            aulaTestefeito.textopreenchido = ""
                            aulaTestefeito.alternativa_selecionada = 0
                            aulaTestefeito.preenchimento = ""
                            aulaTestefeito.unicode = 0
                            aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                            var exer = ExerciciosTela()
                            exer.aulaTestefeito = aulaTestefeito
                            exer.posicaoAulaTela = posicaoAulaTela
                            exer.posicaoTelaLink = posicaoTelaLink
                            exer.feito = true
                            exerciciosTela.add(exer)
                        } else {
                            if (descricao!!.teste_vest == 1) {//não fez mas é para avaliar apenas ao encerrar
                                aulaTestefeito = AulaTestefeito()
                                aulaTestefeito.id_projeto = descricao!!.id_projeto
                                aulaTestefeito.id_aluno = aluno!!.id_aluno
                                aulaTestefeito.codigo_aluno = aluno!!.codigo
                                aulaTestefeito.codigo_aula = descricao!!.codigo
                                aulaTestefeito.tela = classTela!!.tela!!.codigo
                                aulaTestefeito.tipoobj = UtilClass.TIPO_IMAGEM_EFEITO
                                aulaTestefeito.indice = classImagensEfeitos[i].imagensEfeitos!!.indice
                                aulaTestefeito.indice_par = 0
                                aulaTestefeito.indice_obj = 0
                                if (classImagensEfeitos[i].objenunciado != null) {
                                    aulaTestefeito.peso = classImagensEfeitos[i].objenunciado!!.peso
                                } else {
                                    aulaTestefeito.peso = 1.toDouble()
                                }
                                aulaTestefeito.certoerrado = 0.toDouble()
                                aulaTestefeito.indice_soltar = 0
                                aulaTestefeito.pendente = 0
                                aulaTestefeito.resposta = ""
                                aulaTestefeito.textopreenchido = ""
                                aulaTestefeito.alternativa_selecionada = 0
                                aulaTestefeito.preenchimento = ""
                                aulaTestefeito.unicode = 0
                                aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                                var exer = ExerciciosTela()
                                exer.aulaTestefeito = aulaTestefeito
                                exer.posicaoAulaTela = posicaoAulaTela
                                exer.posicaoTelaLink = posicaoTelaLink
                                exer.feito = false
                                exerciciosTela.add(exer)
                            } else {
                                //verificar as mensagens aqui
                                retorno = act!!.getString(R.string.msg_falta_selecionar)
                            }
                        }
                    }
                } else {
                    i = classImagensEfeitos.size
                }
            }
            i++
        }

        //verificar a rótulos
        i = 0
        while (retorno.isEmpty() && i < classRotulos.size) {
            if (classRotulos[i].rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classRotulos[i].clicouObjeto) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ROTULO, classRotulos[i].rotulos!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    aulaTestefeito = AulaTestefeito()
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_ROTULO
                    aulaTestefeito.indice = classRotulos[i].rotulos!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classRotulos[i].objenunciado != null) {
                        aulaTestefeito.peso = classRotulos[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    aulaTestefeito.certoerrado = 0.toDouble()
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    var exer = ExerciciosTela()
                    exer.aulaTestefeito = aulaTestefeito
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.feito = true
                    exerciciosTela.add(exer)
                } else {
                    i = classRotulos.size
                }
            } else if (classRotulos[i].rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ROTULO, classRotulos[i].rotulos!!.indice, 0)

                if (posiExerciciosTela == -1) {//se já estiver no vetor de exercícios, não precisa colocar mais
                    if (classRotulos[i].clicouObjeto) {//clicou certo
                        aulaTestefeito = AulaTestefeito()
                        aulaTestefeito.id_projeto = descricao!!.id_projeto
                        aulaTestefeito.id_aluno = aluno!!.id_aluno
                        aulaTestefeito.codigo_aluno = aluno!!.codigo
                        aulaTestefeito.codigo_aula = descricao!!.codigo
                        aulaTestefeito.tela = classTela!!.tela!!.codigo
                        aulaTestefeito.tipoobj = UtilClass.TIPO_ROTULO
                        aulaTestefeito.indice = classRotulos[i].rotulos!!.indice
                        aulaTestefeito.indice_par = 0
                        aulaTestefeito.indice_obj = 0
                        if (classRotulos[i].objenunciado != null) {
                            aulaTestefeito.peso = classRotulos[i].objenunciado!!.peso
                        } else {
                            aulaTestefeito.peso = 1.toDouble()
                        }
                        aulaTestefeito.certoerrado = 1.toDouble()
                        aulaTestefeito.indice_soltar = 0
                        aulaTestefeito.pendente = 0
                        aulaTestefeito.resposta = ""
                        aulaTestefeito.textopreenchido = ""
                        aulaTestefeito.alternativa_selecionada = 0
                        aulaTestefeito.preenchimento = ""
                        aulaTestefeito.unicode = 0
                        aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                        var exer = ExerciciosTela()
                        exer.aulaTestefeito = aulaTestefeito
                        exer.posicaoAulaTela = posicaoAulaTela
                        exer.posicaoTelaLink = posicaoTelaLink
                        exer.feito = true
                        exerciciosTela.add(exer)
                    } else {
                        if (classTela!!.tela!!.errado_nrespondido == 1) {//não clicou mas é para considerar errado
                            aulaTestefeito = AulaTestefeito()
                            aulaTestefeito.id_projeto = descricao!!.id_projeto
                            aulaTestefeito.id_aluno = aluno!!.id_aluno
                            aulaTestefeito.codigo_aluno = aluno!!.codigo
                            aulaTestefeito.codigo_aula = descricao!!.codigo
                            aulaTestefeito.tela = classTela!!.tela!!.codigo
                            aulaTestefeito.tipoobj = UtilClass.TIPO_ROTULO
                            aulaTestefeito.indice = classRotulos[i].rotulos!!.indice
                            aulaTestefeito.indice_par = 0
                            aulaTestefeito.indice_obj = 0
                            if (classRotulos[i].objenunciado != null) {
                                aulaTestefeito.peso = classRotulos[i].objenunciado!!.peso
                            } else {
                                aulaTestefeito.peso = 1.toDouble()
                            }
                            aulaTestefeito.certoerrado = 0.toDouble()
                            aulaTestefeito.indice_soltar = 0
                            aulaTestefeito.pendente = 0
                            aulaTestefeito.resposta = ""
                            aulaTestefeito.textopreenchido = ""
                            aulaTestefeito.alternativa_selecionada = 0
                            aulaTestefeito.preenchimento = ""
                            aulaTestefeito.unicode = 0
                            aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                            var exer = ExerciciosTela()
                            exer.aulaTestefeito = aulaTestefeito
                            exer.posicaoAulaTela = posicaoAulaTela
                            exer.posicaoTelaLink = posicaoTelaLink
                            exer.feito = true
                            exerciciosTela.add(exer)
                        } else {
                            if (descricao!!.teste_vest == 1) {//não fez mas é para avaliar apenas ao encerrar
                                aulaTestefeito = AulaTestefeito()
                                aulaTestefeito.id_projeto = descricao!!.id_projeto
                                aulaTestefeito.id_aluno = aluno!!.id_aluno
                                aulaTestefeito.codigo_aluno = aluno!!.codigo
                                aulaTestefeito.codigo_aula = descricao!!.codigo
                                aulaTestefeito.tela = classTela!!.tela!!.codigo
                                aulaTestefeito.tipoobj = UtilClass.TIPO_ROTULO
                                aulaTestefeito.indice = classRotulos[i].rotulos!!.indice
                                aulaTestefeito.indice_par = 0
                                aulaTestefeito.indice_obj = 0
                                if (classRotulos[i].objenunciado != null) {
                                    aulaTestefeito.peso = classRotulos[i].objenunciado!!.peso
                                } else {
                                    aulaTestefeito.peso = 1.toDouble()
                                }
                                aulaTestefeito.certoerrado = 0.toDouble()
                                aulaTestefeito.indice_soltar = 0
                                aulaTestefeito.pendente = 0
                                aulaTestefeito.resposta = ""
                                aulaTestefeito.textopreenchido = ""
                                aulaTestefeito.alternativa_selecionada = 0
                                aulaTestefeito.preenchimento = ""
                                aulaTestefeito.unicode = 0
                                aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                                var exer = ExerciciosTela()
                                exer.aulaTestefeito = aulaTestefeito
                                exer.posicaoAulaTela = posicaoAulaTela
                                exer.posicaoTelaLink = posicaoTelaLink
                                exer.feito = false
                                exerciciosTela.add(exer)
                            } else {
                                //verificar as mensagens aqui
                                retorno = act!!.getString(R.string.msg_falta_selecionar)
                            }
                        }
                    }
                } else {
                    i = classRotulos.size
                }
            }
            i++
        }

        //verificar arrastar/soltar
        i = 0
        while (retorno.isEmpty() && i < classArrastarSoltar.size) {
            if (classArrastarSoltar[i].ligouCorreta || classArrastarSoltar[i].ligouErrada || (!classArrastarSoltar[i].ligouCorreta && !classArrastarSoltar[i].ligouErrada && classTela!!.tela!!.errado_nrespondido == -1)) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ARRASTAR, classArrastarSoltar[i].arrastar!!.indice, 0)
                var achou = false
                aulaTestefeito = AulaTestefeito()
                if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                    aulaTestefeito = exerciciosTela[posiExerciciosTela].aulaTestefeito!!
                    achou = true
                } else if (posiExerciciosTela == -1) {
                    achou = true
                }
                if (achou) {
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_ARRASTAR
                    aulaTestefeito.indice = classArrastarSoltar[i].arrastar!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classArrastarSoltar[i].objenunciado != null) {
                        aulaTestefeito.peso = classArrastarSoltar[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classArrastarSoltar[i].ligouErrada || (!classArrastarSoltar[i].ligouCorreta && !classArrastarSoltar[i].ligouErrada && classTela!!.tela!!.errado_nrespondido == -1)) {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    }
                    if (classArrastarSoltar[i].indiceSoltar > -1) {
                        aulaTestefeito.indice_soltar = classArrastarSoltar[i].indiceSoltar
                    } else {
                        aulaTestefeito.indice_soltar = classArrastarSoltar[i].soltar!!.indice
                    }
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    var exer = ExerciciosTela()
                    if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                        exer = exerciciosTela[posiExerciciosTela]
                    }
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.feito = true

                    if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                } else {
                    retorno = act!!.getString(R.string.msg_falta_arrsol)
                }
            } else {
                retorno = act!!.getString(R.string.msg_falta_arrsol)
            }
            i++
        }

        //verificar arrastar/soltar imagens
        i = 0
        while (retorno.isEmpty() && i < classArrastarSoltarImagens.size) {
            if (classArrastarSoltarImagens[i].ligouCorreta || classArrastarSoltarImagens[i].ligouErrada || (!classArrastarSoltarImagens[i].ligouCorreta && !classArrastarSoltarImagens[i].ligouErrada && classTela!!.tela!!.errado_nrespondido == -1)) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ARRASTAR_IMAGEM, classArrastarSoltarImagens[i].arrastarImagens!!.indice, 0)
                var achou = false
                aulaTestefeito = AulaTestefeito()
                if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                    aulaTestefeito = exerciciosTela[posiExerciciosTela].aulaTestefeito!!
                    achou = true
                } else if (posiExerciciosTela == -1) {
                    achou = true
                }
                if (achou) {
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_ARRASTAR_IMAGEM
                    aulaTestefeito.indice = classArrastarSoltarImagens[i].arrastarImagens!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classArrastarSoltarImagens[i].objenunciado != null) {
                        aulaTestefeito.peso = classArrastarSoltarImagens[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classArrastarSoltarImagens[i].ligouErrada || (!classArrastarSoltarImagens[i].ligouCorreta && !classArrastarSoltarImagens[i].ligouErrada && classTela!!.tela!!.errado_nrespondido == -1)) {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    }
                    if (classArrastarSoltarImagens[i].indiceSoltar > -1) {
                        aulaTestefeito.indice_soltar = classArrastarSoltarImagens[i].indiceSoltar
                    } else {
                        aulaTestefeito.indice_soltar = classArrastarSoltarImagens[i].soltarImagens!!.indice
                    }
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    var exer = ExerciciosTela()
                    if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                        exer = exerciciosTela[posiExerciciosTela]
                    }
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.feito = true

                    if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                } else {
                    retorno = act!!.getString(R.string.msg_falta_arrsol)
                }
            } else {
                retorno = act!!.getString(R.string.msg_falta_arrsol)
            }
            i++
        }

        //verificar arrastar dif soltar
        i = 0
        while (retorno.isEmpty() && i < classArrastarSoltarN.size) {
            for (j in 0 until classArrastarSoltarN[i].arrastarN.size) {
                if (classArrastarSoltarN[i].ligouCorreta[j] || classArrastarSoltarN[i].ligouErrada[j] || (!classArrastarSoltarN[i].ligouCorreta[j] && !classArrastarSoltarN[i].ligouErrada[j] && classTela!!.tela!!.errado_nrespondido == -1)) {
                    posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_ARRASTAR_N,
                        classArrastarSoltarN[i].arrastarN[j].indice,
                        classArrastarSoltarN[i].arrastarN[j].indice_obj)
                    var achou = false
                    aulaTestefeito = AulaTestefeito()
                    if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                        aulaTestefeito = exerciciosTela[posiExerciciosTela].aulaTestefeito!!
                        achou = true
                    } else if (posiExerciciosTela == -1) {
                        achou = true
                    }
                    if (achou) {
                        aulaTestefeito.id_projeto = descricao!!.id_projeto
                        aulaTestefeito.id_aluno = aluno!!.id_aluno
                        aulaTestefeito.codigo_aluno = aluno!!.codigo
                        aulaTestefeito.codigo_aula = descricao!!.codigo
                        aulaTestefeito.tela = classTela!!.tela!!.codigo
                        aulaTestefeito.tipoobj = UtilClass.TIPO_ARRASTAR_N
                        aulaTestefeito.indice = classArrastarSoltarN[i].arrastarN[j].indice
                        aulaTestefeito.indice_par = 0
                        aulaTestefeito.indice_obj = classArrastarSoltarN[i].arrastarN[j].indice_obj
                        if (classArrastarSoltarN[i].objenunciado != null) {
                            aulaTestefeito.peso = classArrastarSoltarN[i].objenunciado!!.peso
                        } else {
                            aulaTestefeito.peso = 1.toDouble()
                        }
                        if (classArrastarSoltarN[i].ligouErrada[j] || (!classArrastarSoltarN[i].ligouCorreta[j] && !classArrastarSoltarN[i].ligouErrada[j] && classTela!!.tela!!.errado_nrespondido == -1)) {
                            aulaTestefeito.certoerrado = 0.toDouble()
                        } else {
                            aulaTestefeito.certoerrado = 1.toDouble()
                        }
                        if (classArrastarSoltarN[i].indiceSoltar[j] > -1) {
                            aulaTestefeito.indice_soltar = classArrastarSoltarN[i].indiceSoltar[j]
                        } else {
                            aulaTestefeito.indice_soltar = classArrastarSoltarN[i].soltarN!!.indice
                        }
                        aulaTestefeito.pendente = 0
                        aulaTestefeito.resposta = ""
                        aulaTestefeito.textopreenchido = ""
                        aulaTestefeito.alternativa_selecionada = 0
                        aulaTestefeito.preenchimento = ""
                        aulaTestefeito.unicode = 0
                        aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                        var exer = ExerciciosTela()
                        if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                            exer = exerciciosTela[posiExerciciosTela]
                        }
                        exer.posicaoTelaLink = posicaoTelaLink
                        exer.posicaoAulaTela = posicaoAulaTela
                        exer.feito = true

                        if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                            exerciciosTela[posiExerciciosTela] = exer
                        } else {
                            exerciciosTela.add(exer)
                        }
                    } else {
                        retorno = act!!.getString(R.string.msg_falta_arrsol)
                    }
                } else {
                    retorno = act!!.getString(R.string.msg_falta_arrsol)
                }
            }
            i++
        }

        //verificar dissertativas
        i = 0
        while (retorno.isEmpty() && i < classDissertativa.size) {
            if (classDissertativa[i].editText!!.text.trim().isEmpty() && descricao!!.teste_vest == 0 && classTela!!.tela!!.errado_nrespondido == 0) {
                retorno = act!!.getString(R.string.msg_falta_dissertativa)
            } else {
                posiExerciciosTela =
                    retPosiExercicioTela(UtilClass.TIPO_DISSERTATIVA, classDissertativa[i].dissertativa!!.indice, 0)
                var achou = false
                aulaTestefeito = AulaTestefeito()
                var exer = ExerciciosTela()
                if (posiExerciciosTela > -1 && descricao!!.teste_vest == 1) {
                    exer = exerciciosTela[posiExerciciosTela]
                    aulaTestefeito = exer.aulaTestefeito!!

                    achou = true
                } else if (posiExerciciosTela == -1) {
                    achou = true
                }
                if (achou) {
                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_DISSERTATIVA
                    aulaTestefeito.indice = classDissertativa[i].dissertativa!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classDissertativa[i].objenunciado != null) {
                        aulaTestefeito.peso = classDissertativa[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    aulaTestefeito.certoerrado = 0.toDouble()
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 1
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = classDissertativa[i].editText!!.text.toString()
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    exer.aulaTestefeito = aulaTestefeito
                    exer.feito = classDissertativa[i].editText!!.text.trim().isNotEmpty()
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink

                    if (posiExerciciosTela > -1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                } else {
                    retorno = act!!.getString(R.string.msg_falta_dissertativa)
                }
            }
            i++
        }

        //verificar gira figuras
        i = 0
        while (retorno.isEmpty() && i < classGirasEfeitos.size) {
            //gira figura nunca vai ficar sem resposta
            if (classGirasEfeitos[i].posicaoFigura != classGirasEfeitos[i].girasEfeitos!!.resposta && descricao!!.teste_vest == 0) {//errou
                aulaFeita!!.erros_anterior++
                retorno = act!!.getString(R.string.msg_falta_giras)
            } else {
                posiExerciciosTela =
                    retPosiExercicioTela(UtilClass.TIPO_GIRA, classGirasEfeitos[i].girasEfeitos!!.indice, 0)
                if (posiExerciciosTela == -1 || (posiExerciciosTela > -1 && descricao!!.teste_vest == 1)) {//pode alterar ou não existe
                    aulaTestefeito = AulaTestefeito()
                    var exer = ExerciciosTela()
                    if (posiExerciciosTela > -1) {
                        exer = exerciciosTela[posiExerciciosTela]
                        aulaTestefeito = exer.aulaTestefeito!!
                    }

                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_GIRA
                    aulaTestefeito.indice = classGirasEfeitos[i].girasEfeitos!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classGirasEfeitos[i].objenunciado != null) {
                        aulaTestefeito.peso = classGirasEfeitos[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classGirasEfeitos[i].posicaoFigura != classGirasEfeitos[i].girasEfeitos!!.resposta) {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    }
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    exer.aulaTestefeito = aulaTestefeito
                    exer.feito = true
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink

                    if (posiExerciciosTela > -1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                }
            }
            i++
        }

        //verificar liga pontos
        i = 0
        while (retorno.isEmpty() && i < classLigas.size) {
            if (descricao!!.teste_vest == 0 && classTela!!.tela!!.errado_nrespondido == 0 && !classLigas[i].ligouCorreta1 && !classLigas[i].ligouErrada1) {//todos os ligas devem estar pronto
                retorno = act!!.getString(R.string.msg_falta_ligas)
            }
            if (retorno.isEmpty()) {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_LIGA1, classLigas[i].liga1!!.indice, 0)
                if (posiExerciciosTela == -1 || (posiExerciciosTela > -1 && descricao!!.teste_vest == 1)) {//pode alterar ou não existe
                    var exer = ExerciciosTela()
                    aulaTestefeito = AulaTestefeito()
                    if (posiExerciciosTela > -1) {
                        exer = exerciciosTela[posiExerciciosTela]
                        aulaTestefeito = exer.aulaTestefeito!!
                    }

                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_LIGA1
                    aulaTestefeito.indice = classLigas[i].liga1!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classLigas[i].objenunciado != null) {
                        aulaTestefeito.peso = classLigas[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classLigas[i].ligouCorreta1) {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    }
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    exer.aulaTestefeito = aulaTestefeito
                    if (!classLigas[i].ligouErrada1 && !classLigas[i].ligouCorreta1 && classTela!!.tela!!.errado_nrespondido == 1) {
                        exer.feito = true
                        aulaTestefeito.certoerrado = 0.toDouble()
                    } else {
                        exer.feito = (classLigas[i].ligouErrada1 || classLigas[i].ligouCorreta1)
                    }
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink

                    if (posiExerciciosTela > -1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                }
            }
            i++
        }

        //verificar preenchimentos
        i = 0
        while (retorno.isEmpty() && i < classPreenchimentos.size) {
            if (descricao!!.teste_vest == 0 && classPreenchimentos[i].editText!!.text.trim().isEmpty() && classTela!!.tela!!.errado_nrespondido == 0) {
                retorno = act!!.getString(R.string.msg_falta_preenchimentos)
            } else {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_PREENCHIMENTO, classPreenchimentos[i].preenchimentos!!.indice, 0)
                if (posiExerciciosTela == -1 || (posiExerciciosTela > -1 && descricao!!.teste_vest == 1)) {//pode alterar ou não existe
                    var exer = ExerciciosTela()
                    aulaTestefeito = AulaTestefeito()
                    if (posiExerciciosTela > -1) {
                        exer = exerciciosTela[posiExerciciosTela]
                        aulaTestefeito = exer.aulaTestefeito!!
                    }

                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_PREENCHIMENTO
                    aulaTestefeito.indice = classPreenchimentos[i].preenchimentos!!.indice
                    aulaTestefeito.indice_par = 0
                    aulaTestefeito.indice_obj = 0
                    if (classPreenchimentos[i].objenunciado != null) {
                        aulaTestefeito.peso = classPreenchimentos[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classPreenchimentos[i].editText!!.text.trim().isEmpty()) {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    }
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = classPreenchimentos[i].editText!!.text.toString()
                    aulaTestefeito.alternativa_selecionada = 0
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    exer.aulaTestefeito = aulaTestefeito
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.feito = (classPreenchimentos[i].editText!!.text.trim().isNotEmpty() || (classPreenchimentos[i].editText!!.text.trim().isEmpty() && classTela!!.tela!!.errado_nrespondido == 1))

                    if (posiExerciciosTela > -1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                }
            }
            i++
        }

        //verificar teste de vestibular
        i = 0
        while (retorno.isEmpty() && i < classTestesVest.size) {
            if (descricao!!.teste_vest == 0 && classTestesVest[i].posicaoSelecionada == -1 && classTela!!.tela!!.errado_nrespondido == 0) {
                retorno = act!!.getString(R.string.msg_falta_teste_vest)
            } else {
                posiExerciciosTela = retPosiExercicioTela(UtilClass.TIPO_TESTE_VEST, classTestesVest[i].testesVest!!.indice, 0)
                if (posiExerciciosTela == -1 || (posiExerciciosTela > -1 && descricao!!.teste_vest == 1)) {//pode alterar ou não existe
                    var exer = ExerciciosTela()
                    aulaTestefeito = AulaTestefeito()
                    if (posiExerciciosTela > -1) {
                        exer = exerciciosTela[posiExerciciosTela]
                        aulaTestefeito = exer.aulaTestefeito!!
                    }

                    aulaTestefeito.id_projeto = descricao!!.id_projeto
                    aulaTestefeito.id_aluno = aluno!!.id_aluno
                    aulaTestefeito.codigo_aluno = aluno!!.codigo
                    aulaTestefeito.codigo_aula = descricao!!.codigo
                    aulaTestefeito.tela = classTela!!.tela!!.codigo
                    aulaTestefeito.tipoobj = UtilClass.TIPO_TESTE_VEST
                    aulaTestefeito.indice = classTestesVest[i].testesVest!!.indice
                    aulaTestefeito.indice_par = classTestesVest[i].testesVest!!.resposta
                    aulaTestefeito.indice_obj = 0
                    if (classTestesVest[i].objenunciado != null) {
                        aulaTestefeito.peso = classTestesVest[i].objenunciado!!.peso
                    } else {
                        aulaTestefeito.peso = 1.toDouble()
                    }
                    if (classTestesVest[i].posicaoSelecionada == classTestesVest[i].testesVest!!.resposta) {
                        aulaTestefeito.certoerrado = 1.toDouble()
                    } else {
                        aulaTestefeito.certoerrado = 0.toDouble()
                    }
                    aulaTestefeito.indice_soltar = 0
                    aulaTestefeito.pendente = 0
                    aulaTestefeito.resposta = ""
                    aulaTestefeito.textopreenchido = ""
                    aulaTestefeito.alternativa_selecionada = classTestesVest[i].posicaoSelecionada
                    aulaTestefeito.preenchimento = ""
                    aulaTestefeito.unicode = 0
                    aulaTestefeito.id_tela = classTela!!.tela!!.id_tela

                    exer.aulaTestefeito = aulaTestefeito
                    exer.posicaoAulaTela = posicaoAulaTela
                    exer.posicaoTelaLink = posicaoTelaLink
                    exer.feito = (classTestesVest[i].posicaoSelecionada > -1 || (classTestesVest[i].posicaoSelecionada == -1 && classTela!!.tela!!.errado_nrespondido == 1))

                    if (posiExerciciosTela > -1) {
                        exerciciosTela[posiExerciciosTela] = exer
                    } else {
                        exerciciosTela.add(exer)
                    }
                }
            }
            i++
        }

        aulaAtribuida!!.status = 1

        var calendar = Calendar.getInstance()
        var tmp = calendar.timeInMillis - aulaFeita!!.tempo_tmp
        calendar.timeInMillis = tmp.toLong()

        aulaFeita!!.id_projeto = descricao!!.id_projeto
        aulaFeita!!.id_aluno = aluno!!.id_aluno
        aulaFeita!!.codigo_aluno = aluno!!.codigo
        aulaFeita!!.codigo_aula = descricao!!.codigo
        aulaFeita!!.data = Date(Calendar.getInstance().timeInMillis)
        aulaFeita!!.nota = 0.toDouble()
        aulaFeita!!.tentativas++
        aulaFeita!!.total_minutos = 0
        aulaFeita!!.ultima_tela_feita = classTela!!.tela!!.codigo
        aulaFeita!!.nome = descricao!!.nome
        aulaFeita!!.autor = descricao!!.autor
        aulaFeita!!.assunto = descricao!!.assunto
        aulaFeita!!.materia = descricao!!.materia
        aulaFeita!!.alvo = descricao!!.alvo
        aulaFeita!!.pendente = 0
        aulaFeita!!.status = 1
        aulaFeita!!.origem = UtilClass.ORIGEM_APLICATIVO
        aulaFeita!!.tempo_tmp += ((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)).toLong()
        aulaFeita!!.id_ultima_tela_feita = classTela!!.tela!!.id_tela

        return retorno
    }

    fun finalizarApresentacao() {
        try {
            var files = File(UtilClass.getCaminhoRaiz(act!!) + UtilClass.CAMINHO)
            files.deleteRecursively()
            files.mkdirs()
        } catch (ex: Exception) {
            UtilClass.trataErro(ex)
        }

        descricao = null
        aulaTela = ArrayList()
        posicaoAulaTela = 0
        posicaoTelaLink = 0
        aulaAtribuida = null
        aulaFeita = AulaFeita()
        aulaTestefeito = ArrayList()
        aulaIncompleta = ArrayList()
        exerciciosTela = ArrayList()
        iniciarTela()
    }

    fun finalizarProgress() {
        if (progress != null && progress!!.isShowing) {
            try {
                progress!!.dismiss()
                progress = null
            } catch (e: Exception) {
                UtilClass.trataErro(e)
            }
        }
    }

    fun finalizarProjeto() {
        var posicao = computarNotaFinal()
        if (posicao == -1) {//não tem nenhuma pendencia
            if (UtilClass.retJanelaFinalizar(descricao!!) == UtilClass.AVALIAR) {
                var dialog = DialogJanelaNota(act!!)
                dialog.show()
            }
        }
    }

    fun iniciarProgress(context: Context, msg: String) {
        if (context != null) {
            try {
                var progressBar = ProgressBar(context)
                progress = AlertDialog.Builder(context).create()
                if (msg != null) {
                    progress!!.setTitle(msg)
                }
                progress!!.setView(progressBar)
                progress!!.show()
            } catch (e: Exception) {
                UtilClass.trataErro(e)
            }
        }
    }

    fun iniciarProjeto() {
        finalizarApresentacao()

        UtilClass.verificaPastasIniciais(act!!)

        tempoInicial = Calendar.getInstance()
        descricao = null
        aulaTela = ArrayList()
        posicaoAulaTela = 0
        posicaoTelaLink = 0
        aulaAtribuida = null
        aulaFeita = AulaFeita()
        aulaTestefeito = ArrayList()
        aulaIncompleta = ArrayList()
        exerciciosTela = ArrayList()
        iniciarTela()
    }

    fun iniciarTela() {
        tvArrastar = null
        ivArrastarImagens = null
        ivArrastarN = null
        classLigasUsando = null

        rlTela = null
        removeAlt = 0
        removeLar = 0

        classTela = null

        classImagensEfeitos = ArrayList()
        classTextos = ArrayList()
        classRotulos = ArrayList()
        classFilmesTela = ArrayList()
        classAnimacoes = ArrayList()
        classLinhas = ArrayList()
        classArrastarSoltar = ArrayList()
        classArrastarSoltarImagens = ArrayList()
        classArrastarSoltarN = ArrayList()
        classGirasEfeitos = ArrayList()
        classLigas = ArrayList()
        classPreenchimentos = ArrayList()
        classDissertativa = ArrayList()
        classTestesVest = ArrayList()
    }

    fun moveuApresentacao(ev: MotionEvent) {//para mover pela tela os objetos que arrastam
        if (rlTela != null) {
            if (removeAlt == 0 && removeLar == 0) {
                var largura = 0
                var altura = 0
                if (descricao!!.redim_telas == UtilClass.REDIM_TELAS_TAMANHO_ORIGINAL) {
                    if (classTela!!.tela!!.largura > metrics!!.widthPixels && classTela!!.tela!!.altura > metrics!!.heightPixels) {//scroll na largura e altura
                        var frameLayoutTela = rlTela!!.layoutParams as FrameLayout.LayoutParams
                        largura = frameLayoutTela.width
                        altura = frameLayoutTela.height
                    } else if (classTela!!.tela!!.largura > metrics!!.widthPixels && classTela!!.tela!!.altura <= metrics!!.heightPixels) {//scroll na largura
                        var frameLayoutTela = rlTela!!.layoutParams as FrameLayout.LayoutParams
                        largura = frameLayoutTela.width
                        altura = frameLayoutTela.height
                    } else if (classTela!!.tela!!.largura <= metrics!!.widthPixels && classTela!!.tela!!.altura > metrics!!.heightPixels) {//scroll na altura
                        var frameLayoutTela = rlTela!!.layoutParams as FrameLayout.LayoutParams
                        largura = frameLayoutTela.width
                        altura = frameLayoutTela.height
                    } else {//sem scroll
                        var relativeLayoutTela = rlTela!!.layoutParams as RelativeLayout.LayoutParams
                        largura = relativeLayoutTela.width
                        altura = relativeLayoutTela.height
                    }
                } else if (descricao!!.redim_telas == UtilClass.REDIM_TELAS_TODA_TELA) {//sem scroll
                    var relativeLayoutTela = rlTela!!.layoutParams as RelativeLayout.LayoutParams
                    largura = relativeLayoutTela.width
                    altura = relativeLayoutTela.height
                } else if (descricao!!.redim_telas == UtilClass.REDIM_TELAS_LARGURA_TELA) {//scroll na altura
                    var frameLayoutTela = rlTela!!.layoutParams as FrameLayout.LayoutParams
                    largura = frameLayoutTela.width
                    altura = frameLayoutTela.height
                } else if (descricao!!.redim_telas == UtilClass.REDIM_TELAS_MAXIMO_PROPORCIONAL) {//sem scroll
                    var relativeLayoutTela = rlTela!!.layoutParams as RelativeLayout.LayoutParams
                    largura = relativeLayoutTela.width
                    altura = relativeLayoutTela.height
                }
                if (largura > 0 && altura > 0) {
                    if (metrics!!.widthPixels > largura) {
                        removeLar = (metrics!!.widthPixels - largura) / 2
                    } else {
                        removeLar = (largura - metrics!!.widthPixels) / 2
                    }
                    if (metrics!!.heightPixels > altura) {
                        removeAlt = (metrics!!.heightPixels - altura) / 2
                    } else {
                        removeAlt = (altura - metrics!!.heightPixels) / 2
                    }
                }
            }

            if (tvArrastar != null) {
                var rlArrastar = tvArrastar!!.layoutParams as RelativeLayout.LayoutParams
                rlArrastar.leftMargin = (ev.x - ((rlArrastar.width / 2) + removeLar)).toInt()
                rlArrastar.topMargin = (ev.y - (rlArrastar.height / 2) + removeAlt).toInt()
                tvArrastar!!.layoutParams = rlArrastar
            } else if (ivArrastarImagens != null) {
                var rlArrastarImagens = ivArrastarImagens!!.layoutParams as RelativeLayout.LayoutParams
                rlArrastarImagens.leftMargin = (ev.x - ((rlArrastarImagens.width / 2) + removeLar)).toInt()
                rlArrastarImagens.topMargin = (ev.y - (rlArrastarImagens.height / 2) + removeAlt).toInt()
                ivArrastarImagens!!.layoutParams = rlArrastarImagens
            } else if (ivArrastarN != null) {
                var rlArrastarN = ivArrastarN!!.layoutParams as RelativeLayout.LayoutParams
                rlArrastarN.leftMargin = (ev.x - ((rlArrastarN.width / 2) + removeLar)).toInt()
                rlArrastarN.topMargin = (ev.y - (rlArrastarN.height / 2) + removeAlt).toInt()
                ivArrastarN!!.layoutParams = rlArrastarN
            } else if (classLigasUsando != null && classLigasUsando!!.ivLinha != null) {
                if (classLigasUsando!!.ligando1) {
                    var rlLinha = classLigasUsando!!.ivLinha!!.layoutParams as RelativeLayout.LayoutParams

                    var xLigaIni = Math.round(classLigasUsando!!.liga1!!.esquerdo * classLigasUsando!!.pesoX) + (Math.round(classLigasUsando!!.pesoX * classLigasUsando!!.liga1!!.largura) / 2)
                    var yLigaIni = Math.round(classLigasUsando!!.liga1!!.topo * classLigasUsando!!.pesoY) + (Math.round(classLigasUsando!!.pesoY * classLigasUsando!!.liga1!!.altura) / 2)

                    var posX = ev.x.toInt() - removeLar
                    var posY = ev.y.toInt() - removeAlt

                    var xIni = posX
                    var xFim = xLigaIni
                    if (xLigaIni < xIni) {
                        xIni = xLigaIni
                        xFim = posX
                    }

                    var yIni = posY
                    var yFim = yLigaIni
                    if (yLigaIni < yIni) {
                        yIni = yLigaIni
                        yFim = posY
                    }

                    if ((xFim - xIni) < classLigasUsando!!.liga1!!.espessura_linha) {
                        rlLinha.width = classLigasUsando!!.liga1!!.espessura_linha
                    } else {
                        rlLinha.width = xFim - xIni
                    }
                    if ((yFim - yIni) < classLigasUsando!!.liga1!!.espessura_linha) {
                        rlLinha.height = classLigasUsando!!.liga1!!.espessura_linha
                    } else {
                        rlLinha.height = yFim - yIni
                    }
                    rlLinha.leftMargin = xIni
                    rlLinha.topMargin = yIni

                    classLigasUsando!!.ivLinha!!.layoutParams = rlLinha

                    if (xLigaIni < posX) {
                        xIni = 0
                        xFim = rlLinha.width
                    } else {
                        xIni = rlLinha.width
                        xFim = 0
                    }
                    if (yLigaIni < posY) {
                        yIni = 0
                        yFim = rlLinha.height
                    } else {
                        yIni = rlLinha.height
                        yFim = 0
                    }

                    try {
                        var bitmap = Bitmap.createBitmap(rlLinha.width, rlLinha.height, Bitmap.Config.ARGB_8888)
                        var canvas = Canvas(bitmap)
                        canvas.drawColor(Color.TRANSPARENT)
                        var paint = Paint()
                        paint.color = classLigasUsando!!.liga1!!.cor_linha
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = classLigasUsando!!.liga1!!.espessura_linha.toFloat()
                        paint.isAntiAlias = true
                        canvas.drawLine(xIni.toFloat(), yIni.toFloat(), xFim.toFloat(), yFim.toFloat(), paint)
                        classLigasUsando!!.ivLinha!!.setImageBitmap(bitmap)

                        UtilClass.trataErro("LIGAS", "\n\n\nLiga1: ${classLigasUsando!!.liga1!!.esquerdo} - ${classLigasUsando!!.liga1!!.esquerdo + classLigasUsando!!.liga1!!.largura} - ${classLigasUsando!!.liga1!!.topo} - ${classLigasUsando!!.liga1!!.topo + classLigasUsando!!.liga1!!.altura}\nQuadrado: ${rlLinha.leftMargin} - ${rlLinha.topMargin} - ${rlLinha.leftMargin + rlLinha.width} - ${rlLinha.topMargin + rlLinha.height}\nLinha: $xIni - $yIni - $xFim - $yFim")
                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                    }
                } else if (classLigasUsando!!.ligando2) {
                    var rlLinha = classLigasUsando!!.ivLinha!!.layoutParams as RelativeLayout.LayoutParams

                    var xLigaIni = Math.round(classLigasUsando!!.pesoX * classLigasUsando!!.liga2!!.esquerdo) + (Math.round(classLigasUsando!!.pesoX * classLigasUsando!!.liga2!!.largura) / 2)
                    var yLigaIni = Math.round(classLigasUsando!!.pesoY * classLigasUsando!!.liga2!!.topo) + (Math.round(classLigasUsando!!.pesoY * classLigasUsando!!.liga2!!.altura) / 2)

                    var posX = ev.x.toInt() - removeLar
                    var posY = ev.y.toInt() - removeAlt

                    var xIni = posX
                    var xFim = xLigaIni
                    if (xLigaIni < xIni) {
                        xIni = xLigaIni
                        xFim = posX
                    }

                    var yIni = posY
                    var yFim = yLigaIni
                    if (yLigaIni < yIni) {
                        yIni = yLigaIni
                        yFim = posY
                    }

                    if ((xFim - xIni) < classLigasUsando!!.liga2!!.espessura_linha) {
                        rlLinha.width = classLigasUsando!!.liga2!!.espessura_linha
                    } else {
                        rlLinha.width = xFim - xIni
                    }
                    if ((yFim - yIni) < classLigasUsando!!.liga2!!.espessura_linha) {
                        rlLinha.height = classLigasUsando!!.liga2!!.espessura_linha
                    } else {
                        rlLinha.height = yFim - yIni
                    }
                    rlLinha.leftMargin = xIni
                    rlLinha.topMargin = yIni

                    classLigasUsando!!.ivLinha!!.layoutParams = rlLinha

                    if (xLigaIni < posX) {
                        xIni = 0
                        xFim = rlLinha.width
                    } else {
                        xIni = rlLinha.width
                        xFim = 0
                    }
                    if (yLigaIni < posY) {
                        yIni = 0
                        yFim = rlLinha.height
                    } else {
                        yIni = rlLinha.height
                        yFim = 0
                    }

                    try {
                        var bitmap = Bitmap.createBitmap(rlLinha.width, rlLinha.height, Bitmap.Config.ARGB_8888)
                        var canvas = Canvas(bitmap)
                        canvas.drawColor(Color.TRANSPARENT)
                        var paint = Paint()
                        paint.color = classLigasUsando!!.liga2!!.cor_linha
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = classLigasUsando!!.liga2!!.espessura_linha.toFloat()
                        paint.isAntiAlias = true
                        canvas.drawLine(xIni.toFloat(), yIni.toFloat(), xFim.toFloat(), yFim.toFloat(), paint)
                        classLigasUsando!!.ivLinha!!.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        UtilClass.trataErro(e)
                    }
                }
            }
        }
    }

    fun parouApresentacao(ev: MotionEvent) {
        if (classLigasUsando != null && classLigasUsando!!.ivLinha != null) {
            if (classLigasUsando!!.ligando1) {
                var achou = false
                var i = 0
                var rlLinha = classLigasUsando!!.ivLinha!!.layoutParams as RelativeLayout.LayoutParams
                while (!achou && i < classLigas.size) {

                    var xLigaIni = Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga2!!.esquerdo)
                    var yLigaIni = Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga2!!.topo)
                    var xLigaFim =
                        Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga2!!.esquerdo) + Math.round(
                            classLigasUsando!!.pesoX * classLigas[i]!!.liga2!!.largura)
                    var yLigaFim = Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga2!!.topo) + Math.round(
                        classLigasUsando!!.pesoY * classLigas[i]!!.liga2!!.altura)

                    var posX = ev.x.toInt() - removeLar
                    var posY = ev.y.toInt() - removeAlt

                    if (posX >= xLigaIni && posX <= xLigaFim && posY >= yLigaIni && posY <= yLigaFim) {//ligou certo
                        if (classLigasUsando!!.liga1!!.indice == classLigas[i].liga2!!.indice || classLigasUsando!!.liga1!!.liga_errado == 1) {
                            xLigaIni =
                                Math.round(classLigasUsando!!.pesoX * classLigasUsando!!.liga1!!.esquerdo) + (Math.round(
                                    classLigasUsando!!.pesoX * classLigasUsando!!.liga1!!.largura) / 2)
                            yLigaIni =
                                Math.round(classLigasUsando!!.pesoY * classLigasUsando!!.liga1!!.topo) + (Math.round(
                                    classLigasUsando!!.pesoY * classLigasUsando!!.liga1!!.altura) / 2)
                            xLigaFim =
                                Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga2!!.esquerdo) + (Math.round(
                                    classLigasUsando!!.pesoX * classLigas[i]!!.liga2!!.largura) / 2)
                            yLigaFim =
                                Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga2!!.topo) + (Math.round(
                                    classLigasUsando!!.pesoY * classLigas[i]!!.liga2!!.altura) / 2)

                            if (xLigaFim < xLigaIni) {
                                posX = xLigaFim
                                xLigaFim = xLigaIni
                                xLigaIni = posX
                            }
                            if (yLigaFim < yLigaIni) {
                                posY = yLigaFim
                                yLigaFim = yLigaIni
                                yLigaIni = posY
                            }

                            if ((xLigaFim - xLigaIni) < classLigasUsando!!.liga1!!.espessura_linha) {
                                rlLinha.width = classLigasUsando!!.liga1!!.espessura_linha
                            } else {
                                rlLinha.width = xLigaFim - xLigaIni
                            }
                            if ((yLigaFim - yLigaIni) < classLigasUsando!!.liga1!!.espessura_linha) {
                                rlLinha.height = classLigasUsando!!.liga1!!.espessura_linha
                            } else {
                                rlLinha.height = yLigaFim - yLigaIni
                            }
                            rlLinha.leftMargin = xLigaIni
                            rlLinha.topMargin = yLigaIni

                            classLigasUsando!!.ivLinha!!.layoutParams = rlLinha


                            xLigaIni = 0
                            xLigaFim = rlLinha.width

                            yLigaIni = 0
                            yLigaFim = rlLinha.height

                            try {
                                var bitmap = Bitmap.createBitmap(rlLinha.width, rlLinha.height, Bitmap.Config.ARGB_8888)
                                var canvas = Canvas(bitmap)
                                canvas.drawColor(Color.TRANSPARENT)
                                var paint = Paint()
                                paint.color = classLigasUsando!!.liga1!!.cor_linha
                                paint.style = Paint.Style.STROKE
                                paint.strokeWidth = classLigasUsando!!.liga1!!.espessura_linha.toFloat()
                                paint.isAntiAlias = true
                                canvas.drawLine(xLigaIni.toFloat(),
                                    yLigaIni.toFloat(),
                                    xLigaFim.toFloat(),
                                    yLigaFim.toFloat(),
                                    paint)
                                classLigasUsando!!.ivLinha!!.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                UtilClass.trataErro(e)
                            }

                            if (classLigasUsando!!.liga1!!.indice == classLigas[i].liga2!!.indice) {
                                classLigasUsando!!.ligouCorreta1 = true
                                classLigasUsando!!.ligouCorreta2 = true
                            } else {
                                classLigasUsando!!.ligouErrada1 = true
                            }

                            achou = true
                            i = classLigas.size
                        } else {
                            aulaFeita!!.erros_anterior++
                        }
                    } else {
                        i++
                    }
                }

                if (!achou) {
                    rlLinha.width = 0
                    rlLinha.height = 0
                }

                classLigasUsando!!.ivLinha!!.layoutParams = rlLinha
                classLigasUsando!!.ligando1 = false
                classLigasUsando!!.ligando2 = false
                classLigasUsando = null
                rlTela = null
            } else if (classLigasUsando!!.ligando2) {
                var achou = false
                var i = 0
                var rlLinha = classLigasUsando!!.ivLinha!!.layoutParams as RelativeLayout.LayoutParams
                while (!achou && i < classLigas.size) {

                    var xLigaIni = Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga1!!.esquerdo)
                    var yLigaIni = Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga1!!.topo)
                    var xLigaFim =
                        Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga1!!.esquerdo) + Math.round(
                            classLigasUsando!!.pesoX * classLigas[i]!!.liga1!!.largura)
                    var yLigaFim = Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga1!!.topo) + Math.round(
                        classLigasUsando!!.pesoY * classLigas[i]!!.liga1!!.altura)

                    var posX = ev.x.toInt() - removeLar
                    var posY = ev.y.toInt() - removeAlt

                    if (posX >= xLigaIni && posX <= xLigaFim && posY >= yLigaIni && posY <= yLigaFim) {//ligou certo
                        if (classLigasUsando!!.liga2!!.indice == classLigas[i].liga2!!.indice || classLigasUsando!!.liga1!!.liga_errado == 1) {
                            xLigaIni =
                                Math.round(classLigasUsando!!.pesoX * classLigasUsando!!.liga2!!.esquerdo) + (Math.round(
                                    classLigasUsando!!.pesoX * classLigasUsando!!.liga2!!.largura) / 2)
                            yLigaIni =
                                Math.round(classLigasUsando!!.pesoY * classLigasUsando!!.liga2!!.topo) + (Math.round(
                                    classLigasUsando!!.pesoY * classLigasUsando!!.liga2!!.altura) / 2)
                            xLigaFim =
                                Math.round(classLigasUsando!!.pesoX * classLigas[i]!!.liga1!!.esquerdo) + (Math.round(
                                    classLigasUsando!!.pesoX * classLigas[i]!!.liga1!!.largura) / 2)
                            yLigaFim =
                                Math.round(classLigasUsando!!.pesoY * classLigas[i]!!.liga1!!.topo) + (Math.round(
                                    classLigasUsando!!.pesoY * classLigas[i]!!.liga1!!.altura) / 2)

                            if (xLigaFim < xLigaIni) {
                                posX = xLigaFim
                                xLigaFim = xLigaIni
                                xLigaIni = posX
                            }
                            if (yLigaFim < yLigaIni) {
                                posY = yLigaFim
                                yLigaFim = yLigaIni
                                yLigaIni = posY
                            }

                            if ((xLigaFim - xLigaIni) < classLigasUsando!!.liga2!!.espessura_linha) {
                                rlLinha.width = classLigasUsando!!.liga2!!.espessura_linha
                            } else {
                                rlLinha.width = xLigaFim - xLigaIni
                            }
                            if ((yLigaFim - yLigaIni) < classLigasUsando!!.liga2!!.espessura_linha) {
                                rlLinha.height = classLigasUsando!!.liga2!!.espessura_linha
                            } else {
                                rlLinha.height = yLigaFim - yLigaIni
                            }
                            rlLinha.leftMargin = xLigaIni
                            rlLinha.topMargin = yLigaIni

                            classLigasUsando!!.ivLinha!!.layoutParams = rlLinha


                            xLigaIni = 0
                            xLigaFim = rlLinha.width

                            yLigaIni = 0
                            yLigaFim = rlLinha.height

                            try {
                                var bitmap = Bitmap.createBitmap(rlLinha.width, rlLinha.height, Bitmap.Config.ARGB_8888)
                                var canvas = Canvas(bitmap)
                                canvas.drawColor(Color.TRANSPARENT)
                                var paint = Paint()
                                paint.color = classLigasUsando!!.liga2!!.cor_linha
                                paint.style = Paint.Style.STROKE
                                paint.strokeWidth = classLigasUsando!!.liga2!!.espessura_linha.toFloat()
                                paint.isAntiAlias = true
                                canvas.drawLine(xLigaIni.toFloat(),
                                    yLigaIni.toFloat(),
                                    xLigaFim.toFloat(),
                                    yLigaFim.toFloat(),
                                    paint)
                                classLigasUsando!!.ivLinha!!.setImageBitmap(bitmap)

                            } catch (e: Exception) {
                                UtilClass.trataErro(e)
                            }

                            if (classLigasUsando!!.liga2!!.indice == classLigas[i].liga1!!.indice) {
                                classLigasUsando!!.ligouCorreta1 = true
                                classLigasUsando!!.ligouCorreta2 = true
                            } else {
                                classLigasUsando!!.ligouErrada2 = true
                            }

                            achou = true
                            i = classLigas.size
                        } else {
                            aulaFeita!!.erros_anterior++
                        }
                    } else {
                        i++
                    }
                }

                if (!achou) {
                    rlLinha.width = 0
                    rlLinha.height = 0
                }

                classLigasUsando!!.ivLinha!!.layoutParams = rlLinha
                classLigasUsando!!.ligando1 = false
                classLigasUsando!!.ligando2 = false
                classLigasUsando = null
                rlTela = null
            }
        }
    }

    fun possuiExercicios(): Boolean {
        var possuiExercicios = false

        //verificar as animações
        var i = 0
        while (!possuiExercicios && i < classAnimacoes.size) {
            if (classAnimacoes[i].animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classAnimacoes[i].clicouObjeto) {
                possuiExercicios = true
            } else if (classAnimacoes[i].animacoes!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                possuiExercicios = true
            }
            i++
        }

        //verificar as imagens
        i = 0
        while (!possuiExercicios && i < classImagensEfeitos.size) {
            if (classImagensEfeitos[i].imagensEfeitos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classImagensEfeitos[i].clicouObjeto) {
                possuiExercicios = true
            } else if (classImagensEfeitos[i].imagensEfeitos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                possuiExercicios = true
            }
            i++
        }

        //verificar a rótulos
        i = 0
        while (!possuiExercicios && i < classRotulos.size) {
            if (classRotulos[i].rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_ERRADO && classRotulos[i].clicouObjeto) {
                possuiExercicios = true
            } else if (classRotulos[i].rotulos!!.registra_imagem == UtilClass.REGISTRA_IMAGEM_CERTO) {
                possuiExercicios = true
            }
            i++
        }

        //verificar arrastar/soltar
        if (!possuiExercicios && classArrastarSoltar.size > 0) {
            possuiExercicios = true
        }

        //verificar arrastar/soltar imagens
        if (!possuiExercicios && classArrastarSoltarImagens.size > 0) {
            possuiExercicios = true
        }

        //verificar arrastar dif soltar
        if (!possuiExercicios && classArrastarSoltarN.size > 0) {
            possuiExercicios = true
        }

        //verificar dissertativas
        if (!possuiExercicios && classDissertativa.size > 0) {
            possuiExercicios = true
        }

        //verificar gira figuras
        if (!possuiExercicios && classGirasEfeitos.size > 0) {
            possuiExercicios = true
        }

        //verificar liga pontos
        if (!possuiExercicios && classLigas.size > 0) {
            possuiExercicios = true
        }

        //verificar preenchimentos
        if (!possuiExercicios && classPreenchimentos.size > 0) {
            possuiExercicios = true
        }

        //verificar teste de vestibular
        if (!possuiExercicios && classTestesVest.size > 0) {
            possuiExercicios = true
        }

        return possuiExercicios
    }

    fun retPosiExercicioTela(tipoobj: Long, indice: Int, indice_obj: Int): Int {
        var posi = -1
        var i = 0
        while (i < exerciciosTela.size) {
            if (exerciciosTela[i].aulaTestefeito != null && exerciciosTela[i].aulaTestefeito!!.tipoobj == tipoobj && exerciciosTela[i].aulaTestefeito!!.indice == indice && exerciciosTela[i].aulaTestefeito!!.indice_obj == indice_obj) {
                posi = i
                i = exerciciosTela.size
            } else {
                i++
            }
        }
        return posi
    }

    fun setMessageProgress(msg: String) {
        if (progress != null && progress!!.isShowing) {
            progress!!.setTitle(msg)
        }
    }
}