// OpenAI Client for Vercel - using Gemini API
const https = require('https');

class OpenAIClient {
    constructor() {
        this.apiKey = process.env.GEMINI_API_KEY || 'YOUR_API_KEY_HERE';
        this.baseUrl = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent';
    }

    async generateCrisisAnalysis(playerChoices, gameScore, choicePatterns) {
        const prompt = this.buildCrisisAnalysisPrompt(playerChoices, gameScore, choicePatterns);
        
        try {
            const response = await this.callGeminiAPI(prompt);
            return this.parseGeminiResponse(response);
        } catch (error) {
            console.error('Gemini API Error:', error);
            return this.getFallbackAnalysis(gameScore);
        }
    }

    buildCrisisAnalysisPrompt(playerChoices, gameScore, choicePatterns) {
        const dominantPattern = this.getDominantPattern(choicePatterns);
        
        return `As an MBA-level crisis management expert, provide a professional analysis of this crisis response performance.

PERFORMANCE OVERVIEW:
- Final Score: ${gameScore.score}/100
- Performance Level: ${gameScore.performanceLevel}
- Dominant Leadership Style: ${dominantPattern}

CHOICE ANALYSIS:
${this.formatChoicesForAnalysis(playerChoices)}

PATTERN INSIGHTS:
${this.formatPatternsForAnalysis(choicePatterns)}

Provide a concise, professional analysis covering:
1. Overall Crisis Leadership Assessment (2-3 sentences)
2. Key Strengths Demonstrated (2-3 bullet points)
3. Areas for Strategic Improvement (2-3 bullet points)
4. Crisis Communication Effectiveness (1-2 sentences)
5. Recommended Next Steps for Development (2-3 action items)

Keep the response focused, actionable, and appropriate for executive-level review. Avoid referencing specific turn numbers or choice letters.`;
    }

    formatChoicesForAnalysis(choices) {
        const scenarios = [
            'Initial Crisis Response Approach',
            'Communication Strategy Selection',
            'Media Engagement Method',
            'Resource Allocation Decision',
            'Timeline Management Approach',
            'Risk Assessment Strategy',
            'Employee Communication Method',
            'Customer Relations Approach',
            'Regulatory Response Strategy',
            'Recovery Planning Focus'
        ];

        return choices.map((choice, index) => 
            `${scenarios[index]}: ${this.getChoiceDescription(index, choice)}`
        ).join('\n');
    }

    getChoiceDescription(index, choice) {
        const descriptions = {
            0: { A: 'Aggressive immediate action', B: 'Balanced rapid response', C: 'Cautious assessment first' },
            1: { A: 'Full transparency approach', B: 'Controlled messaging', C: 'Collaborative communication' },
            2: { A: 'Proactive media engagement', B: 'Professional media relations', C: 'Limited media interaction' },
            3: { A: 'Comprehensive crisis team', B: 'External expertise focus', C: 'Minimal resource deployment' },
            4: { A: 'Immediate action timeline', B: 'Structured phased approach', C: 'Extended analysis period' },
            5: { A: 'Full risk assessment', B: 'Focused monitoring', C: 'Scenario-based planning' },
            6: { A: 'Direct town hall approach', B: 'Management cascade system', C: 'Written communication only' },
            7: { A: 'Mass communication strategy', B: 'Personalized outreach', C: 'Minimal customer contact' },
            8: { A: 'Proactive regulatory engagement', B: 'Collaborative compliance', C: 'Reactive regulatory response' },
            9: { A: 'Quick recovery focus', B: 'Comprehensive rebuilding', C: 'Gradual stabilization' }
        };
        
        return descriptions[index]?.[choice] || 'Strategic approach selected';
    }

    formatPatternsForAnalysis(patterns) {
        return Object.entries(patterns)
            .filter(([_, count]) => count > 0)
            .map(([pattern, count]) => `${pattern.charAt(0).toUpperCase() + pattern.slice(1)}: ${count} decisions`)
            .join(', ');
    }

    getDominantPattern(patterns) {
        const maxCount = Math.max(...Object.values(patterns));
        const dominantPatterns = Object.entries(patterns)
            .filter(([_, count]) => count === maxCount)
            .map(([pattern]) => pattern);
        
        return dominantPatterns.length === 1 ? 
            dominantPatterns[0].charAt(0).toUpperCase() + dominantPatterns[0].slice(1) :
            'Balanced Leadership';
    }

