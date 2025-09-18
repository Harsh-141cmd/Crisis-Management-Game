package com.crisisgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.crisisgame.model.GameState;
import com.crisisgame.model.PlayerProfile;
import com.crisisgame.model.StartResponse;
import com.crisisgame.model.TurnResponse;
import com.crisisgame.util.OptionParser;

public class GameService {
    private static final String GEMINI_MODEL = "gemini-2.5-pro";

    private static String systemPromptFor(PlayerProfile p){
        return """
        You are an expert crisis simulation storyteller creating immersive, dynamic corporate crisis scenarios. 

        CORE PRINCIPLES:
        - Generate COMPLETELY UNIQUE scenarios each time - never repeat crisis types or company details
        - Create compelling narrative arcs that evolve organically based on player choices
        - Incorporate current events, technology trends, and realistic business challenges
        - Make every choice meaningful with clear consequences that ripple through the story

        SCENARIO VARIETY (Pick one randomly each game):
        • Tech startup facing data breach/privacy scandal
        • Manufacturing company with safety incident/environmental crisis
        • Healthcare organization dealing with medical malpractice/patient safety
        • Financial services firm with fraud/regulatory investigation
        • Retail chain facing supply chain disruption/labor disputes
        • Entertainment company with celebrity scandal/content controversy
        • Food & beverage company with contamination/health concerns
        • Transportation company with safety/service failures
        • Energy company with environmental disaster/regulatory issues
        • Social media platform with misinformation/content moderation crisis

        PLAYER ROLE & COMPLEXITY PROGRESSION:
        Difficulty 1 (Entry): Communications Coordinator, Junior PR Specialist - Local/departmental issues, straightforward stakeholder management
        Difficulty 2 (Intermediate): Communications Manager, Crisis Response Manager - Regional issues, multi-department coordination, regulatory involvement
        Difficulty 3 (Advanced): Director of Communications, Director of Public Affairs - National issues, industry-wide impact, congressional oversight
        Difficulty 4 (Expert): VP of Corporate Communications, Chief Communications Officer - Global issues, international implications, industry transformation
        Difficulty 5 (Master): CEO, President, C-Suite Executive - Civilization-level crises, paradigm-shifting events, existential challenges

        STORYTELLING REQUIREMENTS:
        - Address player by name and reference their age/gender naturally in context
        - Create 4-6 vivid sentences per turn showing immediate crisis developments
        - Include realistic details: stakeholder reactions, media coverage, internal pressure
        - Show consequences of previous choices rippling through the story
        - Provide EXACTLY 5 options (A, B, C, D, E) with distinct strategic approaches
        - Options should span: immediate response, stakeholder communication, damage control, strategic pivot, bold leadership
        - Each choice should feel authentic to the player's role level and company context
        - Every option should be different and lead to unique story paths

        DYNAMIC ELEMENTS:
        - Introduce unexpected developments based on player decisions
        - Include realistic stakeholders: employees, customers, media, regulators, investors, board
        - Show real-time crisis escalation or de-escalation based on choices
        - Incorporate modern communication channels: social media, news outlets, internal comms

        FINAL ASSESSMENT (Turn 10):
        Provide comprehensive evaluation:
        1. Crisis resolution outcome and company status
        2. Player's career trajectory (promotion, lateral move, termination, industry recognition)
        3. Leadership strengths demonstrated
        4. Areas for improvement with specific examples
        5. Crisis Communication Theory Applied
        6. Long-term reputation impact and lessons learned

        Keep each turn under 200 words but pack with immersive details and meaningful choices.
        """;
    }

    // In-memory session store
    private final Map<String, GameState> sessions = new HashMap<>();
    private final OpenAIClient openAI = new OpenAIClient();
    private static final boolean MOCK_MODE = true; // Temporarily enable to avoid API overload

    // Test method to verify Gemini API connectivity
    public String testGeminiAPI() throws IOException {
        List<OpenAIClient.Message> testMessages = Arrays.asList(
            new OpenAIClient.Message("user", "Hello! Please respond with exactly: 'Gemini API is working correctly'")
        );
        
        return openAI.chat(GEMINI_MODEL, "You are a helpful assistant.", testMessages);
    }

    public StartResponse start(PlayerProfile player) throws IOException {
        String sessionId = UUID.randomUUID().toString();
        GameState state = new GameState(sessionId, player);

        if (MOCK_MODE) {
            // Generate dynamic mock scenarios based on player profile
            return generateDynamicMockScenario(sessionId, state, player);
        }

        String userIntro = String.format(
            "Player Info — Name: %s, Age: %d, Gender: %s, Difficulty: %d. Begin Turn 1 now. " +
            "Write 3-5 sentences, then EXACTLY five labeled options A–E.",
            player.getName(), player.getAge(), player.getGender(), player.getDifficulty()
        );

        String narrative = openAI.chat(
                GEMINI_MODEL,
                systemPromptFor(player),
                List.of(new OpenAIClient.Message("user", userIntro))
        );

        state.getMessagesHistory().add("ASSISTANT:\n" + narrative);
        sessions.put(sessionId, state);

        List<String> options = OptionParser.extractOptions(narrative);
        return new StartResponse(sessionId, state.getTurn(), narrative, options);
    }

