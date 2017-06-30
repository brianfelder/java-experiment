package net.felder.keymapping.ix.converter;

import com.cvent.extensions.Row;
import net.felder.keymapping.ix.config.system.EntityMetadata;
import net.felder.keymapping.ix.model.Converter;
import net.felder.keymapping.ix.model.IxPipelineKey;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
import net.felder.keymapping.ix.model.Pair;
import net.felder.keymapping.ix.util.KeyLookupFunctions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 6/29/17.
 */
public class UdsToSduNosrepConverter implements Converter {
    /**
     * Person spelled backwards!
     */
    private String TARGET_TYPE = "nosrep";

    @Override
    public Pair<IxPipelineKey, IxRecord> convert(
            Map<IxRecordKey, IxRecord> sourceItems,
            Map<String, EntityMetadata> sourceMetadataMap) {
        IxPipelineKey returnPipelineKey = new IxPipelineKey();
        returnPipelineKey.setConverterClassName(this.getClass().getName());
        IxRecord returnIxRecord = null;
        for (Map.Entry<IxRecordKey, IxRecord> entry : sourceItems.entrySet()) {
            IxRecordKey sourceKey = entry.getKey();
            IxRecord sourceValue = entry.getValue();
            returnPipelineKey.getSourceKeys().add(sourceKey);
            IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(sourceKey).get(0);
            if (TARGET_TYPE.equals(targetKey.getItemType())) {
                returnPipelineKey.setTargetKey(targetKey);
                returnIxRecord = this.convertFrom(sourceKey, sourceValue);
            }
        }
        // TODO: if returnIxRecord is null, return null;
        Pair<IxPipelineKey, IxRecord> toReturn = new Pair<>(returnPipelineKey, returnIxRecord);
        return toReturn;
    }

    private IxRecord convertFrom(IxRecordKey sourceKey, IxRecord sourceRecord) {
        IxRecordKey targetKey = KeyLookupFunctions.targetKeysFor(sourceKey).get(0);
        Row newRow = new Row();
        List<Object> rowValues = Arrays.asList(
                targetKey.getItemId(),
                sourceRecord.getRow().getValues().get(1),
                sourceRecord.getRow().getValues().get(2),
                sourceRecord.getRow().getValues().get(3)
        );
        newRow.setValues(rowValues);
        IxRecord toReturn = new IxRecord(TARGET_TYPE, newRow);
        return toReturn;
    }
}
