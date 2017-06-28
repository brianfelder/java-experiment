package net.felder.keymapping.ix.util;

import com.cvent.extensions.Row;

import java.util.Map;

/**
 * Created by bfelder on 6/27/17.
 */
public class RowHelper {
    public static Object getValueByName(Row theRow, String fieldName, Map<String, Integer> lookupMap) {
        Integer columnIndex = lookupMap.get(fieldName);
        if (columnIndex == null) {
            return null;
        }
        return theRow.getValues().get(columnIndex);
    }
}
