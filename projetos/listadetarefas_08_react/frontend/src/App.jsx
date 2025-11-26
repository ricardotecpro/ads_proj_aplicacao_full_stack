import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import HomePage from './pages/HomePage';
import TaskFormPage from './pages/TaskFormPage';

function App() {
  return (
    <Router>
      <div className="app">
        <Header />
        <main>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/new" element={<TaskFormPage />} />
            <Route path="/edit/:id" element={<TaskFormPage />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
