package com.git.hui.task.plugin.ws.impl;

import com.git.hui.task.api.BaseTask;
import com.git.hui.task.plugin.ws.agent.BaseAgent;
import com.git.hui.task.plugin.ws.agent.WebSocketPlugin;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author yihui in 18:41 18/9/28.
 */
@Slf4j
public abstract class BaseWebSocketTask extends BaseTask {
    private WebSocketPlugin webSocketPlugin;

    @Override
    public void process() {
        webSocketPlugin = new WebSocketPlugin(name()) {

            @Override
            protected Map<String, String> getConnectHeader() {
                return BaseWebSocketTask.this.getConnectHeader();
            }

            @Override
            protected String uncompress(byte[] bytes) {
                return BaseWebSocketTask.this.uncompress(bytes);
            }

            @Override
            protected String onData(String data) {
                return BaseWebSocketTask.this.onData(data);
            }

            @Override
            protected List<String> subscribe() {
                return BaseWebSocketTask.this.subscribe();
            }

            @Override
            public String getUrl() {
                return BaseWebSocketTask.this.getUrl();
            }

            @Override
            protected BaseWebSocket getExactWebSocket() {
                return BaseWebSocketTask.this.getExactWebSocket(this);
            }
        };
        webSocketPlugin.start();
        registerShutdownThread();
    }

    @Override
    public void interrupt() {
        webSocketPlugin.shutdown();
        log.info("{} webSocket shutdown!", name());
    }


    /**
     * 因为我们系统的设计目标是用于后端的进行接口验证、数据订正，执行定时任务或校验脚本
     * 所以使用WebSocket任务更适用于测试后端的WebSocket服务，因此这个任务本身不应该长时间运行，这里默认五分钟后关闭长连接
     */
    protected void registerShutdownThread() {
        Executors.newScheduledThreadPool(1).schedule(() -> {
            webSocketPlugin.shutdown();
            log.info("Shutdown WebSocket: {} after run 5 minutes", webSocketPlugin.getAgentName());
        }, 5, TimeUnit.MINUTES);
    }

    /**
     * 通过覆盖实现，来选择具体的WebSocket框架，目前支持OkHttp和Java-WebSocket两种使用姿势
     *
     * @param webSocketPlugin
     * @return
     */
    protected BaseAgent.BaseWebSocket getExactWebSocket(WebSocketPlugin webSocketPlugin) {
        return webSocketPlugin.new JavaWebSocket();
    }

    /**
     * 添加WebSocket的请求头
     *
     * @return
     */
    protected Map<String, String> getConnectHeader() {
        return Collections.emptyMap();
    }

    /**
     * 如果返回的数据，是利用压缩算法进行处理过的，则需要在这里对二进制数据进行解压
     *
     * @param bytes
     * @return
     */
    protected String uncompress(byte[] bytes) {
        return new String(bytes);
    }

    /**
     * 实现具体的接收到数据后，进行处理的逻辑
     *
     * @param data
     * @return
     */
    protected abstract String onData(String data);

    /**
     * 在连接服务器WebSocket之后，向服务端发送的订阅数据
     *
     * @return 返回一个数组，会迭代数组，将每一条记录推送给服务端
     */
    protected abstract List<String> subscribe();

    /**
     * 返回提供WebSocket服务的url地址
     *
     * @return
     */
    public abstract String getUrl();
}
