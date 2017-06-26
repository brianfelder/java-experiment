package net.felder.keymapping;

import net.felder.keymapping.udsclient.Person;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class PersonDeserializer extends KafkaJsonDeserializer<Person> {
    @Override
    protected Class<Person> getType() {
        return Person.class;
    }
}
