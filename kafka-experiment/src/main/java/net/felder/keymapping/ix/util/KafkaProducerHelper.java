package net.felder.keymapping.ix.util;

import net.felder.keymapping.ix.serdes.KafkaJsonSerializer;

import java.util.Properties;

/**
 * Created by bfelder on 6/29/17.
 */
public final class KafkaProducerHelper {
    private KafkaProducerHelper() {
        super();
    }
    
    public static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", KafkaJsonSerializer.class.getName());
        props.put("value.serializer", KafkaJsonSerializer.class.getName());
        props.put("partitioner.class", IxPartitioner.class.getName());
        return props;
    }
}
