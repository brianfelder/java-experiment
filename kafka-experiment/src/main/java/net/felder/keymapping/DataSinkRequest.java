package net.felder.keymapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkRequest {
    private UUID requestId;
    private List<DataSinkRequestItem> items = new ArrayList<>();

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public List<DataSinkRequestItem> getItems() {
        return items;
    }

}
