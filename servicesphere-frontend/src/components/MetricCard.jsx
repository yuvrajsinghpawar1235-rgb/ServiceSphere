import React from 'react';

const MetricCard = ({ label, value, color, percent }) => {
  return (
    <div style={{
      background: 'white',
      padding: '20px',
      borderRadius: '12px',
      border: '1px solid #e2e8f0',
      borderLeft: `4px solid ${color}`,
      boxShadow: '0 1px 3px rgba(0,0,0,0.02)'
    }}>
      <span style={{ fontSize: '11px', color: '#64748b', fontWeight: '700', letterSpacing: '0.5px' }}>{label}</span>
      <h2 style={{ margin: '8px 0 0', color: '#1e293b', fontSize: '28px' }}>{value}</h2>
      <div style={{ width: '100%', background: '#f1f5f9', height: '6px', borderRadius: '3px', marginTop: '12px', overflow: 'hidden' }}>
        <div style={{ width: `${percent}%`, background: color, height: '100%', transition: 'width 0.4s ease' }}></div>
      </div>
    </div>
  );
};

export default MetricCard;