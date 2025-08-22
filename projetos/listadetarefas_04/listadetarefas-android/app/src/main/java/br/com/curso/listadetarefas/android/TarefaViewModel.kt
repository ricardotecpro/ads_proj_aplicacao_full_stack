// Cole o código completo da classe TarefaViewModel do guia anterior aqui.
// Ele contém a classe TarefaUiState e a lógica para carregar, adicionar,
// atualizar e deletar tarefas usando Coroutines.
package br.com.curso.listadetarefas.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TarefaUiState(
    val tarefas: List<Tarefa> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class TarefaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TarefaUiState())
    val uiState: StateFlow<TarefaUiState> = _uiState.asStateFlow()
    private val TAG = "TarefaViewModel"

    init { carregarTarefas() }

    fun carregarTarefas() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val tarefasDaApi = withContext(Dispatchers.IO) { RetrofitClient.instance.getTarefas() }
                _uiState.update { it.copy(isLoading = false, tarefas = tarefasDaApi, error = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao carregar tarefas", e)
                _uiState.update { it.copy(isLoading = false, error = "Falha ao carregar tarefas") }
            }
        }
    }
    fun adicionarTarefa(descricao: String) {
        viewModelScope.launch {
            try {
                val tarefaAdicionada = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.addTarefa(Tarefa(id = null, descricao = descricao, concluida = false))
                }
                _uiState.update { it.copy(tarefas = it.tarefas + tarefaAdicionada) }
            } catch (e: Exception) { Log.e(TAG, "Falha ao adicionar tarefa", e) }
        }
    }
    fun updateTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            try {
                tarefa.id?.let {
                    val tarefaAtualizada = withContext(Dispatchers.IO) { RetrofitClient.instance.updateTarefa(it, tarefa) }
                    _uiState.update { currentState ->
                        currentState.copy(tarefas = currentState.tarefas.map { t -> if (t.id == tarefaAtualizada.id) tarefaAtualizada else t })
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Falha ao atualizar tarefa", e) }
        }
    }
    fun deleteTarefa(id: Long?) {
        viewModelScope.launch {
            try {
                id?.let {
                    withContext(Dispatchers.IO) { RetrofitClient.instance.deleteTarefa(it) }
                    _uiState.update { currentState -> currentState.copy(tarefas = currentState.tarefas.filter { t -> t.id != id }) }
                }
            } catch (e: Exception) { Log.e(TAG, "Falha ao deletar tarefa", e) }
        }
    }
}