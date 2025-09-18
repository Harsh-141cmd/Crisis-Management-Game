import React, { useState, useEffect } from 'react';

const IntroScreen = ({ onComplete }) => {
  const [phase, setPhase] = useState('fade-in'); // 'fade-in', 'logo-reveal', 'title-reveal', 'cinematic-zoom', 'fade-out'
  const [particles, setParticles] = useState([]);

  // Generate particles for background effect
  useEffect(() => {
    const particleArray = [];
    for (let i = 0; i < 50; i++) {
      particleArray.push({
        id: i,
        x: Math.random() * 100,
        y: Math.random() * 100,
        size: Math.random() * 3 + 1,
        speed: Math.random() * 2 + 0.5,
        opacity: Math.random() * 0.5 + 0.2
      });
    }
    setParticles(particleArray);
  }, []);

  useEffect(() => {
    // Phase 1: Initial fade in with particles (0-1s)
    const fadeInTimer = setTimeout(() => {
      setPhase('logo-reveal');
    }, 1000);

    // Phase 2: Logo dramatic reveal (1-3s)
    const logoTimer = setTimeout(() => {
      setPhase('title-reveal');
    }, 3000);

    // Phase 3: Title cinematic reveal (3-5s)
    const titleTimer = setTimeout(() => {
      setPhase('cinematic-zoom');
    }, 5000);

    // Phase 4: Dramatic zoom and build-up (5-7s)
    const zoomTimer = setTimeout(() => {
      setPhase('fade-out');
    }, 7000);

    // Phase 5: Epic fade out (7-8s)
    const fadeOutTimer = setTimeout(() => {
      onComplete();
    }, 8000);

    return () => {
      clearTimeout(fadeInTimer);
      clearTimeout(logoTimer);
      clearTimeout(titleTimer);
      clearTimeout(zoomTimer);
      clearTimeout(fadeOutTimer);
    };
  }, [onComplete]);

  return (
    <div className={`netflix-intro ${phase}`}>
      {/* Animated background particles */}
      <div className="particle-background">
        {particles.map(particle => (
          <div 
            key={particle.id}
            className="particle"
            style={{
              left: `${particle.x}%`,
              top: `${particle.y}%`,
              width: `${particle.size}px`,
              height: `${particle.size}px`,
              opacity: particle.opacity,
              animationDelay: `${particle.id * 0.1}s`,
              animationDuration: `${particle.speed}s`
            }}
          />
        ))}
      </div>

      {/* Netflix-style gradient overlays */}
      <div className="gradient-overlay top-gradient"></div>
      <div className="gradient-overlay bottom-gradient"></div>
      <div className="gradient-overlay center-glow"></div>

      {/* Main content */}
      <div className="netflix-content">
        {/* Logo with cinematic reveal */}
        <div className={`netflix-logo ${phase}`}>
          <div className="logo-container">
            <img src="/logo.jpg" alt="Crisis Management Logo" />
            <div className="logo-glow"></div>
          </div>
        </div>
        
        {/* Cinematic title with typewriter effect */}
        <div className={`netflix-title ${phase}`}>
          <div className="title-container">
            <h1 className="main-title">
              <span className="title-word crisis">CRISIS</span>
              <span className="title-word management">MANAGEMENT</span>
            </h1>
            <div className="subtitle-container">
              <p className="subtitle">THE DECISION MAKING GAME</p>
              <div className="subtitle-line"></div>
            </div>
            <div className="tagline">
              <span>Every choice has consequences.</span>
            </div>
          </div>
        </div>

        {/* Netflix-style loading dots */}
        <div className={`netflix-loading ${phase}`}>
          <div className="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>

      {/* Cinematic light rays */}
      <div className="light-rays">
        <div className="ray ray-1"></div>
        <div className="ray ray-2"></div>
        <div className="ray ray-3"></div>
      </div>
    </div>
  );
};

export default IntroScreen;
