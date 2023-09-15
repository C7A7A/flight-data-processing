package org.example.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.models.FlightRecord;

import java.util.Map;

public class FlightDeserializer implements Deserializer<FlightRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public FlightRecord deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.treeToValue(objectMapper.readTree(data), FlightRecord.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to FlightRecord", e);
        }
    }

    @Override
    public void close() {
    }
}
