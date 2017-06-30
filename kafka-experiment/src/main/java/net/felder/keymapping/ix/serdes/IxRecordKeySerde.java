package net.felder.keymapping.ix.serdes;

import net.felder.keymapping.ix.model.IxRecordKey;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by bfelder on 6/28/17.
 */
public class IxRecordKeySerde implements Serde<IxRecordKey> {
    private final Serializer<IxRecordKey> serializer;
    private final Deserializer<IxRecordKey> deserializer;

    public IxRecordKeySerde() {
        this.serializer = new KafkaJsonSerializer<>();
        this.deserializer = new IxRecordKeyDeserializer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.serializer.configure(configs, isKey);
        this.deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        this.serializer.close();
        this.deserializer.close();
    }

    @Override
    public Serializer<IxRecordKey> serializer() {
        return this.serializer;
    }

    @Override
    public Deserializer<IxRecordKey> deserializer() {
        return this.deserializer;
    }
}
