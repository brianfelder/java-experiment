package net.felder;

import org.apache.camel.Exchange;

/**
 * Created by bfelder on 6/2/17.
 */
public class MessageStatsPrinter {
    int totalMessageCount = 0;
    long lastProcessTimeMillis = 0;
    long firstMessageTimeMillis = 0;
    int messageOutputFrequency;
    int lastMessageOutput = 0;

    public MessageStatsPrinter(int messageOutputFrequency) {
        this.messageOutputFrequency = messageOutputFrequency;
    }

    public void messageIfNeeded(String theMessageBody, int messagesInBatch) {
        if (firstMessageTimeMillis == 0) {
            firstMessageTimeMillis = System.currentTimeMillis();
        }
        if (lastProcessTimeMillis == 0) {
            lastProcessTimeMillis = System.currentTimeMillis();
        }
        totalMessageCount += messagesInBatch;
        if (totalMessageCount - lastMessageOutput >= messageOutputFrequency) {
            long timeNow = System.currentTimeMillis();
            long durationSinceLast = timeNow - lastProcessTimeMillis;
            long totalDuration = timeNow - firstMessageTimeMillis;
            System.out.println("totalMessageCount: " + totalMessageCount +
                    ". totalDuration: " + totalDuration +
                    ". durationSinceLast: " + durationSinceLast +
                    ". bodyString: " + theMessageBody);
            lastProcessTimeMillis = timeNow;
            lastMessageOutput = totalMessageCount;
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
