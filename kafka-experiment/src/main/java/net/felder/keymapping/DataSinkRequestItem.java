package net.felder.keymapping;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkRequestItem {
    private String sourceId;
    private String targetId;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
