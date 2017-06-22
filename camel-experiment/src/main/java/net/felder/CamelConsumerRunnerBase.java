package net.felder;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 * Created by bfelder on 6/6/17.
 */
public abstract class CamelConsumerRunnerBase {
    private static final int MESSAGE_OUTPUT_FREQUENCY = 10_000;

    private Main main;

    public void boot() throws Exception {
        // create a Main instance
        main = new Main();
        // add routes
        main.addRouteBuilder(this.getRouteBuilder());
        // add event listener
        main.addMainListener(this.getMainListener(getClass()));
        // run until you terminate the JVM
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    public RouteBuilder getRouteBuilder() {
        final Processor bodyOutputProcessor = new MessagesTimingProcessor(MESSAGE_OUTPUT_FREQUENCY);
        RouteBuilder toReturn = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(CamelConsumerRunnerBase.this.getConsumerUrl())
                        .process(bodyOutputProcessor);
            }
        };
        return toReturn;
    }

    public MainListenerSupport getMainListener(Class thisClass) {
        MainListenerSupport toReturn = new MainListenerSupport() {
            @Override
            public void afterStart(MainSupport main) {
                System.out.println(thisClass.getSimpleName() + " with Camel is now started!");
            }

            @Override
            public void beforeStop(MainSupport main) {
                System.out.println(thisClass.getSimpleName() + " with Camel is now being stopped!");
            }
        };
        return toReturn;
    }

    public abstract String getConsumerUrl();


}
