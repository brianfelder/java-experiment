package net.felder.keymapping.ix.config.typemap;

import java.util.List;

/**
 * Created by bfelder on 6/30/17.
 */
public interface ConverterTypeMap {
    List<String> sourceTypesRequiredToCreate(String targetType);
    String converterClassFor(String targetType);
}
