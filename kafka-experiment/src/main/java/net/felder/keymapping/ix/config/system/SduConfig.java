package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;
import com.cvent.extensions.FieldType;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 */
public class SduConfig {
    /**
     * TODO: We will need to put this into JSON config file, and include richer config info, including:
     *   identity-field: id
     *   equality-fields: first,last,email
     *   highwatermark-field: lastModified
     *   highwatermark-initialValue: epoch
     *   depends-on: attendee (parentId)
     * @return
     */
    public List<String> types() {
        return ImmutableList.of("nosrep");
    }

    /**
     * TODO: replace this with a query to the target system to get metadata for the type.
     * @param type
     * @return
     */
    public List<Field> fieldsFor(String type) {
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
