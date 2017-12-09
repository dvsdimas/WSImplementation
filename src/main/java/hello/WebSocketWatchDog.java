package hello;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/*
 * Created by dmylnev on 08.12.17.
 */

@Component
public class WebSocketWatchDog implements Runnable {

    private final static Logger logger = Logger.getLogger(WebSocketWatchDog.class);

    @Resource
    private WebSocketHandler handler;

    @PostConstruct
    public void start() {

        // TODO

        logger.info("WebSocketWatchDog successfully initialized");
    }

    @PreDestroy
    public void stop() {

        // TODO

        logger.info("WebSocketWatchDog successfully stopped");

    }

    public void sessionAlive(final WebSocketSession session){

        logger.debug("sessionAlive [" + session + "]");

        // TODO
    }

    public void sessionKilled(final WebSocketSession session){

        logger.debug("sessionKilled [" + session + "]");

        // TODO
    }

    @Override
    public void run() {

    }
}
