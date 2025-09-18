import React, { useState, useEffect, useRef } from 'react';

const GameInterface = ({ sessionId, turn, narrative, options, onChoice, loading }) => {
  const [selectedOption, setSelectedOption] = useState(null);
  const [isTyping, setIsTyping] = useState(true);
  const narrativeRef = useRef(null);

  useEffect(() => {
    setIsTyping(true);
    const timer = setTimeout(() => setIsTyping(false), 2000);
    return () => clearTimeout(timer);
  }, [narrative]);

  useEffect(() => {
    if (narrativeRef.current) {
      narrativeRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, [narrative]);

  const handleChoice = (label) => {
    if (loading) return;
    setSelectedOption(label);
    setTimeout(() => {
      onChoice(label);
      setSelectedOption(null);
    }, 500);
  };

  return (
    <div className="game-interface">
      <div className="interface-container">
        {/* Crisis Status Dashboard */}
        <div className="crisis-dashboard">
          <div className="dashboard-header">
            <div className="crisis-indicator">
              <div className="pulse-ring" />
              <div className="crisis-icon">🚨</div>
            </div>
            <div className="crisis-info">
              <h2>Crisis in Progress</h2>
              <p>Decision Turn {turn} • Executive Action Required</p>
            </div>
          </div>
          
          <div className="crisis-metrics">
            <div className="metric-card">
              <div className="metric-icon">📊</div>
              <div className="metric-value">{Math.max(0, 100 - (turn * 8))}%</div>
              <div className="metric-label">Company Reputation</div>
            </div>
            <div className="metric-card">
              <div className="metric-icon">⏱️</div>
              <div className="metric-value">{11 - turn}</div>
              <div className="metric-label">Decisions Left</div>
            </div>
            <div className="metric-card">
              <div className="metric-icon">🎯</div>
              <div className="metric-value">{Math.floor(Math.random() * 30) + 70}%</div>
              <div className="metric-label">Stakeholder Trust</div>
            </div>
          </div>
        </div>

        {/* Narrative Section */}
        <div className="narrative-section" ref={narrativeRef}>
          <div className="narrative-container">
            <div className="narrative-header">
              <div className="scenario-badge">
                <span className="badge-pulse" />
                <span>Live Scenario</span>
              </div>
              <h3>Crisis Development - Turn {turn}</h3>
            </div>
            
            <div className={`narrative-content ${isTyping ? 'typing' : ''}`}>
              <div className="narrative-text">
                {narrative.split('\n').map((paragraph, index) => (
                  <p key={index} className="narrative-paragraph">
                    {paragraph}
                  </p>
                ))}
              </div>
              
              {isTyping && (
                <div className="typing-indicator">
                  <div className="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Decision Options */}
        <div className="options-section">
          <div className="options-header">
            <h3>Executive Decision Required</h3>
            <p>Choose your response to this crisis situation</p>
          </div>
          
          <div className="options-grid">
            {options.map((option, index) => {
              const label = ["A", "B", "C", "D", "E"][index];
              const isSelected = selectedOption === label;
              
              return (
                <div
                  key={index}
                  className={`option-card ${isSelected ? 'selected' : ''} ${loading ? 'disabled' : ''}`}
                  onClick={() => handleChoice(label)}
                >
                  <div className="option-header">
                    <div className="option-label">{label}</div>
                    <div className="option-indicator">
                      {isSelected && <div className="selection-check">✓</div>}
                    </div>
                  </div>
                  
                  <div className="option-content">
                    <p>{option}</p>
                  </div>
                  
                  <div className="option-hover-effect" />
                </div>
              );
            })}
          </div>
          
          {loading && (
            <div className="processing-overlay">
              <div className="processing-content">
                <div className="loading-spinner-large" />
                <h4>Processing Your Decision...</h4>
                <p>Analyzing impact and generating consequences</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default GameInterface;
