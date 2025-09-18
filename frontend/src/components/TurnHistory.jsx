import React, { useState, useEffect } from 'react';

const TurnHistory = ({ sessionId, isVisible, onClose }) => {
  const [turnHistory, setTurnHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    console.log('TurnHistory useEffect triggered:', { isVisible, sessionId });
    if (isVisible && sessionId) {
      fetchTurnHistory();
    }
  }, [isVisible, sessionId]);

  const fetchTurnHistory = async () => {
    console.log('Fetching turn history for sessionId:', sessionId);
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`http://localhost:8080/api/game/history?sessionId=${sessionId}`);
      console.log('Turn history response:', response.status);
      if (response.ok) {
        const history = await response.json();
        console.log('Turn history data:', history);
        setTurnHistory(history);
      } else {
        setError(`Failed to fetch turn history: ${response.status}`);
        console.error('Failed to fetch turn history');
      }
    } catch (error) {
      setError(`Network error: ${error.message}`);
      console.error('Error fetching turn history:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!isVisible) {
    console.log('TurnHistory not visible, returning null');
    return null;
  }

  console.log('TurnHistory rendering with:', { turnHistory, loading, error });

  return (
    <div className="turn-history" style={{ padding: '2rem', backgroundColor: '#1a1a2e', color: 'white', minHeight: '100vh' }}>
      <div className="history-header" style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <h2 style={{ color: '#ff6b35', marginBottom: '0.5rem' }}>Turn History</h2>
        <p style={{ color: '#cccccc' }}>Review your previous crisis management decisions</p>
      </div>

      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <div style={{ marginBottom: '1rem', padding: '1rem', backgroundColor: 'rgba(255,255,255,0.1)', borderRadius: '8px' }}>
          <strong>Debug Info:</strong>
          <br />
          Session ID: {sessionId || 'Not provided'}
          <br />
          Loading: {loading ? 'Yes' : 'No'}
          <br />
          Error: {error || 'None'}
          <br />
          Turn History Length: {turnHistory.length}
        </div>
        
        {loading && (
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>🔄</div>
            <p>Loading turn history...</p>
          </div>
        )}
        
        {error && (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#ff6b6b' }}>
            <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>❌</div>
            <p>Error: {error}</p>
          </div>
        )}
        
        {!loading && !error && turnHistory.length === 0 && (
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📋</div>
            <h3>No Turn History Available</h3>
            <p style={{ color: '#cccccc', marginTop: '1rem' }}>
              Make some choices in the game first, then come back to see your decisions!
            </p>
          </div>
        )}
        
        {turnHistory.length > 0 && (
          <div>
            <h3 style={{ marginBottom: '1rem', color: '#ff6b35' }}>Found {turnHistory.length} turns in history!</h3>
            {turnHistory.map((turn, index) => (
              <div 
                key={index} 
                style={{ 
                  margin: '1rem 0', 
                  padding: '1.5rem', 
                  backgroundColor: 'rgba(255,255,255,0.05)',
                  border: '1px solid rgba(255,107,53,0.3)',
                  borderRadius: '8px'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                  <strong style={{ color: '#ff6b35' }}>Turn {turn.turnNumber}</strong>
                  <span style={{ 
                    backgroundColor: '#ff6b35', 
                    color: 'white', 
                    padding: '0.2rem 0.5rem', 
                    borderRadius: '4px',
                    fontSize: '0.8rem'
                  }}>
                    Choice: {turn.choiceLabel}
                  </span>
                </div>
                
                {turn.narrative && (
                  <div style={{ marginBottom: '1rem' }}>
                    <strong>Scenario:</strong>
                    <p style={{ marginTop: '0.5rem', color: '#cccccc', lineHeight: '1.6' }}>
                      {turn.narrative.substring(0, 200)}...
                    </p>
                  </div>
                )}
                
                {turn.options && (
                  <div>
                    <strong>Options Available:</strong>
                    <ul style={{ marginTop: '0.5rem', paddingLeft: '1rem' }}>
                      {turn.options.slice(0, 3).map((option, optIndex) => (
                        <li key={optIndex} style={{ color: '#cccccc', marginBottom: '0.3rem' }}>
                          {option.substring(0, 50)}...
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default TurnHistory;
