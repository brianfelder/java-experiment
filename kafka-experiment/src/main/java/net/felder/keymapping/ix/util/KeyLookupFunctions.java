package net.felder.keymapping.ix.util;

import com.google.common.collect.ImmutableList;
import net.felder.keymapping.ix.model.IxRecordKey;

import java.util.List;

/**
 * Created by bfelder on 6/27/17.
 */
public final class KeyLookupFunctions {

    private KeyLookupFunctions() {
        super();
    }

    /**
     *
     * @param sourceKey
     * @return
     */
    public static List<IxRecordKey> targetKeysFor(IxRecordKey sourceKey) {
        // TODO: make a real implementation of this, looking up in Kafka.
        IxRecordKey theKey = mockReversedKeyFor(sourceKey);
        List<IxRecordKey> toReturn = ImmutableList.of(theKey);
        return toReturn;

    }

    /**
     *
     * @param targetKey
     * @return
     */
    public static List<IxRecordKey> sourceKeysFor(IxRecordKey targetKey) {
        // TODO: make a real implementation of this, looking up in Kafka.
        IxRecordKey theKey = mockReversedKeyFor(targetKey);
        List<IxRecordKey> toReturn = ImmutableList.of(theKey);
        return toReturn;
    }

    /**
     * Implementation to reverse the systemName, itemType, and itemId on a key.
     * @param theKey
     * @return
     */
    private static IxRecordKey mockReversedKeyFor(IxRecordKey theKey) {
        IxRecordKey toReturn = new IxRecordKey(theKey.getJobId(),
                reverse(theKey.getSystemName()),
                reverse(theKey.getItemType()),
                reverse(theKey.getItemId()));
        return toReturn;
    }

    private static String reverse(String aString) {
        return new StringBuilder(aString).reverse().toString();
    }
}
