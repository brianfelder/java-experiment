package net.felder.keymapping.ix.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 */
public class IxPipelineKey {
    private List<IxRecordKey> sourceKeys = new ArrayList();
    private IxRecordKey targetKey;
    private String converterClassName;

    public List<IxRecordKey> getSourceKeys() {
        return sourceKeys;
    }

    public void setSourceKeys(List<IxRecordKey> sourceKeys) {
        this.sourceKeys = sourceKeys;
    }

    public IxRecordKey getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(IxRecordKey targetKey) {
        this.targetKey = targetKey;
    }

    public String getConverterClassName() {
        return converterClassName;
    }

    public void setConverterClassName(String converterClassName) {
        this.converterClassName = converterClassName;
    }
}
