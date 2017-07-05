package net.felder.keymapping.ix.sourcehandler;

import com.cvent.extensions.DataRequisitionClient;
import com.cvent.extensions.DataSet;
import com.cvent.extensions.Row;
import com.google.common.collect.ImmutableMap;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.util.Constants;
import net.felder.keymapping.ix.util.TypeFieldCache;
import net.felder.keymapping.ix.util.KafkaProducerHelper;
import net.felder.keymapping.ix.util.RowHelper;
import net.felder.keymapping.source.UdsSkeleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxDataSourceHandler {
    private static final Map<String, DataRequisitionClient> DATA_SOURCE_LOOKUP =
            ImmutableMap.of(
                    "Uds", new UdsSkeleton()
            );

    public static void main(String[] args) {
        // Setup the topic splitter, that will listen to ix_global and split messages into
        setupTopicSplitter();
        try (Producer<IxRecordKey, IxRecord> producer = new KafkaProducer<>(KafkaProducerHelper.getProducerProperties())) {
            // TODO: This will need to be dynamically generated via Jobs, but whatever.
            DataRequisitionClient dataSource = DATA_SOURCE_LOOKUP.get(Constants.SOURCE_SYSTEM_NAME);
            DataSet dataSet = dataSource.requestData(Constants.AUTH_KEY,
                    null,
                    null,
                    null);

            // Set up the fields in the fieldCache.
            TypeFieldCache fieldCache = TypeFieldCache.of(Constants.SOURCE_SYSTEM_NAME);
            fieldCache.setFieldsFor(Constants.SOURCE_ITEM_TYPE, dataSet.getFields());
            Map<String, Integer> fieldLookupMap = fieldCache.getFieldLookupMapFor(Constants.SOURCE_ITEM_TYPE);
            for (int i = 0; i < dataSet.getRows().size(); i++) {
                Row currentRow = dataSet.getRows().get(i);
                IxRecord theRecord = new IxRecord(Constants.SOURCE_ITEM_TYPE, currentRow);
                String id = (String) RowHelper.getValueByName(currentRow, "id", fieldLookupMap);
                IxRecordKey recordKey = new IxRecordKey(Constants.JOB_ID,
                        Constants.SOURCE_SYSTEM_NAME,
                        Constants.SOURCE_ITEM_TYPE,
                        id);
                ProducerRecord<IxRecordKey, IxRecord> theProducerRecord = new ProducerRecord<>(
                        Constants.IX_ITEMS_FROM_SOURCE_TOPIC,
                        recordKey,
                        theRecord);
                producer.send(theProducerRecord);
                System.out.println("Sent: " + recordKey.toString() +
                        " to topic: " + Constants.IX_ITEMS_FROM_SOURCE_TOPIC);
            }
        }
    }

    public static void setupTopicSplitter() {
        // TODO: Set up Stream to split out the messages on main topic into two topics:
        // 1) Messages going to the DataSink, and 2) Message acks from the DataSink.
    }

}
