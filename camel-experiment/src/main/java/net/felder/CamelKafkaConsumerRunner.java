package net.felder;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaEndpoint;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

import java.util.UUID;

/**
 * Created by bfelder on 6/1/17.
 */
public class CamelKafkaConsumerRunner {
    private static final int MESSAGE_OUTPUT_FREQUENCY = 10_000;

    private Main main;

    public static void main(String[] args) throws Exception {
        CamelKafkaConsumerRunner example = new CamelKafkaConsumerRunner();
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
        final Processor bodyOutputProcessor = new MessagesTimingProcessor(MESSAGE_OUTPUT_FREQUENCY);

        @Override
        public void configure() throws Exception {
            KafkaComponent kafkaComponent = (KafkaComponent) getContext().getComponent("kafka");
            KafkaEndpoint endpoint = (KafkaEndpoint) kafkaComponent.createEndpoint(
                    "kafka://localhost:9092?topic=kafkaFirst"
                    + "&groupId=myGroup::" + UUID.randomUUID().toString()
                    + "&autoOffsetReset=earliest"
                    + "&pollTimeoutMs=1000"
            );
            from(endpoint)
                    .process(bodyOutputProcessor);
        }

    }

    public static class Events extends MainListenerSupport {

        @Override
        public void afterStart(MainSupport main) {
            System.out.println("CamelKafkaConsumerRunner with Camel is now started!");
        }

        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("CamelKafkaConsumerRunner with Camel is now being stopped!");
        }
    }


}