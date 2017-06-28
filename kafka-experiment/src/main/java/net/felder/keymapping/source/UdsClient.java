package net.felder.keymapping.source;


import com.cvent.extensions.DataSet;
import com.cvent.extensions.DataSetHelper;
import net.felder.keymapping.ix.util.Constants;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.Map;

/**
 * Created by bfelder on 6/26/17.
 */
public class UdsClient {
    private static String UDS_QUERY_URL = "http://localhost:2015/api/uds/show/automation";
    private static int UDS_QUERY_PERSON_ID_INDEX = 6;
    private static int UDS_QUERY_FIRST_NAME_INDEX = 7;
    private static int UDS_QUERY_LAST_NAME_INDEX = 8;

    private static UdsClient instance = new UdsClient();
    private RestAdapter restAdapter;
    private UdsClientRetrofit udsClientRetrofit;

    /**
     * Used to test this component. Could move to unit test.
     * @param argv
     */
    public static void main(String[] argv) {
        UdsClient client = new UdsClient();
        DataSet dataSet = client.fetchResults(0, Constants.UDS_PAGE_SIZE);
        Map<String, Integer> lookupMap = DataSetHelper.getFieldMap(dataSet);
        for (int n = 0; n < dataSet.getRows().size(); n++) {
            String id = (String) DataSetHelper.getValueByName(dataSet, "submitter_id", n, lookupMap);
            String firstName = (String) DataSetHelper.getValueByName(dataSet, "submitter_first_name", n, lookupMap);
            String lastName = (String) DataSetHelper.getValueByName(dataSet, "submitter_last_name", n, lookupMap);
            System.out.println(id + " " +
                    firstName + " " +
                    lastName);
        }
    }

    private UdsClient() {
        this.restAdapter = new RestAdapter.Builder()
                .setEndpoint(UDS_QUERY_URL)
                .build();
        this.udsClientRetrofit = restAdapter.create(UdsClientRetrofit.class);
    }

    public static UdsClient getInstance() {
        return instance;
    }

    public DataSet fetchResults(Integer offset, Integer limit) {
        DataSet toReturn = udsClientRetrofit.getUdsResults(offset, limit);
        return toReturn;
    }

    private interface UdsClientRetrofit {
        @GET("/sample1")
        DataSet getUdsResults(
                @Query("offset") Integer offset,
                @Query("limit") Integer limit);
    }
}
