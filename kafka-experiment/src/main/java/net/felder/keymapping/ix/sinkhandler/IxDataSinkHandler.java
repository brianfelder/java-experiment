package net.felder.keymapping.ix.sinkhandler;

import com.cvent.extensions.DataDumpClient;
import com.google.common.collect.ImmutableMap;
import net.felder.keymapping.ix.config.system.SystemConfig;
import net.felder.keymapping.ix.config.system.SystemConfigLookup;
import net.felder.keymapping.ix.model.DataSinkResponse;
import net.felder.keymapping.ix.model.DataSinkResponseItem;
import net.felder.keymapping.ix.model.IxDataSet;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.KafkaConsumerThreadBase;
import net.felder.keymapping.ix.util.KafkaProducerHelper;
import net.felder.keymapping.sink.SduSkeleton;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bfelder on 6/29/17.
 */
public class IxDataSinkHandler {
    private static final Map<String, DataDumpClient> DATA_SINK_LOOKUP =
            ImmutableMap.of(
                    "sdU", new SduSkeleton()
            );

    public static void main(String[] argv) throws Exception {
        String sourceTopicName = Constants.IX_ITEMS_TO_SINK_TOPIC;
        String consumerGroupId = Constants.CONSUMER_GROUP_ID;

        IxDataSinkHandler.ConsumerThread consumerRunnable =
                new IxDataSinkHandler.ConsumerThread(sourceTopicName, consumerGroupId);
        consumerRunnable.start();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends KafkaConsumerThreadBase {

        private Map<UUID, IxDataSet> dataSetForBatch;

        public ConsumerThread(String sourceTopicName, String consumerGroupId) {
            this.setTopicName(sourceTopicName);
            this.setGroupId(consumerGroupId);
            dataSetForBatch = new HashMap<>();
        }

        @Override
        protected void startRecordBatch(UUID recordBatchId, Long batchCount) {
            IxDataSet dataSetForThisBatch = new IxDataSet();
            dataSetForBatch.put(recordBatchId, dataSetForThisBatch);
        }

        @Override
        protected void processRecordForBatch(UUID recordBatchId, ConsumerRecord<IxRecordKey, IxRecord> consumerRecord,
                int rowNum) {
            IxDataSet dataSetForThisBatch = dataSetForBatch.get(recordBatchId);
            IxRecord record = consumerRecord.value();
            IxRecordKey key = consumerRecord.key();
            System.out.println("Record: " + key);
            dataSetForThisBatch.getRows().add(record.getRow());
            dataSetForThisBatch.getRowRecordKeys().add(key);
            // TODO: Probably redundant to set items below more than once.
            SystemConfig targetConfig = SystemConfigLookup.getInstance().configFor(key.getSystemName());
            dataSetForThisBatch.setFields(targetConfig.metadataFor(key.getItemType()).getFields());
            dataSetForThisBatch.setEntityName(key.getSystemName());
            System.out.println("    Processed record: " + key);
        }

        @Override
        protected void finishRecordBatch(UUID recordBatchId) {
            IxDataSet dataSetForThisBatch = dataSetForBatch.get(recordBatchId);
            dataSetForThisBatch.setTotal(Integer.toUnsignedLong(dataSetForThisBatch.getRows().size()));
            System.out.println("finishRecordBatch");
            this.handleAcks(dataSetForThisBatch, recordBatchId);
        }

        protected void handleAcks(IxDataSet dataSet, UUID recordBatchId) {
            DataDumpClient dataDumpClient = DATA_SINK_LOOKUP.get(dataSet.getEntityName());
            Response theResponse = dataDumpClient.dumpData(Constants.AUTH_KEY,
                    recordBatchId.toString(),
                    dataSet);
            DataSinkResponse dsResponse = (DataSinkResponse) theResponse.getEntity();
            try (Producer<IxRecordKey, DataSinkResponseItem.Status> producer =
                    new KafkaProducer<>(KafkaProducerHelper.getProducerProperties())) {
                for (int i = 0; i < dsResponse.itemCount(); i++) {
                    DataSinkResponseItem currentEntry = dsResponse.getItemAtIndex(i);
                    IxRecordKey theKey = currentEntry.getRecordKey();
                    DataSinkResponseItem.Status theStatus = currentEntry.getStatus();
                    ProducerRecord<IxRecordKey, DataSinkResponseItem.Status> theProducerRecord = new ProducerRecord<>(
                            Constants.IX_ACKS_FROM_SINK_TOPIC,
                            theKey,
                            theStatus);
                    producer.send(theProducerRecord);
                    System.out.println("Sent: " + theKey.toString() +
                            " with status: " + theStatus +
                            " to topic: " + Constants.IX_ACKS_FROM_SINK_TOPIC);
                }
            }
        }
    }
}
