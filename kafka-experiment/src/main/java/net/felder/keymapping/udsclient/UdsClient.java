package net.felder.keymapping.udsclient;


import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bfelder on 6/26/17.
 */
public class UdsClient {
    private static String UDS_QUERY_URL = "http://localhost:2015/api/uds/show/automation";
    private static int UDS_QUERY_PERSON_ID_INDEX = 6;
    private static int UDS_QUERY_FIRST_NAME_INDEX = 7;
    private static int UDS_QUERY_LAST_NAME_INDEX = 8;
    private static int UDS_PAGE_SIZE = 100;

    private static UdsClient instance = new UdsClient();
    private RestAdapter restAdapter;
    private UdsClientRetrofit udsClientRetrofit;

    /**
     * Used to test this component. Could move to unit test.
     * @param argv
     */
    public static void main(String[] argv) {
        UdsClient client = new UdsClient();
        List<Person> personList = client.fetchPersons();
        for (Person currentPerson : personList) {
            System.out.println(currentPerson.getId() + " " +
                    currentPerson.getFirstName() + " " +
                    currentPerson.getLastName() + " " +
                    currentPerson.getParentId());
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

    public List<Person> fetchPersons() {
        UdsResponse theResponse = this.fetchResults(0, UDS_PAGE_SIZE);
        List<Person> toReturn = new ArrayList<>(theResponse.getRows().size());
        String lastPersonId = null;
        for (Row currentRow : theResponse.getRows()) {
            String personId = (String) currentRow.getValues().get(UDS_QUERY_PERSON_ID_INDEX);
            String firstName = (String) currentRow.getValues().get(UDS_QUERY_FIRST_NAME_INDEX);
            String lastName = (String) currentRow.getValues().get(UDS_QUERY_LAST_NAME_INDEX);
            // ParentId is always the id of the previous person.
            String parentId = lastPersonId;
            lastPersonId = personId;
            toReturn.add(new Person(personId, firstName, lastName, parentId));
        }
        return toReturn;
    }

    public UdsResponse fetchResults(Integer offset, Integer limit) {
        UdsResponse toReturn = udsClientRetrofit.getUdsResults(offset, limit);
        return toReturn;
    }

    private interface UdsClientRetrofit {
        @GET("/sample1")
        UdsResponse getUdsResults(
                @Query("offset") Integer offset,
                @Query("limit") Integer limit);
    }
}
