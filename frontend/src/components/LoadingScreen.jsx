import React from 'react';

const LoadingScreen = ({ message = "Loading..." }) => {
  return (
    <div className="loading-screen">
      <div className="loading-container">
        <div className="loading-animation">
          <div className="loading-orbit">
            <div className="loading-planet"></div>
            <div className="loading-moon"></div>
          </div>
        </div>
        
        <div className="loading-content">
          <h2 className="loading-title">Crisis Management</h2>
          <p className="loading-message">{message}</p>
          
          <div className="loading-progress">
            <div className="progress-bar">
              <div className="progress-fill"></div>
            </div>
            <div className="progress-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoadingScreen;
