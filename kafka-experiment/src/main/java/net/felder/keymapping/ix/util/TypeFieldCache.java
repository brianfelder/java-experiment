package net.felder.keymapping.ix.util;

import com.cvent.extensions.Field;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 6/27/17.
 * // TODO: Make this smarter, TTL, versioning, and the like.
 */
public class TypeFieldCache {

    // TODO: This will really need to be something like dataSource -> accountId -> TypeName -> ArrayList<Field>
    // This will work for PoC.
    private Map<String, List<Field>> fieldsForType;
    private static Map<String, TypeFieldCache> instances = new HashMap<>();

    private TypeFieldCache() {
        super();
        fieldsForType = new HashMap<>();
    }

    /**
     * StoreName might just be a dataSource name ("Oss"), or it might be a dataSource + accountId
     * @param dataSourceName e.g. "Oss"
     * @param accountId e.g. "1234"
     * @return
     */
    public static TypeFieldCache of(String dataSourceName, String accountId) {
        return of(dataSourceName + "::" + accountId);
    }

    public static TypeFieldCache of(String dataSourceName) {
        TypeFieldCache toReturn = instances.get(dataSourceName);
        if (toReturn == null) {
            toReturn = new TypeFieldCache();
            instances.put(dataSourceName, toReturn);
        }
        return toReturn;
    }

    public Map<String, Integer> getFieldLookupMapFor(String typeName) {
        List<Field> typeFields = fieldsForType.get(typeName);
        Map<String, Integer> toReturn = toLookupMap(typeFields);
        return toReturn;
    }

    public List<Field> getFieldsFor(String typeName) {
        List<Field> typeFields = fieldsForType.get(typeName);
        if (typeFields == null) {
            return null;
        }
        return ImmutableList.copyOf(typeFields);
    }

    public void setFieldsFor(String typeName, List<Field> fields) {
        fieldsForType.put(typeName, fields);
    }

    protected Map<String, Integer> toLookupMap(List<Field> fields) {
        if (fields == null) {
            return null;
        }
        Map<String, Integer> toReturn = new HashMap<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            Field currentField = fields.get(i);
            toReturn.put(currentField.getFieldKey(), i);
        }
        return toReturn;
    }
}
