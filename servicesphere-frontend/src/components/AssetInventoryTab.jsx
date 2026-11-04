import React, { useState } from 'react';

const ALLOWED_ASSET_STATUSES = [
  'In Stock',
  'Assigned',
  'In Repair',
  'Retired'
];

export default function AssetInventoryTab({ assets = [], onStatusChange, onAddAsset }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');

  const filteredAssets = assets.filter(asset => {
    const matchesSearch = asset.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          asset.serialNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          asset.category?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'ALL' || asset.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  // Calculate quick metrics
  const totalAssets = assets.length;
  const inStockCount = assets.filter(a => a.status === 'In Stock' || !a.status).length;
  const assignedCount = assets.filter(a => a.status === 'Assigned').length;
  const repairCount = assets.filter(a => a.status === 'In Repair').length;

  return (
    <div style={{ width: '100%', display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* Top Metric Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: '16px', width: '100%' }}>
        <div style={{ background: 'white', padding: '20px', borderRadius: '12px', border: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
          <div>
            <p style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase', margin: 0, letterSpacing: '0.5px' }}>Total Assets</p>
            <h3 style={{ fontSize: '24px', fontWeight: '800', color: '#1e293b', margin: '4px 0 0 0' }}>{totalAssets}</h3>
          </div>
          <div style={{ padding: '10px 12px', background: '#eff6ff', color: '#2563eb', borderRadius: '8px', fontSize: '18px' }}>📦</div>
        </div>
        <div style={{ background: 'white', padding: '20px', borderRadius: '12px', border: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
          <div>
            <p style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase', margin: 0, letterSpacing: '0.5px' }}>In Stock</p>
            <h3 style={{ fontSize: '24px', fontWeight: '800', color: '#059669', margin: '4px 0 0 0' }}>{inStockCount}</h3>
          </div>
          <div style={{ padding: '10px 12px', background: '#ecfdf5', color: '#059669', borderRadius: '8px', fontSize: '18px' }}>✅</div>
        </div>
        <div style={{ background: 'white', padding: '20px', borderRadius: '12px', border: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
          <div>
            <p style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase', margin: 0, letterSpacing: '0.5px' }}>Assigned</p>
            <h3 style={{ fontSize: '24px', fontWeight: '800', color: '#2563eb', margin: '4px 0 0 0' }}>{assignedCount}</h3>
          </div>
          <div style={{ padding: '10px 12px', background: '#eff6ff', color: '#2563eb', borderRadius: '8px', fontSize: '18px' }}>💻</div>
        </div>
        <div style={{ background: 'white', padding: '20px', borderRadius: '12px', border: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', boxShadow: '0 1px 3px rgba(0,0,0,0.05)' }}>
          <div>
            <p style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase', margin: 0, letterSpacing: '0.5px' }}>In Repair</p>
            <h3 style={{ fontSize: '24px', fontWeight: '800', color: '#d97706', margin: '4px 0 0 0' }}>{repairCount}</h3>
          </div>
          <div style={{ padding: '10px 12px', background: '#fffbeb', color: '#d97706', borderRadius: '8px', fontSize: '18px' }}>🛠️</div>
        </div>
      </div>

      {/* Main Content Box */}
      <div style={{ width: '100%', background: 'white', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 1px 3px rgba(0,0,0,0.05)', overflow: 'hidden' }}>
        <div style={{ padding: '24px', borderBottom: '1px solid #f1f5f9', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h2 style={{ fontSize: '18px', fontWeight: '800', color: '#0f172a', margin: 0 }}>Hardware & Asset Directory</h2>
            <p style={{ fontSize: '13px', color: '#64748b', margin: '4px 0 0 0' }}>Manage and track organization inventory status and assignments.</p>
          </div>
          <button
            onClick={onAddAsset}
            style={{ background: '#2563eb', color: 'white', border: 'none', padding: '10px 18px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
          >
            + Add New Asset
          </button>
        </div>

        {/* Filters Toolbar */}
        <div style={{ padding: '20px 24px', background: '#f8fafc', borderBottom: '1px solid #e2e8f0', display: 'flex', gap: '16px', alignItems: 'center', width: '100%', boxSizing: 'border-box' }}>
          <input
            type="text"
            placeholder="Search by asset name, serial number, or category..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ flex: 1, padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '13px', outline: 'none', background: 'white' }}
          />
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            style={{ width: '200px', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '13px', fontWeight: '600', background: 'white', color: '#0f172a' }}
          >
            <option value="ALL">All Statuses ({assets.length})</option>
            {ALLOWED_ASSET_STATUSES.map(status => (
              <option key={status} value={status}>{status}</option>
            ))}
          </select>
        </div>

        {/* Data Table */}
        <div style={{ width: '100%', overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ background: '#f8fafc', borderBottom: '1px solid #e2e8f0', color: '#475569', fontSize: '11px', textTransform: 'uppercase', letterSpacing: '0.6px', fontWeight: '700' }}>
                <th style={{ padding: '14px 24px' }}>Asset Name</th>
                <th style={{ padding: '14px 24px' }}>Serial Number</th>
                <th style={{ padding: '14px 24px' }}>Category</th>
                <th style={{ padding: '14px 24px' }}>Current Status</th>
                <th style={{ padding: '14px 24px' }}>Assigned User</th>
                <th style={{ padding: '14px 24px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredAssets.length > 0 ? (
                filteredAssets.map(asset => (
                  <tr key={asset.id} style={{ borderBottom: '1px solid #f1f5f9', fontSize: '13px' }}>
                    <td style={{ padding: '16px 24px', fontWeight: '700', color: '#1e293b' }}>{asset.name}</td>
                    <td style={{ padding: '16px 24px', color: '#64748b', fontFamily: 'monospace' }}>{asset.serialNumber || 'N/A'}</td>
                    <td style={{ padding: '16px 24px', color: '#475569' }}>
                      <span style={{ background: '#f1f5f9', padding: '4px 10px', borderRadius: '6px', fontSize: '12px', fontWeight: '600' }}>
                        {asset.category || 'General'}
                      </span>
                    </td>
                    <td style={{ padding: '16px 24px' }}>
                      <select
                        value={asset.status || 'In Stock'}
                        onChange={(e) => onStatusChange && onStatusChange(asset.id, e.target.value)}
                        style={{ padding: '6px 10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '12px', fontWeight: '600', background: 'white', color: '#0f172a' }}
                      >
                        {ALLOWED_ASSET_STATUSES.map(status => (
                          <option key={status} value={status}>{status}</option>
                        ))}
                      </select>
                    </td>
                    <td style={{ padding: '16px 24px', color: '#475569', fontWeight: '500' }}>
                      {asset.assignedUser || <span style={{ color: '#94a3b8', fontStyle: 'italic' }}>Unassigned</span>}
                    </td>
                    <td style={{ padding: '16px 24px', textAlign: 'right' }}>
                      <button
                        onClick={() => alert(`View details for asset ID: ${asset.id}`)}
                        style={{ background: '#eff6ff', color: '#2563eb', border: 'none', padding: '6px 12px', borderRadius: '6px', fontWeight: '600', cursor: 'pointer', fontSize: '12px' }}
                      >
                        Inspect
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ padding: '40px', textAlign: 'center', color: '#94a3b8', fontStyle: 'italic' }}>
                    No assets found matching your criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}