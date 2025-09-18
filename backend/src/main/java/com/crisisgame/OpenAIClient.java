package com.crisisgame;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Client for Google's Gemini API using Gemini 2.5 Pro - the most powerful thinking model
 * with maximum response accuracy and state-of-the-art performance for complex reasoning tasks.
 * Note: Gemini 2.5 Pro comes with thinking on by default for enhanced reasoning capabilities.
 */
public class OpenAIClient {
    private static final String API_KEY = System.getenv("GEMINI_API_KEY") != null ? 
        System.getenv("GEMINI_API_KEY") : 
        "YOUR_API_KEY_HERE"; // Replace with your actual API key or set the GEMINI_API_KEY environment variable
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + API_KEY;

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private final Gson gson = new Gson();

    public static class Message {
        public String role;
        public String content;
        public Message(String role, String content){ this.role=role; this.content=content; }
    }

    public String chat(String model, String systemPrompt, List<Message> messages) throws IOException {
        System.out.println("Making Gemini API call...");
        JsonObject body = new JsonObject();
        
        // Add system instruction (correct Gemini format)
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JsonObject systemInstruction = new JsonObject();
            JsonArray systemParts = new JsonArray();
            JsonObject systemPart = new JsonObject();
            systemPart.addProperty("text", systemPrompt);
            systemParts.add(systemPart);
            systemInstruction.add("parts", systemParts);
            body.add("systemInstruction", systemInstruction);
        }
        
        // Add conversation contents
        JsonArray contents = new JsonArray();
        for (Message m : messages) {
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            part.addProperty("text", m.content);
            parts.add(part);
            content.add("parts", parts);
            
            // Gemini uses "user" and "model" roles
            String geminiRole = m.role.equals("assistant") ? "model" : "user";
            content.addProperty("role", geminiRole);
            contents.add(content);
        }
        body.add("contents", contents);

        System.out.println("Request body: " + body.toString());
        
        Request req = new Request.Builder()
                .url(GEMINI_URL)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

        System.out.println("Sending request to: " + GEMINI_URL);
        
        try (Response res = http.newCall(req).execute()) {
            System.out.println("Response received. Status: " + res.code());
            
            if (!res.isSuccessful()) {
                String errorBody = "No error details";
                ResponseBody resBody = res.body();
                if (resBody != null) {
                    errorBody = resBody.string();
                }
                System.out.println("Error response: " + errorBody);
                throw new IOException("Gemini API error: " + res.code() + " " + res.message() + " - " + errorBody);
            }
            
            String responseBody = "{}";
            ResponseBody resBody = res.body();
            if (resBody != null) {
                responseBody = resBody.string();
            }
            System.out.println("Response body: " + responseBody);
            
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            
            // Parse Gemini response format
            if (json.has("candidates") && json.getAsJsonArray("candidates").size() > 0) {
                JsonObject candidate = json.getAsJsonArray("candidates").get(0).getAsJsonObject();
                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");
                    if (content.has("parts") && content.getAsJsonArray("parts").size() > 0) {
                        JsonObject part = content.getAsJsonArray("parts").get(0).getAsJsonObject();
                        String result = part.get("text").getAsString();
                        System.out.println("Extracted result: " + result.substring(0, Math.min(100, result.length())) + "...");
                        return result;
                    }
                }
            }
            
