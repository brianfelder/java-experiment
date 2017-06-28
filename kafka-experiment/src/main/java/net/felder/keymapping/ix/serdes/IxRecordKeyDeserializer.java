package net.felder.keymapping.ix.serdes;


import net.felder.keymapping.ix.model.IxRecordKey;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class IxRecordKeyDeserializer extends KafkaJsonDeserializer<IxRecordKey> {
    @Override
    protected Class<IxRecordKey> getType() {
        return IxRecordKey.class;
    }
}
