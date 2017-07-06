package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;
import com.cvent.extensions.FieldType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 6/30/17.
 * This will read in the system configs and put them in a map for lookup.
 */
public class SystemMetadataLookup {
    private final Map<String, SystemMetadata> lookupMap;

    private static final SystemMetadataLookup INSTANCE = new SystemMetadataLookup();
    public static SystemMetadataLookup getInstance() {
        return INSTANCE;
    }

    private SystemMetadataLookup() {
        this.lookupMap = ImmutableMap.of(
                "sdU", this.getSduMetadata(),
                "Uds", this.getUdsMetadata()
        );
    }

    public SystemMetadata configFor(String systemName) {
        return lookupMap.get(systemName);
    }

    private SystemMetadata getSduMetadata() {
        SduConfig config = new SduConfig();
        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        for (String type : config.types()) {
            TypeMetadata metadata = config.metadataFor(type);
            typeMetadataList.add(metadata);
        }
        SystemMetadata toReturn = new SystemMetadata("sdU", typeMetadataList);
        return toReturn;
    }

    private SystemMetadata getUdsMetadata() {
        UdsConfig config = new UdsConfig();
        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        for (String type : config.types()) {
            TypeMetadata metadata = config.metadataFor(type);
            typeMetadataList.add(metadata);
        }
        SystemMetadata toReturn = new SystemMetadata("Uds", typeMetadataList);
        return toReturn;
    }

    /**
     * Created by bfelder on 6/29/17.
     */
    @Deprecated
    private static class SduConfig {
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

        public TypeMetadata metadataFor(String type) {
            TypeMetadata toReturn = null;
            if ("nosrep".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
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

    /**
     * Created by bfelder on 6/29/17.
     */
    @Deprecated
    private static class UdsConfig {
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
            return ImmutableList.of("person");
        }

        public TypeMetadata metadataFor(String type) {
            TypeMetadata toReturn = null;
            if ("person".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                toReturn.setFields(this.fieldsFor(type));
                toReturn.setEqualityFields(ImmutableList.of("first", "last", "email"));
                toReturn.setIdentityField("id");
            }
            if ("fooType".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                TypeMetadata.Dependency theDependency = new TypeMetadata.Dependency();
                theDependency.setRefType("barType");
                theDependency.setRefField("barId");
                TypeMetadata.Dependency theDependency2 = new TypeMetadata.Dependency();
                theDependency2.setRefType("quxType");
                theDependency2.setRefField("quxId");
                toReturn.setIdentityField("id");
                toReturn.setDependencies(ImmutableList.of(theDependency, theDependency2));
            }
            if ("barType".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                TypeMetadata.Dependency theDependency = new TypeMetadata.Dependency();
                theDependency.setRefType("bazType");
                theDependency.setRefField("bazId");
                toReturn.setIdentityField("id");
                toReturn.setDependencies(ImmutableList.of(theDependency));
            }
            if ("bazType".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                toReturn.setIdentityField("id");
            }
            if ("quxType".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                toReturn.setIdentityField("id");
            }



            if ("hotdog".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                TypeMetadata.Dependency theDependency = new TypeMetadata.Dependency();
                theDependency.setRefType("bun");
                theDependency.setRefField("bunId");
                toReturn.setIdentityField("id");
            }
            if ("bun".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
                toReturn.setIdentityField("id");
            }
            if ("mustard".equals(type)) {
                toReturn = new TypeMetadata();
                toReturn.setTypeName(type);
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
}
