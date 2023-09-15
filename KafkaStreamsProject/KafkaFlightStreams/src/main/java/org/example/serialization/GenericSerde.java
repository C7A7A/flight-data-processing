package org.example.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class GenericSerde<T> implements Serde<T> {
    private final Class<T> tClass;

    public GenericSerde(Class<T> cls) {
        this.tClass = cls;
    }

    @Override
    public Serializer<T> serializer() {
        Map<String, Object> serdeProps = new HashMap<>();
        final Serializer<T> s = new GenericSerializer<T>();
        serdeProps.put("GenericDeserializerClass", tClass);
        s.configure(serdeProps, false);
        return s;
    }

    @Override
    public Deserializer<T> deserializer() {
        Map<String, Object> serdeProps = new HashMap<>();
        final Deserializer<T> d = new GenericDeserializer<T>();
        serdeProps.put("GenericDeserializerClass", tClass);
        d.configure(serdeProps, false);
        return d;
    }
}
