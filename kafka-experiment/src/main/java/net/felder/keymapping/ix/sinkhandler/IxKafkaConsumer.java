package net.felder.keymapping.ix.sinkhandler;

import com.cvent.extensions.DataSet;
import com.cvent.extensions.Field;
import com.cvent.extensions.Row;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.EntityFieldCache;
import net.felder.keymapping.ix.util.KeyLookupFunctions;
import net.felder.keymapping.sink.SduSkeleton;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
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
        String topicName = Constants.IX_ITEMS_FROM_SOURCE_TOPIC;
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
        private SduSkeleton dataSink;

        public ConsumerThread(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
            dataSink = new SduSkeleton();
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
                        DataSet toSend = new DataSet();
                        toSend.setTotal(new Long(records.count()));
                        int rowNum = 0;
                        for (ConsumerRecord<IxRecordKey, IxRecord> record : records) {
                            IxRecord sourceRecord = record.value();
                            IxRecordKey sourceKey = record.key();
                            // TODO: group / multiplex by JobID
                            System.out.println("  Got: " + sourceKey.toString() + " from topic: " + topicName);
                            IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(sourceKey).get(0);
                            System.out.println("    Adding: " + targetKey.toString() + " to send to dataSink.");
                            if (toSend.getFields() == null) {
                                // TODO: We're cheating, and using the source fields here.
                                // We'll need to translate and use target instead.
                                String sourceSystem = sourceKey.getSystemName();
                                String sourceEntity = sourceKey.getItemType();
                                List<Field> entityFields = EntityFieldCache.of(sourceSystem).getFieldsFor(sourceEntity);
                                toSend.setFields(entityFields);
                            }
                            Row rowValue = sourceRecord.getRow();
                            toSend.getRows().add(rowValue);
                            rowNum++;
                        }
                        String dumpId = UUID.randomUUID().toString();
                        Response theResponse = dataSink.dumpData(Constants.AUTH_KEY,
                                dumpId,
                                toSend);
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
