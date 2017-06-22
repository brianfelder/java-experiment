package net.felder;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by bfelder on 6/22/17.
 */
public class AttendeeSorter {
    private static KafkaStreams streams;

    public static void main(String... args) throws Exception {
        createAndStartStreams();
        consumeFromTopicHowManyRecords(Constants.ATTENDEES_BY_FIRST_NAME_TOPIC, 5);
        closeStreams();
    }

    public static void consumeFromTopicHowManyRecords(String topicName, int recordCount) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties());

        consumer.subscribe(Arrays.asList(topicName));

        int consumedCount = 0;
        while (consumedCount < recordCount) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            consumedCount += records.count();
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received:" + record.key() + " - " + record.value());
            }
        }
        consumer.close();
    }

    public static void createAndStartStreams() {
        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> source = builder.stream(Constants.ATTENDEE_INPUT_TOPIC);
        KTable<String, String> attendeesByFirstNameTable = source
                .map((key, value) -> {
                    KeyValue<String, String> toReturn = new KeyValue<>(NameUtils.firstNameFrom(value), value);
                    return toReturn;
                })
                .groupByKey()
                .reduce((aggValue, newValue) -> aggValue + ":::" + newValue, "attendees-by-first-name");
        KStream<String, String> attendeesByFirstName = attendeesByFirstNameTable.toStream();
        attendeesByFirstName.to(Constants.ATTENDEES_BY_FIRST_NAME_TOPIC);

        streams = new KafkaStreams(builder, streamProperties());
        streams.start();
    }

    public static void closeStreams() {
        streams.close();
    }

    public static Properties streamProperties() {
        Properties toReturn = new Properties();
        toReturn.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-attendees" + UUID.randomUUID());
        toReturn.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        toReturn.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        toReturn.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        toReturn.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        toReturn.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return toReturn;
    }

    public static Properties consumerProperties() {
        Properties toReturn = new Properties();
        toReturn.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer" + UUID.randomUUID());
        toReturn.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        toReturn.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        toReturn.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        toReturn.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return toReturn;
    }

    private static class CountAggregator<K, V, T> implements Aggregator<String, Integer, Integer> {

        @Override
        public Integer apply(String aggregateKey, Integer newValue, Integer aggregateValue) {
            Integer toReturn = aggregateValue + newValue;
            System.out.println(String.format("aggregateKey: %s newValue: %d aggregateValue: %d toReturn: %d",
                    aggregateKey,
                    newValue,
                    aggregateValue,
                    toReturn));
            return toReturn;
        }
    }
}
