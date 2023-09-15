package org.example.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.models.AirportRecord;

import java.util.Map;

public class AirportDeserializer implements Deserializer<AirportRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public AirportRecord deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
//            System.out.println("Deserializing...");
            return objectMapper.treeToValue(objectMapper.readTree(data), AirportRecord.class);
//            return objectMapper.readValue(new String(data, "UTF-8"), AirportRecord.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to AirportRecord", e);
        }
    }

    @Override
    public void close() {
    }
}
