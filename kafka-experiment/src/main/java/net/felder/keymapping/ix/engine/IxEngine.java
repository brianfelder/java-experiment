package net.felder.keymapping.ix.engine;

import com.google.common.collect.ImmutableMap;
import net.felder.keymapping.ix.config.system.SduConfig;
import net.felder.keymapping.ix.converter.UdsToSduNosrepConverter;
import net.felder.keymapping.ix.model.IxPipelineKey;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.KafkaConsumerThreadBase;
import net.felder.keymapping.ix.util.KafkaProducerHelper;
import net.felder.keymapping.ix.util.KeyLookupFunctions;
import net.felder.keymapping.sink.SduSkeleton;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bfelder on 6/29/17.
 */
public class IxEngine {

    public static void main(String[] argv) throws Exception {
        String sourceTopicName = Constants.IX_ITEMS_FROM_SOURCE_TOPIC;
        String consumerGroupId = UUID.randomUUID().toString();

        IxEngine.ConsumerThread consumerRunnable = new IxEngine.ConsumerThread(sourceTopicName, consumerGroupId);
        consumerRunnable.start();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends KafkaConsumerThreadBase {

        private SduSkeleton dataSink;
        private SduConfig targetConfig;
        private Map<UUID, List<Map.Entry<IxPipelineKey, IxRecord>>> ixRecordsInBatch;

        public ConsumerThread(String sourceTopicName, String consumerGroupId) {
            this.setTopicName(sourceTopicName);
            this.setGroupId(consumerGroupId);
            dataSink = new SduSkeleton();
            ixRecordsInBatch = new HashMap<>();
            targetConfig = new SduConfig();
        }

        @Override
        protected void startRecordBatch(UUID recordBatchId, Long batchCount) {
            List<Map.Entry<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = new ArrayList<>();
            ixRecordsInBatch.put(recordBatchId, ixRecordsForThisBatch);
        }

        @Override
        protected void processRecordForBatch(UUID recordBatchId, ConsumerRecord<IxRecordKey, IxRecord> consumerRecord,
                int rowNum) {
            List<Map.Entry<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = ixRecordsInBatch.get(recordBatchId);
            IxRecord sourceRecord = consumerRecord.value();
            IxRecordKey sourceKey = consumerRecord.key();
            System.out.println("Record: " + sourceKey);
            // TODO: group / multiplex by JobID
            System.out.println("  Got: " + sourceKey.toString() + " from topic: " + this.getTopicName());
            IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(sourceKey).get(0);
            System.out.println("    Adding: " + targetKey.toString() + " to send to dataSink.");
            UdsToSduNosrepConverter converter = new UdsToSduNosrepConverter();
            Map.Entry<IxPipelineKey, IxRecord> convertResult =
                    converter.convert(ImmutableMap.of(sourceKey, sourceRecord));
            ixRecordsForThisBatch.add(convertResult);
            System.out.println("    Processed record: " + targetKey);
        }

        @Override
        protected void finishRecordBatch(UUID recordBatchId) {
            List<Map.Entry<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = ixRecordsInBatch.get(recordBatchId);
            System.out.println("finishRecordBatch");
            this.toKafka(ixRecordsForThisBatch);
        }

        protected void toKafka(List<Map.Entry<IxPipelineKey, IxRecord>> ixRecordsForABatch) {
            try (Producer<IxRecordKey, IxRecord> producer =
                    new KafkaProducer<>(KafkaProducerHelper.getProducerProperties())) {
                for (int i = 0; i < ixRecordsForABatch.size(); i++) {
                    Map.Entry<IxPipelineKey, IxRecord> currentEntry = ixRecordsForABatch.get(i);
                    IxPipelineKey pipelineKey = currentEntry.getKey();
                    IxRecordKey targetKey = pipelineKey.getTargetKey();
                    IxRecord targetRecord = currentEntry.getValue();
                    ProducerRecord<IxRecordKey, IxRecord> theProducerRecord = new ProducerRecord<>(
                            Constants.IX_ITEMS_TO_SINK_TOPIC,
                            targetKey,
                            targetRecord);
                    producer.send(theProducerRecord);
                    System.out.println("Sent: " + targetKey.toString() +
                            " to topic: " + Constants.IX_ITEMS_TO_SINK_TOPIC);
                }
            }
        }
    }
}