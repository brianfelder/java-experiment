package net.felder;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

/**
 * Created by bfelder on 5/31/17.
 */
public class KafkaProducerTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;
    protected SomeGuy theGuy;

    @Override
    public boolean isDumpRouteCoverage() {
        return true;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        theGuy = new SomeGuy("Brian", "Felder");
    }

    @Override
    public CamelContext createCamelContext() throws Exception {
        return super.createCamelContext();
    }

    @Test
    public void sendOneMessage() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        template.sendBody(theGuy.getFirstName());

        resultEndpoint.assertIsSatisfied();
        Object theResult = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals(theResult, theGuy.getFirstName());
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {

        final Processor bodyOutputProcessor = new Processor() {
            public void process(Exchange exchange) throws Exception {
                Object theBody = exchange.getIn().getBody();
                System.out.println("bodyString: " + theBody.toString());
            }
        };


        RoutesBuilder toReturn = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("kafka://localhost:9092?topic=kafkaFirst")
                        .process(bodyOutputProcessor)
                        .to("mock:result");
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