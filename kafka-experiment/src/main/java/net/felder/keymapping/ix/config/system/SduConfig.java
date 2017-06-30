package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;
import com.cvent.extensions.FieldType;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 */
public class SduConfig implements SystemConfig {
    /**
     * TODO: We will need to put this into JSON config file, and include richer config info, including:
     *   identity-field: id
     *   equality-fields: first,last,email
     *   highwatermark-field: lastModified
     *   highwatermark-initialValue: epoch
     *   depends-on: attendee (parentId)
     * @return
     */
    @Override
    public List<String> types() {
        return ImmutableList.of("nosrep");
    }

    @Override
    public EntityMetadata metadataFor(String type) {
        EntityMetadata toReturn = null;
        if ("nosrep".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setFields(this.fieldsFor(type));
            toReturn.setEqualityFields(ImmutableList.of("firstName", "lastName", "emailAddress"));
            toReturn.setIdentityField("id");
        }
        return toReturn;
    }

    /**
     * TODO: replace this with a query to the target system to get metadata for the type.
     * @param type
     * @return
     */
    private List<Field> fieldsFor(String type) {
        if ("nosrep".equals(type)) {
            return ImmutableList.of(
                    new Field("id", FieldType.STRING),
                    new Field("firstName", FieldType.STRING),
                    new Field("lastName", FieldType.STRING),
                    new Field("emailAddress", FieldType.STRING)
            );
        } else {
            throw new IllegalArgumentException("type " + type + " not recognized.");
        }
    }
}
