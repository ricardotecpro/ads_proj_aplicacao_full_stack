# 📱 Módulo 4: Mobile (Android) - Adotando Padrões de UI Modernos (MVVM)

**Objetivo:** Implementar uma interação CRUD moderna e fluida, utilizando gestos de deslizar (swipe), seguindo o padrão **MVVM** com Jetpack Compose.

### ### 👉 Passo 1: Implementando Ações de Deslizar (Swipe)
* **Melhoria:** Substituímos botões estáticos por ações de deslizar, um padrão de UX universal em dispositivos móveis. Usaremos o `SwipeToDismissBox` do Material 3, a abordagem recomendada pelo Google.

1.  Primeiro, modifique a `LazyColumn` em `MainActivity.kt` para usar o novo componente `TarefaItemSwipeable`.
2.  Crie o novo Composable `TarefaItemSwipeable`.

**`MainActivity.kt` (Trecho da `LazyColumn`)**
```kotlin
// ... dentro do Composable TarefaScreen ...
LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(tarefas, key = { it.id!! }) { tarefa ->
        // Substituindo o antigo TarefaItem pelo novo componente com swipe
        TarefaItemSwipeable( 
            tarefa = tarefa,
            onEdit = { onTaskClick(tarefa) }, // Abre o diálogo de edição
            onDelete = { onDeleteTask(tarefa.id) },
            onCheckedChange = { isChecked -> onUpdateTask(tarefa.copy(concluida = isChecked)) }
        )
        Divider()
    }
}
```

**Novo Composable: `TarefaItemSwipeable.kt`**
* **Boas Práticas:** Este Composable é reutilizável e encapsula toda a lógica e estado do gesto de swipe, mantendo a `MainActivity` mais limpa.
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarefaItemSwipeable(
    tarefa: Tarefa,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {
                DismissValue.DismissedToEnd -> onDelete() // Swipe para a direita -> Deletar
                DismissValue.DismissedToStart -> onEdit() // Swipe para a esquerda -> Editar
                DismissValue.Default -> {}
            }
            // Retorna false para que o item não suma da lista, apenas dispare a ação.
            return@rememberDismissState false 
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true, // Habilita swipe para direita
        enableDismissFromEndToStart = true, // Habilita swipe para esquerda
        backgroundContent = {
            // Desenha o fundo dinâmico (cor e ícone) baseado na direção do swipe
            val direction = dismissState.dismissDirection ?: return@SwipeToDismissBox
            val color by animateColorAsState(
                when (direction) {
                    DismissDirection.StartToEnd -> Color(0xFFFF4444)
                    DismissDirection.EndToStart -> Color(0xFF33B5E5)
                }
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Delete
                DismissDirection.EndToStart -> Icons.Default.Edit
            }
            
            Box(
                Modifier.fillMaxSize().background(color).padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = "Ação", tint = Color.White)
            }
        }
    ) {
        // Conteúdo principal do item (o mesmo do TarefaItem anterior)
        TarefaItem(tarefa = tarefa, onCheckedChange = onCheckedChange, onDeleteClick = {}, onTaskClick = {})
    }
}
```
* O `ViewModel` não precisa de alterações, pois a View (`MainActivity`) continua chamando os mesmos métodos de negócio. A mudança é puramente na camada de UI.