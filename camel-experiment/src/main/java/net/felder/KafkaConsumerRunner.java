package net.felder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 * Created by bfelder on 6/1/17.
 */
public class KafkaConsumerRunner {

    private Main main;

    public static void main(String[] args) throws Exception {
        KafkaConsumerRunner example = new KafkaConsumerRunner();
        example.boot();
    }

    public void boot() throws Exception {
        // create a Main instance
        main = new Main();
        // add routes
        main.addRouteBuilder(new MyRouteBuilder());
        // add event listener
        main.addMainListener(new Events());
        // run until you terminate the JVM
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    private static class MyRouteBuilder extends RouteBuilder {
        final Processor bodyOutputProcessor = new Processor() {
            public void process(Exchange exchange) throws Exception {
                Object theBody = exchange.getIn().getBody();
                System.out.println("bodyString: " + theBody.toString());
            }
        };

        @Override
        public void configure() throws Exception {
            from("kafka://localhost:9092?topic=kafkaFirst&groupId=myGroup")
                    .split()
                    .body()
                    .process(bodyOutputProcessor)
                    .to("mock:result");
        }
    }

    public static class Events extends MainListenerSupport {

        @Override
        public void afterStart(MainSupport main) {
            System.out.println("KafkaConsumerRunner with Camel is now started!");
        }

        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("KafkaConsumerRunner with Camel is now being stopped!");
        }
    }
}