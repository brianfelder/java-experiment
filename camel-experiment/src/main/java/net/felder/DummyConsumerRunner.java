package net.felder;

import net.felder.component.DummyComponent;
import net.felder.component.DummyEndpoint;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 * Created by bfelder on 6/1/17.
 */
public class DummyConsumerRunner {
    private static final int MESSAGE_OUTPUT_FREQUENCY = 10_000;

    private Main main;

    public static void main(String[] args) throws Exception {
        DummyConsumerRunner example = new DummyConsumerRunner();
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
            DummyComponent dummyComponent = (DummyComponent) getContext().getComponent("dummy");
            DummyEndpoint endpoint = (DummyEndpoint) dummyComponent.createEndpoint("dummy://noop");
            from(endpoint)
                    .process(bodyOutputProcessor);
        }

    }

    public static class Events extends MainListenerSupport {

        @Override
        public void afterStart(MainSupport main) {
            System.out.println("DummyConsumerRunner with Camel is now started!");
        }

        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("DummyConsumerRunner with Camel is now being stopped!");
        }
    }


}