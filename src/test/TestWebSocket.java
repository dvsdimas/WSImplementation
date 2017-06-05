import hello.Application;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = Application.class)
public class TestWebSocket
{
    private static final String WEBSOCKET_URI = "ws://localhost:8080/name";

    private static final String TEST_MESSAGE = "This is WebSocket test message";

    private final BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>();

    private WebSocketClient webSocketClient = null;

    @Before
    public void setup() throws URISyntaxException, InterruptedException {
        webSocketClient = new WebSocketClient(new URI(WEBSOCKET_URI)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {
                System.out.print(s);

                blockingQueue.add(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {


            }
        };

        webSocketClient.connect();

        while (!webSocketClient.isOpen()) {
            Thread.sleep(1000);
        }

        System.out.println("Connected");
    }

    @Test
    public void clientConnected_messageReceived() throws Exception
    {
        webSocketClient.send(TEST_MESSAGE);

        final String received = blockingQueue.poll(1, TimeUnit.SECONDS);

        Assert.assertNotNull(received);
        Assert.assertTrue(TEST_MESSAGE.equals(received));
    }
}