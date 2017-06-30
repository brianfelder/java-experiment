package net.felder.keymapping.source;


import com.cvent.extensions.DataRequisitionClient;
import com.cvent.extensions.DataRequisitionRequest;
import com.cvent.extensions.DataSet;
import com.cvent.extensions.EntityList;
import com.cvent.extensions.Metadata;
import com.cvent.extensions.ReportContext;
import net.felder.keymapping.ix.util.Constants;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

/**
 * Created by bfelder on 6/26/17.
 */
public class UdsSkeleton implements DataRequisitionClient {
    private static String UDS_QUERY_URL = "http://localhost:2015/api/uds/show/ix-uds";

    private RestAdapter restAdapter;
    private UdsClientRetrofit udsClientRetrofit;

    public UdsSkeleton() {
        this.restAdapter = new RestAdapter.Builder()
                .setEndpoint(UDS_QUERY_URL)
                .build();
        this.udsClientRetrofit = restAdapter.create(UdsClientRetrofit.class);
    }

    @Override
    public DataSet requestData(@Header("Authorization") String authKey, @Path("entityId") String s1,
            @Query("environment") String s2, @Body DataRequisitionRequest dataRequisitionRequest) {
        DataSet dataSet = udsClientRetrofit.getUdsResults(0, Constants.UDS_PAGE_SIZE);
        return dataSet;
    }

    @Override
    public DataSet requestData(@Header("Authorization") String authKey, @Header("Account-Id") String s1,
            @Path("entityId") String s2, @Query("environment") String s3,
            @Body DataRequisitionRequest dataRequisitionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response requestDataAsync(@Header("Authorization") String authKey, @Path("entityId") String s1,
            @Query("dataDumpId") String s2, @Query("dumpServiceUrl") String s3, @Query("environment") String s4,
            @Body DataRequisitionRequest dataRequisitionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Response requestDataAsync(@Header("Authorization") String authKey, @Header("Account-Id") String s1,
            @Path("entityId") String s2, @Query("dataDumpId") String s3, @Query("dumpServiceUrl") String s4,
            @Query("environment") String s5, @Body DataRequisitionRequest dataRequisitionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Metadata requestMetadata(@Header("Authorization") String authKey, @Path("entityId") String s1,
            @Query("environment") String s2, @Body ReportContext reportContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Metadata requestMetadata(@Header("Authorization") String authKey, @Header("Account-Id") String s1,
            @Path("entityId") String s2, @Query("environment") String s3, @Body ReportContext reportContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityList requestEntities(@Header("Authorization") String authKey, @Query("environment") String s1,
            @Body TypedOutput typedOutput) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityList requestEntities(@Header("Authorization") String authKey, @Header("Account-Id") String s1,
            @Query("environment") String s2, @Body TypedOutput typedOutput) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Metadata requestMetadataWithCriteria(@Header("Authorization") String authKey, @Path("entityId") String s1,
            @Query("environment") String s2, @Body DataRequisitionRequest dataRequisitionRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Metadata requestMetadataWithCriteria(@Header("Authorization") String authKey, @Header("Account-Id") String s1,
            @Path("entityId") String s2, @Query("environment") String s3,
            @Body DataRequisitionRequest dataRequisitionRequest) {
        throw new UnsupportedOperationException();
    }

    private interface UdsClientRetrofit {
        @GET("/person")
        DataSet getUdsResults(
                @Query("offset") Integer offset,
                @Query("limit") Integer limit);
    }
}
