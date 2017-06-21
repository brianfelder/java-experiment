package net.felder.component.kafkasimple;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Created by bfelder on 3/31/16.
 * Represents the component that manages {@link KafkaSimpleEndpoint}.
 */
public class KafkaSimpleComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint theEndpoint = new KafkaSimpleEndpoint(uri, this);
        setProperties(theEndpoint, parameters);
        return theEndpoint;
    }

}
