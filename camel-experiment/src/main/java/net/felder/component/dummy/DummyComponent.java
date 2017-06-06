package net.felder.component.dummy;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

/**
 * Created by bfelder on 3/31/16.
 * Represents the component that manages {@link DummyEndpoint}.
 */
public class DummyComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint theEndpoint = new DummyEndpoint(uri, this);
        setProperties(theEndpoint, parameters);
        return theEndpoint;
    }

}