    private StartResponse generateDynamicMockScenario(String sessionId, GameState state, PlayerProfile player) {
        int difficulty = player.getDifficulty();
        
        // Enhanced scenarios based on difficulty level
        String[][][] difficultyScenarios = {
            // Difficulty 1: Entry-level scenarios - Local/Department issues
            {
                {
                    "You're %s, a %d-year-old %s working as %s at TechStart Inc., a small startup. A minor data glitch has exposed 500 customer email addresses to a marketing partner. Your manager asks you to draft an apology email while the CEO handles the technical fix. The local news is asking questions, and a few customers have called complaining. You need to craft appropriate communication.",
                    "A) Draft a simple apology email acknowledging the minor issue",
                    "B) Wait for technical team to provide more details first",
                    "C) Consult with legal team about appropriate language",
                    "D) Prepare FAQ document for customer service team",
                    "E) Research best practices for similar situations online"
                },
                {
                    "Welcome %s! As a %d-year-old %s serving as %s at LocalManu Corp, you're dealing with a workplace safety incident. A worker slipped and was injured due to a wet floor that wasn't properly marked. The injury is minor, but HR wants you to help coordinate communication with the worker's family and document lessons learned for future prevention.",
                    "A) Contact the injured worker's family with a personal call",
                    "B) Focus on documenting the incident for internal records",
                    "C) Work with facilities to improve safety signage immediately",
                    "D) Coordinate with HR on worker support and communication",
                    "E) Research workplace safety communication best practices"
                }
            },
            // Difficulty 2: Intermediate scenarios - Regional/Multi-department impact
            {
                {
                    "You're %s, a %d-year-old %s working as %s at MidSize Solutions. A cybersecurity breach has compromised 15,000 customer accounts across three states. Local media outlets are covering the story, state regulators have opened an investigation, and your customer service lines are overwhelmed. The CEO expects you to coordinate the communication response while IT works on containment.",
                    "A) Issue immediate public statement acknowledging breach scope and response actions",
                    "B) Coordinate with state regulators first to ensure compliance with disclosure requirements",
                    "C) Focus on customer notification and support before public communications",
                    "D) Engage cybersecurity experts to provide technical credibility to communications",
                    "E) Develop comprehensive multi-channel communication strategy for different stakeholder groups"
                },
                {
                    "Welcome %s! As a %d-year-old %s in the role of %s at Regional Foods Corp, you're facing a serious challenge. E. coli contamination has been traced to your company's lettuce supply, affecting customers in 5 states with 8 hospitalizations. The CDC is investigating, grocery chains are pulling products, and national news crews are gathering outside your facilities. You must coordinate a complex multi-stakeholder response.",
                    "A) Implement immediate voluntary recall across all affected regions",
                    "B) Coordinate closely with CDC and health authorities on investigation timeline",
                    "C) Focus communication efforts on supporting affected families first",
                    "D) Engage with retail partners to coordinate messaging and supply chain response",
                    "E) Establish crisis communication center to manage multiple stakeholder communications"
                }
            },
            // Difficulty 3: Advanced scenarios - National/Industry-wide implications
            {
                {
                    "You're %s, a %d-year-old %s serving as %s at NationTech Corp, a major technology firm. A sophisticated nation-state cyber attack has breached your cloud infrastructure, potentially accessing sensitive data from millions of users including government contractors. The FBI has launched an investigation, Congress is demanding hearings, international partners are questioning security protocols, and your stock has dropped 25%%. This crisis has national security implications.",
                    "A) Coordinate with federal authorities while maintaining transparency with affected stakeholders",
                    "B) Implement comprehensive security overhaul and communicate progress publicly",
                    "C) Focus on supporting affected government and enterprise clients with priority response",
                    "D) Engage with international partners and industry leaders on coordinated security response",
                    "E) Develop strategic communications addressing national security concerns and business continuity"
                },
                {
                    "Welcome %s! As a %d-year-old %s working as %s at GlobalManufacturing Inc., you're managing a catastrophic situation. An explosion at your primary chemical plant has killed 3 workers and released toxic clouds affecting nearby communities. EPA is conducting emergency response, international environmental groups are mobilizing, class-action lawsuits are being filed, and regulatory agencies in multiple countries are suspending operations. This crisis threatens the company's global operations.",
                    "A) Coordinate comprehensive response addressing worker families, community safety, and environmental impact",
                    "B) Establish international crisis response center with regulatory agencies and environmental experts",
                    "C) Focus on immediate community evacuation and health support before addressing business implications",
                    "D) Engage with global environmental organizations and regulatory bodies on remediation strategy",
                    "E) Develop integrated crisis response addressing legal, environmental, operational, and reputational challenges"
                }
            },
            // Difficulty 4: Expert scenarios - Global/Transformational impact
            {
                {
                    "As %s, a %d-year-old %s in your executive role as %s at GlobalSocial Corp, you're confronting an unprecedented crisis. Your platform's AI algorithm has been systematically promoting extremist content leading to real-world violence in 12 countries. Whistleblower documents reveal internal knowledge dating back years. The UN is calling for investigation, governments are threatening regulation, advertisers representing $5 billion have suspended campaigns, and employee walkouts are spreading globally. This crisis threatens the future of social media governance.",
                    "A) Implement immediate AI algorithm shutdown and engage with international regulatory bodies",
                    "B) Establish global transparency initiative with external oversight and regular public accountability",
                    "C) Focus on supporting affected communities worldwide and funding violence prevention programs",
                    "D) Lead industry transformation by creating new ethical AI standards and governance frameworks",
                    "E) Develop comprehensive global response addressing regulatory, ethical, operational, and societal implications"
                },
                {
                    "You're %s, a %d-year-old %s serving as %s at PharmaGlobal Inc. A critical medication manufactured at your facilities has been linked to serious side effects affecting patients worldwide. Internal documents suggest possible cover-up of early warning signs. Health agencies in 30+ countries are launching investigations, medical professionals are questioning prescription practices, patient advocacy groups are organizing international litigation, and your research integrity is under global scrutiny. This crisis could reshape pharmaceutical industry standards.",
                    "A) Establish global patient safety response with full transparency and independent medical review",
                    "B) Coordinate with international health agencies on comprehensive safety assessment and regulatory compliance",
                    "C) Focus on supporting affected patients worldwide with medical care and compensation programs",
                    "D) Lead industry transformation in safety standards and transparent reporting protocols",
                    "E) Develop integrated global response addressing medical, regulatory, legal, and ethical dimensions"
                }
            },
            // Difficulty 5: Master scenarios - Civilization/Paradigm-shifting impact
            {
                {
                    "You're %s, a %d-year-old %s serving as %s at QuantumCorp, the world's leading quantum computing company. A critical security flaw in your quantum encryption technology has been discovered, potentially compromising global financial systems, military communications, and state secrets. The vulnerability affects every major government and corporation using your technology. Markets are crashing, international diplomatic relations are strained, cyber warfare capabilities are questioned, and the fundamental trust in digital security is collapsing. This crisis could reshape global information security paradigms.",
                    "A) Coordinate with world governments and international bodies on global security infrastructure protection",
                    "B) Lead international consortium to develop next-generation security standards and implementation protocols",
                    "C) Focus on immediate protection of critical infrastructure while developing long-term solutions",
                    "D) Pioneer new paradigm in quantum security with open-source collaboration and transparency",
                    "E) Orchestrate civilization-level response addressing national security, economic stability, and technological trust"
                },
                {
                    "Welcome %s! As a %d-year-old %s in your role as %s at BioGenesis Corp, you're facing humanity's greatest crisis. Your genetically modified organisms, released globally to address climate change, have begun mutating unpredictably. Environmental systems worldwide are destabilizing, food chains are collapsing, and some mutations pose existential threats to ecosystems. The UN Security Council is in emergency session, scientific communities are calling for unprecedented global intervention, and humanity's survival may depend on your crisis response. This is a civilization-defining moment.",
                    "A) Coordinate global scientific response with immediate environmental containment and reversal strategies",
                    "B) Establish international crisis response with world governments, UN, and scientific institutions",
                    "C) Focus on immediate ecosystem protection and food security while developing long-term solutions",
                    "D) Lead unprecedented global collaboration on environmental restoration and species protection",
                    "E) Orchestrate humanity's response to existential threat requiring complete paradigm shift in environmental stewardship"
                }
            }
        };
        
        // Select scenario based on difficulty level (0-4 index)
        int difficultyIndex = Math.min(difficulty - 1, 4);
        String[][] scenarios = difficultyScenarios[difficultyIndex];
        String[] scenario = scenarios[(int)(Math.random() * scenarios.length)];
        
        // Determine role based on difficulty
        String role = getRoleByDifficulty(player.getDifficulty());
        
        String narrative = String.format(scenario[0], 
            player.getName(), player.getAge(), player.getGender(), role);
        
        String[] options = {scenario[1], scenario[2], scenario[3], scenario[4], scenario[5]};
        
        sessions.put(sessionId, state);
        return new StartResponse(sessionId, 1, narrative, Arrays.asList(options));
    }

