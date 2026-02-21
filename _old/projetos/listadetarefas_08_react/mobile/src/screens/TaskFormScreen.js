import React, { useState, useEffect } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, ScrollView } from 'react-native';
import TaskService from '../services/TaskService';

const TaskFormScreen = ({ route, navigation }) => {
    const { id } = route.params || {};
    const isEdit = !!id;

    const [titulo, setTitulo] = useState('');
    const [descricao, setDescricao] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (isEdit) {
            loadTask();
        }
    }, [id]);

    const loadTask = async () => {
        try {
            setLoading(true);
            const data = await TaskService.getById(id);
            setTitulo(data.titulo);
            setDescricao(data.descricao);
        } catch (error) {
            Alert.alert("Erro", "Falha ao carregar tarefa.");
            navigation.goBack();
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async () => {
        if (!titulo.trim()) {
            Alert.alert("Atenção", "O título é obrigatório.");
            return;
        }

        try {
            setLoading(true);
            const task = { titulo, descricao };

            if (isEdit) {
                await TaskService.update(id, task);
                Alert.alert("Sucesso", "Tarefa atualizada!");
            } else {
                await TaskService.create(task);
                Alert.alert("Sucesso", "Tarefa criada!");
            }
            navigation.goBack();
        } catch (error) {
            console.error(error);
            Alert.alert("Erro", "Falha ao salvar tarefa.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <ScrollView contentContainerStyle={styles.container}>
            <Text style={styles.label}>Título</Text>
            <TextInput
                style={styles.input}
                value={titulo}
                onChangeText={setTitulo}
                placeholder="Ex: Estudar React Native"
                placeholderTextColor="#94a3b8"
            />

            <Text style={styles.label}>Descrição</Text>
            <TextInput
                style={[styles.input, styles.textArea]}
                value={descricao}
                onChangeText={setDescricao}
                placeholder="Detalhes da tarefa..."
                placeholderTextColor="#94a3b8"
                multiline
                numberOfLines={4}
                textAlignVertical="top"
            />

            <TouchableOpacity
                style={[styles.button, loading && styles.buttonDisabled]}
                onPress={handleSubmit}
                disabled={loading}
            >
                <Text style={styles.buttonText}>
                    {loading ? "Salvando..." : (isEdit ? "Atualizar Tarefa" : "Criar Tarefa")}
                </Text>
            </TouchableOpacity>
        </ScrollView>
    );
};

const styles = StyleSheet.create({
    container: {
        flexGrow: 1,
        padding: 20,
        backgroundColor: '#f8fafc',
    },
    label: {
        fontSize: 16,
        fontWeight: '600',
        color: '#475569',
        marginBottom: 8,
        marginTop: 16,
    },
    input: {
        backgroundColor: 'white',
        borderWidth: 1,
        borderColor: '#cbd5e1',
        borderRadius: 8,
        padding: 12,
        fontSize: 16,
        color: '#1e293b',
    },
    textArea: {
        minHeight: 100,
    },
    button: {
        backgroundColor: '#6366f1',
        padding: 16,
        borderRadius: 12,
        alignItems: 'center',
        marginTop: 32,
        shadowColor: "#6366f1",
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 4,
        elevation: 4,
    },
    buttonDisabled: {
        backgroundColor: '#a5b4fc',
    },
    buttonText: {
        color: 'white',
        fontSize: 18,
        fontWeight: 'bold',
    },
});

export default TaskFormScreen;
