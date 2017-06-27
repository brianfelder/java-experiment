package net.felder.keymapping;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkEmulator {
    public DataSinkResponse handleRequest(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = DataSinkResponse.from(theRequest);
        for (DataSinkRequestItem requestItem : theRequest.getItems()) {
            DataSinkResponseItem responseItem = DataSinkResponseItem.from(requestItem);
            responseItem.setStatus(DataSinkResponseItem.Status.Ok);
            // Make targetId the reverse of sourceid.
            responseItem.setTargetId(new StringBuilder(requestItem.getSourceId()).reverse().toString());
        }
        return toReturn;
    }
}
