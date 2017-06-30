package net.felder.keymapping.ix.serdes;

import net.felder.keymapping.ix.model.IxRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by bfelder on 6/28/17.
 */
public class IxRecordSerde implements Serde<IxRecord> {
    private final Serializer<IxRecord> serializer;
    private final Deserializer<IxRecord> deserializer;

    public IxRecordSerde() {
        this.serializer = new KafkaJsonSerializer<>();
        this.deserializer = new IxRecordDeserializer();
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
    public Serializer<IxRecord> serializer() {
        return this.serializer;
    }

    @Override
    public Deserializer<IxRecord> deserializer() {
        return this.deserializer;
    }
}
