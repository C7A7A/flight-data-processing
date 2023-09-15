package org.example.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.models.AnomalyRecord;

import java.util.Map;

public class AnomalyDeserializer implements Deserializer<AnomalyRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public AnomalyRecord deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.treeToValue(objectMapper.readTree(data), AnomalyRecord.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to AnomalyRecord", e);
        }
    }

    @Override
    public void close() {
    }
}
