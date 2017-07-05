package net.felder.keymapping.ix.config.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 6/30/17.
 */
public class SystemMetadata {
    private String systemName;
    private Map<String, TypeMetadata> entities = new HashMap<>();

    public SystemMetadata(String systemName, List<TypeMetadata> entities) {
        this.systemName = systemName;
        for (TypeMetadata metadata : entities) {
            this.entities.put(metadata.getTypeName(), metadata);
        }
    }

    public String getSystemName() {
        return systemName;
    }

    public TypeMetadata typeMetadataFor(String typeName) {
        TypeMetadata toReturn = entities.get(typeName);
        return toReturn;
    }
}
