package net.felder.keymapping.udsclient;

import java.util.List;

/**
 * Created by bfelder on 6/26/17.
 */
public class UdsResponse {
    private int total;
    private List<Field> fields;
    private List<Row> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
