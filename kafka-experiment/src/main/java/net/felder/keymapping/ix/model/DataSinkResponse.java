package net.felder.keymapping.ix.model;

import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponse {

    private UUID requestId;
    private DataSinkResponseItem[] items;

    public static DataSinkResponse from(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = new DataSinkResponse(theRequest);
        return toReturn;
    }

    private DataSinkResponse(DataSinkRequest theRequest) {
        this.requestId = theRequest.getRequestId();
        this.items = new DataSinkResponseItem[theRequest.getItemKeys().length];
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setItemAtIndex(DataSinkResponseItem responseItem, int index) {
        this.items[index] = responseItem;
    }

    public int itemCount() {
        if (this.items == null) {
            return 0;
        }
        return this.items.length;
    }
}
