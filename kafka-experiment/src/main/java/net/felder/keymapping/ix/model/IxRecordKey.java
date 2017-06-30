package net.felder.keymapping.ix.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by bfelder on 6/26/17.
 */
public class IxRecordKey {
    private String jobId;
    private String systemName;
    private String itemType;
    private String itemId;

    public IxRecordKey() {
        super();
    }

    public IxRecordKey(String jobId, String systemName, String itemType, String itemId) {
        this.jobId = jobId;
        this.systemName = systemName;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * For partitioning, we want to include only the jobId and systemName in the hash.
     * This is to ensure that all records for the same Job, coming from the same System, end up on the
     * same partition and are processed in order.
     * @return
     */
    public int partitionHash() {
        return Objects.hashCode(jobId, systemName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobId, systemName, itemType, itemId);
    }

    @Override
    public boolean equals(Object otherObject) {
        return Objects.equal(this, otherObject);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .addValue(jobId)
                .addValue(systemName)
                .addValue(itemType)
                .addValue(itemId)
                .toString();
    }
}
