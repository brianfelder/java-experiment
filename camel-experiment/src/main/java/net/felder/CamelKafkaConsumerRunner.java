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
        // NOTE: the receiveBufferBytes parameter defaulted to 32768 in the Camel component.
        // That particular parameter caused David's and Cezar's machine to slow down on some reads,
        // taking like 5 seconds rather than 100ms. Cezar found that increasing that number
        // (here, to 64k, which is the default for Kafka) mitigated that behavior.
        return "kafka://localhost:9092?topic=kafkaFirst"
                + "&groupId=myGroup::" + UUID.randomUUID().toString()
                + "&autoOffsetReset=earliest"
                + "&pollTimeoutMs=1000"
                + "&receiveBufferBytes=65536";
    }
}