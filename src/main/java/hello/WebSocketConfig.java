package hello;

/*
 * Created by dmylnev on 05.06.2017.
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private WebSocketHandler webSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/name").setAllowedOrigins("*");
    }

}
