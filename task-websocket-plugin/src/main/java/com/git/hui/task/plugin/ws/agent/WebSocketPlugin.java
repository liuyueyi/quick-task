package com.git.hui.task.plugin.ws.agent;

import lombok.extern.slf4j.Slf4j;
import okio.ByteString;

import java.util.List;

/**
 * Created by @author yihui in 15:32 18/9/26.
 */
@Slf4j
public abstract class WebSocketPlugin extends BaseAgent {
    public WebSocketPlugin(String agentName) {
        super(agentName);
    }

    @Override
    protected void onOpen() {
        log.info("Open WebSocket: {}", agentName);
        List<String> subs = subscribe();
        subs.forEach(this::sendMessage);
    }

    @Override
    protected void onMessage(String text) {
        String res = onData(text);
        log.info("socket:{} response: {}", agentName, res);
    }

    @Override
    protected void onMessage(ByteString text) {
        String res = onData(uncompress(text.toByteArray()));
        log.info("socket: {} response: {}", agentName, res);
    }

    public void shutdown() {
        this.state = WebSocketState.NOT_CONNECT;
        exactWebSocket.shutdown();
        log.info("socket [{}] shutdown");
    }

    /**
     * 加密算法返回时，通过这种方法进行解压
     *
     * @param bytes
     * @return
     */
    protected abstract String uncompress(byte[] bytes);

    /**
     * 具体的处理返回的结果
     *
     * @param data
     */
    protected abstract String onData(String data);

    /**
     * 发送订阅信息
     */
    protected abstract List<String> subscribe();
}
