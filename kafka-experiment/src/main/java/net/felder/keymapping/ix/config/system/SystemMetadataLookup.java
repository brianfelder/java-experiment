package net.felder.keymapping.ix.config.system;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bfelder on 6/30/17.
 * This will read in the system configs and put them in a map for lookup.
 */
public class SystemMetadataLookup {
    private final Map<String, SystemMetadata> lookupMap;

    private static final SystemMetadataLookup INSTANCE = new SystemMetadataLookup();
    public static SystemMetadataLookup getInstance() {
        return INSTANCE;
    }

    private SystemMetadataLookup() {
        this.lookupMap = ImmutableMap.of(
                "sdU", this.getSduMetadata(),
                "Uds", this.getUdsMetadata()
        );
    }

    public SystemMetadata configFor(String systemName) {
        return lookupMap.get(systemName);
    }

    private SystemMetadata getSduMetadata() {
        SduConfig config = new SduConfig();
        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        for (String type : config.types()) {
            TypeMetadata metadata = config.metadataFor(type);
            typeMetadataList.add(metadata);
        }
        SystemMetadata toReturn = new SystemMetadata("sdU", typeMetadataList);
        return toReturn;
    }

    private SystemMetadata getUdsMetadata() {
        UdsConfig config = new UdsConfig();
        List<TypeMetadata> typeMetadataList = new ArrayList<>();
        for (String type : config.types()) {
            TypeMetadata metadata = config.metadataFor(type);
            typeMetadataList.add(metadata);
        }
        SystemMetadata toReturn = new SystemMetadata("Uds", typeMetadataList);
        return toReturn;
    }
}
