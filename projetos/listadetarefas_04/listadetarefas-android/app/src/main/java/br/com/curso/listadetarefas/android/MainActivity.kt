@file:OptIn(ExperimentalMaterial3Api::class)
package br.com.curso.listadetarefas.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
// --- REMOVER IMPORTS ANTIGOS ---
// import androidx.compose.material.pullrefresh.PullRefreshIndicator
// import androidx.compose.material.pullrefresh.pullRefresh
// import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
// --- ADICIONAR NOVO IMPORT DO MATERIAL 3 ---
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.curso.listadetarefas.android.ui.theme.listadetarefasAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            listadetarefasAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TarefaApp()
                }
            }
        }
    }
}

@Composable
fun TarefaApp(tarefaViewModel: TarefaViewModel = viewModel()) {
    val uiState by tarefaViewModel.uiState.collectAsState()
    var tarefaParaEditar by remember { mutableStateOf<Tarefa?>(null) }

    // --- MUDANÇA: Usar o rememberPullToRefreshState do Material 3 ---
    val pullToRefreshState = rememberPullToRefreshState()

    // Lógica para lidar com a atualização
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            tarefaViewModel.carregarTarefas()
        }
    }

    // Lógica para parar a animação de refresh quando o carregamento terminar
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("To-Do List Android") }) }) { paddingValues ->
        // --- MUDANÇA: Usar PullToRefreshBox ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            if (uiState.error != null) {
                Text(
                    text = "Erro: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                // O conteúdo da tela (a lista) vai aqui dentro
                TarefaScreen(
                    tarefas = uiState.tarefas,
                    isLoading = uiState.isLoading,
                    onAddTask = tarefaViewModel::adicionarTarefa,
                    onUpdateTask = tarefaViewModel::updateTarefa,
                    onDeleteTask = tarefaViewModel::deleteTarefa,
                    onTaskClick = { tarefa -> tarefaParaEditar = tarefa }
                )
            }

            // O indicador de refresh agora é o PullToRefreshContainer
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // O diálogo de edição permanece o mesmo
            tarefaParaEditar?.let { tarefa ->
                EditTaskDialog(
                    tarefa = tarefa,
                    onDismiss = { tarefaParaEditar = null },
                    onSave = { novaDescricao ->
                        val tarefaAtualizada = tarefa.copy(descricao = novaDescricao)
                        tarefaViewModel.updateTarefa(tarefaAtualizada)
                        tarefaParaEditar = null
                    }
                )
            }
        }
    }
}

@Composable
fun TarefaScreen(
    tarefas: List<Tarefa>,
    isLoading: Boolean,
    onAddTask: (String) -> Unit,
    onUpdateTask: (Tarefa) -> Unit,
    onDeleteTask: (Long?) -> Unit,
    onTaskClick: (Tarefa) -> Unit
) {
    var textoNovaTarefa by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = textoNovaTarefa,
                onValueChange = { textoNovaTarefa = it },
                label = { Text("Nova tarefa") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (textoNovaTarefa.isNotBlank()) {
                    onAddTask(textoNovaTarefa)
                    textoNovaTarefa = ""
                }
            }) { Text("Add") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // A lógica de exibição (loading, lista vazia, lista com itens) permanece a mesma
        if (isLoading && tarefas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (tarefas.isEmpty()) {
            Text(
                text = "Nenhuma tarefa encontrada.\nPuxe para atualizar ou adicione uma nova!",
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tarefas, key = { it.id!! }) { tarefa ->
                    TarefaItem(
                        tarefa = tarefa,
                        onCheckedChange = { isChecked -> onUpdateTask(tarefa.copy(concluida = isChecked)) },
                        onDeleteClick = { onDeleteTask(tarefa.id) },
                        onTaskClick = { onTaskClick(tarefa) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TarefaItem(
    tarefa: Tarefa,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = tarefa.concluida, onCheckedChange = onCheckedChange)
        Text(
            text = tarefa.descricao ?: "",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            style = if (tarefa.concluida) LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) else LocalTextStyle.current
        )
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Deletar Tarefa")
        }
    }
}

@Composable
fun EditTaskDialog(tarefa: Tarefa, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var textoEditado by remember { mutableStateOf(tarefa.descricao ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarefa") },
        text = {
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { Button(onClick = { if (textoEditado.isNotBlank()) { onSave(textoEditado) } }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