            throw new IOException("Unexpected Gemini API response format: " + responseBody);
        } catch (Exception e) {
            System.out.println("Exception during API call: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Generate contextual images based on crisis scenario and performance results
     */
    public String generateImage(String description) throws IOException {
        System.out.println("Generating contextual image for: " + description);
        
        String imagePrompt = description.toLowerCase();
        
        // Extract key attributes
        boolean isExcellent = imagePrompt.contains("excellent") || imagePrompt.contains("successful") || imagePrompt.contains("confident") || imagePrompt.contains("triumphant");
        boolean isPoor = imagePrompt.contains("poor") || imagePrompt.contains("failed") || imagePrompt.contains("stressed") || imagePrompt.contains("reflective") || imagePrompt.contains("challenging");
        
        // Determine performance level
        String performanceLevel = "adequate"; // default
        if (isExcellent) performanceLevel = "excellent";
        else if (isPoor) performanceLevel = "poor";
        
        // Determine crisis type
        String crisisType = getCrisisType(imagePrompt);
        
        // Get crisis-management relevant image
        return getCrisisManagementImage(performanceLevel, crisisType, description);
    }
    
    private String getCrisisType(String prompt) {
        if (prompt.contains("tech") || prompt.contains("cyber") || prompt.contains("data") || prompt.contains("quantum") || prompt.contains("digital")) 
            return "technology";
        if (prompt.contains("manufacturing") || prompt.contains("factory") || prompt.contains("safety") || prompt.contains("explosion") || prompt.contains("industrial")) 
            return "manufacturing";
        if (prompt.contains("healthcare") || prompt.contains("medical") || prompt.contains("hospital") || prompt.contains("pharma") || prompt.contains("patient")) 
            return "healthcare";
        if (prompt.contains("food") || prompt.contains("contamination") || prompt.contains("outbreak") || prompt.contains("recall") || prompt.contains("restaurant")) 
            return "food";
        if (prompt.contains("social media") || prompt.contains("platform") || prompt.contains("content") || prompt.contains("algorithm") || prompt.contains("online")) 
            return "social_media";
        if (prompt.contains("financial") || prompt.contains("bank") || prompt.contains("fraud") || prompt.contains("regulatory") || prompt.contains("investment")) 
            return "financial";
        if (prompt.contains("environment") || prompt.contains("pollution") || prompt.contains("toxic") || prompt.contains("cleanup") || prompt.contains("climate")) 
            return "environmental";
        if (prompt.contains("transport") || prompt.contains("airline") || prompt.contains("logistics") || prompt.contains("supply") || prompt.contains("shipping")) 
            return "transportation";
        return "general";
    }
    
    private String getCrisisManagementImage(String performanceLevel, String crisisType, String description) {
        java.util.Random random = new java.util.Random(System.currentTimeMillis() + description.hashCode());
        
        // Crisis Management Excellence - Success scenarios
        String[] excellentCrisisImages = {
            "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=800&h=600&fit=crop&q=80", // Professional boardroom success
            "https://images.unsplash.com/photo-1521791136064-7986c2920216?w=800&h=600&fit=crop&q=80", // Executive team celebration
            "https://images.unsplash.com/photo-1552664730-d307ca884978?w=800&h=600&fit=crop&q=80", // Strategic planning success
            "https://images.unsplash.com/photo-1556761175-4b46a572b786?w=800&h=600&fit=crop&q=80", // Command center coordination
            "https://images.unsplash.com/photo-1554774853-719586f82d77?w=800&h=600&fit=crop&q=80", // Professional presentation
            "https://images.unsplash.com/photo-1600880292203-757bb62b4baf?w=800&h=600&fit=crop&q=80", // Leadership meeting
        };
        
        // Crisis Management - Adequate Performance
        String[] adequateCrisisImages = {
            "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=800&h=600&fit=crop&q=80", // Professional concentration
            "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=800&h=600&fit=crop&q=80", // Team collaboration
            "https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=800&h=600&fit=crop&q=80", // Strategic discussion
            "https://images.unsplash.com/photo-1559136555-9303baea8ebd?w=800&h=600&fit=crop&q=80", // Professional briefing
            "https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800&h=600&fit=crop&q=80", // Crisis coordination
            "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800&h=600&fit=crop&q=80", // Office crisis management
        };
        
        // Crisis Management - Learning from Challenges
        String[] challengingCrisisImages = {
            "https://images.unsplash.com/photo-1551836022-deb4988cc6c0?w=800&h=600&fit=crop&q=80", // Professional under pressure
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&h=600&fit=crop&q=80", // Thoughtful executive
            "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&h=600&fit=crop&q=80", // Learning from setbacks
            "https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=800&h=600&fit=crop&q=80", // Professional development
            "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800&h=600&fit=crop&q=80", // Crisis recovery planning
            "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800&h=600&fit=crop&q=80", // Executive reflection
        };
        
        // Technology Crisis Specific Images
        String[] technologyCrisisImages = {
            "https://images.unsplash.com/photo-1551434678-e076c223a692?w=800&h=600&fit=crop&q=80", // Tech crisis command center
            "https://images.unsplash.com/photo-1581092795360-fd1ca04f0952?w=800&h=600&fit=crop&q=80", // Cybersecurity response
            "https://images.unsplash.com/photo-1517077304055-6e89abbf09b0?w=800&h=600&fit=crop&q=80", // Digital crisis management
            "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800&h=600&fit=crop&q=80", // Tech leadership meeting
            "https://images.unsplash.com/photo-1560472355-536de3962603?w=800&h=600&fit=crop&q=80", // Data breach response
        };
        
        // Healthcare Crisis Images
        String[] healthcareCrisisImages = {
            "https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=800&h=600&fit=crop&q=80", // Healthcare leadership
            "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=800&h=600&fit=crop&q=80", // Medical crisis response
            "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=800&h=600&fit=crop&q=80", // Hospital administration
            "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800&h=600&fit=crop&q=80", // Healthcare teamwork
        };
        
        // Financial Crisis Images  
        String[] financialCrisisImages = {
            "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800&h=600&fit=crop&q=80", // Financial boardroom
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=600&fit=crop&q=80", // Banking crisis response
            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=800&h=600&fit=crop&q=80", // Financial executive
            "https://images.unsplash.com/photo-1553729459-efe14ef6055d?w=800&h=600&fit=crop&q=80", // Financial analysis
        };
        
        // Environmental Crisis Images
        String[] environmentalCrisisImages = {
            "https://images.unsplash.com/photo-1611273426858-450d8e3c9fce?w=800&h=600&fit=crop&q=80", // Environmental response
            "https://images.unsplash.com/photo-1560707303-4e980ce876ad?w=800&h=600&fit=crop&q=80", // Sustainability planning
            "https://images.unsplash.com/photo-1597149198537-a5a2d8d8d4d5?w=800&h=600&fit=crop&q=80", // Environmental leadership
            "https://images.unsplash.com/photo-1569163139394-de44e1d3a715?w=800&h=600&fit=crop&q=80", // Green crisis management
        };
        
        // Selection Logic: Crisis-specific images take priority
        String[] selectedPool = null;
        
        // 60% chance to use crisis-specific images if available
        if (random.nextDouble() < 0.6) {
            switch (crisisType) {
                case "technology": selectedPool = technologyCrisisImages; break;
                case "healthcare": selectedPool = healthcareCrisisImages; break;
                case "financial": selectedPool = financialCrisisImages; break;
                case "environmental": selectedPool = environmentalCrisisImages; break;
            }
        }
        
        // If no crisis-specific image selected, use performance-based images
        if (selectedPool == null) {
            switch (performanceLevel) {
                case "excellent":
                    selectedPool = excellentCrisisImages;
                    break;
                case "poor":
                    selectedPool = challengingCrisisImages;
                    break;
                default:
                    selectedPool = adequateCrisisImages;
                    break;
            }
        }
        
        return selectedPool[random.nextInt(selectedPool.length)];
    }

    // Legacy method for backward compatibility
    public String generateImage(String prompt, String size) throws IOException {
        return generateImage(prompt);
    }
}