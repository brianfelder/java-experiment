package net.felder.keymapping.ix.model;

import com.cvent.extensions.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 * TODO: Implementing this till we have modified the DataSet.
 */
public class IxDataSet extends DataSet {
    private String typeName;
    private List<IxRecordKey> rowRecordKeys;

    public IxDataSet() {
        super();
        rowRecordKeys = new ArrayList<>();
    }

    public IxDataSet(int rowCount) {
        super();
        rowRecordKeys = new ArrayList(rowCount);
    }

    public static IxDataSet from(DataSet dataSet) {
        if (IxDataSet.class.isAssignableFrom(dataSet.getClass())) {
            return (IxDataSet) dataSet;
        }
        IxDataSet toReturn = new IxDataSet((int) (long) dataSet.getTotal());
        toReturn.setTotal(dataSet.getTotal());
        toReturn.setFields(new ArrayList(dataSet.getFields()));
        toReturn.setRows(new ArrayList(dataSet.getRows()));
        toReturn.setRowRecordKeys(new ArrayList(dataSet.getRows().size()));
        return toReturn;
    }

    public List<IxRecordKey> getRowRecordKeys() {
        return rowRecordKeys;
    }

    public void setRowRecordKeys(List<IxRecordKey> rowRecordKeys) {
        this.rowRecordKeys = rowRecordKeys;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
