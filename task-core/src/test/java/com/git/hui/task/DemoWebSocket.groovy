package com.git.hui.task

import com.alibaba.fastjson.JSONObject
import com.git.hui.task.plugin.ws.impl.BaseWebSocketTask
import com.git.hui.task.plugin.ws.util.EncrypteUtil

/**
 * Created by @author YiHui in 14:56 18/7/2.
 */
class DemoWebSocket extends BaseWebSocketTask {
    @Override
    protected Map<String, String> getConnectHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Language", "zh_CN");
        return header;
    }


    @Override
    protected String uncompress(byte[] bytes) {
        try {
            return EncrypteUtil.gizpDec(bytes);
        } catch (Exception e) {
            println "uncompress error! e:" + e
            return "";
        }
    }

    @Override
    protected String onData(String data) {
        println "receive data: " + data
        return data;
    }

    @Override
    protected List<String> subscribe() {
        Map<String, Object> params = new HashMap<>(2);
        params.put("event", "sub");
        params.put("params", new HashMap<String, Object>(4) {
            private static final long serialVersionUID = -4208702393625298308L;
            {
                put("channel", "market_ethbtc_depth_step0");
                put("cb_id", 123);
                put("asks", 150);
                put("bids", 150);
            }
        });
        return Arrays.asList(JSONObject.toJSONString(params));
    }

    @Override
    String getUrl() {
        return "wss://api.cointiger.pro/exchange-market/ws";
    }
}
