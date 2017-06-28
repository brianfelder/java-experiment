package net.felder.keymapping.ix;

import net.felder.keymapping.ix.model.DataSinkRequest;
import net.felder.keymapping.ix.model.DataSinkResponse;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.sink.DataSinkEmulator;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.KeyLookupFunctions;
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
        String topicName = Constants.IX_FROM_SOURCE_TOPIC;
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
        private KafkaConsumer<IxRecordKey, IxRecord> kafkaConsumer;
        private DataSinkEmulator dataSink;

        public ConsumerThread(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
            dataSink = new DataSinkEmulator();
        }

        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    "net.felder.keymapping.ix.serdes.IxRecordKeyDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "net.felder.keymapping.ix.serdes.IxRecordDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
            configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));
            //Start processing messages
            try {
                for (int i = 0; i < 30; i++) {
                    ConsumerRecords<IxRecordKey, IxRecord> records = kafkaConsumer.poll(100);
                    System.out.println("Polled...");
                    if (records != null && !records.isEmpty()) {
                        System.out.println("Got some records!");
                        DataSinkRequest request = new DataSinkRequest(UUID.randomUUID(), records.count());
                        int rowNum = 0;
                        for (ConsumerRecord<IxRecordKey, IxRecord> record : records) {
                            System.out.println("  Got: " + record.key().toString() + " from topic: " + topicName);
                            IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(record.key()).get(0);
                            System.out.println("    Adding: " + targetKey.toString() + " to send to dataSink.");
                            request.getItemKeys()[rowNum] = targetKey;
                            request.getItems()[rowNum] = record.value();
                            rowNum++;
                        }
                        DataSinkResponse theResponse = dataSink.handleRequest(request);
                        System.out.println("Got a response: " + theResponse);
                        // TODO: Send response OK records back to Kafka. Send to the same topic and partition.
                        // But first, we need to consume from a Stream, rather than directly from the topic.
                        // Otherwise, we will have a loop.
                    }
                    Thread.sleep(1000);
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

        public KafkaConsumer<IxRecordKey, IxRecord> getKafkaConsumer() {
            return this.kafkaConsumer;
        }


    }
}
