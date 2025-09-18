// Crisis Game Service - Node.js version for Vercel
class GameService {
    static evaluateGame(playerChoices) {
        // Calculate performance score based on choices
        let score = 0;
        let reasoning = [];

        // Leadership Communication (Choices 1-3)
        const leadershipChoices = [playerChoices[0], playerChoices[1], playerChoices[2]];
        const leadershipScore = this.evaluateLeadership(leadershipChoices);
        score += leadershipScore;
        reasoning.push(`Leadership Communication: ${leadershipScore}/30 points`);

        // Strategic Response (Choices 4-6)
        const strategicChoices = [playerChoices[3], playerChoices[4], playerChoices[5]];
        const strategicScore = this.evaluateStrategy(strategicChoices);
        score += strategicScore;
        reasoning.push(`Strategic Response: ${strategicScore}/30 points`);

        // Stakeholder Management (Choices 7-10)
        const stakeholderChoices = [playerChoices[6], playerChoices[7], playerChoices[8], playerChoices[9]];
        const stakeholderScore = this.evaluateStakeholder(stakeholderChoices);
        score += stakeholderScore;
        reasoning.push(`Stakeholder Management: ${stakeholderScore}/40 points`);

        return {
            score: Math.round(score),
            reasoning,
            performanceLevel: this.getPerformanceLevel(score)
        };
    }

    static evaluateLeadership(choices) {
        let score = 0;
        
        // Immediate Response (Choice 1)
        if (choices[0] === 'B') score += 12; // Balanced, immediate response
        else if (choices[0] === 'A') score += 8; // Aggressive but shows leadership
        else score += 5; // Passive approach

        // Communication Style (Choice 2)
        if (choices[1] === 'A') score += 10; // Transparent communication
        else if (choices[1] === 'C') score += 8; // Measured response
        else score += 4; // Deflection

        // Media Handling (Choice 3)
        if (choices[2] === 'B') score += 8; // Professional media engagement
        else if (choices[2] === 'A') score += 6; // Proactive but risky
        else score += 3; // Avoidance

        return score;
    }

    static evaluateStrategy(choices) {
        let score = 0;

        // Resource Allocation (Choice 4)
        if (choices[0] === 'A') score += 12; // Comprehensive crisis team
        else if (choices[0] === 'B') score += 8; // External expertise
        else score += 5; // Limited response

        // Timeline Management (Choice 5)
        if (choices[1] === 'B') score += 10; // Balanced timeline
        else if (choices[1] === 'A') score += 6; // Rushed response
        else score += 4; // Slow response

        // Risk Assessment (Choice 6)
        if (choices[2] === 'A') score += 8; // Comprehensive risk analysis
        else if (choices[2] === 'C') score += 7; // Scenario planning
        else score += 4; // Basic monitoring

        return score;
    }

    static evaluateStakeholder(choices) {
        let score = 0;

        // Employee Communication (Choice 7)
        if (choices[0] === 'A') score += 12; // Town hall approach
        else if (choices[0] === 'B') score += 8; // Management cascade
        else score += 5; // Email only

        // Customer Relations (Choice 8)
        if (choices[1] === 'B') score += 10; // Personalized outreach
        else if (choices[1] === 'A') score += 7; // Mass communication
        else score += 4; // Minimal communication

        // Regulatory Response (Choice 9)
        if (choices[2] === 'A') score += 10; // Proactive compliance
        else if (choices[2] === 'B') score += 8; // Collaborative approach
        else score += 5; // Reactive response

        // Recovery Planning (Choice 10)
        if (choices[3] === 'B') score += 8; // Comprehensive recovery plan
        else if (choices[3] === 'A') score += 6; // Quick fixes
        else score += 4; // Wait and see

        return score;
    }

    static getPerformanceLevel(score) {
        if (score >= 85) return "Exceptional Crisis Leadership";
        if (score >= 75) return "Strong Crisis Management";
        if (score >= 65) return "Competent Crisis Response";
        if (score >= 50) return "Adequate Crisis Handling";
        return "Needs Crisis Management Development";
    }

    static analyzeChoicePatterns(choices) {
        const patterns = {
            proactive: 0,
            reactive: 0,
            collaborative: 0,
            authoritative: 0,
            transparent: 0,
            defensive: 0
        };

        // Analyze patterns based on choice combinations
        choices.forEach((choice, index) => {
            switch (index) {
                case 0: // Immediate response
                    if (choice === 'A') patterns.authoritative++;
                    if (choice === 'B') patterns.proactive++;
                    if (choice === 'C') patterns.reactive++;
                    break;
                case 1: // Communication style
                    if (choice === 'A') patterns.transparent++;
                    if (choice === 'B') patterns.defensive++;
                    if (choice === 'C') patterns.collaborative++;
                    break;
                case 2: // Media handling
                    if (choice === 'A') patterns.proactive++;
                    if (choice === 'B') patterns.collaborative++;
                    if (choice === 'C') patterns.defensive++;
                    break;
                case 6: // Employee communication
                    if (choice === 'A') patterns.transparent++;
                    if (choice === 'B') patterns.collaborative++;
                    if (choice === 'C') patterns.authoritative++;
                    break;
                case 8: // Regulatory response
                    if (choice === 'A') patterns.proactive++;
                    if (choice === 'B') patterns.collaborative++;
                    if (choice === 'C') patterns.reactive++;
                    break;
            }
        });

        return patterns;
    }
}

module.exports = GameService;
