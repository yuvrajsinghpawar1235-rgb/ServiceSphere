import React, { useState } from 'react';

const KnowledgeBaseTab = ({ articles }) => {
  const [query, setQuery] = useState('');

  const filtered = articles.filter(a =>
    a.title.toLowerCase().includes(query.toLowerCase()) ||
    (a.body || a.content || '').toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.6fr', gap: '24px' }}>

      {/* Left Column: Search & Quick Guidelines */}
      <div style={{ background: 'white', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', height: 'fit-content' }}>
        <h3 style={{ margin: '0 0 16px 0', color: '#1e293b', fontSize: '18px' }}>Knowledge Search</h3>
        <input
          type="text"
          placeholder="Search articles (e.g., VPN, Password)..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box' }}
        />
        <div style={{ marginTop: '16px', padding: '14px', background: '#f8fafc', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ margin: '0 0 8px 0', fontSize: '13px', color: '#2563eb' }}>💡 Quick Tip</h4>
          <p style={{ margin: 0, fontSize: '12px', color: '#64748b', lineHeight: '1.5' }}>
            Search standard operating procedures and troubleshooting guides before escalating to tier-2 support agents.
          </p>
        </div>
      </div>

      {/* Right Column: Article Feed */}
      <div>
        <h3 style={{ margin: '0 0 16px 0', color: '#1e293b', fontSize: '18px' }}>
          Articles Found ({filtered.length})
        </h3>
        {filtered.length === 0 ? (
          <div style={{ background: 'white', padding: '40px', borderRadius: '12px', border: '1px solid #e2e8f0', textAlign: 'center', color: '#64748b' }}>
            No knowledge base articles match your query.
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', maxHeight: '550px', overflowY: 'auto', paddingRight: '4px' }}>
            {filtered.map((art) => (
              <div
                key={art.id}
                style={{ background: 'white', padding: '18px', borderRadius: '10px', border: '1px solid #e2e8f0', transition: 'border 0.2s' }}
                onMouseEnter={(e) => e.currentTarget.style.borderColor = '#2563eb'}
                onMouseLeave={(e) => e.currentTarget.style.borderColor = '#e2e8f0'}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <h4 style={{ margin: 0, color: '#2563eb', fontSize: '15px', fontWeight: '700' }}>{art.title}</h4>
                  <span style={{ fontSize: '11px', color: '#64748b', background: '#f1f5f9', padding: '3px 8px', borderRadius: '6px', fontWeight: '600' }}>
                    {art.category || 'General'}
                  </span>
                </div>
                <p style={{ margin: '0 0 12px 0', color: '#64748b', fontSize: '13px', lineHeight: '1.5' }}>
                  {art.body || art.content}
                </p>
                <div style={{ display: 'flex', justifyContent: 'flex-end', paddingTop: '10px', borderTop: '1px solid #f1f5f9', fontSize: '12px' }}>
                  <span style={{ color: '#2563eb', fontWeight: '600', cursor: 'pointer' }}>Read Full Guide &rarr;</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

    </div>
  );
};

export default KnowledgeBaseTab;