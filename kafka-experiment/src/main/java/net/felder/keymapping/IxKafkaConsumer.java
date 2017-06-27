package net.felder.keymapping;

import net.felder.Constants;
import net.felder.keymapping.udsclient.Person;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxKafkaConsumer {
    private static Scanner in;
    private static boolean stop = false;

    public static void main(String[] argv) throws Exception {
        in = new Scanner(System.in);
        String topicName = Constants.KEYMAPPING_INPUT_TOPIC;
        String groupId = UUID.randomUUID().toString();

        ConsumerThread consumerRunnable = new ConsumerThread(topicName, groupId);
        consumerRunnable.start();
        String line = "";
        while (!line.equals("exit")) {
            line = in.next();
        }
        consumerRunnable.getKafkaConsumer().wakeup();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends Thread {
        private String topicName;
        private String groupId;
        private KafkaConsumer<IxRecordKey, Person> kafkaConsumer;
        private DataSinkEmulator dataSink;

        public ConsumerThread(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
        }

        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    "net.felder.keymapping.IxRecordKeyDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "net.felder.keymapping.PersonDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
            configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));
            //Start processing messages
            try {
                ConsumerRecords<IxRecordKey, Person> records = kafkaConsumer.poll(100);
                System.out.println("Polled...");
                if (records != null && !records.isEmpty()) {
                    System.out.println("Got some records!");
                    DataSinkRequest request = new DataSinkRequest();
                    request.setRequestId(UUID.randomUUID());
                    for (ConsumerRecord<IxRecordKey, Person> record : records) {
                        DataSinkRequestItem requestItem = new DataSinkRequestItem();
                        requestItem.setSourceId(record.key().getItemId());
                        requestItem.setData(record.value());
                        request.getItems().add(requestItem);
                    }
                    DataSinkResponse theResponse = dataSink.handleRequest(request);
                    // TODO: Send response OK records back to Kafka. Send to the same topic and partition.
                    // But first, we need to consume from a Stream, rather than directly from the topic.
                    // Otherwise, we will have a loop.
                }
            } catch (WakeupException ex) {
                System.out.println("Exception caught " + ex.getMessage());
            } finally {
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }

        public KafkaConsumer<IxRecordKey, Person> getKafkaConsumer() {
            return this.kafkaConsumer;
        }


    }
}
