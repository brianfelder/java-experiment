package net.felder;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created by bfelder on 6/22/17.
 */
public class MassProducer {
    private static final int MESSAGE_COUNT = 10_000;

    public static void main(String... args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        KafkaProducer<String, String> producer =
                new KafkaProducer(properties, new StringSerializer(), new StringSerializer());

        for (int i = 0; i < 10000; i++) {
            String key = "attendee-" + Integer.toString(i);
            String value = NameUtils.randomName();
            producer.send(new ProducerRecord<>(Constants.ATTENDEE_INPUT_TOPIC, null, null, key, value));
            System.out.println("Sent tuple. key = " + key + " value = " + value);
        }

        producer.close();
    }

}
