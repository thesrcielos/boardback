package com.board.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * WS configurations
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final BBHandler bbHandler;

    public WebSocketConfig(BBHandler bbHandler) {
        this.bbHandler = bbHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(bbHandler, "/bbService")
                .setAllowedOrigins("*");
    }
}
