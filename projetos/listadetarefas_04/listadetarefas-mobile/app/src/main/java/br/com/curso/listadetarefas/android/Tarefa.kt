package br.com.curso.listadetarefas.android

// --- CORREÇÃO AQUI ---
// Adicionando o import para a anotação @Transient
import kotlin.jvm.Transient

data class Tarefa(
    val id: Long?,
    var descricao: String?,
    var concluida: Boolean,
    // @Transient informa ao Gson para ignorar este campo na serialização para a API.
    // É um estado que existe apenas na interface do usuário (UI).
    @Transient var selecionada: Boolean = false
)