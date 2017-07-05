package net.felder.keymapping.ix.config.system;

import com.cvent.extensions.Field;

import java.util.List;

/**
 * Created by bfelder on 6/30/17.
 */
public class TypeMetadata {
    private String typeName;
    private List<Field> fields;
    private String identityField;
    private List<String> equalityFields;
    private String highWatermarkField;
    private Object highWatermarkInitialValue;
    private List<Dependency> dependencies;
    private List<String> valueDigestExclusionFields;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(
            List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getValueDigestExclusionFields() {
        return valueDigestExclusionFields;
    }

    public void setValueDigestExclusionFields(List<String> valueDigestExclusionFields) {
        this.valueDigestExclusionFields = valueDigestExclusionFields;
    }

    public static class Dependency {
        String refType;
        String refField;

        public String getRefType() {
            return refType;
        }

        public void setRefType(String refType) {
            this.refType = refType;
        }

        public String getRefField() {
            return refField;
        }

        public void setRefField(String refField) {
            this.refField = refField;
        }
    }

}
