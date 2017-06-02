package net.felder;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bfelder on 5/31/17.
 */
public class KafkaProducerTest extends CamelTestSupport {
    private static final int MESSAGES_TO_SEND = 100_000_000;
    private static final int MESSAGE_OUTPUT_FREQUENCY = 10_000;

    // @EndpointInject(uri = "mock:result")
    // protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    // protected SomeGuy theGuy;

    @Override
    public boolean isDumpRouteCoverage() {
        return true;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // theGuy = new SomeGuy("Brian", "Felder");
    }

    @Override
    public CamelContext createCamelContext() throws Exception {
        return super.createCamelContext();
    }

    @Test
    public void sendMessages() throws Exception {
        // resultEndpoint.expectedMessageCount(MESSAGES_TO_SEND);

        for (int i = 0; i < MESSAGES_TO_SEND; i++) {
            Date currentDate = new Date();
            template.sendBody("msg #" + i + " " + currentDate.toString());
        }

        // resultEndpoint.assertIsSatisfied();
        // Object theResult = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        // assertEquals(theResult, theGuy.getFirstName());
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

    @Override
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

    public static class SomeGuy implements Serializable {
        private String firstName;
        private String lastName;

        public SomeGuy(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public int hashCode() {
            int toReturn = Objects.hashCode(this.firstName, this.lastName);
            return toReturn;
        }

        @Override
        public boolean equals(Object someOtherGuy) {
            if (someOtherGuy == null ||
                    !this.getClass().isAssignableFrom(someOtherGuy.getClass())) {
                return false;
            }
            SomeGuy toTest = (SomeGuy)someOtherGuy;
            return toTest.hashCode() == this.hashCode();
        }

        @Override
        public String toString() {
            String toReturn = MoreObjects.toStringHelper(this)
                    .add("firstName", firstName)
                    .add("lastName", lastName)
                    .toString();
            return toReturn;
        }
    }
}