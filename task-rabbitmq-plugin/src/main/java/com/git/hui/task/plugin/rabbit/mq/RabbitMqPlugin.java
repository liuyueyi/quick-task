package com.git.hui.task.plugin.rabbit.mq;

import lombok.Data;
import lombok.Getter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.function.Supplier;

/**
 * Created by @author yihui in 16:21 18/7/19.
 */
public class RabbitMqPlugin {
    @Getter
    private static MQContainer mqContainer = new MQContainer();

    public static SimpleMessageListenerContainer registerConsumer(MqConf conf,
            Supplier<ChannelAwareMessageListener> consumer) throws Exception {
        ConnectionFactory fac = conf.getFactory();
        RabbitAdmin rabbitAdmin = mqContainer.getRabbitAdmin(conf.getAdmin(), fac);

        SimpleMessageListenerContainer container =
                RabbitMQContainerFactory.builder().autoAck(conf.getAutoAck()).autoDeleted(conf.getAutoDelete())
                        .durable(conf.getDurable()).mqContainer(mqContainer).exchange(conf.getExchange())
                        .queue(conf.getQueue()).routingKey(conf.getRoutingKey()).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(fac).build().getObject();
        container.setMessageListener(consumer.get());
        container.start();
        return container;
    }

    @Data
    public static class MqConf {
        private ConnectionFactory factory;
        private String admin;
        private String exchange;
        private String queue;
        private String routingKey;
        private Boolean durable = false;
        private Boolean autoAck = false;
        private Boolean autoDelete = true;
    }
}
