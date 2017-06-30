package net.felder.keymapping.ix.config.system;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by bfelder on 6/30/17.
 */
public class SystemConfigLookup {
    private final Map<String, SystemConfig> systemConfigMap;

    private static final SystemConfigLookup INSTANCE = new SystemConfigLookup();
    public static SystemConfigLookup getInstance() {
        return INSTANCE;
    }

    private SystemConfigLookup() {
        this.systemConfigMap = ImmutableMap.of(
                "sdU", new SduConfig(),
                "Uds", new UdsConfig()
        );
    }

    public SystemConfig configFor(String systemName) {
        return systemConfigMap.get(systemName);
    }
}
