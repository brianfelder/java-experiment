/**
 * Copyright 2015 Confluent Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package net.felder.keymapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Generic JSON deserializer.
 */
public abstract class KafkaJsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper objectMapper;

    /**
     * Default constructor needed by Kafka
     */
    public KafkaJsonDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }


    @Override
    public T deserialize(String ignored, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, getType());
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    protected abstract Class<T> getType();

    @Override
    public void close() {

    }
}