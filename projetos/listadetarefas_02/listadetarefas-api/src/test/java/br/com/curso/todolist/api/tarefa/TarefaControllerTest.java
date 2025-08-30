package br.com.curso.todolist.api.tarefa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarefaService tarefaService;

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos para JSON

    @Test
    void listarTarefas_deveRetornarListaDeTarefas() throws Exception {
        // Arrange
        Tarefa tarefa1 = new Tarefa(1L, "Tarefa 1", "Desc 1", false);
        Tarefa tarefa2 = new Tarefa(2L, "Tarefa 2", "Desc 2", true);
        when(tarefaService.listarTodas()).thenReturn(List.of(tarefa1, tarefa2));

        // Act & Assert
        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo", is("Tarefa 1")))
                .andExpect(jsonPath("$[1].titulo", is("Tarefa 2")));
    }

    @Test
    void buscarTarefaPorId_quandoEncontrada_deveRetornarTarefa() throws Exception {
        // Arrange
        Tarefa tarefa = new Tarefa(1L, "Tarefa Teste", "Desc Teste", false);
        when(tarefaService.buscarPorId(1L)).thenReturn(Optional.of(tarefa));

        // Act & Assert
        mockMvc.perform(get("/tarefas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Tarefa Teste")));
    }

    @Test
    void buscarTarefaPorId_quandoNaoEncontrada_deveRetornarNotFound() throws Exception {
        // Arrange
        when(tarefaService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tarefas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarTarefa_deveRetornarTarefaCriadaComStatusCreated() throws Exception {
        // Arrange
        Tarefa tarefaParaCriar = new Tarefa(null, "Nova Tarefa", "Nova Desc", false);
        Tarefa tarefaCriada = new Tarefa(1L, "Nova Tarefa", "Nova Desc", false);
        when(tarefaService.criar(any(Tarefa.class))).thenReturn(tarefaCriada);

        // Act & Assert
        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tarefaParaCriar)))
                .andExpect(status().isCreated()) // Verifica o status 201 Created
                .andExpect(header().exists("Location")) // Verifica se o cabeçalho Location existe
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Nova Tarefa")));
    }

    @Test
    void atualizarTarefa_quandoEncontrada_deveRetornarTarefaAtualizada() throws Exception {
        // Arrange
        Tarefa tarefaAtualizada = new Tarefa(1L, "Tarefa Atualizada", "Desc Atualizada", true);
        when(tarefaService.atualizar(eq(1L), any(Tarefa.class))).thenReturn(tarefaAtualizada);

        // Act & Assert
        mockMvc.perform(put("/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tarefaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Tarefa Atualizada")))
                .andExpect(jsonPath("$.concluida", is(true)));
    }

    @Test
    void deletarTarefa_quandoEncontrada_deveRetornarNoContent() throws Exception {
        // Arrange
        doNothing().when(tarefaService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/tarefas/1"))
                .andExpect(status().isNoContent());
    }
}