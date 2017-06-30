package net.felder.keymapping.ix.model;

import java.util.Map;

/**
 * Created by bfelder on 6/29/17.
 */
public interface Converter {
    OrderedPair<IxPipelineKey,IxRecord> convert(Map<IxRecordKey, IxRecord> sourceItems);
}
