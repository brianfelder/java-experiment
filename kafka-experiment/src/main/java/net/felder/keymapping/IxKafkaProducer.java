package net.felder.keymapping;

import net.felder.Constants;
import net.felder.keymapping.udsclient.Person;
import net.felder.keymapping.udsclient.UdsClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

import java.util.List;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxKafkaProducer {
    public static void main(String[] args) {
        try (Producer<IxRecordKey, Person> producer = new KafkaProducer<>(getProducerProperties())) {
            List<Person> personList = UdsClient.getInstance().fetchPersons();
            for (Person currentPerson : personList) {
                IxRecordKey recordKey = new IxRecordKey(Constants.KEYMAPPING_JOB_ID,
                        Constants.KEYMAPPING_SYSTEM_NAME,
                        Constants.KEYMAPPING_PERSON_TYPE,
                        currentPerson.getId());
                ProducerRecord<IxRecordKey, Person> data = new ProducerRecord<>(Constants.KEYMAPPING_INPUT_TOPIC,
                        recordKey,
                        currentPerson);
                producer.send(data);
            }
        }
        // TODO: Set up Stream to split out the messages on main topic into two topics:
        // 1) Messages going to the DataSink, and 2) Message acks from the DataSink.
    }

    public static void setupTopicSplitter() {

    }

    public static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "net.felder.keymapping.KafkaJsonSerializer");
        props.put("value.serializer", "net.felder.keymapping.KafkaJsonSerializer");
        props.put("partitioner.class", "net.felder.keymapping.IxPartitioner");
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
