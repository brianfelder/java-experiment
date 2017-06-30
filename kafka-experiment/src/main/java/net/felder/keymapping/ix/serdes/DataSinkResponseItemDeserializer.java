package net.felder.keymapping.ix.serdes;

import net.felder.keymapping.ix.model.DataSinkResponseItem;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class DataSinkResponseItemDeserializer extends KafkaJsonDeserializer<DataSinkResponseItem> {
    @Override
    protected Class<DataSinkResponseItem> getType() {
        return DataSinkResponseItem.class;
    }
}
