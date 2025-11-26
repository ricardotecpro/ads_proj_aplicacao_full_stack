import React, { useEffect, useState } from 'react';
import TaskService from '../services/TaskService';
import { useNavigate, useParams } from 'react-router-dom';

const TaskFormPage = () => {
    const [task, setTask] = useState({ titulo: '', descricao: '', concluida: false });
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    useEffect(() => {
        if (isEdit) {
            loadTask();
        }
    }, [id]);

    const loadTask = async () => {
        try {
            const data = await TaskService.getById(id);
            setTask(data);
        } catch (error) {
            console.error("Error loading task", error);
            alert("Failed to load task");
            navigate('/');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setTask(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEdit) {
                await TaskService.update(id, task);
            } else {
                await TaskService.create(task);
            }
            navigate('/');
        } catch (error) {
            console.error("Error saving task", error);
            alert("Failed to save task");
        }
    };

    return (
        <div className="container" style={{ maxWidth: '600px' }}>
            <h1 className="page-title">{isEdit ? 'Edit Task' : 'New Task'}</h1>
            <div className="card">
                <form onSubmit={handleSubmit}>
                    <div className="input-group">
                        <label htmlFor="titulo">Title</label>
                        <input
                            type="text"
                            id="titulo"
                            name="titulo"
                            value={task.titulo}
                            onChange={handleChange}
                            required
                            placeholder="e.g., Learn React"
                        />
                    </div>

                    <div className="input-group">
                        <label htmlFor="descricao">Description</label>
                        <textarea
                            id="descricao"
                            name="descricao"
                            value={task.descricao}
                            onChange={handleChange}
                            rows="4"
                            placeholder="Details about the task..."
                        />
                    </div>

                    <div style={{ display: 'flex', gap: '1rem', marginTop: '2rem' }}>
                        <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
                            {isEdit ? 'Update Task' : 'Create Task'}
                        </button>
                        <button type="button" onClick={() => navigate('/')} className="btn btn-secondary">
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default TaskFormPage;
