import React from 'react';

const Navbar = ({ user, onLogout }) => {
  return (
    <header style={{
      background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
      padding: '20px 28px',
      borderRadius: '16px',
      border: '1px solid #e2e8f0',
      marginBottom: '28px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.02)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <div style={{
          width: '40px', height: '40px', borderRadius: '10px', background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontWeight: '800', fontSize: '18px',
          boxShadow: '0 4px 10px rgba(37, 99, 235, 0.3)'
        }}>
          S
        </div>
        <div>
          <h1 style={{ color: '#0f172a', margin: 0, fontSize: '20px', fontWeight: '800', letterSpacing: '-0.5px' }}>
            ServiceSphere <span style={{ color: '#2563eb', fontSize: '13px', fontWeight: '600', marginLeft: '6px' }}>ITSM Enterprise</span>
          </h1>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        <div style={{ textAlign: 'right' }}>
          <span style={{ fontSize: '13px', color: '#0f172a', display: 'block', fontWeight: '700' }}>{user.fullName || user.email}</span>
          <span style={{ fontSize: '10px', color: '#2563eb', fontWeight: '800', textTransform: 'uppercase', background: '#eff6ff', padding: '2px 8px', borderRadius: '6px', letterSpacing: '0.5px' }}>
            {user.role || 'USER'}
          </span>
        </div>
        <button
          onClick={onLogout}
          style={{ padding: '8px 16px', background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca', borderRadius: '8px', cursor: 'pointer', fontSize: '12px', fontWeight: '700' }}
        >
          Logout
        </button>
      </div>
    </header>
  );
};

export default Navbar;