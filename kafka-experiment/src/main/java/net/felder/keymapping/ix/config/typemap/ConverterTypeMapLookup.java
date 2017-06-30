package net.felder.keymapping.ix.config.typemap;

import com.google.common.collect.ImmutableMap;
import net.felder.keymapping.ix.model.Pair;

import java.util.Map;

/**
 * Created by bfelder on 6/30/17.
 */
public class ConverterTypeMapLookup {
    private final Map<Pair<String, String>, ConverterTypeMap> lookupMap;

    private static final ConverterTypeMapLookup INSTANCE = new ConverterTypeMapLookup();
    public static ConverterTypeMapLookup getInstance() {
        return INSTANCE;
    }

    private ConverterTypeMapLookup() {
        this.lookupMap = ImmutableMap.of(
                new Pair<>("Uds", "sdU"), new UdsToSduConverterTypeMap()
        );
    }


    public ConverterTypeMap converterTypeMapFor(String sourceSystem, String targetSystem) {
        return lookupMap.get(new Pair<>(sourceSystem, targetSystem));
    }

}
