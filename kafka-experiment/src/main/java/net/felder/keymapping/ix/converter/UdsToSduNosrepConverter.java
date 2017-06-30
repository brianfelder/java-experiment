package net.felder.keymapping.ix.converter;

import com.cvent.extensions.Row;
import com.google.common.base.Objects;
import net.felder.keymapping.ix.model.Converter;
import net.felder.keymapping.ix.model.IxPipelineKey;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;
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
    public Map.Entry<IxPipelineKey, IxRecord> convert(Map<IxRecordKey, IxRecord> sourceItems) {
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
                returnIxRecord = this.convertFrom(sourceValue);
            }
        }
        Map.Entry toReturn = this.from(returnPipelineKey, returnIxRecord);
        return toReturn;
    }

    private IxRecord convertFrom(IxRecord sourceRecord) {
        Row newRow = new Row();
        List<Object> rowValues = Arrays.asList(
                sourceRecord.getRow().getValues().get(0),
                sourceRecord.getRow().getValues().get(1),
                sourceRecord.getRow().getValues().get(2),
                sourceRecord.getRow().getValues().get(3)
        );
        newRow.setValues(rowValues);
        IxRecord toReturn = new IxRecord(TARGET_TYPE, newRow);
        return toReturn;
    }

    private Map.Entry<IxPipelineKey, IxRecord> from(final IxPipelineKey pipelineKey, final IxRecord record) {
        // TODO: Blech.
        Map.Entry<IxPipelineKey, IxRecord> toReturn = new Map.Entry<IxPipelineKey, IxRecord>() {
            @Override
            public IxPipelineKey getKey() {
                return pipelineKey;
            }

            @Override
            public IxRecord getValue() {
                return record;
            }

            @Override
            public IxRecord setValue(IxRecord value) {
                return value;
            }

            @Override
            public boolean equals(Object o) {
                return this.hashCode() == o.hashCode();
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(pipelineKey, record);
            }
        };
        return toReturn;
    }
}
