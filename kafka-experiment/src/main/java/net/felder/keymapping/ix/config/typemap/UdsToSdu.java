package net.felder.keymapping.ix.config.typemap;

import com.google.common.collect.ImmutableList;
import net.felder.keymapping.ix.converter.UdsToSduNosrepConverter;

import java.util.List;

/**
 * Created by bfelder on 6/29/17.
 */
public class UdsToSdu {

    public List<String> sourceTypesRequiredToCreate(String targetType) {
        if ("nosrep".equals(targetType)) {
            return ImmutableList.of("person");
        } else {
            throw new IllegalArgumentException("type " + targetType + " not recognized.");
        }
    }

    public String converterClassFor(String targetType) {
        if ("nosrep".equals(targetType)) {
            return UdsToSduNosrepConverter.class.getName();
        } else {
            throw new IllegalArgumentException("type " + targetType + " not recognized.");
        }
    }
}
