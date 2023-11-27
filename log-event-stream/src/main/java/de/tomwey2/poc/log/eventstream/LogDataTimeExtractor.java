package de.tomwey2.poc.log.eventstream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Extracts the embedded timestamp of a record.
 * @see <a href="https://kafka.apache.org/21/documentation/streams/developer-guide/config-streams#timestamp-extractor">Configuring a stream application</a>
 */
public class LogDataTimeExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long partitionTime) {
        long timestampMillis = -1;
        try {
            JSONObject json = new JSONObject(consumerRecord.value());
            LocalDateTime timestamp = (LocalDateTime) json.get("timestamp");
            if (timestamp != null) {
                timestampMillis = getTimestampMillis(timestamp);
            }
        } catch (JSONException e) {
            System.out.println("JSON exception in: " + consumerRecord.value());
        }
        if (timestampMillis < 0) {
            // fall back to wall-clock time (processing-time).
            timestampMillis = System.currentTimeMillis();
        }
        return timestampMillis;
    }

    private long getTimestampMillis(final LocalDateTime timestamp) {
        return timestamp.toEpochSecond(ZoneOffset.UTC) * 1000;
    }
}