    async callGeminiAPI(prompt) {
        return new Promise((resolve, reject) => {
            const postData = JSON.stringify({
                contents: [{
                    parts: [{
                        text: prompt
                    }]
                }],
                generationConfig: {
                    temperature: 0.7,
                    topK: 40,
                    topP: 0.95,
                    maxOutputTokens: 1024
                }
            });

            const options = {
                hostname: 'generativelanguage.googleapis.com',
                port: 443,
                path: `/v1beta/models/gemini-2.0-flash-exp:generateContent?key=${this.apiKey}`,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(postData)
                }
            };

            const req = https.request(options, (res) => {
                let data = '';
                
                res.on('data', (chunk) => {
                    data += chunk;
                });
                
                res.on('end', () => {
                    try {
                        const response = JSON.parse(data);
                        resolve(response);
                    } catch (error) {
                        reject(new Error('Failed to parse API response'));
                    }
                });
            });

            req.on('error', (error) => {
                reject(error);
            });

            req.write(postData);
            req.end();
        });
    }

    parseGeminiResponse(response) {
        try {
            if (response.candidates && response.candidates[0] && response.candidates[0].content) {
                return response.candidates[0].content.parts[0].text;
            }
            throw new Error('Invalid response format');
        } catch (error) {
            console.error('Error parsing Gemini response:', error);
            throw error;
        }
    }

    getFallbackAnalysis(gameScore) {
        const level = gameScore.performanceLevel;
        const score = gameScore.score;

        let analysis = `**Overall Crisis Leadership Assessment**\n`;
        
        if (score >= 85) {
            analysis += `Your crisis response demonstrates exceptional leadership capabilities with a comprehensive approach to stakeholder management and strategic communication. The systematic decision-making process reflects advanced crisis management expertise suitable for senior executive roles.\n\n`;
        } else if (score >= 75) {
            analysis += `Your crisis management approach shows strong leadership fundamentals with effective stakeholder engagement and strategic thinking. The balanced decision-making demonstrates solid crisis communication skills with room for executive-level refinement.\n\n`;
        } else if (score >= 65) {
            analysis += `Your crisis response reflects competent management skills with adequate stakeholder consideration and communication planning. The approach shows understanding of crisis fundamentals while indicating opportunities for strategic enhancement.\n\n`;
        } else if (score >= 50) {
            analysis += `Your crisis handling demonstrates basic management awareness with some stakeholder consideration. The response shows foundational understanding while highlighting significant areas for crisis leadership development.\n\n`;
        } else {
            analysis += `Your crisis response indicates need for substantial development in crisis management fundamentals, stakeholder engagement, and strategic communication planning to meet executive-level crisis leadership standards.\n\n`;
        }

        analysis += this.getFallbackStrengths(score);
        analysis += this.getFallbackImprovements(score);
        analysis += this.getFallbackCommunication(score);
        analysis += this.getFallbackNextSteps(score);

        return analysis;
    }

    getFallbackStrengths(score) {
        let strengths = `**Key Strengths Demonstrated**\n`;
        
        if (score >= 75) {
            strengths += `• Strategic thinking in resource allocation and timeline management\n`;
            strengths += `• Effective stakeholder communication and engagement approach\n`;
            strengths += `• Proactive risk assessment and crisis response planning\n\n`;
        } else if (score >= 50) {
            strengths += `• Basic understanding of crisis management fundamentals\n`;
            strengths += `• Recognition of stakeholder communication importance\n`;
            strengths += `• Willingness to engage in structured crisis response\n\n`;
        } else {
            strengths += `• Participation in crisis decision-making process\n`;
            strengths += `• Awareness of multiple response options\n`;
            strengths += `• Foundation for crisis management skill development\n\n`;
        }
        
        return strengths;
    }

    getFallbackImprovements(score) {
        let improvements = `**Areas for Strategic Improvement**\n`;
        
        if (score >= 75) {
            improvements += `• Enhanced media relations and public communication strategy\n`;
            improvements += `• Advanced stakeholder mapping and engagement planning\n`;
            improvements += `• Executive-level crisis communication refinement\n\n`;
        } else if (score >= 50) {
            improvements += `• Strengthened crisis communication planning and execution\n`;
            improvements += `• Improved stakeholder analysis and engagement strategies\n`;
            improvements += `• Enhanced risk assessment and mitigation planning\n\n`;
        } else {
            improvements += `• Fundamental crisis management theory and application\n`;
            improvements += `• Basic stakeholder identification and communication skills\n`;
            improvements += `• Strategic thinking and decision-making under pressure\n\n`;
        }
        
        return improvements;
    }

    getFallbackCommunication(score) {
        let communication = `**Crisis Communication Effectiveness**\n`;
        
        if (score >= 75) {
            communication += `Communication strategy demonstrates strong understanding of transparency, timing, and stakeholder needs with executive-level messaging capability.\n\n`;
        } else if (score >= 50) {
            communication += `Communication approach shows basic effectiveness with opportunities to enhance strategic messaging and stakeholder-specific communication.\n\n`;
        } else {
            communication += `Communication strategy requires significant development in messaging clarity, stakeholder analysis, and strategic communication planning.\n\n`;
        }
        
        return communication;
    }

    getFallbackNextSteps(score) {
        let nextSteps = `**Recommended Next Steps for Development**\n`;
        
        if (score >= 75) {
            nextSteps += `• Advanced crisis simulation exercises with media training components\n`;
            nextSteps += `• Executive crisis communication workshop participation\n`;
            nextSteps += `• Crisis leadership mentoring with senior executives who have managed major crises\n`;
        } else if (score >= 50) {
            nextSteps += `• Comprehensive crisis management training program enrollment\n`;
            nextSteps += `• Stakeholder mapping and communication planning workshops\n`;
            nextSteps += `• Crisis response simulation exercises with feedback and coaching\n`;
        } else {
            nextSteps += `• Foundational crisis management education and certification\n`;
            nextSteps += `• Basic business communication and strategic thinking development\n`;
            nextSteps += `• Mentoring relationship with experienced crisis management professionals\n`;
        }
        
        return nextSteps;
    }
}

module.exports = OpenAIClient;
