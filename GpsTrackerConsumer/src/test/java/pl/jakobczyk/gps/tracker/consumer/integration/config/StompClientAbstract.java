package pl.jakobczyk.gps.tracker.consumer.integration.config;

import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

public abstract class StompClientAbstract {

    private static final String WEBSOCKET_TOPIC = "/topic/gps";

    public volatile BlockingQueue<String> blockingQueue;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public StompClientAbstract(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public void setUpWebsocketConnection(Integer port) throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();

        stompClient.setTaskScheduler(taskScheduler);
        stompClient.setMessageConverter(new StringMessageConverter());
        StompSessionHandler sessionHandler = sessionHandler();
        CompletableFuture<StompSession> future =
                stompClient.connectAsync("ws://localhost:" + port + "/gs-guide-websocket", sessionHandler);
        StompSession session = future.get(1000, TimeUnit.MILLISECONDS);
        session.subscribe(WEBSOCKET_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                countDownLatch.countDown();
                blockingQueue.add((String) payload);
            }
        });
        waitForMessage();
    }

    public void waitForMessage() throws InterruptedException {
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
    }

    protected abstract StompSessionHandlerAdapter sessionHandler();

}