    private String getRoleByDifficulty(int difficulty) {
        return switch(difficulty) {
            case 1 -> "Communications Coordinator";
            case 2 -> "Crisis Communications Manager";
            case 3 -> "Director of Public Affairs";
            case 4 -> "VP of Corporate Communications";
            case 5 -> "Chief Communications Officer";
            default -> "Communications Specialist";
        };
    }

    private TurnResponse generateDynamicMockTurnResponse(String sessionId, GameState state, String choice) {
        int turn = state.getTurn();
        PlayerProfile player = state.getPlayer();
        
        // Dynamic narratives based on turn progression
        String[][] turnNarratives = {
            // Early turns (2-4) - Immediate crisis response
            {
                "Turn %d: Your decision to %s has triggered immediate reactions across all stakeholder groups. Social media sentiment is shifting rapidly as news outlets pick up the story. Your legal team reports that regulatory agencies are requesting detailed documentation while your HR department is fielding calls from concerned employees. The CEO has scheduled an emergency board meeting for tomorrow morning.",
                "Turn %d: The crisis escalates as your choice to %s becomes public knowledge. Competitor companies are distancing themselves while industry analysts debate the long-term implications. Your customer service department reports a 300%% increase in calls, and the IT security team has discovered additional vulnerabilities. Key investors are demanding an immediate strategy meeting.",
                "Turn %d: Following your decision to %s, the crisis has attracted international attention. Multiple government agencies are launching investigations while consumer advocacy groups organize boycotts. Your supply chain partners are reconsidering contracts, and your stock price has fluctuated by 15%%. Emergency protocols are now in effect across all departments."
            },
            // Mid turns (5-7) - Stakeholder management
            {
                "Turn %d: Your strategic choice to %s has begun showing measurable results. Key stakeholders are responding differently - some with increased confidence, others with lingering skepticism. Media coverage is evolving from breaking news to analysis pieces. Your communications team has tracked sentiment across 50+ platforms, revealing complex public opinion patterns that require sophisticated management.",
                "Turn %d: The implementation of your %s strategy has reached a critical juncture. Internal teams are reporting mixed feedback from focus groups while external analysts publish conflicting assessments. Your crisis response has become a case study in real-time, with business schools requesting interviews. The next decision will likely determine the long-term trajectory of recovery.",
                "Turn %d: Your approach to %s has established new industry precedents. Competitors are adopting similar strategies while regulatory bodies use your response as a benchmark for future guidelines. Employee morale surveys show improvement, but customer trust metrics remain volatile. The crisis has evolved into a complex ecosystem requiring nuanced navigation."
            },
            // Late turns (8-10) - Resolution and consequences
            {
                "Turn %d: As you implement %s, the crisis enters its final phase. Long-term implications are becoming clear as quarterly reports reflect the full impact. Your leadership during this crisis has been noted by industry publications, and headhunters are making unsolicited contact. The board of directors is preparing their final assessment of the crisis management effectiveness.",
                "Turn %d: Your decision to %s represents the culminating moment of the crisis response. Stakeholder confidence is stabilizing at new levels while your personal reputation has been fundamentally shaped by this experience. The crisis has transformed from an emergency into a defining chapter of your career and the company's history.",
                "Turn %d: The final implementation of %s concludes this crisis management scenario. All stakeholders have reached their positions on the company's handling of the situation. Your leadership style has been tested under extreme pressure, and the outcomes will influence industry best practices for years to come. This experience has become a defining moment in your professional development."
            }
        };
        
        // Dynamic options based on turn phase
        String[][] turnOptions = {
            // Early turns - Crisis response
            {
                "A) Launch comprehensive stakeholder communication campaign across all channels",
                "B) Implement immediate operational changes to address root causes",
                "C) Engage external crisis management consultants for strategic guidance",
                "D) Focus on damage control while preparing long-term recovery strategy",
                "E) Coordinate with industry leaders to establish unified response protocols"
            },
            // Mid turns - Strategic management
            {
                "A) Develop transparent progress reporting system for all stakeholders",
                "B) Initiate innovative solutions that could transform the industry standard",
                "C) Build strategic partnerships to strengthen recovery efforts",
                "D) Implement advanced monitoring systems to prevent future crises",
                "E) Create comprehensive training programs based on lessons learned"
            },
            // Late turns - Resolution and legacy
            {
                "A) Establish permanent organizational changes based on crisis learnings",
                "B) Develop thought leadership content to share insights with industry",
                "C) Create crisis preparedness framework for other organizations",
                "D) Focus on reputation rebuilding through strategic community engagement",
                "E) Document comprehensive case study for future crisis management reference"
            }
        };
        
        // Select narrative and options based on turn
        int phase = Math.min(2, (turn - 2) / 3); // 0=early, 1=mid, 2=late
        int variation = (turn - 2) % 3; // Variation within phase
        
        String narrative = String.format(turnNarratives[phase][variation], turn, choice);
        String[] options = turnOptions[phase];
        
        return TurnResponse.ongoing(sessionId, turn, narrative, Arrays.asList(options));
    }

