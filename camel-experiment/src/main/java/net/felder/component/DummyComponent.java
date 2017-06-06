package net.felder.component;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.UriEndpointComponent;

import java.util.Map;

/**
 * Created by bfelder on 3/31/16.
 * Represents the component that manages {@link DummyEndpoint}.
 */
public class DummyComponent extends UriEndpointComponent {

    public DummyComponent() {
        super(DummyEndpoint.class);
    }

    public DummyComponent(Class<? extends Endpoint> endpointClass) {
        super(endpointClass);
    }

    public DummyComponent(CamelContext context) {
        super(context, DummyEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint theEndpoint = new DummyEndpoint(uri, this);
        setProperties(theEndpoint, parameters);
        return theEndpoint;
    }

}
