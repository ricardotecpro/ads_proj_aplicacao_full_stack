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
                val novaTarefa = Tarefa(id = null, descricao = descricao, concluida = false)
                val tarefaAdicionada = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.addTarefa(novaTarefa)
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
                        val listaAtualizada = currentState.tarefas.map { t ->
                            if (t.id == tarefaAtualizada.id) tarefaAtualizada.copy(selecionada = t.selecionada) else t
                        }
                        currentState.copy(tarefas = listaAtualizada)
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

    // --- NOVA FUNÇÃO ---
    // Alterna o estado de seleção de uma tarefa específica.
    fun toggleSelecao(id: Long) {
        _uiState.update { currentState ->
            val tarefasAtualizadas = currentState.tarefas.map {
                if (it.id == id) it.copy(selecionada = !it.selecionada) else it
            }
            currentState.copy(tarefas = tarefasAtualizadas)
        }
    }

    // --- NOVA FUNÇÃO ---
    // Deleta todas as tarefas que estão marcadas como selecionadas.
    fun deletarTarefasSelecionadas() {
        viewModelScope.launch {
            val tarefasParaDeletar = _uiState.value.tarefas.filter { it.selecionada && it.id != null }
            if (tarefasParaDeletar.isEmpty()) return@launch

            try {
                // Deleta as tarefas na API
                withContext(Dispatchers.IO) {
                    tarefasParaDeletar.forEach { tarefa ->
                        RetrofitClient.instance.deleteTarefa(tarefa.id!!)
                    }
                }
                // Remove as tarefas da UI
                _uiState.update { currentState ->
                    val idsParaDeletar = tarefasParaDeletar.map { it.id }.toSet()
                    currentState.copy(tarefas = currentState.tarefas.filter { it.id !in idsParaDeletar })
                }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao deletar tarefas selecionadas", e)
                _uiState.update { it.copy(error = "Falha ao deletar tarefas") }
            }
        }
    }
}
