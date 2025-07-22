package com.board;

import com.board.auth.BBHandler;
import com.board.auth.WebSocketAuthService;
import com.board.auth.redis.SocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BBHandlerTest {
    @Mock
    private WebSocketAuthService authService;
    @Mock
    private WebSocketSession session;
    private BBHandler bbHandler;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bbHandler = new BBHandler(authService);
    }

    @Test
    void afterConnectionEstablished_invalidToken_closesSession() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=badtoken"));
        when(authService.isValidToken("badtoken")).thenReturn(false);
        doNothing().when(session).close(any(CloseStatus.class));
        bbHandler.afterConnectionEstablished(session);
        verify(session).close(any(CloseStatus.class));
    }

    @Test
    void afterConnectionEstablished_validToken_addsSession() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        when(authService.isValidToken("goodtoken")).thenReturn(true);
        doNothing().when(session).sendMessage(any(TextMessage.class));
        bbHandler.afterConnectionEstablished(session);
        // No exception = success
    }

    @Test
    void afterConnectionEstablished_validToken_addsSession_sendMsgs() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        when(authService.isValidToken("goodtoken")).thenReturn(true);
        doNothing().when(session).sendMessage(any(TextMessage.class));
        bbHandler.afterConnectionEstablished(session);
        SocketMessage msg = new SocketMessage("CLEAR", "clear");
        String payload = objectMapper.writeValueAsString(msg);
        bbHandler.handleTextMessage(session, new TextMessage(payload));
        bbHandler.afterConnectionEstablished(session);
        // No exception = success
    }

    @Test
    void handleTextMessage_clearType_clearsMessages() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        SocketMessage msg = new SocketMessage("CLEAR", "clear");
        String payload = objectMapper.writeValueAsString(msg);
        bbHandler.handleTextMessage(session, new TextMessage(payload));
        // No exception = success
    }

    @Test
    void handleTextMessage_boardType() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        SocketMessage msg = new SocketMessage("BOARD", "{board}");
        String payload = objectMapper.writeValueAsString(msg);
        bbHandler.handleTextMessage(session, new TextMessage(payload));
        // No exception = success
    }

    @Test
    void handleTextMessage_chatType() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        SocketMessage msg = new SocketMessage("CHAT", "{hello}");
        String payload = objectMapper.writeValueAsString(msg);
        bbHandler.handleTextMessage(session, new TextMessage(payload));
        // No exception = success
    }

    @Test
    void handleTextMessage_nondefineType() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        SocketMessage msg = new SocketMessage("NOT_KNOWN", "{hello}");
        String payload = objectMapper.writeValueAsString(msg);
        bbHandler.handleTextMessage(session, new TextMessage(payload));
        // No exception = success
    }

    @Test
    void handleCloseConn() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        doNothing().when(session).close(any(CloseStatus.class));
        bbHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
    }

    @Test
    void handleBroadcastMessage() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        when(session.isOpen()).thenReturn(true);
        bbHandler.broadcast("MSG");
    }

    @Test
    void handleBroadcastMessageClosedConn() throws Exception {
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/bbService?token=goodtoken"));
        when(session.isOpen()).thenReturn(false);
        bbHandler.broadcast("MSG");
    }


} 