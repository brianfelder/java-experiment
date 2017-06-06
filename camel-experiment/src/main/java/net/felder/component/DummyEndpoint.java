package net.felder.component;

import net.felder.component.consumer.DummyConsumer;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;

import java.util.concurrent.ExecutorService;

/**
*/
@UriEndpoint(scheme = "dummy",
syntax = "dummy://noop",
consumerOnly = false,
consumerPrefix = "consumer",
label = "api,file",
title = "DummyEndpoint")
public class DummyEndpoint extends DefaultEndpoint {

    public DummyEndpoint() {
    }

    public DummyEndpoint(String uri, DummyComponent component) {
        super(uri, component);
    }


    @Override
    public Producer createProducer() throws Exception {
        return null;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new DummyConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    public ExecutorService createExecutor() {
        return getCamelContext().getExecutorServiceManager().newFixedThreadPool(this, "DummyConsumer[]", 10);
    }
}
