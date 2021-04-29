package com.git.hui.task.plugin.rabbit.mq;

import lombok.Builder;
import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;

/**
 * Created by yihui in 09:38 18/6/5.
 */
@Data
@Builder
public class RabbitMQContainerFactory implements FactoryBean<SimpleMessageListenerContainer> {
    private MQContainer mqContainer;
    private String exchange;

    private String queue;
    private String routingKey;


    private Boolean autoDeleted;
    private Boolean durable;
    private Boolean autoAck;

    private ConnectionFactory connectionFactory;
    private RabbitAdmin rabbitAdmin;
    private Integer concurrentNum;

    private Integer fetchCount;

    private TopicExchange buildExchange() {
        return mqContainer.getExchange(exchange);
    }


    private Queue buildQueue() {
        if (StringUtils.isEmpty(queue)) {
            throw new IllegalArgumentException("queue name should not be null!");
        }

        return new Queue(queue, durable == null ? false : durable, false, autoDeleted == null ? true : autoDeleted);
    }


    private Binding bind(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }


    private void check() {
        if (rabbitAdmin == null || connectionFactory == null) {
            throw new IllegalArgumentException("rabbitAdmin and connectionFactory should not be null!");
        }
    }


    @Override
    public SimpleMessageListenerContainer getObject() throws Exception {
        check();

        Queue queue = buildQueue();
        TopicExchange exchange = buildExchange();
        Binding binding = bind(queue, exchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);


        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setRabbitAdmin(rabbitAdmin);
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queue);
        container.setPrefetchCount(fetchCount == null ? 100 : fetchCount);
        container.setConcurrentConsumers(concurrentNum == null ? 1 : concurrentNum);
        container.setAcknowledgeMode(autoAck ? AcknowledgeMode.NONE : AcknowledgeMode.MANUAL);
        return container;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleMessageListenerContainer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