    private TurnResponse generateDynamicFinalResults(String sessionId, GameState state, String choice) {
        PlayerProfile player = state.getPlayer();
        int difficulty = player.getDifficulty();
        
        // Enhanced performance scoring based on choices and consistency
        int performanceScore = calculatePerformanceScore(state, choice, difficulty);
        
        // Generate AI-based results using Gemini API
        try {
            String resultsPrompt = buildResultsPrompt(state, player, performanceScore, difficulty);
            String aiResults = openAI.chat("gemini-2.5-pro", 
                "You are an expert MBA crisis management instructor providing personalized feedback to students.", 
                List.of(new OpenAIClient.Message("user", resultsPrompt)));
            
            // Parse AI results into structured format
            ParsedResults parsedResults = parseAIResults(aiResults);
            
            String finalNarrative = String.format(
                "FINAL RESULTS: After 10 turns of intense %s-level crisis management, %s has navigated the complex %s scenario. " +
                "Your strategic decisions shaped stakeholder responses, media coverage, and long-term organizational outcomes. " +
                "The crisis tested your abilities as a %s, and the results reflect both your leadership growth and areas for future development.",
                getDifficultyName(difficulty), player.getName(), getScenarioType(state), getRoleByDifficulty(difficulty)
            );
            
            // Generate performance-based image description
            String imageDescription = generateImageDescription(player, performanceScore, difficulty);
            String imageUrl = null;
            try {
                imageUrl = openAI.generateImage(imageDescription);
            } catch (IOException e) {
                System.err.println("Failed to generate image: " + e.getMessage());
            }
            
            // Calculate accurate performance percentage (not always 100%)
            int accuratePercentage = calculateAccuratePercentage(performanceScore, state.getChoiceHistory(), difficulty);
            
            return TurnResponse.finished(sessionId, state.getTurn(), finalNarrative,
                parsedResults.outcome, parsedResults.career, parsedResults.strengths,
                parsedResults.improvements, parsedResults.leadership, parsedResults.crisisTheory, 
                imageUrl, accuratePercentage);
                
        } catch (Exception e) {
            System.err.println("Failed to generate AI results: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to basic results if AI fails
            return generateFallbackResults(sessionId, state, choice, performanceScore, difficulty);
        }
    }
    
    private String buildResultsPrompt(GameState state, PlayerProfile player, int performanceScore, int difficulty) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("CRISIS MANAGEMENT GAME FINAL RESULTS ANALYSIS\n\n");
        
        prompt.append("PLAYER PROFILE:\n");
        prompt.append("- Name: ").append(player.getName()).append("\n");
        prompt.append("- Role: ").append(getRoleByDifficulty(difficulty)).append("\n");
        prompt.append("- Difficulty: ").append(getDifficultyName(difficulty)).append("\n");
        prompt.append("- Performance Score: ").append(performanceScore).append("/5\n\n");
        
        prompt.append("CHOICE HISTORY (All 10 turns):\n");
        List<String> choices = state.getChoiceHistory();
        for (int i = 0; i < choices.size(); i++) {
            prompt.append("Turn ").append(i + 1).append(": ").append(choices.get(i)).append("\n");
        }
        
        prompt.append("\nCRISIS SCENARIO TYPE: ").append(getScenarioType(state)).append("\n\n");
        
        prompt.append("INSTRUCTIONS:\n");
        prompt.append("Based on the player's choices and performance, provide concise MBA-level crisis management analysis. ");
        prompt.append("Keep responses brief and professional. Do NOT reference specific turn numbers or choice letters (A, B, C, etc.). ");
        prompt.append("Your response must be in this EXACT format:\n\n");
        
        prompt.append("OUTCOME: [One concise sentence describing the overall crisis resolution outcome]\n\n");
        
        prompt.append("CAREER: [One sentence about career impact - be realistic based on performance]\n\n");
        
        prompt.append("STRENGTHS: [One sentence highlighting key strengths demonstrated]\n\n");
        
        prompt.append("IMPROVEMENTS: [One sentence about main areas for improvement]\n\n");
        
        prompt.append("LEADERSHIP: [One sentence describing their leadership approach]\n\n");
        
        prompt.append("CRISIS_THEORY: [Identify which specific MBA crisis communication theory they primarily applied from this comprehensive list: " +
            "Image Restoration Theory (Benoit), Situational Crisis Communication Theory/SCCT (Coombs), Excellence Theory (Grunig & Hunt), " +
            "Stakeholder Theory (Freeman), Contingency Theory (Cameron), Issues Management Theory (Chase & Jones), " +
            "Apologia Theory (Ware & Linkugel), Discourse of Renewal (Seeger), Crisis & Emergency Risk Communication/CERC (Reynolds), " +
            "Attribution Theory (Weiner), Social Learning Theory (Bandura), Systems Theory (Von Bertalanffy), " +
            "Chaos Theory (Seeger), Media Dependency Theory (Ball-Rokeach), Organizational Learning Theory (Argyris), " +
            "Prospect Theory (Kahneman & Tversky), Social Identity Theory (Tajfel), or Resilience Theory (Holling). " +
            "Choose the MOST appropriate theory based on their decision patterns. Provide ONLY the theory name and a brief 1-2 sentence explanation " +
            "of why it fits their overall approach. Do NOT reference specific turns or choice letters.]\n\n");
        
        prompt.append("Keep all responses concise and professional. Focus on overall patterns, not specific choices.");
        
        return prompt.toString();
    }
    
    private static class ParsedResults {
        String outcome, career, strengths, improvements, leadership, crisisTheory;
    }
    
