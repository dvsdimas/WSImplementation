package hello;

//import com.cash.server.exception.ClientUnauthorized;
//import com.cash.server.exception.app.*;
//import com.cash.server.exception.user.InvalidUserUuid;
//import com.cash.server.exception.user.UserUuidNotFound;
//import com.cash.server.request.GameBaseRequest;
//import com.cash.server.response.code.ResponseCodes;
//import com.cash.server.service.app.GameBuilder;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import javax.annotation.Resource;
import java.io.IOException;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final static Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

//    @Resource
//    SecureAuthentication secureAuthentication;
//
//    @Resource
//    GameBuilder gameBuilder;

    @Resource
    private WebSocketWatchDog dog;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        String response;
//        logger.debug("received websocket player message [header, message]: " + "[" + session.getHandshakeHeaders().get("player_message") + ", " + message.getPayload() + "]");
//        if (isMessageValid(session, message.getPayload())) {
//            GameBaseRequest playerMessage = new ObjectMapper().readValue(message.getPayload(), GameBaseRequest.class);
//            response = syncPlayerStatus(playerMessage, true);
//
//        } else {
//            response = ResponseCodes.badRequest;
//        }
//        logger.debug("sending websocket message: " + response);
//        session.sendMessage(new TextMessage(response));

        dog.sessionAlive(session); // ADD MY CODE
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        authenticate(session);
        logger.debug("websocket connection established.. player header message: " + session.getHandshakeHeaders().get("player_message"));
        session.sendMessage(new TextMessage("connection established.."));

        dog.sessionAlive(session); // ADD MY CODE
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.debug("close websocket for player: " + session.getHandshakeHeaders().get("player_message"));
//        if (session.getHandshakeHeaders().containsKey("player_message") && session.getHandshakeHeaders().get("player_message") != null) {
//            GameBaseRequest playerMessage = new ObjectMapper().readValue(session.getHandshakeHeaders().get("player_message").get(0), GameBaseRequest.class);
//            syncPlayerStatus(playerMessage, false);
//        }

        dog.sessionKilled(session); // ADD MY CODE
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.debug("handle websocket transport error for player: " + session.getHandshakeHeaders().get("player_message"));
//        if (session.getHandshakeHeaders().containsKey("player_message") && session.getHandshakeHeaders().get("player_message") != null) {
//            GameBaseRequest playerMessage = new ObjectMapper().readValue(session.getHandshakeHeaders().get("player_message").get(0), GameBaseRequest.class);
//            syncPlayerStatus(playerMessage, false);
//        }

        dog.sessionKilled(session); // ADD MY CODE
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.debug("handle websocket pong message for player: " + session.getHandshakeHeaders().get("player_message"));
//        if (session.getHandshakeHeaders().containsKey("player_message") && session.getHandshakeHeaders().get("player_message") != null) {
//            GameBaseRequest playerMessage = new ObjectMapper().readValue(session.getHandshakeHeaders().get("player_message").get(0), GameBaseRequest.class);
//            syncPlayerStatus(playerMessage, true);
//        }

        dog.sessionAlive(session); // ADD MY CODE
    }

    private void authenticate(WebSocketSession session) throws IOException {
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(session.getHandshakeHeaders());
//        try {
//            secureAuthentication.authenticate(httpEntity);
//        } catch (ClientUnauthorized e) {
//            logger.debug("websocket unauthorized client with auth key: " + session.getHandshakeHeaders().get("authorization"));
//            session.sendMessage(new TextMessage(ResponseCodes.unauthorized));
//            throw e;
//        }
    }

//    private String syncPlayerStatus(GameBaseRequest playerMessage, boolean isActive) {
//        String message;
//        try {
//            gameBuilder.syncPlayerStatus(playerMessage, isActive);
//            message = playerMessage.toString();
//        } catch (GameIdBadRequest | InvalidUserUuid | UserUuidNotFound | GameIdNotFound | GameIsNotActive | GameIsNotStartedYet |
//                GameQualifyingQuestionNotFound | GameQualifyingQuestionCurrentlyPlaying | PlayerNotPassedPreviousStage e) {
//            message = e.getResultCode();
//        } catch (Exception e) {
//            message = ResponseCodes.failed;
//        }
//        return message;
//    }
//
//    private boolean isMessageValid(WebSocketSession session, String message) {
//        return session.getHandshakeHeaders().containsKey("player_message") &&
//                session.getHandshakeHeaders().get("player_message") != null &&
//                session.getHandshakeHeaders().get("player_message").get(0).equals(message);
//    }
}
