package br.com.curso.listadetarefas.android
// data class gera automaticamente getters, setters, equals, etc.
data class Tarefa(
    val id: Long?,
    var descricao: String?,
    var concluida: Boolean
)