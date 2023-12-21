package com.alexeyanufriev.amqpbulkreject;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class DemoPostProcessor implements MessagePostProcessor {

    // for demo purposes we want to drop 2nd message
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        if (new String(message.getBody()).equals("message-2")) {
            throw new AmqpRejectAndDontRequeueException("message-2 rejected", true, null);
        }
        return message;
    }
}
