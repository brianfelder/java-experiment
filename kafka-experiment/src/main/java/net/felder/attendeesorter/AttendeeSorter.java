package net.felder.attendeesorter;

import com.google.common.base.Strings;
import net.felder.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
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
        consumeFromTopicHowManyRecords(Constants.ATTENDEES_BY_FIRST_NAME_TOPIC, 10000);
        closeStreams();
    }

    public static void consumeFromTopicHowManyRecords(String topicName, int recordCount) {
        KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(consumerProperties());

        consumer.subscribe(Arrays.asList(topicName));

        int consumedCount = 0;
        while (consumedCount < recordCount) {
            ConsumerRecords<String, Object> records = consumer.poll(1000);
            consumedCount += records.count();
            for (ConsumerRecord<String, Object> record : records) {
                System.out.println("Received:" + record.key() + " - " + record.value());
            }
        }
        consumer.close();
    }

    public static void createAndStartStreams() {
        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> source = builder.stream(Constants.ATTENDEE_INPUT_TOPIC);

        // Concatenate names onto the end of the key.
        /*
        KTable<String, String> attendeesByFirstNameConcatenateTable = source
                .map((key, value) -> {
                    KeyValue<String, String> toReturn = new KeyValue<>(NameUtils.firstNameFrom(value), value);
                    return toReturn;
                })
                .groupByKey()
                .reduce((aggValue, newValue) -> aggValue + ":::" + newValue, "attendees-by-first-name-aggregated");
        KStream<String, String> attendeesByFirstNameConcat = attendeesByFirstNameConcatenateTable.toStream();
        attendeesByFirstNameConcat.to(Constants.ATTENDEES_BY_FIRST_NAME_TOPIC);
        */

        // Value is sum of the instances of the name.
        KTable<String, String> attendeesByFirstNameSumTable = source
                // Make the first name the key.
                .map((key, value) -> {
                    KeyValue<String, String> toReturn = new KeyValue<>(NameUtils.firstNameFrom(value), value);
                    return toReturn;
                })
                // Group and count the result.
                .groupByKey()
                .count("attendees-by-first-name-sum")
                // Convert values to String for processing.
                .mapValues((value) -> Strings.padStart(value.toString(), 4, '0'));
        KStream<String, String> attendeesByFirstNameSum = attendeesByFirstNameSumTable.toStream();
        attendeesByFirstNameSum.to(Constants.ATTENDEES_BY_FIRST_NAME_TOPIC);

        streams = new KafkaStreams(builder, streamCountProperties());
        streams.start();
    }

    public static void closeStreams() {
        streams.close();
    }

    public static Properties streamCountProperties() {
        Properties toReturn = new Properties();
        toReturn.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-attendees" + UUID.randomUUID());
        toReturn.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        toReturn.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // Using Long value serde.
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
}
