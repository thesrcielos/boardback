package com.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * WebSocket configuration class for enabling WebSocket support in a Spring Boot application.
 * <p>
 * This class registers the {@link ServerEndpointExporter} bean required to detect and initialize
 * WebSocket endpoints annotated with {@code @ServerEndpoint}. It also enables Spring's
 * scheduling support via {@code @EnableScheduling}, which allows the use of scheduled tasks.
 * </p>
 *
 * @author YourName
 */
@Configuration
@EnableScheduling
public class BBConfigurator {

    /**
     * Declares the {@link ServerEndpointExporter} bean.
     * <p>
     * This bean is necessary for deploying WebSocket endpoints using the standard Java EE
     * {@code @ServerEndpoint} annotation in a Spring Boot embedded servlet container (e.g. Tomcat).
     * Without this bean, WebSocket endpoints would not be registered.
     * </p>
     *
     * @return the {@link ServerEndpointExporter} bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
