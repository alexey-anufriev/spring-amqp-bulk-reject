package com.alexeyanufriev.amqpbulkreject;

import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.Getter;

public class DemoShutdownListener implements ShutdownListener {

    @Getter
    private String cause;

    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        this.cause = cause.getMessage();
    }

}
