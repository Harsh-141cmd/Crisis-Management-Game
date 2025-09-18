import React from 'react';

const GameHeader = ({ sessionId, turn, maxTurns = 10, gameState }) => {
  // Calculate progress based on turn, but cap at 100% for turn 10 and above
  const progress = sessionId ? Math.min((turn / maxTurns) * 100, 100) : 0;
  
  // Round progress to avoid decimal updates
  const roundedProgress = Math.round(progress);
  
  // Determine what to show in the turn counter
  const getTurnDisplay = () => {
    if (!sessionId) return null;
    
    if (gameState === 'results' || turn > maxTurns) {
      return 'Results';
    }
    
    return `Turn ${turn} of ${maxTurns}`;
  };

  return (
    <header className="game-header static-header">
      <div className="header-container">
        <div className="brand">
          <img src="/logo.jpg" alt="Crisis Comm Logo" className="brand-logo" />
          <span className="brand-text">Crisis Management</span>
        </div>
        
        {sessionId && (
          <div className="game-progress">
            <div className="progress-info">
              <span className="turn-counter">{getTurnDisplay()}</span>
              <span className="progress-percentage">{roundedProgress}%</span>
            </div>
            <div className="progress-bar static-progress">
              <div 
                className="progress-fill static-fill" 
                style={{ 
                  width: `${roundedProgress}%`
                }}
              />
            </div>
          </div>
        )}

        <div className="header-actions">
          {sessionId && (
            <div className="status-indicator">
              <div className="pulse-dot" />
              Session Active
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default GameHeader;
