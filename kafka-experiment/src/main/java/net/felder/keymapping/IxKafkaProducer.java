package net.felder.keymapping;

import net.felder.keymapping.udsclient.Person;
import net.felder.keymapping.udsclient.UdsClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

import java.util.List;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxKafkaProducer {
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "net.felder.keymapping.KafkaJsonSerializer");

        Producer<String, Person> producer = new KafkaProducer<>(props);

        List<Person> personList = UdsClient.getInstance().fetchPersons();
        for(Person currentPerson : personList) {
            String recordKey = "Job-123XYZ::Uds::person::" + currentPerson.getId();
            ProducerRecord<String, Person> data = new ProducerRecord<>(
                    "kafka_test", recordKey, currentPerson);
            producer.send(data);
        }

        producer.close();

    }
}
