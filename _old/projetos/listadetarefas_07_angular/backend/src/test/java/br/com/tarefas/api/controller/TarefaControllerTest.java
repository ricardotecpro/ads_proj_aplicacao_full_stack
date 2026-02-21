package br.com.tarefas.api.controller;

import br.com.tarefas.api.dto.TarefaDTO;
import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
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
    private ObjectMapper objectMapper;

    @Test
    void listar_DeveRetornarListaVazia() throws Exception {
        when(tarefaService.listarTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void criar_DeveRetornarTarefaCriada() throws Exception {
        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Nova Tarefa");
        dto.setConcluida(false);

        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Nova Tarefa");
        tarefa.setConcluida(false);

        when(tarefaService.criarTarefa(any(Tarefa.class))).thenReturn(tarefa);

        mockMvc.perform(post("/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Nova Tarefa"));
    }
}
