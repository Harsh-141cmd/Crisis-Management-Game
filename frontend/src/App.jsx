import React, { useState, useEffect } from "react";
import { startGame, sendChoice } from "./api";
import GameHeader from "./components/GameHeader";
import PlayerSetupForm from "./components/PlayerSetupForm";
import GameInterface from "./components/GameInterface";
import GameResults from "./components/GameResults";
import LoadingScreen from "./components/LoadingScreen";
import IntroScreen from "./components/IntroScreen";
import "./styles/enhanced-ui.css";

export default function App() {
  const [sessionId, setSessionId] = useState(null);
  const [turn, setTurn] = useState(0);
  const [narrative, setNarrative] = useState("");
  const [options, setOptions] = useState([]);
  const [final, setFinal] = useState(null);
  const [loading, setLoading] = useState(false);
  const [gameState, setGameState] = useState('intro'); // 'intro', 'loading', 'setup', 'playing', 'results'
  const [loadingMessage, setLoadingMessage] = useState("Loading...");

  // Simulate initial loading
  useEffect(() => {
    
  }, []);

  const onIntroComplete = () => {
    setGameState('loading');
    setLoadingMessage("Initializing Crisis Simulation System...");
    
    const timer = setTimeout(() => {
      setGameState('setup');
    }, 2000);

    return () => clearTimeout(timer);
  };

  const onStart = async (formData) => {
    setLoading(true);
    setLoadingMessage("Generating crisis scenario...");
    
    console.log('Starting game with data:', formData);
    
    try {
      const res = await startGame(formData);
      console.log('Game start response:', res);
      console.log('Setting sessionId to:', res.sessionId);
      console.log('Setting narrative to:', res.narrative);
      console.log('Setting options to:', res.options);
      
      setSessionId(res.sessionId);
      setTurn(res.turn);
      setNarrative(res.narrative);
      setOptions(res.options);
      setFinal(null);
      setGameState('playing');
      
      console.log('Game state set to playing');
    } catch (err) {
      console.error('Game start error details:', err);
      alert('Failed to start game: ' + err.message + '\n\nCheck browser console for details.');
    } finally {
      setLoading(false);
      console.log('Loading set to false');
    }
  };

  const onChoice = async (label) => {
    if (!sessionId) return;
    setLoading(true);
    setLoadingMessage("Processing your decision...");
    
    try {
      const res = await sendChoice({ sessionId, choice: label });
      setTurn(res.turn);
      setNarrative(res.narrative);
      
      if (res.gameOver) {
        setFinal(res);
        setOptions([]);
        setGameState('results');
      } else {
        setOptions(res.options || []);
      }
    } catch (err) {
      alert('Failed to process choice: ' + err.message);
      console.error('Choice processing error:', err);
    } finally {
      setLoading(false);
    }
  };

  const onRestart = () => {
    setSessionId(null);
    setTurn(0);
    setNarrative("");
    setOptions([]);
    setFinal(null);
    setGameState('intro');
    setLoading(false);
  };

  if (gameState === 'intro') {
    return <IntroScreen onComplete={onIntroComplete} />;
  }

  if (gameState === 'loading') {
    return <LoadingScreen message={loadingMessage} />;
  }

  return (
    <div className="app">
      <GameHeader 
        sessionId={sessionId}
        turn={turn}
        maxTurns={10}
        gameState={gameState}
      />

      {gameState === 'setup' && (
        <PlayerSetupForm 
          onStart={onStart}
          loading={loading}
        />
      )}

      {gameState === 'playing' && sessionId && !final && (
        <GameInterface
          sessionId={sessionId}
          turn={turn}
          narrative={narrative}
          options={options}
          onChoice={onChoice}
          loading={loading}
        />
      )}

      {gameState === 'results' && final && (
        <GameResults
          final={final}
          onRestart={onRestart}
        />
      )}

      {loading && gameState !== 'loading' && (
        <LoadingScreen message={loadingMessage} />
      )}
    </div>
  );
}