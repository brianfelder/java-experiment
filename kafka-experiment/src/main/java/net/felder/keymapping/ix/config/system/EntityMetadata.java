package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;

import java.util.List;

/**
 * Created by bfelder on 6/30/17.
 */
public class EntityMetadata {
    private String entityName;
    private List<Field> fields;
    private String identityField;
    private List<String> equalityFields;
    private String highWatermarkField;
    private Object highWatermarkInitialValue;
    private List<IntraSystemDependency> intraSystemDependencies;
    private List<String> valueDigestExclusionFields;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getIdentityField() {
        return identityField;
    }

    public void setIdentityField(String identityField) {
        this.identityField = identityField;
    }

    public List<String> getEqualityFields() {
        return equalityFields;
    }

    public void setEqualityFields(List<String> equalityFields) {
        this.equalityFields = equalityFields;
    }

    public String getHighWatermarkField() {
        return highWatermarkField;
    }

    public void setHighWatermarkField(String highWatermarkField) {
        this.highWatermarkField = highWatermarkField;
    }

    public Object getHighWatermarkInitialValue() {
        return highWatermarkInitialValue;
    }

    public void setHighWatermarkInitialValue(Object highWatermarkInitialValue) {
        this.highWatermarkInitialValue = highWatermarkInitialValue;
    }

    public List<IntraSystemDependency> getIntraSystemDependencies() {
        return intraSystemDependencies;
    }

    public void setIntraSystemDependencies(
            List<IntraSystemDependency> intraSystemDependencies) {
        this.intraSystemDependencies = intraSystemDependencies;
    }

    public List<String> getValueDigestExclusionFields() {
        return valueDigestExclusionFields;
    }

    public void setValueDigestExclusionFields(List<String> valueDigestExclusionFields) {
        this.valueDigestExclusionFields = valueDigestExclusionFields;
    }

    public static class IntraSystemDependency {
        String entityName;
        String throughField;

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public String getThroughField() {
            return throughField;
        }

        public void setThroughField(String throughField) {
            this.throughField = throughField;
        }
    }

}
