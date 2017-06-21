package net.felder;

/**
 * Created by cezargrzelak on 6/7/17.
 */
public class AttendeeDeserializer extends KafkaJsonDeserializer<Attendee> {
    @Override
    protected Class<Attendee> getType() {
        return Attendee.class;
    }
}
