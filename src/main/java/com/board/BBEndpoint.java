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

/**
 * WebSocket endpoint for handling real-time drawing messages.
 * <p>
 * This class manages connections to the "/bbService" WebSocket endpoint,
 * handles broadcasting of drawing events (points, clear actions, etc.),
 * and maintains a shared message history for new clients.
 * </p>
 *
 * <p>
 * It uses {@code @ServerEndpoint} to define the WebSocket route,
 * and maintains a static queue of sessions and a shared message list.
 * </p>
 *
 * Example message format:
 * <pre>
 *   {"x":123,"y":456,"color":"#FF0000","size":20}
 * </pre>
 *
 * Special command:
 * <pre>
 *   {"msg":"CLEAR"}
 * </pre>
 *
 * @author You
 */
@Component
@ServerEndpoint("/bbService")
public class BBEndpoint {

    /** Logger for server activity and errors */
    private static final Logger logger = Logger.getLogger(BBEndpoint.class.getName());

    /** Thread-safe queue of connected WebSocket sessions */
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    /** Shared list of all messages (drawing events), used to replay history to new clients */
    static List<String> messages = new ArrayList<>();

    /** The current session of this specific endpoint instance */
    Session ownSession = null;

    /**
     * Broadcasts a message to all connected clients, except the sender.
     * If the message is a "CLEAR" command, clears the shared message history.
     *
     * @param msg the message to send (JSON string)
     */
    public void send(String msg) {
        try {
            if ("CLEAR".equals(msg)) {
                messages.clear();
                msg = "{\"msg\":\"CLEAR\"}";
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

    /**
     * Handles incoming messages from clients (e.g. drawing points).
     * Broadcasts the message to all other clients.
     *
     * @param message the JSON message from the client
     * @param session the session that sent the message
     */
    @OnMessage
    public void processPoint(String message, Session session) {
        System.out.println("Point received: " + message + ". From session: " + session);
        this.send(message);
    }

    /**
     * Handles new WebSocket connections.
     * Adds the session to the queue and replays the entire drawing history.
     *
     * @param session the new client session
     */
    @OnOpen
    public void openConnection(Session session) {
        queue.add(session);
        ownSession = session;
        logger.log(Level.INFO, "Connection opened: " + session.getId());

        try {
            session.getBasicRemote().sendText("Connection established.");

            // Send drawing history to the new client
            for (String msg : messages) {
                session.getBasicRemote().sendText(msg);
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles session closure and removes the session from the active queue.
     *
     * @param session the closed session
     */
    @OnClose
    public void closedConnection(Session session) {
        queue.remove(session);
        logger.log(Level.INFO, "Connection closed: " + session.getId());
    }

    /**
     * Handles WebSocket errors, logs them, and removes the session from the queue.
     *
     * @param session the session that experienced an error
     * @param t the error/exception thrown
     */
    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        logger.log(Level.SEVERE, "Connection error: " + t.toString());
    }
}
