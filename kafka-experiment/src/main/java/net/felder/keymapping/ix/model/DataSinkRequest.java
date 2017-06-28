package net.felder.keymapping.ix.model;

import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkRequest {
    private UUID requestId;
    private IxRecord[] items;
    /**
     * The target keys known to IX for the items in the DataSet items.
     * Length of array == the number of Rows in the DataSet.
     */
    private IxRecordKey[] itemKeys;

    public DataSinkRequest(UUID requestId, int itemCount) {
        this.requestId = requestId;
        this.items = new IxRecord[itemCount];
        this.itemKeys = new IxRecordKey[itemCount];
    }

    public UUID getRequestId() {
        return requestId;
    }

    public IxRecord[] getItems() {
        return items;
    }

    public IxRecordKey[] getItemKeys() {
        return itemKeys;
    }
}
