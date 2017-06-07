package net.felder;

import java.util.UUID;

/**
 * Created by bfelder on 6/1/17.
 */
public class CamelKafkaConsumerRunner extends CamelConsumerRunnerBase {


    public static void main(String[] args) throws Exception {
        CamelKafkaConsumerRunner example = new CamelKafkaConsumerRunner();
        example.boot();
    }

    @Override
    public String getConsumerUrl() {
        return "kafka://localhost:9092?topic=kafkaFirst"
                + "&groupId=myGroup::" + UUID.randomUUID().toString()
                + "&autoOffsetReset=earliest"
                + "&receiveBufferBytes=65536";
    }
}