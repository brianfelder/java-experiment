package net.felder.component.kafkasimple.consumer;

import net.felder.component.kafkasimple.KafkaSimpleEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


public class KafkaSimpleConsumer extends DefaultConsumer {
    private static final transient Logger LOG = LoggerFactory
            .getLogger(KafkaSimpleConsumer.class);
    private static final int CONSUMER_COUNT = 1;

    private static final String TOPIC_NAME = "kafkaFirst";
    private static final String GROUP_NAME = "myGroup::" + UUID.randomUUID().toString();

    protected ExecutorService executor;
    private final List<KafkaSimpleFetchRecords> tasks = new ArrayList<>();

    public KafkaSimpleConsumer(KafkaSimpleEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        LOG.info("Starting consumer");
        super.doStart();

        executor = ((KafkaSimpleEndpoint) getEndpoint()).createExecutor();
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            KafkaSimpleFetchRecords task = new KafkaSimpleFetchRecords(TOPIC_NAME, GROUP_NAME);
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
                tasks.forEach(KafkaSimpleFetchRecords::shutdown);
                executor.shutdownNow();
            }
        }
        tasks.clear();
        executor = null;

        super.doStop();
    }

    class KafkaSimpleFetchRecords implements Runnable {
        private String topicName;
        private String groupId;
        private KafkaConsumer<String, String> kafkaConsumer;

        public KafkaSimpleFetchRecords(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
            configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));

            while (isRunAllowed() && !isStoppingOrStopped() && !isSuspendingOrSuspended()) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                Iterator<TopicPartition> it = records.partitions().iterator();
                while (it.hasNext()) {
                    TopicPartition topicPartition = it.next();
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
                    Iterator<ConsumerRecord<String, String>> partitionLastOffset = partitionRecords.iterator();
                    while (partitionLastOffset.hasNext()) {
                        ConsumerRecord<String, String> record = partitionLastOffset.next();
                        Exchange exchange = this.createKafkaExchange(record);
                        try {
                            KafkaSimpleConsumer.this.getProcessor().process(exchange);
                        } catch (Exception e) {
                            getExceptionHandler().handleException("Error during processing", exchange, e);
                        }

                    }
                }
            }
        }

        public Exchange createKafkaExchange(ConsumerRecord record) {
            Exchange exchange = getEndpoint().createExchange();

            Message message = exchange.getIn();
            message.setHeader(KafkaConstants.PARTITION, record.partition());
            message.setHeader(KafkaConstants.TOPIC, record.topic());
            message.setHeader(KafkaConstants.OFFSET, record.offset());
            if (record.key() != null) {
                message.setHeader(KafkaConstants.KEY, record.key());
            }
            message.setBody(record.value());

            return exchange;
        }

        private void shutdown() {
            try {
                kafkaConsumer.close();
            } catch (Exception e) {
                LOG.warn("Couldn't shut down kafkaConsumer.", e);
            }
        }
    }


}
