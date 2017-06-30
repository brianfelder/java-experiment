package net.felder.keymapping.ix.model;

/**
 * Created by bfelder on 6/26/17.
 */
public class DataSinkResponse {

    private String dataDumpId;
    private DataSinkResponseItem[] items;

    public static DataSinkResponse from(String dataDumpId, int itemCount) {
        DataSinkResponse toReturn = new DataSinkResponse(dataDumpId, itemCount);
        return toReturn;
    }

    private DataSinkResponse(String dataDumpId, int itemCount) {
        this.dataDumpId = dataDumpId;
        this.items = new DataSinkResponseItem[itemCount];
    }

    public String getDataDumpId() {
        return dataDumpId;
    }

    public void setItemAtIndex(DataSinkResponseItem responseItem, int index) {
        this.items[index] = responseItem;
    }

    public DataSinkResponseItem getItemAtIndex(int index) {
        return this.items[index];
    }

    public int itemCount() {
        if (this.items == null) {
            return 0;
        }
        return this.items.length;
    }
}