    private ParsedResults parseAIResults(String aiResults) {
        ParsedResults results = new ParsedResults();
        
        try {
            String[] lines = aiResults.split("\n");
            
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("OUTCOME:")) {
                    results.outcome = line.substring(8).trim();
                } else if (line.startsWith("CAREER:")) {
                    results.career = line.substring(7).trim();
                } else if (line.startsWith("STRENGTHS:")) {
                    results.strengths = line.substring(10).trim();
                } else if (line.startsWith("IMPROVEMENTS:")) {
                    results.improvements = line.substring(13).trim();
                } else if (line.startsWith("LEADERSHIP:")) {
                    results.leadership = line.substring(11).trim();
                } else if (line.startsWith("CRISIS_THEORY:")) {
                    results.crisisTheory = line.substring(14).trim();
                }
            }
            
            // Ensure all fields have values
            if (results.outcome == null) results.outcome = "Crisis management completed with mixed results";
            if (results.career == null) results.career = "Role maintained with additional training needs";
            if (results.strengths == null) results.strengths = "Demonstrated resilience under pressure";
            if (results.improvements == null) results.improvements = "Could improve strategic planning and stakeholder communication";
            if (results.leadership == null) results.leadership = "Developing leadership style with room for growth";
            if (results.crisisTheory == null) results.crisisTheory = "Mixed crisis communication approach without clear theoretical framework";
            
        } catch (Exception e) {
            System.err.println("Error parsing AI results: " + e.getMessage());
            // Return default values if parsing fails
            results.outcome = "Crisis management approach showed both strengths and areas for development";
            results.career = "Position maintained with focus on professional development";
            results.strengths = "Showed commitment to learning and problem-solving";
            results.improvements = "Could benefit from more strategic planning and stakeholder analysis";
            results.leadership = "Emerging leadership skills with potential for growth";
            results.crisisTheory = "Applied mixed crisis management approaches - recommend focusing on specific theoretical frameworks";
        }
        
        return results;
    }
    
    private int calculateAccuratePercentage(int performanceScore, List<String> choices, int difficulty) {
        // Base score from performance (40% of total)
        int baseScore = (performanceScore - 1) * 20; // 0, 20, 40, 60, 80
        
        // Consistency bonus/penalty (20% of total)
        int consistencyScore = calculateConsistencyScore(choices);
        
        // Difficulty adjustment (10% of total)  
        int difficultyBonus = (difficulty - 1) * 5; // 0, 5, 10 bonus for higher difficulty
        
        // Choice quality analysis (30% of total)
        int choiceQualityScore = analyzeChoiceQuality(choices);
        
        int finalScore = baseScore + consistencyScore + difficultyBonus + choiceQualityScore;
        
        // Ensure score is within reasonable bounds (30-95% range)
        return Math.max(30, Math.min(95, finalScore));
    }
    
    private int calculateConsistencyScore(List<String> choices) {
        if (choices.size() < 3) return 0;
        
        // Look for strategic consistency patterns
        int proactiveChoices = 0;
        int reactiveChoices = 0;
        int stakeholderFocused = 0;
        
        for (String choice : choices) {
            String c = choice.toLowerCase();
            if (c.contains("proactive") || c.contains("prevent") || c.contains("anticipate") || c.contains("prepare")) {
                proactiveChoices++;
            }
            if (c.contains("respond") || c.contains("react") || c.contains("address") || c.contains("immediate")) {
                reactiveChoices++;
            }
            if (c.contains("stakeholder") || c.contains("community") || c.contains("customer") || c.contains("partner")) {
                stakeholderFocused++;
            }
        }
        
        // Reward consistency in approach
        int maxApproach = Math.max(proactiveChoices, Math.max(reactiveChoices, stakeholderFocused));
        double consistencyRatio = (double) maxApproach / choices.size();
        
        if (consistencyRatio >= 0.7) return 15; // Highly consistent
        if (consistencyRatio >= 0.5) return 10; // Moderately consistent  
        if (consistencyRatio >= 0.3) return 5;  // Somewhat consistent
        return 0; // Inconsistent approach
    }
    
    private int analyzeChoiceQuality(List<String> choices) {
        int qualityScore = 0;
        
        for (String choice : choices) {
            String c = choice.toLowerCase();
            
            // Reward strategic thinking keywords
            if (c.contains("strategic") || c.contains("comprehensive") || c.contains("systematic")) {
                qualityScore += 2;
            }
            
            // Reward stakeholder consideration
            if (c.contains("stakeholder") || c.contains("community") || c.contains("transparent")) {
                qualityScore += 2;
            }
            
            // Reward long-term thinking
            if (c.contains("long-term") || c.contains("sustainable") || c.contains("future")) {
                qualityScore += 2;
            }
            
            // Penalize purely defensive choices
            if (c.contains("deny") || c.contains("blame") || c.contains("minimize") && !c.contains("damage")) {
                qualityScore -= 1;
            }
        }
        
        return Math.max(0, Math.min(30, qualityScore)); // Cap at 30 points
    }
    
    private TurnResponse generateFallbackResults(String sessionId, GameState state, String choice, int performanceScore, int difficulty) {
        // Simple fallback results if AI generation fails
        String[] outcomes = {
            "Crisis managed with significant learning opportunities",
            "Crisis handled with adequate results", 
            "Crisis successfully managed with positive outcomes",
            "Crisis excellently resolved with industry recognition"
        };
        
        String[] careers = {
            "Role maintained with additional crisis management training",
            "Position secured with enhanced responsibilities",
            "Promoted to Senior " + getRoleByDifficulty(difficulty),
            "Promoted to executive leadership position"
        };
        
        int resultIndex = Math.min(performanceScore - 1, 3);
        int accuratePercentage = calculateAccuratePercentage(performanceScore, state.getChoiceHistory(), difficulty);
        
        // Enhanced fallback theory analysis with variety
        String[] theories = {
            "Situational Crisis Communication Theory (SCCT): Your responses show pattern recognition and context-appropriate communication strategies.",
            "Stakeholder Theory: Your approach prioritized balanced stakeholder engagement and relationship management throughout the crisis.",
            "Image Restoration Theory: Your communication strategy focused on reputation protection and organizational image repair techniques.", 
            "Excellence Theory: Your crisis response emphasized two-way symmetric communication and relationship building with key stakeholders.",
            "Contingency Theory: Your approach adapted communication strategies based on situational factors and stakeholder dynamics.",
            "Systems Theory: Your crisis management recognized the interconnected nature of organizational relationships and external factors.",
            "Discourse of Renewal: Your communication style aimed to transform the crisis into opportunities for organizational growth and learning.",
            "Crisis & Emergency Risk Communication (CERC): Your approach balanced transparency with risk management in public communication.",
            "Organizational Learning Theory: Your responses demonstrated capacity for adapting strategies based on crisis feedback and outcomes.",
            "Attribution Theory: Your communication addressed responsibility assignment while managing stakeholder perceptions of causation.",
            "Resilience Theory: Your approach focused on organizational adaptability and recovery rather than just crisis containment.",
            "Issues Management Theory: Your strategy showed proactive identification and management of emerging crisis-related issues."
        };
        
        String appliedTheory = theories[performanceScore % theories.length];
        
        String finalNarrative = String.format(
            "FINAL RESULTS: After 10 turns of %s-level crisis management, %s navigated the %s scenario. Results reflect your crisis management approach and areas for development.",
            getDifficultyName(difficulty), state.getPlayer().getName(), getScenarioType(state)
        );
        
        return TurnResponse.finished(sessionId, state.getTurn(), finalNarrative,
            outcomes[resultIndex], careers[resultIndex], 
            "Demonstrated crisis management capabilities", 
            "Could enhance strategic planning", 
            "Developing crisis leadership skills",
            appliedTheory, null, accuratePercentage);
    }
    
    private String generateImageDescription(PlayerProfile player, int performanceIndex, int difficulty) {
        String gender = player.getGender().toLowerCase();
        String role = getRoleByDifficulty(difficulty);
        
        // Add randomization to descriptions
        java.util.Random random = new java.util.Random(System.currentTimeMillis() + player.getName().hashCode());
        
        // Create pools of varied descriptions for each performance level
        String[] excellentDescriptions = {
            String.format("Confident %s %s in executive boardroom presenting successful crisis resolution to stakeholders, professional business attire, leadership success, excellent performance", gender, role),
            String.format("Triumphant %s %s celebrating successful crisis management outcome with senior leadership team, corporate victory, strategic excellence", gender, role),
            String.format("Professional %s %s delivering keynote presentation on crisis leadership best practices, industry recognition, thought leadership", gender, role),
            String.format("Successful %s %s in high-tech crisis command center coordinating multi-stakeholder response, innovation leadership, strategic command", gender, role),
            String.format("Accomplished %s %s receiving recognition for outstanding crisis management performance, professional achievement, leadership excellence", gender, role)
        };
        
        String[] goodDescriptions = {
            String.format("Professional %s %s in modern office leading crisis management team meeting, collaborative leadership, successful teamwork", gender, role),
            String.format("Competent %s %s facilitating cross-functional crisis response coordination, effective leadership, team collaboration", gender, role),
            String.format("Skilled %s %s presenting crisis resolution strategy to board of directors, professional competence, strategic thinking", gender, role),
            String.format("Effective %s %s conducting stakeholder briefing on crisis management progress, communication leadership, relationship management", gender, role),
            String.format("Capable %s %s overseeing crisis response operations from modern command center, operational excellence, team coordination", gender, role)
        };
        
        String[] adequateDescriptions = {
            String.format("Determined %s %s at desk reviewing crisis management reports, learning from experience, professional development", gender, role),
            String.format("Focused %s %s analyzing crisis response data and stakeholder feedback, continuous improvement, strategic analysis", gender, role),
            String.format("Thoughtful %s %s in conference room planning next steps for crisis management, strategic planning, professional growth", gender, role),
            String.format("Diligent %s %s working late reviewing crisis communication strategies, dedication to improvement, professional commitment", gender, role),
            String.format("Analytical %s %s studying crisis management best practices and lessons learned, knowledge building, skill development", gender, role)
        };
        
        String[] challengingDescriptions = {
            String.format("Reflective %s %s in office after challenging crisis experience, stress management, learning from setbacks, professional growth", gender, role),
            String.format("Resilient %s %s taking time to process difficult crisis management lessons, emotional intelligence, recovery planning", gender, role),
            String.format("Contemplative %s %s reviewing what went wrong during crisis response, self-reflection, professional learning", gender, role),
            String.format("Determined %s %s seeking mentorship and guidance after challenging crisis experience, growth mindset, seeking support", gender, role),
            String.format("Introspective %s %s planning recovery strategy after difficult crisis management outcome, resilience building, future planning", gender, role)
        };
        
        // Select appropriate description pool based on performance
        String[] descriptions;
        if (performanceIndex >= 3) {
            descriptions = excellentDescriptions;
        } else if (performanceIndex >= 2) {
            descriptions = goodDescriptions;
        } else if (performanceIndex >= 1) {
            descriptions = adequateDescriptions;
        } else {
            descriptions = challengingDescriptions;
        }
        
        // Return random description from the appropriate pool
        return descriptions[random.nextInt(descriptions.length)];
    }

    private int calculatePerformanceScore(GameState state, String lastChoice, int difficulty) {
        List<String> choiceHistory = state.getChoiceHistory();
        if (choiceHistory == null) {
            choiceHistory = new ArrayList<>();
        }
        choiceHistory.add(lastChoice);
        
        int baseScore = 50; // Start with neutral score
        
        // 1. Choice Consistency Analysis (20 points)
        int consistencyScore = analyzeChoiceConsistency(choiceHistory);
        
        // 2. Strategic Thinking Analysis (20 points)
        int strategicScore = analyzeStrategicThinking(choiceHistory, difficulty);
        
        // 3. Crisis Phase Management (20 points)  
        int phaseScore = analyzeCrisisPhaseManagement(choiceHistory);
        
        // 4. Stakeholder Balance (20 points)
        int stakeholderScore = analyzeStakeholderBalance(choiceHistory);
        
        // 5. Difficulty Adjustment (20 points)
        int difficultyBonus = calculateDifficultyBonus(difficulty, choiceHistory.size());
        
        int totalScore = baseScore + consistencyScore + strategicScore + phaseScore + stakeholderScore + difficultyBonus;
        
        // Normalize to 1-4 scale for result selection
        if (totalScore >= 85) return 4; // Excellent
        if (totalScore >= 70) return 3; // Good  
        if (totalScore >= 55) return 2; // Adequate
        return 1; // Needs Improvement
    }
    
    private int analyzeChoiceConsistency(List<String> choices) {
        if (choices.size() < 3) return 10; // Not enough data, give neutral score
        
        Map<String, Integer> choiceCount = new HashMap<>();
        for (String choice : choices) {
            choiceCount.put(choice, choiceCount.getOrDefault(choice, 0) + 1);
        }
        
        // Balanced approach (not too repetitive, not too chaotic) scores higher
        int uniqueChoices = choiceCount.size();
        if (uniqueChoices >= 3 && uniqueChoices <= 4) return 20; // Good balance
        if (uniqueChoices == 2 || uniqueChoices == 5) return 15; // Slightly unbalanced
        return 10; // Too repetitive or too chaotic
    }
    
    private int analyzeStrategicThinking(List<String> choices, int difficulty) {
        // A, B, C, D, E represent different strategic approaches
        // A = Immediate/Reactive, B = Collaborative, C = Analytical, D = Innovative, E = Comprehensive
        
        int score = 10; // Base strategic score
        
        // Look for strategic progression (reactive -> collaborative -> analytical -> innovative)
        for (int i = 1; i < choices.size(); i++) {
            String prev = choices.get(i-1);
            String curr = choices.get(i);
            
            // Reward strategic escalation patterns
            if (isStrategicProgression(prev, curr)) {
                score += 2;
            }
        }
        
        // Difficulty-appropriate choices
        if (difficulty >= 4) { // Expert/Master levels
            long innovativeChoices = choices.stream().filter(c -> c.equals("D") || c.equals("E")).count();
            score += (int)(innovativeChoices * 2); // Reward innovation at high levels
        }
        
        return Math.min(20, score);
    }
    
    private boolean isStrategicProgression(String prev, String curr) {
        // Define strategic progression: A(reactive) -> B(collaborative) -> C(analytical) -> D(innovative) -> E(comprehensive)
        String[] progression = {"A", "B", "C", "D", "E"};
        int prevIndex = java.util.Arrays.asList(progression).indexOf(prev);
        int currIndex = java.util.Arrays.asList(progression).indexOf(curr);
        
        return currIndex > prevIndex; // Moving toward more sophisticated approaches
    }
    
    private int analyzeCrisisPhaseManagement(List<String> choices) {
        int score = 10; // Base score
        int totalChoices = choices.size();
        
        if (totalChoices >= 8) { // Enough choices to analyze phases
            // Early phase (turns 1-3): Should be more reactive/immediate (A, B)
            List<String> earlyChoices = choices.subList(0, Math.min(3, totalChoices));
            long reactiveEarly = earlyChoices.stream().filter(c -> c.equals("A") || c.equals("B")).count();
            
            // Mid phase (turns 4-7): Should be more analytical/collaborative (B, C)
            if (totalChoices > 3) {
                List<String> midChoices = choices.subList(3, Math.min(7, totalChoices));
                long analyticalMid = midChoices.stream().filter(c -> c.equals("B") || c.equals("C")).count();
                score += analyticalMid * 2;
            }
            
            // Late phase (turns 8-10): Should be more strategic/innovative (C, D, E)
            if (totalChoices > 7) {
                List<String> lateChoices = choices.subList(7, totalChoices);
                long strategicLate = lateChoices.stream().filter(c -> c.equals("C") || c.equals("D") || c.equals("E")).count();
                score += strategicLate * 3;
            }
            
            score += reactiveEarly * 2; // Reward appropriate early responses
        }
        
        return Math.min(20, score);
    }
    
    private int analyzeStakeholderBalance(List<String> choices) {
        // Each choice type represents focus on different stakeholders
        // A = Internal/immediate, B = External/collaborative, C = Data/analytical, D = Innovation, E = Comprehensive
        
        Map<String, Integer> stakeholderFocus = new HashMap<>();
        for (String choice : choices) {
            stakeholderFocus.put(choice, stakeholderFocus.getOrDefault(choice, 0) + 1);
        }
        
        int uniqueApproaches = stakeholderFocus.size();
        
        // Reward balanced stakeholder management
        if (uniqueApproaches >= 4) return 20; // Excellent balance
        if (uniqueApproaches == 3) return 15; // Good balance
        if (uniqueApproaches == 2) return 10; // Limited balance
        return 5; // Poor balance
    }
    
    private int calculateDifficultyBonus(int difficulty, int choicesCount) {
        // Bonus points for managing higher difficulty levels successfully
        int baseBonus = (difficulty - 1) * 3; // 0, 3, 6, 9, 12 for difficulties 1-5
        
        // Completion bonus
        int completionBonus = choicesCount >= 10 ? 5 : 0;
        
        return Math.min(20, baseBonus + completionBonus);
    }

    private String getDifficultyName(int difficulty) {
        return switch(difficulty) {
            case 1 -> "Entry-level";
            case 2 -> "Intermediate";
            case 3 -> "Advanced";
            case 4 -> "Expert";
            case 5 -> "Master";
            default -> "Standard";
        };
    }

    private String getScenarioType(GameState state) {
        // This would ideally track which scenario was used, for now return generic
        String[] types = {"technology", "manufacturing", "healthcare", "social media", "food safety"};
        return types[(int)(Math.random() * types.length)];
    }

    public TurnResponse turn(String sessionId, String choice) throws IOException {
        GameState state = sessions.get(sessionId);
        if (state == null) throw new IOException("Invalid sessionId");
        if (state.isFinished()) throw new IOException("Game already finished");

        boolean finalTurn = state.getTurn() >= 10;
        
        if (MOCK_MODE) {
            // Track the choice
            state.addChoice(choice);
            
            // Generate dynamic mock response based on turn and choice
            state.nextTurn();
            
            if (!finalTurn) {
                return generateDynamicMockTurnResponse(sessionId, state, choice);
            } else {
                // Generate dynamic final results based on difficulty and performance
                return generateDynamicFinalResults(sessionId, state, choice);
            }
        }

        String continuationUserMsg;
        if (!finalTurn) {
            continuationUserMsg =
                "Player chooses option " + choice.toUpperCase() + ". Continue to next turn. " +
                "Write 3-5 sentences and then provide EXACTLY five labeled options A–E.";
        } else {
            continuationUserMsg =
                "Player chooses option " + choice.toUpperCase() + ". This was the 10th turn. " +
                "Now provide the final analysis per rules (items 1–6). Do NOT include further options.";
        }

        String narrative = openAI.chat(
                GEMINI_MODEL,
                systemPromptFor(state.getPlayer()),
                buildConversationHistory(state, continuationUserMsg)
        );

        state.getMessagesHistory().add("USER CHOICE: " + choice);
        state.getMessagesHistory().add("ASSISTANT:\n" + narrative);

        if (!finalTurn) {
            state.nextTurn();
            List<String> options = OptionParser.extractOptions(narrative);
            return TurnResponse.ongoing(state.getSessionId(), state.getTurn(), narrative, options);
        } else {
            state.setFinished(true);

            String outcome = extractBullet(narrative, 1);
            String career  = extractBullet(narrative, 2);
            String good    = extractBullet(narrative, 3);
            String better  = extractBullet(narrative, 4);
            String style   = extractBullet(narrative, 5);
            String theory  = extractBullet(narrative, 6);

            PlayerProfile p = state.getPlayer();
            String imgPrompt = String.format(
                "Professional corporate executive portrait of %s (%s, age %d) in a modern office setting after managing a crisis. " +
                "Subject shows %s expression reflecting their %s performance. " +
                "Background: Executive office with crisis management displays, news monitors, conference table. " +
                "Lighting: Professional corporate photography, soft professional lighting. " +
                "Style: Photorealistic, high-quality business portrait, professional business attire. " +
                "Mood: %s, confident leadership presence. Camera: Professional headshot style, shallow depth of field.",
                p.getName(),
                p.getGender().toLowerCase(),
                p.getAge(),
                determineFacialExpression(outcome),
                determinePerformanceLevel(outcome),
                determineMoodFromOutcome(outcome)
            );

            String imageUrl = openAI.generateImage(imgPrompt);

            return TurnResponse.finished(
                    state.getSessionId(),
                    state.getTurn(),
                    narrative, // full final write-up
                    emptyTo(narrative, outcome),
                    emptyTo("See narrative.", career),
                    emptyTo("See narrative.", good),
                    emptyTo("See narrative.", better),
                    emptyTo("See narrative.", style),
                    emptyTo("See narrative.", theory),
                    imageUrl,
                    75 // Default score for real AI responses
            );
        }
    }

    private static String extractBullet(String text, int n){
        
        var p = java.util.regex.Pattern.compile("(?m)^[\\s]*" + n + "[\\)\\.:\\-]\\s*(.+)$");
        var m = p.matcher(text);
        return m.find() ? m.group(1).trim() : "";
    }

    private static String determineFacialExpression(String outcome) {
        if (outcome == null) return "focused and determined";
        String lower = outcome.toLowerCase();
        if (lower.contains("success") || lower.contains("excellent") || lower.contains("outstanding")) {
            return "confident and satisfied";
        } else if (lower.contains("good") || lower.contains("effective") || lower.contains("positive")) {
            return "professional and composed";
        } else if (lower.contains("poor") || lower.contains("failed") || lower.contains("crisis")) {
            return "concerned but determined";
        } else {
            return "thoughtful and analytical";
        }
    }
    
    private static String determinePerformanceLevel(String outcome) {
        if (outcome == null) return "professional";
        String lower = outcome.toLowerCase();
        if (lower.contains("success") || lower.contains("excellent")) {
            return "exceptional";
        } else if (lower.contains("good") || lower.contains("effective")) {
            return "strong";
        } else if (lower.contains("average") || lower.contains("adequate")) {
            return "competent";
        } else {
            return "challenging but learning";
        }
    }
    
    private static String determineMoodFromOutcome(String outcome) {
        if (outcome == null) return "Professional and focused";
        String lower = outcome.toLowerCase();
        if (lower.contains("success") || lower.contains("excellent")) {
            return "Triumphant and confident";
        } else if (lower.contains("good") || lower.contains("effective")) {
            return "Satisfied and professional";
        } else if (lower.contains("poor") || lower.contains("failed")) {
            return "Resilient and determined";
        } else {
            return "Contemplative and strategic";
        }
    }

    private static String emptyTo(String fallback, String val){
        return (val == null || val.isBlank()) ? fallback : val;
    }

    private List<OpenAIClient.Message> buildConversationHistory(GameState state, String currentUserMsg) {
        List<OpenAIClient.Message> messages = new ArrayList<>();
        
        // Add initial context with player profile (condensed)
        String playerContext = String.format(
            "Player: %s (%s, age %d, difficulty %d)", 
            state.getPlayer().getName(), 
            state.getPlayer().getGender(), 
            state.getPlayer().getAge(), 
            state.getPlayer().getDifficulty()
        );
        
        // Only include the last 2-3 exchanges to avoid overloading the API
        List<String> history = state.getMessagesHistory();
        int maxHistoryItems = Math.min(6, history.size()); // Last 3 exchanges (6 messages)
        
        if (maxHistoryItems > 0) {
            // Add player context as initial message
            messages.add(new OpenAIClient.Message("user", playerContext + ". Continue the crisis scenario."));
            
            // Add recent conversation history
            for (int i = Math.max(0, history.size() - maxHistoryItems); i < history.size(); i++) {
                String msg = history.get(i);
                if (msg.startsWith("USER CHOICE: ")) {
                    String choice = msg.substring("USER CHOICE: ".length());
                    messages.add(new OpenAIClient.Message("user", "Player chooses: " + choice));
                } else if (msg.startsWith("ASSISTANT:\n")) {
                    // Only include a summary of the assistant response, not the full text
                    String content = msg.substring("ASSISTANT:\n".length());
                    String summary = content.length() > 200 ? content.substring(0, 200) + "..." : content;
                    messages.add(new OpenAIClient.Message("assistant", summary));
                }
            }
        }
        
        // Add current user message
        messages.add(new OpenAIClient.Message("user", currentUserMsg));
        
        return messages;
    }
}