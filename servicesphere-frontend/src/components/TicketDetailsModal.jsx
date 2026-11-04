import React, { useState, useEffect } from 'react';
import { getTicketComments, addTicketComment, getTicketHistory, escalateTicket, archiveTicket } from '../services/api';

const TICKET_STATUSES = [
  'OPEN',
  'IN PROGRESS',
  'WAITING FOR REQUESTER',
  'ESCALATED',
  'RESOLVED',
  'CLOSED',
  'ARCHIVED'
];

const TicketDetailsModal = ({
  ticket,
  user,
  onClose,
  onStatusChange,
  onAgentAssign,
  onTicketUpdated
}) => {
  const [commentsList, setCommentsList] = useState([]);
  const [historyList, setHistoryList] = useState([]);
  const [commentInput, setCommentInput] = useState('');
  const [activeSubTab, setActiveSubTab] = useState('comments');
  const [status, setStatus] = useState(ticket?.status || 'OPEN');

  useEffect(() => {
    if (ticket?.id) {
      setStatus(ticket.status || 'OPEN');
      loadActivityData();
    }
  }, [ticket?.id]);

  const loadActivityData = async () => {
    try {
      const [comments, history] = await Promise.all([
        getTicketComments(ticket.id).catch(() => []),
        getTicketHistory(ticket.id).catch(() => [])
      ]);
      setCommentsList(Array.isArray(comments) ? comments : []);
      setHistoryList(Array.isArray(history) ? history : []);
    } catch (err) {
      console.error("Failed to load ticket activity feed", err);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!commentInput.trim()) return;
    try {
      const newComment = {
        message: commentInput,
        authorName: user?.fullName || user?.email || 'User',
        authorId: user?.id || 1,
        isInternal: false
      };
      await addTicketComment(ticket.id, newComment);
      setCommentInput('');
      await loadActivityData();
    } catch (err) {
      console.error("Failed to post comment:", err);
    }
  };

  const handleEscalateAction = async () => {
    try {
      if (escalateTicket) await escalateTicket(ticket.id);
      setStatus('ESCALATED');
      if (onStatusChange) onStatusChange(ticket.id, 'ESCALATED');
      if (onTicketUpdated) onTicketUpdated();
      loadActivityData();
    } catch (err) {
      console.error("Failed to escalate ticket:", err);
    }
  };

  const handleArchiveAction = async () => {
    try {
      if (archiveTicket) await archiveTicket(ticket.id);
      setStatus('ARCHIVED');
      if (onStatusChange) onStatusChange(ticket.id, 'ARCHIVED');
      if (onTicketUpdated) onTicketUpdated();
      loadActivityData();
    } catch (err) {
      console.error("Failed to archive ticket:", err);
    }
  };

  if (!ticket) return null;

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
      background: 'rgba(15, 23, 42, 0.65)', backdropFilter: 'blur(6px)',
      display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000, padding: '20px'
    }}>
      <div className="modal-content-wrapper" style={{
        background: 'white', borderRadius: '16px', width: '100%', maxWidth: '680px',
        maxHeight: '90vh', overflowY: 'auto', padding: '32px', border: '1px solid #e2e8f0', boxShadow: '0 25px 50px -12px rgba(15, 23, 42, 0.25)'
      }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px', borderBottom: '1px solid #f1f5f9', paddingBottom: '20px' }}>
          <div>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '6px' }}>
              <span style={{ fontSize: '12px', fontWeight: '800', color: '#2563eb', background: '#eff6ff', padding: '3px 10px', borderRadius: '6px', letterSpacing: '0.5px' }}>
                {ticket.ticketNumber || `#${ticket.id}`}
              </span>
              <span style={{ fontSize: '12px', fontWeight: '700', color: '#64748b', background: '#f1f5f9', padding: '3px 10px', borderRadius: '6px' }}>
                {ticket.category || 'General IT'}
              </span>
            </div>
            <h2 style={{ margin: 0, color: '#0f172a', fontSize: '22px', fontWeight: '800' }}>{ticket.title}</h2>
          </div>
          <button
            onClick={onClose}
            style={{ background: '#f1f5f9', border: 'none', borderRadius: '50%', width: '36px', height: '36px', cursor: 'pointer', color: '#64748b', fontSize: '18px', fontWeight: 'bold', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
          >
            &times;
          </button>
        </div>

        {/* Description Box */}
        <div style={{ marginBottom: '24px' }}>
          <h4 style={{ fontSize: '11px', color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '8px', fontWeight: '700' }}>Incident Description</h4>
          <p style={{ color: '#334155', fontSize: '14px', lineHeight: '1.6', background: '#f8fafc', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0', margin: 0 }}>
            {ticket.description}
          </p>
        </div>

        {/* Action Controls Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '28px', background: '#f8fafc', padding: '20px', borderRadius: '12px', border: '1px solid #e2e8f0' }}>
          <div>
            <label style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', display: 'block', marginBottom: '6px', letterSpacing: '0.5px' }}>LIFECYCLE STATUS</label>
            <select
              value={status}
              onChange={(e) => {
                const newStatus = e.target.value;
                setStatus(newStatus);
                if (onStatusChange) onStatusChange(ticket.id, newStatus);
              }}
              style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontWeight: '600', background: 'white', color: '#0f172a', fontSize: '14px' }}
            >
              {TICKET_STATUSES.map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>
          <div>
            <label style={{ fontSize: '11px', fontWeight: '700', color: '#64748b', display: 'block', marginBottom: '6px', letterSpacing: '0.5px' }}>ASSIGNED AGENT</label>
            <select
              value={ticket.assignedAgentName || 'Unassigned'}
              onChange={(e) => onAgentAssign && onAgentAssign(ticket.id, e.target.value)}
              style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontWeight: '600', background: 'white', color: '#0f172a', fontSize: '14px' }}
            >
              <option value="Unassigned">Unassigned</option>
              <option value="Sarah Support Agent">Sarah Support Agent</option>
              <option value="Mark Lead Manager">Mark Lead Manager</option>
            </select>
          </div>
        </div>

        {/* Quick Action Buttons */}
        <div style={{ display: 'flex', gap: '12px', marginBottom: '24px' }}>
          {status !== 'ESCALATED' && (
            <button
              type="button"
              onClick={handleEscalateAction}
              style={{ background: '#d97706', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
            >
              ⚡ Escalate Ticket
            </button>
          )}
          {status !== 'ARCHIVED' && (
            <button
              type="button"
              onClick={handleArchiveAction}
              style={{ background: '#475569', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
            >
              📁 Archive Ticket
            </button>
          )}
        </div>

        {/* Activity Feed Sub-Tabs */}
        <div>
          <div style={{ display: 'flex', gap: '16px', borderBottom: '1px solid #e2e8f0', marginBottom: '12px', paddingBottom: '8px' }}>
            <button
              onClick={() => setActiveSubTab('comments')}
              style={{ background: 'none', border: 'none', fontWeight: activeSubTab === 'comments' ? '700' : '500', color: activeSubTab === 'comments' ? '#2563eb' : '#64748b', cursor: 'pointer', fontSize: '14px', padding: 0 }}
            >
              Comments ({commentsList.length})
            </button>
            <button
              onClick={() => setActiveSubTab('history')}
              style={{ background: 'none', border: 'none', fontWeight: activeSubTab === 'history' ? '700' : '500', color: activeSubTab === 'history' ? '#2563eb' : '#64748b', cursor: 'pointer', fontSize: '14px', padding: 0 }}
            >
              Audit Trail / History ({historyList.length})
            </button>
          </div>

          {activeSubTab === 'comments' ? (
            <>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '16px', maxHeight: '180px', overflowY: 'auto', paddingRight: '4px' }}>
                {commentsList.length === 0 ? (
                  <div style={{ background: '#f8fafc', padding: '20px', borderRadius: '10px', textAlign: 'center', border: '1px dashed #cbd5e1' }}>
                    <p style={{ fontSize: '13px', color: '#64748b', fontStyle: 'italic', margin: 0 }}>No comments recorded yet.</p>
                  </div>
                ) : (
                  commentsList.map(c => (
                    <div key={c.id} style={{ background: '#f8fafc', padding: '12px 16px', borderRadius: '10px', border: '1px solid #e2e8f0', fontSize: '13px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px', fontWeight: '700', color: '#1e293b' }}>
                        <span>{c.authorName || 'User'}</span>
                        <span style={{ fontSize: '11px', color: '#94a3b8', fontWeight: '400' }}>{new Date(c.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                      </div>
                      <div style={{ color: '#475569', lineHeight: '1.4' }}>{c.message}</div>
                    </div>
                  ))
                )}
              </div>

              <form onSubmit={handleAddComment} style={{ display: 'flex', gap: '10px' }}>
                <input
                  type="text"
                  placeholder="Type an update or internal note..."
                  value={commentInput}
                  onChange={(e) => setCommentInput(e.target.value)}
                  style={{ flex: 1, padding: '12px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '13px', outline: 'none' }}
                />
                <button type="submit" style={{ background: '#2563eb', color: 'white', border: 'none', padding: '12px 20px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}>
                  Post Note
                </button>
              </form>
            </>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '220px', overflowY: 'auto', paddingRight: '4px' }}>
              {historyList.length === 0 ? (
                <div style={{ background: '#f8fafc', padding: '20px', borderRadius: '10px', textAlign: 'center', border: '1px dashed #cbd5e1' }}>
                  <p style={{ fontSize: '13px', color: '#64748b', fontStyle: 'italic', margin: 0 }}>No audit history recorded.</p>
                </div>
              ) : (
                historyList.map(h => (
                  <div key={h.id} style={{ background: '#f8fafc', padding: '10px 14px', borderRadius: '8px', border: '1px solid #e2e8f0', fontSize: '12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <strong style={{ color: '#1e293b' }}>{h.eventType}</strong> — Actor: <span style={{ color: '#2563eb' }}>{h.actorName}</span> (New: {h.newValue || 'N/A'})
                    </div>
                    <span style={{ color: '#94a3b8', fontSize: '11px' }}>{new Date(h.timestamp).toLocaleString()}</span>
                  </div>
                ))
              )}
            </div>
          )}
        </div>

        {/* Footer */}
        <div style={{ marginTop: '32px', textAlign: 'right', borderTop: '1px solid #f1f5f9', paddingTop: '20px' }}>
          <button onClick={onClose} style={{ background: '#e2e8f0', color: '#334155', border: 'none', padding: '10px 20px', borderRadius: '8px', fontWeight: '600', cursor: 'pointer' }}>
            Done
          </button>
        </div>
      </div>
    </div>
  );
};

export default TicketDetailsModal;