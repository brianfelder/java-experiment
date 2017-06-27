package net.felder.keymapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponse {

    private UUID requestId;
    private List<DataSinkResponseItem> items;

    public static DataSinkResponse from(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = new DataSinkResponse(theRequest);
        return toReturn;
    }

    private DataSinkResponse(DataSinkRequest theRequest) {
        this.requestId = theRequest.getRequestId();
        this.items = new ArrayList<>(theRequest.getItems().size());
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void addItem(DataSinkResponseItem responseItem) {
        items.add(responseItem);
    }
}
