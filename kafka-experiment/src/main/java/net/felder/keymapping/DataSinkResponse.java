package net.felder.keymapping;

import java.util.UUID;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponse {
    public enum Status {
        Ok,
        Error
    }

    private UUID requestId;
    private Status status;
    private String sourceId;
    private String targetId;

    public static DataSinkResponse from(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = new DataSinkResponse(theRequest);
        return toReturn;
    }

    private DataSinkResponse(DataSinkRequest theRequest) {
        this.requestId = theRequest.getRequestId();
        this.sourceId = theRequest.getSourceId();
        this.targetId = theRequest.getTargetId();
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
