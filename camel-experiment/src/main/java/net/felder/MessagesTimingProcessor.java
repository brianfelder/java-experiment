package net.felder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by bfelder on 6/2/17.
 */
public class MessagesTimingProcessor implements Processor {
    int messageCount = 0;
    long lastProcessTime = 0;
    int messageOutputFrequency;
    int lastMessageOutput = 0;

    public MessagesTimingProcessor(int messageOutputFrequency) {
        this.messageOutputFrequency = messageOutputFrequency;
    }

    public void process(Exchange exchange) throws Exception {
        if (lastProcessTime == 0) {
            lastProcessTime = System.currentTimeMillis();
        }
        messageCount += this.getMessageCountFor(exchange);
        if (messageCount - lastMessageOutput >= messageOutputFrequency) {
            long timeNow = System.currentTimeMillis();
            long durationSinceLast = timeNow - lastProcessTime;
            Object theBody = exchange.getIn().getBody();
            System.out.println("messageCount: " + messageCount +
                    ". durationSinceLast: " + durationSinceLast +
                    ". bodyString: " + theBody.toString());
            lastProcessTime = timeNow;
            lastMessageOutput = messageCount;
        }
    }

    private int getMessageCountFor(Exchange exchange) {
        Integer toReturn = exchange.getProperty("CamelAggregatedSize", Integer.class);
        if (toReturn == null || toReturn == 0) {
            toReturn = 1;
        }
        return toReturn;
    }
}