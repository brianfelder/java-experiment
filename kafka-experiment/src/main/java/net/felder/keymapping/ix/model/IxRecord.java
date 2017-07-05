package net.felder.keymapping.ix.model;

import com.cvent.extensions.Row;

/**
 * TODO: Should we make this a message with headers instead?
 * Created by bfelder on 6/28/17.
 */
public class IxRecord {
    private String typeName;
    private Row row;

    public IxRecord() {
        super();
    }

    public IxRecord(String typeName, Row row) {
        this.typeName = typeName;
        this.row = row;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }
}
