package net.felder;

/**
 * Created by bfelder on 6/22/17.
 */
public class NameUtils {
    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Carol", "Dan", "Edith",
            "Frank", "Georgia", "Hank", "Ingrid", "Jerry"};
    private static final String[] LAST_NAMES = {"Aardvark", "Brown", "Carlson", "Drummond", "Engels",
            "Felderman", "Garcia", "Hernandez", "Isadore", "Jordan"};

    public static String randomName() {
        int firstNameIndex = (int) Math.round(Math.floor(Math.random() * FIRST_NAMES.length));
        int lastNameIndex = (int) Math.round(Math.floor(Math.random() * LAST_NAMES.length));
        String firstName = FIRST_NAMES[firstNameIndex];
        String lastName = LAST_NAMES[lastNameIndex];
        return firstName + " " + lastName;
    }

    private static String[] nameArrayFrom(String fullName) {
        String[] toReturn = fullName.split(" ");
        return toReturn;
    }

    private static String nameFromFullNameAtIndex(String fullName, int index) {
        String[] nameArray = nameArrayFrom(fullName);
        String toReturn = nameArray[index];
        return toReturn;
    }

    public static String firstNameFrom(String fullName) {
        String toReturn = nameFromFullNameAtIndex(fullName, 0);
        return toReturn;
    }

    public static String lastNameFrom(String fullName) {
        String toReturn = nameFromFullNameAtIndex(fullName, 1);
        return toReturn;
    }
}
