package com.board.auth;

import com.board.auth.redis.SocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * BBHandler class handles the ws connection and messages
 */
@Component
public class BBHandler extends TextWebSocketHandler {

    private static final Queue<WebSocketSession> sessions = new ConcurrentLinkedQueue<>();
    private static final List<String> messages = new ArrayList<>();
    private final WebSocketAuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BBHandler(WebSocketAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getToken(session);
        if (!authService.isValidToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
            return;
        }

        sessions.add(session);
        for (String msg : messages) {
            session.sendMessage(new TextMessage(msg));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(message.getPayload());
        SocketMessage socketMessage = objectMapper.readValue(message.getPayload(), SocketMessage.class);

        String payloadToSend = message.getPayload();

        switch (socketMessage.getType()) {
            case "CLEAR" -> {
                messages.clear();
                payloadToSend = objectMapper.writeValueAsString(new SocketMessage("CLEAR", "CLEARMessage"));
            }
            case "BOARD" -> messages.add(payloadToSend);
            case "CHAT" -> System.out.println("Chat message: " + socketMessage.getMessage());
            default -> System.out.println("Tipo desconocido: " + socketMessage.getType());
        }

        for (WebSocketSession s : sessions) {
            if (s.isOpen() && !s.equals(session)) {
                s.sendMessage(new TextMessage(payloadToSend));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcast(String msg) {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(msg));
                } catch (IOException e) {

                }
            }
        }
    }
    private String getToken(WebSocketSession session) {
        List<String> tokens = Objects.requireNonNull(session.getUri()).getQuery() != null
                ? Arrays.stream(session.getUri().getQuery().split("&"))
                .filter(p -> p.startsWith("token="))
                .map(p -> p.substring("token=".length()))
                .toList()
                : List.of();
        return tokens.isEmpty() ? null : tokens.get(0);
    }
}