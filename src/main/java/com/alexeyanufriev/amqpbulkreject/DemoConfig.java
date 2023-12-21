package com.alexeyanufriev.amqpbulkreject;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfig {

    // this is needed just to observe the behaviour of the channel
    @Bean
    public DemoShutdownListener shutdownListener() {
        return new DemoShutdownListener();
    }

    @Bean(name = "demoRabbitConnectionFactory")
    public SimpleRabbitListenerContainerFactory demoRabbitConnectionFactory(
            CachingConnectionFactory connectionFactory, DemoShutdownListener shutdownListener) {

        // plug custom listener to be able to monitor the state of channels on closing
        connectionFactory.addChannelListener((channel, transactional) ->
                channel.addShutdownListener(shutdownListener));

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setAfterReceivePostProcessors(new DemoPostProcessor());

        return factory;
    }

    @Bean(name = "demoQueue")
    public Queue demoQueue() {
        return QueueBuilder.nonDurable("demo-queue").build();
    }

}
