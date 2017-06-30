package net.felder.keymapping.sink;


import com.cvent.extensions.CacheStatus;
import com.cvent.extensions.DataDumpClient;
import com.cvent.extensions.DataSet;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import net.felder.keymapping.ix.model.DataSinkResponse;
import net.felder.keymapping.ix.model.DataSinkResponseItem;
import net.felder.keymapping.ix.model.IxDataSet;
import net.felder.keymapping.ix.model.IxRecordKey;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

import javax.ws.rs.core.Response;

/**
 * Created by bfelder on 6/26/17.
 * Skeleton of what a dataSink would implement. Called "Sdu", as an imaginary reversed "Uds".
 */
public class SduSkeleton implements DataDumpClient {

    @Override
    public Response createDump(@Header("Authorization") String authKey, @Query("dataDumpId") String dataDumpId,
            @Body TypedOutput typedOutput) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheStatus getStatus(@Header("Authorization") String authKey, @Path("dataDumpId") String dataDumpId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response dumpData(@Header("Authorization") String authKey, @Path("dataDumpId") String dataDumpId,
            @Body DataSet dataSet) {
        DataSinkResponse dsResponse = DataSinkResponse.from(dataDumpId, (int) (long) dataSet.getTotal());
        IxDataSet ixDataSet = IxDataSet.from(dataSet);
        for (int i = 0; i < dataSet.getTotal(); i++) {
            IxRecordKey currentRecordKey = ixDataSet.getRowRecordKeys().get(i);
            // TODO: Don't mock this. Every 4th item is an error.
            DataSinkResponseItem.Status itemStatus =
                    (i % 4 == 0) ? DataSinkResponseItem.Status.Error : DataSinkResponseItem.Status.Ok;
            DataSinkResponseItem responseItem = new DataSinkResponseItem(currentRecordKey, itemStatus);
            dsResponse.setItemAtIndex(responseItem, i);
        }
        Response toReturn = new ResponseBuilderImpl()
                .entity(dsResponse)
                .status(Response.Status.OK)
                .build();
        return toReturn;
    }

    @Override
    public Response dumpBinaryData(@Header("Authorization") String authKey, @Path("dataDumpId") String dataDumpId,
            @Body byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response publishDump(@Header("Authorization") String authKey, @Path("dataDumpId") String dataDumpId,
            @Body TypedOutput typedOutput) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response abandonDump(@Header("Authorization") String authKey, @Path("dataDumpId") String dataDumpId,
            @Query("reason") String reason, @Body TypedOutput typedOutput) {
        throw new UnsupportedOperationException();
    }
}
