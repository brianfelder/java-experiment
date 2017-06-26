package net.felder.keymapping;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkEmulator {
    public DataSinkResponse createAttendee(DataSinkRequest theRequest) {
        DataSinkResponse toReturn = DataSinkResponse.from(theRequest);
        toReturn.setStatus(DataSinkResponse.Status.Ok);
        return toReturn;
    }
}
