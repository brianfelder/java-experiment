package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;
import com.cvent.extensions.FieldType;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 */
public class UdsConfig implements SystemConfig {
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
        return ImmutableList.of("person");
    }

    @Override
    public EntityMetadata metadataFor(String type) {
        EntityMetadata toReturn = null;
        if ("person".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setFields(this.fieldsFor(type));
            toReturn.setEqualityFields(ImmutableList.of("first", "last", "email"));
            toReturn.setIdentityField("id");
        }
        if ("fooType".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            EntityMetadata.IntraSystemDependency theDependency = new EntityMetadata.IntraSystemDependency();
            theDependency.setEntityName("barType");
            theDependency.setThroughField("barId");
            EntityMetadata.IntraSystemDependency theDependency2 = new EntityMetadata.IntraSystemDependency();
            theDependency2.setEntityName("quxType");
            theDependency2.setThroughField("quxId");
            toReturn.setIdentityField("id");
            toReturn.setIntraSystemDependencies(ImmutableList.of(theDependency, theDependency2));
        }
        if ("barType".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            EntityMetadata.IntraSystemDependency theDependency = new EntityMetadata.IntraSystemDependency();
            theDependency.setEntityName("bazType");
            theDependency.setThroughField("bazId");
            toReturn.setIdentityField("id");
            toReturn.setIntraSystemDependencies(ImmutableList.of(theDependency));
        }
        if ("bazType".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setIdentityField("id");
        }
        if ("quxType".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setIdentityField("id");
        }



        if ("hotdog".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            EntityMetadata.IntraSystemDependency theDependency = new EntityMetadata.IntraSystemDependency();
            theDependency.setEntityName("bun");
            theDependency.setThroughField("bunId");
            toReturn.setIdentityField("id");
        }
        if ("bun".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setIdentityField("id");
        }
        if ("mustard".equals(type)) {
            toReturn = new EntityMetadata();
            toReturn.setEntityName(type);
            toReturn.setIdentityField("id");
        }
        return toReturn;
    }

    /**
     * TODO: replace this with a query to the target system to get metadata for the type.
     * @param type
     * @return
     */
    public List<Field> fieldsFor(String type) {
        if ("person".equals(type)) {
            return ImmutableList.of(
                    new Field("id", FieldType.STRING),
                    new Field("first", FieldType.STRING),
                    new Field("last", FieldType.STRING),
                    new Field("email", FieldType.STRING)
            );
        } else {
            throw new IllegalArgumentException("type " + type + " not recognized.");
        }
    }
}
