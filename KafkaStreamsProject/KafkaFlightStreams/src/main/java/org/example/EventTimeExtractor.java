package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.example.models.FlightRecord;

public class EventTimeExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long previousTimestamp) {
        long timestamp = -1;
        String line;

        if (consumerRecord.value() instanceof String) {
            line = (String) consumerRecord.value();

            if (FlightRecord.isLineCorrect(line)) {
                timestamp = FlightRecord.parseFromLine(line).extractTimestampInMillis();
            } else {
                timestamp = System.currentTimeMillis();
            }
        }

        return timestamp;
    }
}
