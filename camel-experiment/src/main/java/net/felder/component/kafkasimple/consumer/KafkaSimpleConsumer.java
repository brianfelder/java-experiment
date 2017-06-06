package net.felder.component.kafkasimple.consumer;

import net.felder.component.kafkasimple.KafkaSimpleEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.impl.DefaultConsumer;
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
    private final Processor processor;


    public KafkaSimpleConsumer(KafkaSimpleEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.processor = processor;
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

            // These are the properties of the Camel standard KafkaConsumer.
            configProperties.put("sasl.kerberos.ticket.renew.jitter", "0.05");
            configProperties.put("metrics.num.samples", "2");
            configProperties.put("key.deserializer", "class org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("request.timeout.ms", "40000");
            configProperties.put("value.deserializer", "class org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor");
            configProperties.put("send.buffer.bytes", "131072");
            configProperties.put("sasl.kerberos.kinit.cmd", "/usr/bin/kinit");
            configProperties.put("ssl.enabled.protocols", "TLSv1.2,TLSv1.1,TLSv1");
            configProperties.put("sasl.kerberos.ticket.renew.window.factor", "0.8");
            configProperties.put("metadata.max.age.ms", "300000");
            configProperties.put("ssl.keystore.type", "JKS");
            configProperties.put("enable.auto.commit", "true");
            configProperties.put("ssl.trustmanager.algorithm", "PKIX");
            configProperties.put("ssl.keymanager.algorithm", "SunX509");
            configProperties.put("security.protocol", "PLAINTEXT");
            configProperties.put("sasl.mechanism", "GSSAPI");
            configProperties.put("ssl.protocol", "TLS");
            configProperties.put("group.id", "myGroup::4042708d-db99-47b0-8cf4-d86749333fc5");
            configProperties.put("check.crcs", "true");
            configProperties.put("ssl.truststore.type", "JKS");
            configProperties.put("auto.offset.reset", "earliest");
            configProperties.put("bootstrap.servers", "localhost:9092");
            configProperties.put("session.timeout.ms", "30000");
            configProperties.put("fetch.min.bytes", "1024");
            configProperties.put("heartbeat.interval.ms", "3000");
            configProperties.put("max.partition.fetch.bytes", "1048576");
            configProperties.put("connections.max.idle.ms", "540000");
            configProperties.put("receive.buffer.bytes", "32768");
            configProperties.put("sasl.kerberos.min.time.before.relogin", "60000");
            configProperties.put("fetch.max.wait.ms", "500");
            configProperties.put("retry.backoff.ms", "100");
            configProperties.put("metrics.sample.window.ms", "30000");
            configProperties.put("auto.commit.interval.ms", "5000");
            configProperties.put("reconnect.backoff.ms", "50");



            // This is the original set of properties we used when preformance was better.
            /*
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
            configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            */

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
                            try {
                                KafkaSimpleConsumer.this.processor.process(exchange);
                            } catch (Exception e) {
                                KafkaSimpleConsumer.this.getExceptionHandler().handleException("Error during " +
                                        "processing", exchange, e);
                            }
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
            message.setHeader(KafkaConstants.OFFSET, Long.valueOf(record.offset()));
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
