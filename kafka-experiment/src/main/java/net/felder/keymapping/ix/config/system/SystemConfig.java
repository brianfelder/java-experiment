package net.felder.keymapping.ix.config.system;

import java.util.List;

/**
 * Created by bfelder on 6/30/17.
 */
public interface SystemConfig {
    /**
     * The list of types supported by this system.
     * This would call to getEntities on the system to build this list.
     */
    public List<String> types();

    /**
     * The fields for a particular type.
     * This would call getMetadata for the entity on a system to build the list.
     * @param type
     * @return
     */
    public TypeMetadata metadataFor(String type);

}
