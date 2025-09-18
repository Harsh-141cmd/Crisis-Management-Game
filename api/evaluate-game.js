const GameService = require('./gameService');
const OpenAIClient = require('./openaiClient');

// Enable CORS
const corsHeaders = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type',
};

// Handle preflight OPTIONS requests
function handleCORS(req, res) {
    if (req.method === 'OPTIONS') {
        res.writeHead(200, corsHeaders);
        res.end();
        return true;
    }
    
    // Add CORS headers to all responses
    Object.entries(corsHeaders).forEach(([key, value]) => {
        res.setHeader(key, value);
    });
    return false;
}

module.exports = async (req, res) => {
    // Handle CORS
    if (handleCORS(req, res)) return;

    try {
        if (req.method === 'POST') {
            let body = '';
            
            // Read the request body
            await new Promise((resolve, reject) => {
                req.on('data', chunk => {
                    body += chunk.toString();
                });
                
                req.on('end', () => {
                    resolve();
                });
                
                req.on('error', (error) => {
                    reject(error);
                });
            });

            const gameData = JSON.parse(body);
            console.log('Received game data:', gameData);

            // Validate input
            if (!gameData.choices || !Array.isArray(gameData.choices) || gameData.choices.length !== 10) {
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ 
                    error: 'Invalid input: choices array must contain exactly 10 elements' 
                }));
                return;
            }

            // Evaluate the game
            const gameScore = GameService.evaluateGame(gameData.choices);
            const choicePatterns = GameService.analyzeChoicePatterns(gameData.choices);

            console.log('Game evaluation completed:', {
                score: gameScore.score,
                level: gameScore.performanceLevel,
                patterns: choicePatterns
            });

            // Generate AI analysis
            const openAIClient = new OpenAIClient();
            let crisisAnalysis;

            try {
                console.log('Attempting to generate AI analysis...');
                crisisAnalysis = await openAIClient.generateCrisisAnalysis(
                    gameData.choices, 
                    gameScore, 
                    choicePatterns
                );
                console.log('AI analysis generated successfully');
            } catch (error) {
                console.error('AI analysis failed, using fallback:', error.message);
                crisisAnalysis = openAIClient.getFallbackAnalysis(gameScore);
            }

            // Prepare response
            const response = {
                success: true,
                score: gameScore.score,
                performanceLevel: gameScore.performanceLevel,
                reasoning: gameScore.reasoning,
                choicePatterns: choicePatterns,
                crisisAnalysis: crisisAnalysis,
                timestamp: new Date().toISOString()
            };

            console.log('Sending response:', {
                success: response.success,
                score: response.score,
                level: response.performanceLevel,
                analysisLength: response.crisisAnalysis.length
            });

            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify(response));

        } else if (req.method === 'GET') {
            // Health check endpoint
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ 
                status: 'ok', 
                message: 'Crisis Game API is running',
                timestamp: new Date().toISOString(),
                apiKey: process.env.GEMINI_API_KEY ? 'configured' : 'missing'
            }));

        } else {
            res.writeHead(405, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ error: 'Method not allowed' }));
        }

    } catch (error) {
        console.error('API Error:', error);
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ 
            error: 'Internal server error',
            message: error.message,
            timestamp: new Date().toISOString()
        }));
    }
};
