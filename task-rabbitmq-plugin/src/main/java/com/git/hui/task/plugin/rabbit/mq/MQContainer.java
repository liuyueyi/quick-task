package com.git.hui.task.plugin.rabbit.mq;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yihui in 09:59 18/6/5.
 */
public class MQContainer {

    private Map<String, ConnectionFactory> factoryCache;
    private Map<String, RabbitAdmin> adminCache;
    private Map<String, TopicExchange> exchangeCache;

    public MQContainer() {
        factoryCache = new ConcurrentHashMap<>();
        adminCache = new ConcurrentHashMap<>();
        exchangeCache = new ConcurrentHashMap<>();
    }

    public ConnectionFactory getConnectionFactory(String name) {
        return factoryCache.get(name);
    }

    public void registerFactory(String name, ConnectionFactory factory) {
        factoryCache.put(name, factory);
    }

    public RabbitAdmin getRabbitAdmin(String name, ConnectionFactory factory) {
        RabbitAdmin admin = adminCache.get(name);
        if (admin == null) {
            synchronized (this) {
                if (adminCache.get(name) == null) {
                    admin = new RabbitAdmin(factory);
                    adminCache.put(name, admin);
                }
            }
        }

        return admin;
    }

    public TopicExchange getExchange(String name) {
        TopicExchange exchange = exchangeCache.get(name);
        if (exchange == null) {
            synchronized (this) {
                if (exchangeCache.get(name) == null) {
                    exchange = new TopicExchange(name);
                    exchangeCache.put(name, exchange);
                }
            }
        }
        return exchange;
    }
}
