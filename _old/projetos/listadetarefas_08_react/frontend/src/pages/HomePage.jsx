import React, { useEffect, useState } from 'react';
import TaskService from '../services/TaskService';
import { Link } from 'react-router-dom';

const HomePage = () => {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadTasks();
    }, []);

    const loadTasks = async () => {
        try {
            const data = await TaskService.getAll();
            setTasks(data);
        } catch (error) {
            console.error("Error loading tasks", error);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Are you sure you want to delete this task?")) {
            try {
                await TaskService.delete(id);
                loadTasks();
            } catch (error) {
                console.error("Error deleting task", error);
            }
        }
    };

    const toggleComplete = async (task) => {
        try {
            await TaskService.update(task.id, { ...task, concluida: !task.concluida });
            loadTasks();
        } catch (error) {
            console.error("Error updating task", error);
        }
    };

    if (loading) return <div className="container">Loading...</div>;

    return (
        <div className="container">
            <h1 className="page-title">My Tasks</h1>

            <div style={{ display: 'grid', gap: '1rem' }}>
                {tasks.map(task => (
                    <div key={task.id} className="card" style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        borderLeft: task.concluida ? '4px solid var(--success-color)' : '4px solid var(--primary-color)',
                        opacity: task.concluida ? 0.7 : 1
                    }}>
                        <div>
                            <h3 style={{
                                textDecoration: task.concluida ? 'line-through' : 'none',
                                marginBottom: '0.5rem'
                            }}>
                                {task.titulo}
                            </h3>
                            <p style={{ color: 'var(--text-muted)' }}>{task.descricao}</p>
                        </div>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <button
                                onClick={() => toggleComplete(task)}
                                className="btn"
                                style={{
                                    backgroundColor: task.concluida ? 'var(--surface-color)' : 'var(--success-color)',
                                    color: task.concluida ? 'var(--text-muted)' : 'white',
                                    border: task.concluida ? '1px solid var(--border-color)' : 'none'
                                }}
                            >
                                {task.concluida ? 'Undo' : 'Complete'}
                            </button>
                            <Link to={`/edit/${task.id}`} className="btn btn-secondary">Edit</Link>
                            <button onClick={() => handleDelete(task.id)} className="btn btn-danger">Delete</button>
                        </div>
                    </div>
                ))}

                {tasks.length === 0 && (
                    <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
                        <p style={{ fontSize: '1.2rem', color: 'var(--text-muted)' }}>No tasks found. Create one!</p>
                        <br />
                        <Link to="/new" className="btn btn-primary">Create Task</Link>
                    </div>
                )}
            </div>
        </div>
    );
};

export default HomePage;
