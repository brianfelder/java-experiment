package net.felder.component.kafkasimple;

import net.felder.component.kafkasimple.consumer.KafkaSimpleConsumer;
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
title = "KafkaSimpleEndpoint")
public class KafkaSimpleEndpoint extends DefaultEndpoint {

    public KafkaSimpleEndpoint() {
    }

    public KafkaSimpleEndpoint(String uri, KafkaSimpleComponent component) {
        super(uri, component);
    }


    @Override
    public Producer createProducer() throws Exception {
        return null;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new KafkaSimpleConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public ExecutorService createExecutor() {
        return getCamelContext().getExecutorServiceManager().newFixedThreadPool(this, "KafkaSimpleConsumer[]", 10);
    }
}
