package net.felder.keymapping;

import java.util.List;
import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkRequest {
    private UUID requestId;
    private String sourceId;
    private String targetId;
    private List itemsToSend;

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List getItemsToSend() {
        return itemsToSend;
    }

    public void setItemsToSend(List itemsToSend) {
        this.itemsToSend = itemsToSend;
    }

}
