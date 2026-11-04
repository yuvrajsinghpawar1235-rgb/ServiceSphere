import React from 'react';

const FilterPills = ({ activeFilter, onSelect }) => {
  const options = ['ALL', 'OPEN', 'HIGH', 'ASSIGNED'];
  return (
    <div style={{ display: 'flex', gap: '6px' }}>
      {options.map(pill => (
        <button
          key={pill}
          onClick={() => onSelect(pill)}
          style={{
            padding: '4px 12px',
            fontSize: '11px',
            fontWeight: '700',
            borderRadius: '20px',
            border: '1px solid',
            borderColor: activeFilter === pill ? '#2563eb' : '#cbd5e1',
            background: activeFilter === pill ? '#eff6ff' : 'white',
            color: activeFilter === pill ? '#2563eb' : '#64748b',
            cursor: 'pointer'
          }}
        >
          {pill}
        </button>
      ))}
    </div>
  );
};

export default FilterPills;