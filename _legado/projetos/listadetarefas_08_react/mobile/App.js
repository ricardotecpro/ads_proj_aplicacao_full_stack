import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { StatusBar } from 'expo-status-bar';
import HomeScreen from './src/screens/HomeScreen';
import TaskFormScreen from './src/screens/TaskFormScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <StatusBar style="auto" />
      <Stack.Navigator
        initialRouteName="Home"
        screenOptions={{
          headerStyle: {
            backgroundColor: '#6366f1',
          },
          headerTintColor: '#fff',
          headerTitleStyle: {
            fontWeight: 'bold',
          },
        }}
      >
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{ title: 'Minhas Tarefas' }}
        />
        <Stack.Screen
          name="TaskForm"
          component={TaskFormScreen}
          options={({ route }) => ({
            title: route.params?.id ? 'Editar Tarefa' : 'Nova Tarefa'
          })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
