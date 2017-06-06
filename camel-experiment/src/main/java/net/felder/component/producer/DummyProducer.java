package net.felder.component.producer;


import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * Created by bfelder on 3/31/16.
 * Used AbstractGitHubProducer as a starting point for this class:
 * https://git-wip-us.apache.org/repos/asf?p=camel.git;a=blob
 * ;f=components/camel-github/src/main/java/org/apache/camel/component/github/producer/AbstractGitHubProducer.java
 * ;h=36894c3c6b14b8588d4387618728e5ca9dfcaaf1;hb=refs/heads/camel-2.17.x
 *
 * @param <T> Type parameter.
 */
public class DummyProducer<T> extends DefaultProducer {

    public DummyProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // Process here.
    }


}
