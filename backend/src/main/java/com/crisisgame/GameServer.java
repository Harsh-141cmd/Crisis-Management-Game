package com.crisisgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.crisisgame.model.PlayerProfile;
import com.crisisgame.model.StartResponse;
import com.crisisgame.model.TurnResponse;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class GameServer {

    private static final GameService gameService = new GameService();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // Add CORS support for all endpoints
        server.createContext("/api/game/start", new CORSHandler(new StartGameHandler()));
        server.createContext("/api/game/turn", new CORSHandler(new TurnHandler()));
        
        server.setExecutor(null);
        server.start();
        System.out.println("Crisis Game Server started on port 8081");
    }

    static class CORSHandler implements HttpHandler {
        private final HttpHandler handler;

        public CORSHandler(HttpHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

            // Handle preflight OPTIONS request
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            handler.handle(exchange);
        }
    }

    static class StartGameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Read request body
                    String requestBody = readRequestBody(exchange);
                    
                    // Parse player profile from JSON
                    PlayerProfile player = gson.fromJson(requestBody, PlayerProfile.class);
                    
                    // Start new game
                    StartResponse response = gameService.start(player);
                    
                    // Send response as JSON
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJsonResponse(exchange, 500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    static class TurnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Read request body
                    String requestBody = readRequestBody(exchange);
                    
                    // Parse turn request from JSON
                    @SuppressWarnings("unchecked")
                    Map<String, String> turnRequest = gson.fromJson(requestBody, Map.class);
                    String sessionId = turnRequest.get("sessionId");
                    String choice = turnRequest.get("choice");
                    
                    // Process turn
                    TurnResponse response = gameService.turn(sessionId, choice);
                    
                    // Send response as JSON
                    String jsonResponse = gson.toJson(response);
                    sendJsonResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendJsonResponse(exchange, 500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
