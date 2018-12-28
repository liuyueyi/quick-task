package com.git.hui.task.plugin.ws.agent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

/**
 * @author chenlei
 * @date 2018/1/17
 */
@Slf4j
@Getter
public abstract class BaseAgent {
    /**
     * 如果N毫秒内没数据，则重连
     */
    private static final int DEFAULT_IDLE_TIME = 30 * 1000;
    private static final int SLEEP_TIME_IN_RECONNECT = 3 * 1000;
    private static final int WARN_WHEN_RECONNECT_TIME = 20;

    private Request socketRequest;
    public WebSocketState state;
    private int reconnectTime;

    /**
     * 具体选择的websocket
     */
    protected BaseWebSocket exactWebSocket;

    protected String agentName;

    /**
     * 简单粗暴，直接使用上一次收到消息的时间作为心跳时间
     * 因为订阅了很多渠道，所以如果N秒内一条消息都没收到，则直接定义为断开了连接，需要重连
     */
    protected long lastMessageTime;

    BaseAgent(String agentName) {
        this.agentName = agentName;

        this.lastMessageTime = 0;
        this.reconnectTime = 0;
        this.state = WebSocketState.NOT_CONNECT;
        this.exactWebSocket = getExactWebSocket();
    }

    protected BaseWebSocket getExactWebSocket() {
        return new JavaWebSocket();
    }

    /**
     * start to agent
     */
    public void start() {
        // connect
        exactWebSocket.connect();
    }

    private void addHeaders(Request.Builder builder) {
        Map<String, String> header = getConnectHeader();
        if (header == null || header.isEmpty()) {
            return;
        }

        for (String key : header.keySet()) {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(header.get(key))) {
                continue;
            }

            builder.addHeader(key, header.get(key));
        }
    }

    /**
     * 子类根据需要实现该方法添加Header
     */
    protected Map<String, String> getConnectHeader() {
        return Collections.emptyMap();
    }

    /**
     * build request url
     *
     * @return
     */
    public abstract String getUrl();

    /**
     * on open
     */
    protected abstract void onOpen();

    /**
     * on message
     *
     * @param text
     */
    protected abstract void onMessage(String text);

    /**
     * on message
     *
     * @param text
     */
    protected abstract void onMessage(ByteString text);

    /**
     * send message
     *
     * @param message
     */
    protected void sendMessage(String message) {
        exactWebSocket.sendMessage(message);
    }

    @Override
    public String toString() {
        return "BaseAgent{" + "agent='" + agentName + '\'' + '}';
    }


    public interface BaseWebSocket {
        void shutdown();

        void connect();

        void sendMessage(String message);
    }

    public class JavaWebSocket implements BaseWebSocket {
        private WebSocketClient socketClient;

        @Override
        public void shutdown() {
            socketClient.close();
        }

        /**
         * connect to websocket
         */
        @Override
        public void connect() {
            log.info("WebSocket is connecting, agent:{}", agentName);
            state = WebSocketState.CONNECTING;

            String url = getUrl();
            if (StringUtils.isEmpty(url)) {
                state = WebSocketState.NOT_CONNECT;
                return;
            }

            if (socketRequest == null) {
                Request.Builder builder = new Request.Builder().url(url);
                addHeaders(builder);
                socketRequest = builder.build();
            }

            socketClient = new WebSocketClient(URI.create(url), getConnectHeader()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket connected, agent:{}", agentName);
                    lastMessageTime = System.currentTimeMillis();
                    state = WebSocketState.CONNECTED;
                    reconnectTime = 0;

                    BaseAgent.this.onOpen();
                }

                @Override
                public void onMessage(String message) {
                    lastMessageTime = System.currentTimeMillis();
                    try {
                        BaseAgent.this.onMessage(message);
                    } catch (Exception e) {
                        log.error("Got exception, agent:{}, text:{}", agentName, message, e);
                    }
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    lastMessageTime = System.currentTimeMillis();
                    try {
                        BaseAgent.this.onMessage(ByteString.of(bytes.array()));
                    } catch (Exception e) {
                        log.error("Got exception, agent:{}, text:{}", agentName, bytes, e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("WebSocket closed, code:{}, reason:{}, agent:{}", code, reason, agentName);
                }

                @Override
                public void onError(Exception ex) {
                    // 会关闭连接，回收长连接线程，因此需要重新开一个客户端
                    log.warn("WebSocket error! agent: {}, e: {}", agentName, ex);
                }
            };
            socketClient.setConnectionLostTimeout(30);
            socketClient.connect();
        }

        /**
         * send message
         *
         * @param message
         */
        @Override
        public void sendMessage(String message) {
            if (socketClient != null && state.equals(WebSocketState.CONNECTED)) {
                socketClient.send(message);
            }
        }
    }


    public class OkWebSocket implements BaseWebSocket {
        private OkHttpClient httpClient;
        private volatile WebSocket socketClient;

        @Override
        public void shutdown() {
            socketClient.close(0, "close");
        }

        /**
         * connect to websocket
         */
        @Override
        public void connect() {
            log.info("Websocket is connecting, agent:{}", agentName);
            state = WebSocketState.CONNECTING;

            String url = getUrl();
            if (StringUtils.isEmpty(url)) {
                state = WebSocketState.NOT_CONNECT;
                return;
            }

            if (socketRequest == null) {
                Request.Builder builder = new Request.Builder().url(url);
                addHeaders(builder);
                socketRequest = builder.build();
            }

            if (httpClient == null) {
                //取消证书验证
                httpClient = new OkHttpClient.Builder().hostnameVerifier((hostname, session) -> true).build();
            }

            OkWebSocket okWebSocket = this;
            socketClient = httpClient.newWebSocket(socketRequest, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    log.info("Websocket connected, agent:{}", agentName);
                    lastMessageTime = System.currentTimeMillis();
                    state = WebSocketState.CONNECTED;
                    reconnectTime = 0;

                    BaseAgent.this.onOpen();
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    lastMessageTime = System.currentTimeMillis();
                    try {
                        BaseAgent.this.onMessage(text);
                    } catch (Exception e) {
                        log.error("Got exception, agent:{}, text:{}", agentName, text, e);
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    lastMessageTime = System.currentTimeMillis();
                    BaseAgent.this.onMessage(bytes);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    log.warn("WebSocket closed, code:{}, reason:{}, agent:{}", code, reason, agentName);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    log.warn("WebSocket failure, msg:{}, agent:{}, e:{}", response, agentName, t);
                }
            });

        }

        /**
         * send message
         *
         * @param message
         */
        @Override
        public void sendMessage(String message) {
            if (socketClient != null && state.equals(WebSocketState.CONNECTED)) {
                socketClient.send(message);
            }
        }
    }
}
