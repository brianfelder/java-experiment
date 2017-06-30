package net.felder.keymapping.ix.serdes;

import net.felder.keymapping.ix.model.IxRecord;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class IxRecordDeserializer extends KafkaJsonDeserializer<IxRecord> {
    @Override
    protected Class<IxRecord> getType() {
        return IxRecord.class;
    }
}
