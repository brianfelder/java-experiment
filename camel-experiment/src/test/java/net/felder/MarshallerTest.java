package net.felder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 5/24/17.
 */
public class MarshallerTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    protected Object mapObject;
    protected SomeGuy theGuy;

    @Override
    public boolean isDumpRouteCoverage() {
        return true;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mapObject = this.createMap();
        theGuy = new SomeGuy("Brian", "Felder");
    }

    private static Map createMap() {
        List theList = ImmutableList.of("boo", "hoo");
        Map theMap = ImmutableMap.of("theList", theList);
        Map toReturn = ImmutableMap.of("theMap", theMap, "randomString", "Whatup");
        return toReturn;
    }

    @Test
    public void testMapRoundTrip() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        template.sendBody(mapObject);

        resultEndpoint.assertIsSatisfied();
        Object theResult = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals(theResult, mapObject);
        this.validateMap(theResult);
    }

    @Test
    public void testBeanRoundTrip() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        template.sendBody(theGuy);

        resultEndpoint.assertIsSatisfied();
        Object theResult = resultEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals(theResult, theGuy);
    }

    public void validateMap(Object theBodyObject) throws Exception {
        // Get the thing to compare to.
        Map<String, Map<String, List<String>>> typedMapObject = (Map) mapObject;
        String expectedString = typedMapObject.get("theMap").get("theList").get(0);

        // Get the body object.
        Map<String, Map<String, List<String>>> actualBodyMap = (Map) theBodyObject;
        String actualString = actualBodyMap.get("theMap").get("theList").get(0);

        // Compare the two.
        assertEquals(expectedString, actualString);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        final Processor stringTester = new Processor() {

            public void process(Exchange exchange) throws Exception {
                Object theBody = exchange.getIn().getBody();
                byte[] theBodyBytes = (byte[]) theBody;
                String theBodyString = new String(theBodyBytes, "UTF-8");
                System.out.println("theBodyString: " + theBodyString);
            }
        };

        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                        .marshal()
                        .serialization()
                        .process(stringTester)
                        .unmarshal()
                        .serialization()
                        .to("mock:result");
            }
        };
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
    }
}
