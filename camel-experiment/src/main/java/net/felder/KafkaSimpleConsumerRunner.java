package net.felder;

/**
 * Created by bfelder on 6/1/17.
 */
public class KafkaSimpleConsumerRunner extends CamelConsumerRunnerBase {


    public static void main(String[] args) throws Exception {
        KafkaSimpleConsumerRunner example = new KafkaSimpleConsumerRunner();
        example.boot();
    }

    @Override
    public String getConsumerUrl() {
        return "kafkasimple://noop";
    }
}