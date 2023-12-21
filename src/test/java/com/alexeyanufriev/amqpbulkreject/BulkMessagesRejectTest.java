package com.alexeyanufriev.amqpbulkreject;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
class BulkMessagesRejectTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DemoShutdownListener shutdownListener;

    @Test
    void listenerShouldDropOnlySecondMessage() throws InterruptedException {
        // send a normal message that will be accepted for processing
        this.rabbitTemplate.convertAndSend("demo-queue", "message-1");

        // wait a bit to let message-1 start its processing
        Thread.sleep(500);

        // send a message that will be rejected by post-processor with `AmqpRejectAndDontRequeueException`
        this.rabbitTemplate.convertAndSend("demo-queue", "message-2");

        // wait until the listener is closed and get the error from the channel
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .until(() -> this.shutdownListener.getCause() != null);

        // expect that the channel closed w/o errors
        Assertions.assertThat(this.shutdownListener.getCause())
                .doesNotContain("unknown delivery tag 1");
    }

}
