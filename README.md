# Spring AMQP Bulk Reject Demo

## Problem Statement

### Prerequisites

- AMQP Listener in Manual Ack mode that needs 2s to process the message;
- PostProcessor that drop the second message right away;

### Test Flow

- Message 1 is being sent;
- Message 1 is being consumed by the listener;
- Message 1 starts processing (it needs to 2 seconds to complete it, before it is Ack-ed);
- Message 2 is being sent;
- PostProcessor throws `AmqpRejectAndDontRequeueException` to drop Message 2;

### Expected Result

- Message 2 is dropped;
- Message 1 is processed and Ack-ed.

### Actual Result

- After `AmqpRejectAndDontRequeueException` exception Consumer is restarted;
- Delivery tags of the messages are cleared, so no way to ack/nack messages properly;
- Message 1 cannot be Ack-ed anymore due to error: `unknown delivery tag 1`.

## Reproducing Steps

`mvn clean test`
