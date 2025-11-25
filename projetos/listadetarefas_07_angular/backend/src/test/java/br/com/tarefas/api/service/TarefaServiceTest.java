package br.com.tarefas.api.service;

import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TarefaServiceTest {

    @InjectMocks
    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarTarefa_ComSucesso() {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Teste");

        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        Tarefa criada = tarefaService.criarTarefa(tarefa);
        assertNotNull(criada);
        assertEquals("Teste", criada.getTitulo());
    }

    @Test
    void criarTarefa_SemTitulo_DeveLancarExcecao() {
        Tarefa tarefa = new Tarefa();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tarefaService.criarTarefa(tarefa);
        });

        assertEquals("O titulo da tarefa e obrigatorio.", exception.getMessage());
    }

    @Test
    void buscarPorId_Existente() {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        Tarefa encontrada = tarefaService.buscarPorId(1L);
        assertEquals(1L, encontrada.getId());
    }

    @Test
    void buscarPorId_NaoExistente_DeveLancarExcecao() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tarefaService.buscarPorId(1L);
        });
    }
}
