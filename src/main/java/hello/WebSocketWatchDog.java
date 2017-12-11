package hello;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
 * Created by dmylnev on 08.12.17.
 */

@Component
public class WebSocketWatchDog implements Runnable {

    private final static Logger logger = Logger.getLogger(WebSocketWatchDog.class);

    private final static int cores = Runtime.getRuntime().availableProcessors();

    private final static PingMessage ping = new PingMessage();

    private final ConcurrentHashMap<WebSocketSession, Long> sessions = new ConcurrentHashMap<>();

    private final Thread thread = new Thread(this, "WebSocketWatchDogThread");
    private volatile boolean working = false;
    private long prevExecTime = 0;

    @Value("${dog.task.period.sec:10}")
    private int deltaTime;

    @Value("${websocket.timeout.sec:30}")
    private int wsTimeout;

    @Resource
    private WebSocketHandler handler;

    @PostConstruct
    public void start() {

        if( (deltaTime < 1) || (deltaTime > 100) ) {
            logger.warn("Property 'dog.task.period.sec' has unexpected value [" + deltaTime + "]. Will be used default value 10 sec.");
            deltaTime = 10;
        }

        if( (wsTimeout < 1) || (wsTimeout > 300) ) {
            logger.warn("Property 'websocket.timeout.sec' has unexpected value [" + wsTimeout + "]. Will be used default value 30 sec.");
            wsTimeout = 30;
        }

        working = true;

        thread.setDaemon(true);

        thread.start();

        logger.info("WebSocketWatchDog successfully initialized");
    }

    @PreDestroy
    public void stop() {

        working = false;

        try {
            thread.join(10000);
        } catch (InterruptedException e) {
            logger.error("Cannot stop WebSocketWatchDog thread during 10 sec time !!!");
            return;
        }

        logger.info("WebSocketWatchDog successfully stopped");

    }

    public void sessionAlive(final WebSocketSession session){

        if(session == null) throw new IllegalArgumentException("WebSocketSession cannot be null");

        if(logger.isDebugEnabled()) {
            logger.debug("sessionAlive [" + session + "]");
        }

        sessions.put(session, System.currentTimeMillis());
    }

    public void sessionKilled(final WebSocketSession session){

        if(session == null) throw new IllegalArgumentException("WebSocketSession cannot be null");

        if(logger.isDebugEnabled()) {
            logger.debug("sessionKilled [" + session + "]");
        }

        sessions.remove(session);
    }

    @Override
    public void run() {

        logger.info("WebSocketWatchDog thread started");

        while (working) {
            try {

                waitFor(TimeUnit.SECONDS.toMillis(deltaTime));

                if(!working) break;

                checkStaleSessions();

                if(!working) break;

                pingSessions();

            } catch (Throwable th) {
                logger.error("WebSocketWatchDog thread caught ex - " + th.getMessage() + "]", th);
            }
        }

        logger.info("WebSocketWatchDog thread is shutting down");
    }

    private void waitFor(final long ms) throws InterruptedException {

        if(prevExecTime > 0) {
            while ( ((System.currentTimeMillis() - prevExecTime) <= ms) && working ) {
                Thread.sleep(100);
            }
        }

        prevExecTime = System.currentTimeMillis();
    }

    private void invalidateSession(final WebSocketSession session) {

        if(session == null) return;

        sessions.remove(session);

        try {
            session.close(CloseStatus.SESSION_NOT_RELIABLE);
        } catch (IOException e) {
            if(logger.isDebugEnabled()) {
                logger.warn("Session [" + session + "] close error [" + e.getMessage() + "]", e);
            }
        }
    }

    private void checkStaleSessions() {

        final long now = System.currentTimeMillis();

        final Set<WebSocketSession> stale = new HashSet<>();

        for(final Map.Entry<WebSocketSession, Long> kv : sessions.entrySet()) {

            if( (now - kv.getValue() > TimeUnit.SECONDS.toMillis(wsTimeout)) ) {
                stale.add(kv.getKey());

                if(logger.isDebugEnabled()) {
                    logger.debug("Found stale Session [" + kv.getKey() + "]. It will be invalidated.");
                }
            }
        }

        stale.forEach(this::invalidateSession);

        stale.clear();
    }

    private void pingSessions() {

        final Set<WebSocketSession> broken = ConcurrentHashMap.newKeySet();

        sessions.forEachKey(cores, (s) -> {

            try {
                if(s.isOpen()) {
                    s.sendMessage(ping);
                    return;
                }
            } catch (IOException e) {
                if(logger.isDebugEnabled()) {
                    logger.error("Session [" + s + "] ping error. Invalidating session.");
                }
            }

            broken.add(s);
        } );

        broken.forEach(this::invalidateSession);

        broken.clear();
    }

}
