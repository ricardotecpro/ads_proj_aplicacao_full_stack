import { List, ListItem, ListItemText, IconButton, Checkbox } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

function TaskList({ tasks, onToggle, onDelete }) {
    return (
        <List>
            {tasks.length === 0 ? (
                <ListItem>
                    <ListItemText primary="Nenhuma tarefa encontrada." />
                </ListItem>
            ) : (
                tasks.map((task) => (
                    <ListItem
                        key={task.id}
                        secondaryAction={
                            <IconButton edge="end" aria-label="delete" onClick={() => onDelete(task.id)}>
                                <DeleteIcon />
                            </IconButton>
                        }
                        disablePadding
                    >
                        <Checkbox
                            edge="start"
                            checked={task.completed}
                            tabIndex={-1}
                            disableRipple
                            onChange={() => onToggle(task)}
                        />
                        <ListItemText
                            primary={task.title}
                            secondary={task.description}
                            style={{
                                textDecoration: task.completed ? 'line-through' : 'none',
                                color: task.completed ? 'grey' : 'inherit'
                            }}
                        />
                    </ListItem>
                ))
            )}
        </List>
    );
}

export default TaskList;