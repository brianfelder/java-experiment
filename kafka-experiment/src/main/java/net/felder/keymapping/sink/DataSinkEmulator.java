package net.felder.keymapping.sink;


import net.felder.keymapping.ix.model.DataSinkRequest;
import net.felder.keymapping.ix.model.DataSinkResponse;
import net.felder.keymapping.ix.model.DataSinkResponseItem;
import net.felder.keymapping.ix.model.IxRecord;
import net.felder.keymapping.ix.model.IxRecordKey;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkEmulator {
    public DataSinkResponse handleRequest(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = DataSinkResponse.from(theRequest);
        for (int i = 0; i < theRequest.getItemKeys().length; i++) {
            IxRecord currentRecord = theRequest.getItems()[i];
            IxRecordKey currentRecordKey = theRequest.getItemKeys()[i];
            // TODO: Don't mock this. Every 4th item is an error.
            DataSinkResponseItem.Status itemStatus =
                    (i % 4 == 0) ? DataSinkResponseItem.Status.Error : DataSinkResponseItem.Status.Ok;
            DataSinkResponseItem responseItem = new DataSinkResponseItem(currentRecordKey, itemStatus);
            toReturn.addItem(responseItem);
        }
        return toReturn;
    }
}
