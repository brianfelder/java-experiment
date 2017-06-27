package net.felder.keymapping;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponseItem {
    public enum Status {
        Ok,
        Error
    }

    private Status status;
    private String sourceId;
    private String targetId;

    public static DataSinkResponseItem from(DataSinkRequestItem theRequest) {
        DataSinkResponseItem toReturn = new DataSinkResponseItem(theRequest);
        return toReturn;
    }

    private DataSinkResponseItem(DataSinkRequestItem theRequest) {
        this.sourceId = theRequest.getSourceId();
        this.targetId = theRequest.getTargetId();
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
