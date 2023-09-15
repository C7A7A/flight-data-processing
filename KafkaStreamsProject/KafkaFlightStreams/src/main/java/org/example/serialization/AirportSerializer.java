package org.example.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.example.models.AirportRecord;

import java.util.Map;

public class AirportSerializer implements Serializer<AirportRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, AirportRecord data) {
        try {
            if (data == null){
                System.out.println("Null received at serializing");
                return null;
            }
//            System.out.println("Serializing...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing AnomalyRecord to byte[]");
        }
    }

    @Override
    public void close() {
    }
}
