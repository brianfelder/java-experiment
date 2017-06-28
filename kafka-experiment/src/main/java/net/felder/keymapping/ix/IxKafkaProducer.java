package net.felder.keymapping.ix;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxKafkaProducer {
    public static void main(String[] args) {
        // Setup the topic splitter, that will listen to ix_global and split messages into
        setupTopicSplitter();
        /*
        try (Producer<IxRecordKey, IxRecord> producer = new KafkaProducer<>(getProducerProperties())) {
            DataSet dataSet = UdsClient.getInstance().fetchResults(0, Constants.UDS_PAGE_SIZE);
            EntityFieldCache fieldCache = EntityFieldCache.of(Constants.SOURCE_SYSTEM_NAME);
            fieldCache.setFieldsFor(Constants.SOURCE_ITEM_TYPE, dataSet.getFields());
            Map<String, Integer> personLookupMap = fieldCache.getFieldLookupMapFor(Constants.SOURCE_ITEM_TYPE);
            for (int i = 0; i < dataSet.getRows().size(); i++) {
                Row currentRow = dataSet.getRows().get(i);
                IxRecord theRecord = new IxRecord(Constants.SOURCE_ITEM_TYPE, currentRow);
                String id = (String) RowHelper.getValueByName(currentRow, "submitter_id", personLookupMap);
                IxRecordKey recordKey = new IxRecordKey(Constants.JOB_ID,
                        Constants.SOURCE_SYSTEM_NAME,
                        Constants.SOURCE_ITEM_TYPE,
                        id);
                ProducerRecord<IxRecordKey, IxRecord> data = new ProducerRecord<>(Constants.IX_GLOBAL_TOPIC,
                        recordKey,
                        theRecord);
                producer.send(data);
                System.out.println("Sent: " + recordKey.toString() + " to topic: " + Constants.IX_GLOBAL_TOPIC);
                // TODO: Derive this through a stream.
                ProducerRecord<IxRecordKey, IxRecord> data2 = new ProducerRecord<>(Constants.IX_FROM_SOURCE_TOPIC,
                        recordKey,
                        theRecord);
                producer.send(data2);
                System.out.println("Sent: " + recordKey.toString() + " to topic: " + Constants.IX_FROM_SOURCE_TOPIC);
            }
        }
        */
    }

    public static void setupTopicSplitter() {
        // TODO: Set up Stream to split out the messages on main topic into two topics:
        // 1) Messages going to the DataSink, and 2) Message acks from the DataSink.
    }

    public static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "net.felder.keymapping.ix.serdes.KafkaJsonSerializer");
        props.put("value.serializer", "net.felder.keymapping.ix.serdes.KafkaJsonSerializer");
        props.put("partitioner.class", "net.felder.keymapping.ix.IxPartitioner");
        return props;
    }

    public static Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount" + UUID.randomUUID());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return props;
    }
}
