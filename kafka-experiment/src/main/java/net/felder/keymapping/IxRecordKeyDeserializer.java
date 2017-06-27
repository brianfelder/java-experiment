package net.felder.keymapping;

import net.felder.keymapping.udsclient.Person;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class IxRecordKeyDeserializer extends KafkaJsonDeserializer<IxRecordKey> {
    @Override
    protected Class<IxRecordKey> getType() {
        return IxRecordKey.class;
    }
}
