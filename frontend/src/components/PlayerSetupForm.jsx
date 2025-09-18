import React, { useState, useRef, useEffect } from 'react';

const PlayerSetupForm = ({ onStart, loading }) => {
  const [form, setForm] = useState({ 
    name: '', 
    age: 25, 
    gender: '', 
    difficulty: 3 
  });
  const [focusedField, setFocusedField] = useState(null);
  const formRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          formRef.current.classList.add('visible');
        }
      },
      { threshold: 0.3 }
    );
    
    if (formRef.current) {
      observer.observe(formRef.current);
    }
    
    return () => observer.disconnect();
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('PlayerSetupForm: Form submitted with data:', form);
    console.log('PlayerSetupForm: Loading state:', loading);
    
    const gameData = {
      name: form.name,
      age: Number(form.age),
      gender: form.gender,
      difficulty: Number(form.difficulty)
    };
    
    console.log('PlayerSetupForm: Calling onStart with:', gameData);
    onStart(gameData);
  };

  const difficultyLabels = {
    1: { title: 'Intern', desc: 'PR Assistant / HR Assistant' },
    2: { title: 'Manager', desc: 'PR Manager / HR Manager' },
    3: { title: 'Senior', desc: 'Head of Communications / HR' },
    4: { title: 'Director', desc: 'VP Corporate Affairs / HR' },
    5: { title: 'Executive', desc: 'CEO / CFO / CTO / CHRO' }
  };

  return (
    <>
      {/* Full-screen hero section - Mobile/Tablet only */}
      <div className="hero-section">
        <div className="hero-background">
          <div className="floating-particles">
            {[...Array(20)].map((_, i) => (
              <div key={i} className="particle" style={{
                left: `${Math.random() * 100}%`,
                animationDelay: `${Math.random() * 3}s`,
                animationDuration: `${3 + Math.random() * 4}s`
              }} />
            ))}
          </div>
        </div>
        
        <div className="hero-content">
          <div className="hero-badge">
            <span className="badge-icon">⚡</span>
            <span>Crisis Management Ready</span>
          </div>
          <h1 className="hero-main-title">
            Master the Art of
            <span className="gradient-text"> Crisis Management</span>
          </h1>
          <p className="hero-main-description">
            Step into the shoes of a corporate leader facing real-world crisis scenarios. 
            Make critical decisions, manage stakeholders, and navigate through complex 
            challenges that will test your communication and leadership skills.
          </p>
          <div className="scroll-indicator">
            <div className="scroll-arrow">↓</div>
            <span>Scroll to Begin</span>
          </div>
        </div>
      </div>

      {/* Form section */}
      <div className="setup-container">
        <div className="setup-background">
          <div className="floating-particles">
            {[...Array(20)].map((_, i) => (
              <div key={i} className="particle" style={{
                left: `${Math.random() * 100}%`,
                animationDelay: `${Math.random() * 3}s`,
                animationDuration: `${3 + Math.random() * 4}s`
              }} />
            ))}
          </div>
        </div>

        <div className="setup-content" ref={formRef}>
          {/* Desktop hero section */}
          <div className="setup-hero">
            <div className="hero-badge">
              <span className="badge-icon">⚡</span>
              <span>Crisis Management Ready</span>
            </div>
            <h1 className="hero-title">
              Master the Art of
              <span className="gradient-text"> Crisis Management</span>
            </h1>
            <p className="hero-description">
              Step into the shoes of a corporate leader facing real-world crisis scenarios. 
              Make critical decisions, manage stakeholders, and navigate through complex 
              challenges that will test your communication and leadership skills.
            </p>
          </div>

          <div className="setup-form-container">
            <form onSubmit={handleSubmit} className="setup-form">
              <div className="form-header">
                <h2>Create Your Executive Profile</h2>
                <p>Customize your role and begin your crisis management journey</p>
              </div>

            <div className="form-grid">
              <div className={`form-group ${focusedField === 'name' ? 'focused' : ''}`}>
                <label htmlFor="name">Executive Name</label>
                <input
                  id="name"
                  type="text"
                  placeholder="Enter your name"
                  required
                  value={form.name}
                  onChange={(e) => setForm({...form, name: e.target.value})}
                  onFocus={() => setFocusedField('name')}
                  onBlur={() => setFocusedField(null)}
                />
                <div className="form-accent" />
              </div>

              <div className={`form-group ${focusedField === 'gender' ? 'focused' : ''}`}>
                <label htmlFor="gender">Gender</label>
                <select
                  id="gender"
                  required
                  value={form.gender}
                  onChange={(e) => setForm({...form, gender: e.target.value})}
                  onFocus={() => setFocusedField('gender')}
                  onBlur={() => setFocusedField(null)}
                  className="form-select"
                >
                  <option value="">Select your gender</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                </select>
                <div className="form-accent" />
              </div>

              <div className={`form-group ${focusedField === 'age' ? 'focused' : ''}`}>
                <label htmlFor="age">Age</label>
                <input
                  id="age"
                  type="number"
                  min="18"
                  max="80"
                  required
                  value={form.age}
                  onChange={(e) => setForm({...form, age: e.target.value})}
                  onFocus={() => setFocusedField('age')}
                  onBlur={() => setFocusedField(null)}
                />
                <div className="form-accent" />
              </div>
            </div>

            <div className="difficulty-section">
              <label className="difficulty-label">Select Your Executive Level</label>
              <div className="difficulty-grid">
                {[1, 2, 3, 4, 5].map((level) => (
                  <div
                    key={level}
                    className={`difficulty-card ${form.difficulty === level ? 'selected' : ''}`}
                    onClick={() => setForm({...form, difficulty: level})}
                  >
                    <div className="difficulty-header">
                      <span className="difficulty-number">{level}</span>
                      <span className="difficulty-title">{difficultyLabels[level].title}</span>
                    </div>
                    <p className="difficulty-desc">{difficultyLabels[level].desc}</p>
                    <div className="difficulty-indicator">
                      {[...Array(5)].map((_, i) => (
                        <div
                          key={i}
                          className={`indicator-dot ${i < level ? 'active' : ''}`}
                        />
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <button 
              type="submit" 
              disabled={loading}
              className={`submit-button ${loading ? 'loading' : ''}`}
            >
              {loading ? (
                <>
                  <div className="loading-spinner" />
                  <span>Generating Crisis Scenario...</span>
                </>
              ) : (
                <>
                  <span>Begin Crisis Management</span>
                  <div className="button-arrow">→</div>
                </>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
    </>
  );
};

export default PlayerSetupForm;
