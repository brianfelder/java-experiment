package net.felder.keymapping.ix.model;

import java.util.Map;

/**
 * Created by bfelder on 6/29/17.
 */
public interface Converter {
    // TODO: Blech. Using Map.Entry rather than Pair, and it ain't pretty.
    Map.Entry<IxPipelineKey,IxRecord> convert(Map<IxRecordKey, IxRecord> sourceItems);
}
