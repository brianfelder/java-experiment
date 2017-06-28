package net.felder.keymapping.ix.util;

import com.cvent.extensions.Field;

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
    private Map<String, Map<String, Integer>> fieldLookupMapForEntity;
    private static Map<String, EntityFieldCache> instances = new HashMap<>();

    private EntityFieldCache() {
        super();
        fieldLookupMapForEntity = new HashMap<>();
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
        Map<String, Integer> toReturn = fieldLookupMapForEntity.get(entityName);
        return toReturn;
    }

    public void setFieldsFor(String entityName, List<Field> fields) {
        Map<String, Integer> lookupMap = this.toLookupMap(fields);
        fieldLookupMapForEntity.put(entityName, lookupMap);
    }

    public Map<String, Integer> toLookupMap(List<Field> fields) {
        Map<String, Integer> toReturn = new HashMap<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            Field currentField = fields.get(i);
            toReturn.put(currentField.getFieldKey(), i);
        }
        return toReturn;
    }
}
