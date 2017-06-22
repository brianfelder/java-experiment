package net.felder;

/**
 * Created by bfelder on 6/1/17.
 */
public class DummyConsumerRunner extends CamelConsumerRunnerBase {


    public static void main(String[] args) throws Exception {
        DummyConsumerRunner example = new DummyConsumerRunner();
        example.boot();
    }

    @Override
    public String getConsumerUrl() {
        return "dummy://noop";
    }
}