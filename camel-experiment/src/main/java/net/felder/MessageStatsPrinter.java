package net.felder;

import org.apache.camel.Exchange;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by bfelder on 6/2/17.
 */
public class MessageStatsPrinter {
    private AtomicLong totalMessageCount = new AtomicLong();
    private AtomicLong lastProcessTimeMillis = new AtomicLong();
    private AtomicLong firstMessageTimeMillis = new AtomicLong();
    private final int messageOutputFrequency;
    private AtomicLong lastMessageOutput = new AtomicLong();

    public MessageStatsPrinter(int messageOutputFrequency) {
        this.messageOutputFrequency = messageOutputFrequency;
    }

    public void messageIfNeeded(String theMessageBody, int messagesInBatch) {
        if (firstMessageTimeMillis.longValue() == 0) {
            firstMessageTimeMillis.set(System.currentTimeMillis());
        }
        if (lastProcessTimeMillis.longValue() == 0) {
            lastProcessTimeMillis.set(System.currentTimeMillis());
        }
        totalMessageCount.set(totalMessageCount.intValue() + messagesInBatch);
        if (totalMessageCount.longValue() - lastMessageOutput.longValue() >= messageOutputFrequency) {
            long timeNow = System.currentTimeMillis();
            long durationSinceLast = timeNow - lastProcessTimeMillis.longValue();
            long totalDuration = timeNow - firstMessageTimeMillis.longValue();
            System.out.println("totalMessageCount: " + totalMessageCount +
                    ". totalDuration: " + totalDuration +
                    ". durationSinceLast: " + durationSinceLast +
                    ". bodyString: " + theMessageBody);
            lastProcessTimeMillis.set(timeNow);
            lastMessageOutput.set(totalMessageCount.longValue());
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
