package com.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
@Component
@ServerEndpoint("/bbService")
public class BBEndpoint {
    private static final Logger logger = Logger.getLogger(BBEndpoint.class.getName());

    static Queue<Session> queue = new ConcurrentLinkedQueue<>();
    static List<String> messages = new ArrayList<>();

    Session ownSession = null;

    public void send(String msg) {
        try {
            if ("CLEAR".equals(msg)) {
                messages.clear();
                msg = "{\"msg\":\"CLEAR\"}";;
            } else {
                messages.add(msg);
            }

            for (Session session : queue) {
                if (!session.equals(this.ownSession)) {
                    session.getBasicRemote().sendText(msg);
                }
                logger.log(Level.INFO, "Sent: {0}", msg);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
    }

    // 🧩 Llega un punto (x, y, color, size, etc.)
    @OnMessage
    public void processPoint(String message, Session session) {
        System.out.println("Point received: " + message + ". From session: " + session);
        this.send(message);
    }

    @OnOpen
    public void openConnection(Session session) {
        queue.add(session);
        ownSession = session;
        logger.log(Level.INFO, "Connection opened: " + session.getId());

        try {
            session.getBasicRemote().sendText("Connection established.");

            for (String msg : messages) {
                session.getBasicRemote().sendText(msg);
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
        logger.log(Level.INFO, "Connection closed: " + session.getId());
    }

    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        logger.log(Level.SEVERE, "Connection error: " + t.toString());
    }
}
