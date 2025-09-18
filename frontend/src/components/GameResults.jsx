import React, { useState, useRef, useEffect } from 'react';

const GameResults = ({ final, onRestart }) => {
  const [activeTab, setActiveTab] = useState('overview');
  const [isVisible, setIsVisible] = useState(false);
  const resultsRef = useRef(null);

  useEffect(() => {
    const timer = setTimeout(() => setIsVisible(true), 300);
    return () => clearTimeout(timer);
  }, []);

  const getScoreColor = (score) => {
    if (score >= 80) return '#4ade80'; // green
    if (score >= 60) return '#facc15'; // yellow
    if (score >= 40) return '#f97316'; // orange
    return '#ef4444'; // red
  };

  const calculateOverallScore = () => {
    // Use the backend-calculated performance score if available
    if (final.performanceScore && final.performanceScore > 0) {
      return final.performanceScore;
    }
    
    // Fallback to simple narrative analysis for older responses
    const narrative = final.narrative.toLowerCase();
    let score = 50; // base score
    
    if (narrative.includes('success') || narrative.includes('promoted')) score += 30;
    if (narrative.includes('excellent') || narrative.includes('outstanding')) score += 20;
    if (narrative.includes('failed') || narrative.includes('fired')) score -= 30;
    if (narrative.includes('mistake') || narrative.includes('error')) score -= 15;
    
    return Math.max(0, Math.min(100, score));
  };

  const overallScore = calculateOverallScore();

  const tabs = [
    { id: 'overview', label: 'Overview', icon: '📊' },
    { id: 'analysis', label: 'Analysis', icon: '🔍' },
    { id: 'skills', label: 'Skills', icon: '🎯' },
    { id: 'growth', label: 'Growth', icon: '📈' }
  ];

  return (
    <div className={`results-container ${isVisible ? 'visible' : ''}`}>
      <div className="results-background">
        <div className="results-particles">
          {[...Array(30)].map((_, i) => (
            <div
              key={i}
              className="result-particle"
              style={{
                left: `${Math.random() * 100}%`,
                animationDelay: `${Math.random() * 5}s`,
                animationDuration: `${5 + Math.random() * 10}s`
              }}
            />
          ))}
        </div>
      </div>

      <div className="results-content" ref={resultsRef}>
        {/* Results Header */}
        <div className="results-header">
          <div className="completion-badge">
            <div className="badge-icon">🏆</div>
            <span>Crisis Simulation Complete</span>
          </div>
          
          <h1 className="results-title">
            Crisis Management
            <span className="gradient-text"> Assessment Complete</span>
          </h1>
          

        {/* Results Navigation */}
        <div className="results-nav">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              className={`nav-tab ${activeTab === tab.id ? 'active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              <span className="tab-icon">{tab.icon}</span>
              <span className="tab-label">{tab.label}</span>
            </button>
          ))}
        </div>

        {/* Results Content */}
        <div className="results-tabs">
          {activeTab === 'overview' && (
            <div className="tab-content overview-tab">
              <div className="overview-grid">
                <div className="outcome-card">
                  <h3>Crisis Outcome</h3>
                  <div className="outcome-content">
                    <p>{final.outcome || 'Crisis resolved with mixed results'}</p>
                  </div>
                </div>
                
                <div className="career-card">
                  <h3>Career Impact</h3>
                  <div className="career-content">
                    <p>{final.careerResult || 'Professional standing maintained'}</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'analysis' && (
            <div className="tab-content analysis-tab">
              <div className="analysis-grid">
                <div className="strengths-card">
                  <div className="card-header">
                    <div className="card-icon positive">💪</div>
                    <h3>What You Did Well</h3>
                  </div>
                  <div className="card-content">
                    <p>{final.strengths || 'Strong decision-making under pressure'}</p>
                  </div>
                </div>
                
                <div className="improvements-card">
                  <div className="card-header">
                    <div className="card-icon neutral">🎯</div>
                    <h3>Areas for Improvement</h3>
                  </div>
                  <div className="card-content">
                    <p>{final.improvements || 'Consider stakeholder perspectives more carefully'}</p>
                  </div>
                </div>
                
                <div className="leadership-card">
                  <div className="card-header">
                    <div className="card-icon highlight">👑</div>
                    <h3>Leadership Style</h3>
                  </div>
                  <div className="card-content">
                    <p>{final.leadershipStyle || 'Decisive and action-oriented leadership approach'}</p>
                  </div>
                </div>
                
                <div className="theory-card">
                  <div className="card-header">
                    <div className="card-icon academic">📚</div>
                    <h3>Crisis Theory Application</h3>
                  </div>
                  <div className="card-content">
                    <p>{final.crisisTheory || 'Applied situational crisis communication theory effectively'}</p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'skills' && (
            <div className="tab-content skills-tab">
              <div className="skills-assessment">
                <h3>Communication Skills Assessment</h3>
                <div className="skills-grid">
                  {[
                    { skill: 'Crisis Response', score: Math.floor(Math.random() * 30) + 70 },
                    { skill: 'Stakeholder Management', score: Math.floor(Math.random() * 30) + 65 },
                    { skill: 'Decision Making', score: Math.floor(Math.random() * 30) + 75 },
                    { skill: 'Media Relations', score: Math.floor(Math.random() * 30) + 60 },
                    { skill: 'Team Leadership', score: Math.floor(Math.random() * 30) + 80 },
                    { skill: 'Strategic Thinking', score: Math.floor(Math.random() * 30) + 70 }
                  ].map((item, index) => (
                    <div key={index} className="skill-item">
                      <div className="skill-header">
                        <span className="skill-name">{item.skill}</span>
                        <span className="skill-score">{item.score}%</span>
                      </div>
                      <div className="skill-bar">
                        <div 
                          className="skill-fill"
                          style={{ 
                            width: `${item.score}%`,
                            backgroundColor: getScoreColor(item.score)
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'growth' && (
            <div className="tab-content growth-tab">
              <div className="growth-recommendations">
                <h3>Professional Development Recommendations</h3>
                <div className="recommendations-grid">
                  <div className="recommendation-card">
                    <div className="rec-icon">📖</div>
                    <h4>Recommended Reading</h4>
                    <ul>
                      <li>Crisis Communications: A Casebook Approach</li>
                      <li>Managing the Unexpected</li>
                      <li>The Crisis Manager: Facing Disasters</li>
                    </ul>
                  </div>
                  
                  <div className="recommendation-card">
                    <div className="rec-icon">🎓</div>
                    <h4>Skill Development</h4>
                    <ul>
                      <li>Advanced Crisis Communication Training</li>
                      <li>Media Interview Skills Workshop</li>
                      <li>Executive Leadership Program</li>
                    </ul>
                  </div>
                  
                  <div className="recommendation-card">
                    <div className="rec-icon">🔄</div>
                    <h4>Practice Areas</h4>
                    <ul>
                      <li>Scenario-based simulation exercises</li>
                      <li>Cross-functional team collaboration</li>
                      <li>Stakeholder mapping and analysis</li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* AI Generated Image */}
        {final.imageUrl && (
          <div className="results-image">
            <h3>Your Crisis Management Journey</h3>
            <div className="image-container">
              <img 
                src={final.imageUrl} 
                alt="Crisis management visualization"
                className="result-image"
              />
              <div className="image-overlay">
                <p>AI-generated visualization of your crisis management style</p>
              </div>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="results-actions">
          <button 
            onClick={onRestart}
            className="restart-button"
          >
            <span className="button-icon">🔄</span>
            <span>Start New Crisis Simulation</span>
          </button>
        </div>
      </div>
    </div>
    </div>
  );
};

export default GameResults;
