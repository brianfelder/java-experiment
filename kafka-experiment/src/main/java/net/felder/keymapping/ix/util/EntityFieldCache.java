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
public class EntityFieldCache {

    // TODO: This will really need to be something like dataSource -> accountId -> EntityType -> ArrayList<Field>
    // This will work for PoC.
    private Map<String, List<Field>> fieldsForEntity;
    private static Map<String, EntityFieldCache> instances = new HashMap<>();

    private EntityFieldCache() {
        super();
        fieldsForEntity = new HashMap<>();
    }

    /**
     * StoreName might just be a dataSource name ("Oss"), or it might be a dataSource + accountId
     * @param dataSourceName e.g. "Oss"
     * @param accountId e.g. "1234"
     * @return
     */
    public static EntityFieldCache of(String dataSourceName, String accountId) {
        return of(dataSourceName + "::" + accountId);
    }

    public static EntityFieldCache of(String dataSourceName) {
        EntityFieldCache toReturn = instances.get(dataSourceName);
        if (toReturn == null) {
            toReturn = new EntityFieldCache();
            instances.put(dataSourceName, toReturn);
        }
        return toReturn;
    }

    public Map<String, Integer> getFieldLookupMapFor(String entityName) {
        List<Field> entityFields = fieldsForEntity.get(entityName);
        Map<String, Integer> toReturn = toLookupMap(entityFields);
        return toReturn;
    }

    public List<Field> getFieldsFor(String entityName) {
        List<Field> entityFields = fieldsForEntity.get(entityName);
        if (entityFields == null) {
            return null;
        }
        return ImmutableList.copyOf(entityFields);
    }

    public void setFieldsFor(String entityName, List<Field> fields) {
        fieldsForEntity.put(entityName, fields);
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
