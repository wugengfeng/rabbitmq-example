package com.wgf.producer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 库存交换机和队列配置
 */
@Configuration
public class StockConfig {
    @Bean
    public DirectExchange stockExchange(@Value("${mq.stock.exchange}") String exchange) {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue deductionQueue(@Value("${mq.stock.deduction.queue}") String deductionQueue) {
        return new Queue(deductionQueue);
    }

    @Bean
    public Binding deductionQueueBinding(DirectExchange stockExchange,
                                         Queue deductionQueue,
                                         @Value("${mq.stock.deduction.routing-key}") String routingKey) {
        return BindingBuilder.bind(deductionQueue).to(stockExchange).with(routingKey);
    }
}
