package net.felder.keymapping;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxPartitioner implements Partitioner {
    private final AtomicInteger counter = new AtomicInteger((new Random()).nextInt());

    public IxPartitioner() {
    }

    public void configure(Map<String, ?> configs) {
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (key != null && IxRecordKey.class.isAssignableFrom(key.getClass())) {
            IxRecordKey ixRecordKey = (IxRecordKey) key;
            return Utils.toPositive(ixRecordKey.partitionHash()) % numPartitions;
        } else {
            int nextValue = this.counter.getAndIncrement();
            List availablePartitions = cluster.availablePartitionsForTopic(topic);
            if(availablePartitions.size() > 0) {
                int part = Utils.toPositive(nextValue) % availablePartitions.size();
                return ((PartitionInfo)availablePartitions.get(part)).partition();
            } else {
                return Utils.toPositive(nextValue) % numPartitions;
            }
        }
    }

    public void close() {
    }
}
