import React, { useEffect, useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert, RefreshControl } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import TaskService from '../services/TaskService';

const HomeScreen = ({ navigation }) => {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);

    const loadTasks = async () => {
        try {
            const data = await TaskService.getAll();
            setTasks(data);
        } catch (error) {
            console.error("Error loading tasks", error);
            Alert.alert("Erro", "Não foi possível carregar as tarefas.");
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadTasks();
        }, [])
    );

    const onRefresh = () => {
        setRefreshing(true);
        loadTasks();
    };

    const handleDelete = (id) => {
        Alert.alert(
            "Excluir Tarefa",
            "Tem certeza que deseja excluir esta tarefa?",
            [
                { text: "Cancelar", style: "cancel" },
                {
                    text: "Excluir",
                    style: "destructive",
                    onPress: async () => {
                        try {
                            await TaskService.delete(id);
                            loadTasks();
                        } catch (error) {
                            Alert.alert("Erro", "Falha ao excluir tarefa.");
                        }
                    }
                }
            ]
        );
    };

    const toggleComplete = async (task) => {
        try {
            await TaskService.update(task.id, { ...task, concluida: !task.concluida });
            loadTasks();
        } catch (error) {
            Alert.alert("Erro", "Falha ao atualizar tarefa.");
        }
    };

    const renderItem = ({ item }) => (
        <View style={[styles.card, item.concluida && styles.cardCompleted]}>
            <View style={styles.cardContent}>
                <Text style={[styles.title, item.concluida && styles.textCompleted]}>{item.titulo}</Text>
                <Text style={styles.description}>{item.descricao}</Text>
            </View>
            <View style={styles.actions}>
                <TouchableOpacity onPress={() => toggleComplete(item)} style={styles.actionButton}>
                    <Text style={styles.actionText}>{item.concluida ? "Desfazer" : "Concluir"}</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => navigation.navigate('TaskForm', { id: item.id })} style={styles.actionButton}>
                    <Text style={styles.actionText}>Editar</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => handleDelete(item.id)} style={[styles.actionButton, styles.deleteButton]}>
                    <Text style={[styles.actionText, styles.deleteText]}>Excluir</Text>
                </TouchableOpacity>
            </View>
        </View>
    );

    return (
        <View style={styles.container}>
            <FlatList
                data={tasks}
                keyExtractor={item => item.id.toString()}
                renderItem={renderItem}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                ListEmptyComponent={<Text style={styles.emptyText}>Nenhuma tarefa encontrada.</Text>}
            />
            <TouchableOpacity
                style={styles.fab}
                onPress={() => navigation.navigate('TaskForm')}
            >
                <Text style={styles.fabText}>+</Text>
            </TouchableOpacity>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f8fafc',
        padding: 16,
    },
    card: {
        backgroundColor: 'white',
        borderRadius: 12,
        padding: 16,
        marginBottom: 12,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
        borderLeftWidth: 5,
        borderLeftColor: '#6366f1',
    },
    cardCompleted: {
        borderLeftColor: '#10b981',
        opacity: 0.8,
    },
    cardContent: {
        marginBottom: 12,
    },
    title: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#1e293b',
        marginBottom: 4,
    },
    textCompleted: {
        textDecorationLine: 'line-through',
        color: '#94a3b8',
    },
    description: {
        color: '#64748b',
    },
    actions: {
        flexDirection: 'row',
        justifyContent: 'flex-end',
        gap: 12,
    },
    actionButton: {
        padding: 8,
    },
    actionText: {
        color: '#6366f1',
        fontWeight: '600',
    },
    deleteButton: {

    },
    deleteText: {
        color: '#ef4444',
    },
    fab: {
        position: 'absolute',
        bottom: 24,
        right: 24,
        backgroundColor: '#6366f1',
        width: 56,
        height: 56,
        borderRadius: 28,
        justifyContent: 'center',
        alignItems: 'center',
        elevation: 8,
        shadowColor: "#6366f1",
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 4,
    },
    fabText: {
        color: 'white',
        fontSize: 32,
        fontWeight: 'bold',
        marginTop: -4,
    },
    emptyText: {
        textAlign: 'center',
        marginTop: 50,
        color: '#94a3b8',
        fontSize: 16,
    }
});

export default HomeScreen;
