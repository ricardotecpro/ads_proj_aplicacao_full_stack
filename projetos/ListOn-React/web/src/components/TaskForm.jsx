import { useState } from 'react';
import { TextField, Button, Box } from '@mui/material';

function TaskForm({ onAddTask }) {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!title.trim()) return; // Não adiciona se o título estiver vazio

        onAddTask(title, description);
        setTitle('');
        setDescription('');
    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
            <TextField
                margin="normal"
                required
                fullWidth
                id="title"
                label="Título da Tarefa"
                name="title"
                autoFocus
                value={title}
                onChange={(e) => setTitle(e.target.value)}
            />
            <TextField
                margin="normal"
                fullWidth
                id="description"
                label="Descrição (Opcional)"
                name="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
            />
            <Button
                type="submit"
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
            >
                Adicionar Tarefa
            </Button>
        </Box>
    );
}

export default TaskForm;