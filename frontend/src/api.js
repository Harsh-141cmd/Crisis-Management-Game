// Use local backend for development, Vercel API for production
const API_BASE = process.env.NODE_ENV === 'production' 
  ? "https://crisisgame-nl118bs81-harshs-projects-f0379c1a.vercel.app/api" 
  : "http://localhost:8081/api/game";

export async function startGame(payload) {
  console.log('API: Starting game with payload:', payload);
  
  const endpoint = `${API_BASE}/start`;
  console.log('API: Calling start endpoint:', endpoint);
  
  try {
    const res = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });
    
    console.log('API: Response status:', res.status, res.statusText);
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('API: Error response:', errorText);
      throw new Error(`HTTP ${res.status}: ${errorText}`);
    }
    
    const data = await res.json();
    console.log('API: Start game response:', data);
    return data;
  } catch (error) {
    console.error('API: Start game error:', error);
    throw error;
  }
}

export async function sendChoice({ sessionId, choice }) {
  console.log('API: Sending choice with sessionId:', sessionId, 'choice:', choice);
  
  const endpoint = `${API_BASE}/turn`;
  console.log('API: Calling turn endpoint:', endpoint);
  
  try {
    const res = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ sessionId, choice })
    });
    
    console.log('API: Response status:', res.status, res.statusText);
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('API: Error response:', errorText);
      throw new Error(`HTTP ${res.status}: ${errorText}`);
    }
    
    const data = await res.json();
    console.log('API: Turn response:', data);
    return data;
  } catch (error) {
    console.error('API: Turn error:', error);
    throw error;
  }
}

export async function evaluateGame(choices) {
  console.log('API: Evaluating game with choices:', choices);
  
  const endpoint = process.env.NODE_ENV === 'production' 
    ? `${API_BASE}/evaluate-game`
    : `${API_BASE}/evaluate`;
  
  console.log('API: Calling evaluation endpoint:', endpoint);
  
  try {
    const res = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ choices })
    });
    
    console.log('API: Response status:', res.status, res.statusText);
    
    if (!res.ok) {
      const errorText = await res.text();
      console.error('API: Error response:', errorText);
      throw new Error(`HTTP ${res.status}: ${errorText}`);
    }
    
    const result = await res.json();
    console.log('API: Success response:', result);
    return result;
    
  } catch (error) {
    console.error('API: Network or parsing error:', error);
    throw error;
  }
}