package net.felder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;

/**
 * Created by bfelder on 6/1/17.
 */
public class CamelKafkaProducerRunner {
    private static final int MESSAGES_TO_SEND = 100_000_000;
    private static final int MESSAGE_OUTPUT_FREQUENCY = 10_000;

    public static void main(String[] args) throws Exception {
        CamelKafkaProducerRunner example = new CamelKafkaProducerRunner();
        //example.boot();
    }

    public void boot() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        // ProducerTemplate template = new DefaultProducerTemplate();
    }

    class ArrayListAggregationStrategy implements AggregationStrategy {

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            Object newBody = newExchange.getIn().getBody();
            ArrayList<Object> list = null;
            if (oldExchange == null) {
                list = new ArrayList<Object>();
                list.add(newBody);
                newExchange.getIn().setBody(list);
                return newExchange;
            } else {
                list = oldExchange.getIn().getBody(ArrayList.class);
                list.add(newBody);
                return oldExchange;
            }
        }
    }

    // @Override
    protected RoutesBuilder createRouteBuilder() {

        final Processor bodyOutputProcessor = new Processor() {
            public void process(Exchange exchange) throws Exception {
                Object theBody = exchange.getIn().getBody();
                if (MESSAGES_TO_SEND == 1) {
                    System.out.println("Sending: " + theBody.toString());
                }
            }
        };

        final Processor messagesTimingProcessor = new MessagesTimingProcessor(MESSAGE_OUTPUT_FREQUENCY);

        RoutesBuilder toReturn = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .aggregate(constant(true), new ArrayListAggregationStrategy())
                        .completionSize(1000)
                        .completionTimeout(5000)
                        .to("kafka://localhost:9092?topic=kafkaFirst"
                                // + "&producerBatchSize=200000"
                                // + "&lingerMs=5"
                                // + "&maxInFlightRequest=1000"
                        )
                        .process(bodyOutputProcessor)
                        .process(messagesTimingProcessor)
                // .to("mock:result")
                ;
            }
        };

        return toReturn;
    }


}