package net.felder.component.consumer;

import net.felder.component.DummyEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class DummyConsumer extends DefaultConsumer {
    private static final transient Logger LOG = LoggerFactory
            .getLogger(DummyConsumer.class);
    private static final int CONSUMER_COUNT = 1;

    protected ExecutorService executor;
    private final List<DummyConsumer.DummyFetchRecords> tasks = new ArrayList<>();

    public DummyConsumer(DummyEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        LOG.info("Starting consumer");
        super.doStart();

        executor = ((DummyEndpoint) getEndpoint()).createExecutor();
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            DummyFetchRecords task = new DummyFetchRecords();
            executor.submit(task);
            tasks.add(task);
        }
    }

    @Override
    protected void doStop() throws Exception {
        LOG.info("Stopping consumer");

        if (executor != null) {
            if (getEndpoint() != null && getEndpoint().getCamelContext() != null) {
                getEndpoint().getCamelContext().getExecutorServiceManager().shutdownGraceful(executor);
            } else {
                executor.shutdownNow();
            }
            if (!executor.isTerminated()) {
                tasks.forEach(DummyFetchRecords::shutdown);
                executor.shutdownNow();
            }
        }
        tasks.clear();
        executor = null;

        super.doStop();
    }

    class DummyFetchRecords implements Runnable {

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            while (isRunAllowed() && !isStoppingOrStopped() && !isSuspendingOrSuspended()) {
                Exchange exchange = this.createDummyExchange();
                try {
                    DummyConsumer.this.getProcessor().process(exchange);
                } catch (Exception e) {
                    getExceptionHandler().handleException("Error during processing", exchange, e);
                }
            }
        }

        public Exchange createDummyExchange() {
            Exchange exchange = getEndpoint().createExchange();
            Message message = exchange.getIn();
            message.setHeader("kafka.PARTITION", "a partition");
            message.setHeader("kafka.TOPIC", "a topic");
            message.setHeader("kafka.OFFSET", 123L);
            message.setBody("Dummy - " + System.currentTimeMillis());
            return exchange;
        }

        private void shutdown() {
            // We would do what we need to cleanup here.
        }
    }


}
