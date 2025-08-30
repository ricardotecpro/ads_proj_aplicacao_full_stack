@file:OptIn(ExperimentalMaterial3Api::class)

package br.com.curso.listadetarefas.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.curso.listadetarefas.android.ui.theme.ActionBlue
import br.com.curso.listadetarefas.android.ui.theme.ActionGreen
import br.com.curso.listadetarefas.android.ui.theme.ActionRed
import br.com.curso.listadetarefas.android.ui.theme.ActionGray
import br.com.curso.listadetarefas.android.ui.theme.ActionYellow
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
    val existemTarefasSelecionadas = uiState.tarefas.any { it.selecionada }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Lista de Tarefas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ActionBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { tarefaViewModel.carregarTarefas() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Atualizar", tint = Color.White)
                    }
                    if (existemTarefasSelecionadas) {
                        IconButton(onClick = { tarefaViewModel.deletarTarefasSelecionadas() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir Selecionadas", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.error != null) {
                Text(
                    text = "Erro: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                TarefaScreen(
                    tarefas = uiState.tarefas,
                    isLoading = uiState.isLoading,
                    onAddTask = tarefaViewModel::adicionarTarefa,
                    onUpdateTask = tarefaViewModel::updateTarefa,
                    onDeleteTask = tarefaViewModel::deleteTarefa,
                    onEditClick = { tarefa -> tarefaParaEditar = tarefa },
                    onToggleSelecao = tarefaViewModel::toggleSelecao
                )
            }

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
    onEditClick: (Tarefa) -> Unit,
    onToggleSelecao: (Long) -> Unit
) {
    var textoNovaTarefa by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoNovaTarefa,
                onValueChange = { textoNovaTarefa = it },
                label = { Text("O que precisa ser feito?") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (textoNovaTarefa.isNotBlank()) {
                        onAddTask(textoNovaTarefa)
                        textoNovaTarefa = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Adicionar")
            }
        }

        if (isLoading && tarefas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (tarefas.isEmpty()) {
            Text(
                text = "Nenhuma tarefa encontrada.\nAdicione uma nova tarefa!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            TaskListHeader()
            Divider()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tarefas, key = { it.id!! }) { tarefa ->
                    TarefaItem(
                        tarefa = tarefa,
                        onStatusChange = { isChecked -> onUpdateTask(tarefa.copy(concluida = isChecked)) },
                        onDeleteClick = { onDeleteTask(tarefa.id) },
                        onEditClick = { onEditClick(tarefa) },
                        onToggleSelecao = { onToggleSelecao(tarefa.id!!) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TaskListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Sel.", modifier = Modifier.width(48.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Text("Status", modifier = Modifier.width(64.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        // --- CORREÇÃO AQUI ---
        // Combinando os dois modifiers em um só para o Text "Descrição"
        Text(
            text = "Descrição",
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Text("Ações", modifier = Modifier.width(100.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TarefaItem(
    tarefa: Tarefa,
    onStatusChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleSelecao: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
            Checkbox(checked = tarefa.selecionada, onCheckedChange = { onToggleSelecao() })
        }
        Box(modifier = Modifier.width(64.dp), contentAlignment = Alignment.Center) {
            Checkbox(checked = tarefa.concluida, onCheckedChange = onStatusChange)
        }
        Text(
            text = tarefa.descricao ?: "",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            style = if (tarefa.concluida)
                MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
            else MaterialTheme.typography.bodyLarge
        )
        Row(
            modifier = Modifier.width(100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar Tarefa", tint = ActionYellow)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Deletar Tarefa", tint = ActionRed)
            }
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
                label = { Text("Nova descrição") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (textoEditado.isNotBlank()) { onSave(textoEditado) } },
                colors = ButtonDefaults.buttonColors(containerColor = ActionGreen)
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = ActionGray)
            ) {
                Text("Cancelar")
            }
        }
    )
}