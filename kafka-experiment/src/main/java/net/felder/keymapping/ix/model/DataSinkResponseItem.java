package net.felder.keymapping.ix.model;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponseItem {
    public enum Status {
        Ok,
        Error
    }

    private Status status;
    private IxRecordKey itemKey;

    public DataSinkResponseItem(IxRecordKey itemKey, Status status) {
        this.itemKey = itemKey;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public IxRecordKey getRecordKey() {
        return itemKey;
    }
}
