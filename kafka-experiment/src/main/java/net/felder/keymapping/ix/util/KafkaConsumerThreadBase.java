package net.felder.keymapping.ix.util;

import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.serdes.IxRecordDeserializer;
import net.felder.keymapping.ix.serdes.IxRecordKeyDeserializer;
import net.felder.keymapping.ix.serdes.IxRecordKeySerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public abstract class KafkaConsumerThreadBase extends Thread {
    private String topicName;
    private String groupId;
    private long sleepTime = 1000;
    private int loopCount = 30;
    private KafkaConsumer<IxRecordKey, IxRecord> kafkaConsumer;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public KafkaConsumer<IxRecordKey, IxRecord> getKafkaConsumer() {
        return this.kafkaConsumer;
    }

    public void run() {
        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<>(this.getKafkaConsumerProperties());
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        //Start processing messages
        try {
            for (int i = 0; i < loopCount; i++) {
                ConsumerRecords<IxRecordKey, IxRecord> records = kafkaConsumer.poll(100);
                System.out.println("Polled...");
                if (records != null && !records.isEmpty()) {
                    int rowNum = 0;
                    UUID recordBatchId = UUID.randomUUID();
                    this.startRecordBatch(recordBatchId, new Long(records.count()));
                    for (ConsumerRecord<IxRecordKey, IxRecord> record : records) {
                        this.processRecordForBatch(recordBatchId, record, rowNum);
                        rowNum++;
                    }
                    this.finishRecordBatch(recordBatchId);
                }
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException ex) {
            System.out.println("Exception caught " + ex.getMessage());
        } catch (WakeupException ex) {
            System.out.println("Exception caught " + ex.getMessage());
        } finally {
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }
    }

    protected Properties getKafkaConsumerProperties() {
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                IxRecordKeyDeserializer.class.getName());
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                IxRecordDeserializer.class.getName());
        configProperties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, IxRecordKeySerde.class.getName());
        configProperties.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, IxRecordKeySerde.class.getName());
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configProperties;
    }

    ////// Implemented by subclasses.
    protected abstract void startRecordBatch(UUID recordBatchId, Long batchCount);
    protected abstract void processRecordForBatch(UUID recordBatchId, ConsumerRecord<IxRecordKey, IxRecord>
            consumerRecord, int
            rowNum);
    protected abstract void finishRecordBatch(UUID recordBatchId);
}
