package net.felder.keymapping.ix.engine;

import com.google.common.collect.ImmutableMap;
import net.felder.keymapping.ix.config.system.SystemConfig;
import net.felder.keymapping.ix.config.system.SystemConfigLookup;
import net.felder.keymapping.ix.config.typemap.ConverterTypeMap;
import net.felder.keymapping.ix.config.typemap.ConverterTypeMapLookup;
import net.felder.keymapping.ix.model.Converter;
import net.felder.keymapping.ix.model.IxPipelineKey;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.model.Pair;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.KafkaConsumerThreadBase;
import net.felder.keymapping.ix.util.KafkaProducerHelper;
import net.felder.keymapping.ix.util.KeyLookupFunctions;
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
        String consumerGroupId = Constants.CONSUMER_GROUP_ID;

        IxEngine.ConsumerThread consumerRunnable = new IxEngine.ConsumerThread(sourceTopicName, consumerGroupId);
        consumerRunnable.start();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends KafkaConsumerThreadBase {

        private Map<UUID, List<Pair<IxPipelineKey, IxRecord>>> ixRecordsInBatch;

        public ConsumerThread(String sourceTopicName, String consumerGroupId) {
            this.setTopicName(sourceTopicName);
            this.setGroupId(consumerGroupId);
            ixRecordsInBatch = new HashMap<>();
        }

        @Override
        protected void startRecordBatch(UUID recordBatchId, Long batchCount) {
            List<Pair<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = new ArrayList<>();
            ixRecordsInBatch.put(recordBatchId, ixRecordsForThisBatch);
        }

        @Override
        protected void processRecordForBatch(UUID recordBatchId, ConsumerRecord<IxRecordKey, IxRecord> consumerRecord,
                int rowNum) {
            List<Pair<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = ixRecordsInBatch.get(recordBatchId);
            IxRecord sourceRecord = consumerRecord.value();
            IxRecordKey sourceKey = consumerRecord.key();
            System.out.println("Record: " + sourceKey);
            // TODO: group / multiplex by JobID
            System.out.println("  Got: " + sourceKey.toString() + " from topic: " + this.getTopicName());
            IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(sourceKey).get(0);
            System.out.println("    Adding: " + targetKey.toString() + " to send to dataSink.");

            // TODO: We might want to do this lookup once, based on the job.
            ConverterTypeMap converterTypeMap =
                    ConverterTypeMapLookup.getInstance()
                            .converterTypeMapFor(sourceKey.getSystemName(), targetKey.getSystemName());
            String converterClassName = converterTypeMap.converterClassFor(targetKey.getItemType());
            Class converterClass = null;
            Converter converter = null;
            try {
                converterClass = Class.forName(converterClassName);
                converter = (Converter) converterClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            SystemConfig systemConfig = SystemConfigLookup.getInstance().configFor(sourceKey.getSystemName());
            Pair<IxPipelineKey, IxRecord> convertResult =
                    converter.convert(ImmutableMap.of(
                            sourceKey, sourceRecord),
                            ImmutableMap.of(
                                    sourceKey.getItemType(),
                                    systemConfig.metadataFor(sourceKey.getItemType())
                            ));
            ixRecordsForThisBatch.add(convertResult);
            System.out.println("    Processed record: " + targetKey);
        }

        @Override
        protected void finishRecordBatch(UUID recordBatchId) {
            List<Pair<IxPipelineKey, IxRecord>> ixRecordsForThisBatch = ixRecordsInBatch.get(recordBatchId);
            System.out.println("finishRecordBatch");
            this.toKafka(ixRecordsForThisBatch);
        }

        protected void toKafka(List<Pair<IxPipelineKey, IxRecord>> ixRecordsForABatch) {
            try (Producer<IxRecordKey, IxRecord> producer =
                    new KafkaProducer<>(KafkaProducerHelper.getProducerProperties())) {
                for (int i = 0; i < ixRecordsForABatch.size(); i++) {
                    Pair<IxPipelineKey, IxRecord> currentEntry = ixRecordsForABatch.get(i);
                    IxPipelineKey pipelineKey = currentEntry.getItem1();
                    IxRecordKey targetKey = pipelineKey.getTargetKey();
                    IxRecord targetRecord = currentEntry.getItem2();
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