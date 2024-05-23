package de.tomwey2.poc.log.contextstream;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JSONSerde implements Serializer<JSONObject>, Deserializer<JSONObject>, Serde<JSONObject> {

    @Override
    public JSONObject deserialize(String s, byte[] bytes) {
        String retrievedJsonString = new String(bytes, StandardCharsets.UTF_8);
        return new JSONObject(retrievedJsonString);
    }

    @Override
    public byte[] serialize(String s, JSONObject jsonObject) {
        String jsonString = jsonObject.toString();
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}

    @Override
    public Serializer<JSONObject> serializer() {
        return this;
    }

    @Override
    public Deserializer<JSONObject> deserializer() {
        return this;
    }

}
