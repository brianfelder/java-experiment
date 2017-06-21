package net.felder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by bfelder on 6/2/17.
 */
public class MessagesTimingProcessor implements Processor {
    private MessageStatsPrinter messageStatsPrinter;

    public MessagesTimingProcessor(int messageOutputFrequency) {
        this.messageStatsPrinter = new MessageStatsPrinter(messageOutputFrequency);
    }

    public void process(Exchange exchange) throws Exception {
        String messageBody = exchange.getIn().getBody().toString();
        int messageCountForExchange = this.getMessageCountFor(exchange);
        this.messageStatsPrinter.messageIfNeeded(messageBody, messageCountForExchange);
    }

    private int getMessageCountFor(Exchange exchange) {
        Integer toReturn = exchange.getProperty("CamelAggregatedSize", Integer.class);
        if (toReturn == null || toReturn == 0) {
            toReturn = 1;
        }
        return toReturn;
    }
}
