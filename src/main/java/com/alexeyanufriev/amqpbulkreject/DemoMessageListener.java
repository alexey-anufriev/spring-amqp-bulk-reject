package com.alexeyanufriev.amqpbulkreject;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DemoMessageListener {

    @RabbitListener(queues = "demo-queue", containerFactory = "demoRabbitConnectionFactory")
    public void demoListener(Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) final long tag) {
        new Thread(() -> {
            log.info("Received message with tag: {}", tag);

            try {
                log.info("Started processing message with tag: {}", tag);
                Thread.sleep(2000L); // simulate some off-line processing
                log.info("Finished processing message with tag: {}", tag);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                log.info("Accepting message with tag: {}", tag);
                channel.basicAck(tag, false);
                log.info("Accepted message with tag: {}", tag);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

}
