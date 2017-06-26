package net.felder.keymapping.udsclient;

/**
 * Created by bfelder on 6/26/17.
 */
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private String parentId;

    public Person(String id, String firstName, String lastName, String parentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
