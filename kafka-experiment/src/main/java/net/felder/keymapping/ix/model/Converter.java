package net.felder.keymapping.ix.model;

import net.felder.keymapping.ix.config.system.EntityMetadata;

import java.util.Map;

/**
 * Created by bfelder on 6/29/17.
 */
public interface Converter {
    Pair<IxPipelineKey,IxRecord> convert(
            Map<IxRecordKey, IxRecord> sourceItems,
            Map<String, EntityMetadata> sourceMetadataMap);
}
