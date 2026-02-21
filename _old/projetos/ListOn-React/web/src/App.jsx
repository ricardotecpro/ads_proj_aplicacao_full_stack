import { useState, useEffect } from 'react';
import { Container, Typography, Box, CssBaseline, AppBar, Toolbar } from '@mui/material';
import TaskList from './components/TaskList';
import TaskForm from './components/TaskForm';
import { getTasks, createTask, updateTask, deleteTask } from './api/apiService';

function App() {
    const [tasks, setTasks] = useState([]);

    // Função para carregar as tarefas da API
    const fetchTasks = async () => {
        try {
            const response = await getTasks();
            setTasks(response.data);
        } catch (error) {
            console.error("Erro ao buscar tarefas:", error);
        }
    };

    // useEffect para carregar as tarefas quando o componente montar
    useEffect(() => {
        fetchTasks();
    }, []);

    // --- Funções de Manipulação de Dados ---

    const handleAddTask = async (title, description) => {
        try {
            const newTask = { title, description, completed: false };
            await createTask(newTask);
            fetchTasks(); // Recarrega a lista
        } catch (error) {
            console.error("Erro ao adicionar tarefa:", error);
        }
    };

    const handleToggleTask = async (task) => {
        try {
            const updatedTask = { ...task, completed: !task.completed };
            await updateTask(task.id, updatedTask);
            fetchTasks(); // Recarrega a lista
        } catch (error) {
            console.error("Erro ao atualizar tarefa:", error);
        }
    };

    const handleDeleteTask = async (id) => {
        try {
            await deleteTask(id);
            fetchTasks(); // Recarrega a lista
        } catch (error) {
            console.error("Erro ao deletar tarefa:", error);
        }
    };

    return (
        <>
            <CssBaseline />
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6">
                        ListOn - Lista de Tarefas
                    </Typography>
                </Toolbar>
            </AppBar>
            <Container maxWidth="md">
                <Box mt={4}>
                    <Typography variant="h4" gutterBottom>
                        Nova Tarefa
                    </Typography>
                    <TaskForm onAddTask={handleAddTask} />
                </Box>
                <Box mt={4}>
                    <Typography variant="h4" gutterBottom>
                        Tarefas
                    </Typography>
                    <TaskList
                        tasks={tasks}
                        onToggle={handleToggleTask}
                        onDelete={handleDeleteTask}
                    />
                </Box>
            </Container>
        </>
    );
}

export default App;