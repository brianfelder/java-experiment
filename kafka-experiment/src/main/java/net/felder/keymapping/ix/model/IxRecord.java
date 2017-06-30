package net.felder.keymapping.ix.model;

import com.cvent.extensions.Row;

/**
 * TODO: Should we make this a message with headers instead?
 * Created by bfelder on 6/28/17.
 */
public class IxRecord {
    private String entityType;
    private Row row;

    public IxRecord() {
        super();
    }

    public IxRecord(String entityType, Row row) {
        this.entityType = entityType;
        this.row = row;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }
}
