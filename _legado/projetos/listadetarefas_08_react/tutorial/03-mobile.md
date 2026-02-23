# 🎓 Módulo 3: Mobile com React Native

**Objetivo**: Levar nossa aplicação para o celular usando o mesmo conhecimento de React.

---

## 1. O que é React Native?
É uma forma de criar apps nativos (Android e iOS) usando JavaScript e React. Em vez de `<div>` e `<h1>`, usamos `<View>` e `<Text>`.

---

## 2. Criando o Projeto com Expo

O **Expo** facilita muito a configuração.

1. Na raiz do projeto:
   ```bash
   npx create-expo-app mobile --template blank
   cd mobile
   npm install axios
   ```

---

## 3. Adaptando a API

Crie `src/services/api.js`.
⚠️ **Atenção**: O emulador Android não entende `localhost`. Use `10.0.2.2`.

```javascript
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://10.0.2.2:8080/api'
});

export default api;
```

---

## 4. Criando a Tela

Edite o `App.js`:

```javascript
import { useEffect, useState } from 'react';
import { StyleSheet, Text, View, FlatList } from 'react-native';
import api from './src/services/api';

export default function App() {
  const [tasks, setTasks] = useState([]);

  useEffect(() => {
    api.get('/tarefas')
       .then(response => setTasks(response.data))
       .catch(error => console.error(error));
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Minhas Tarefas Mobile</Text>
      <FlatList
        data={tasks}
        keyExtractor={item => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text>{item.titulo}</Text>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, marginTop: 30 },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 },
  card: { padding: 15, backgroundColor: '#ddd', marginBottom: 10, borderRadius: 8 }
});
```

---

## 🛑 Pare e Teste

1. Rode o backend.
2. Rode o mobile: `npx expo start`.
3. Pressione `a` para abrir no Emulador Android.

---

## 🏆 Desafio
Adicione um botão flutuante (+) para criar novas tarefas.
