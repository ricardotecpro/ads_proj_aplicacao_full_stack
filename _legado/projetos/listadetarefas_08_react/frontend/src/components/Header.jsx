import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
    return (
        <header style={{
            backgroundColor: 'var(--surface-color)',
            borderBottom: '1px solid var(--border-color)',
            padding: '1rem 0',
            marginBottom: '2rem'
        }}>
            <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Link to="/" style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{ fontSize: '2rem' }}>🚀</span> TaskMaster
                </Link>
                <nav>
                    <Link to="/" className="btn btn-secondary" style={{ marginRight: '1rem' }}>List</Link>
                    <Link to="/new" className="btn btn-primary">New Task</Link>
                </nav>
            </div>
        </header>
    );
};

export default Header;
