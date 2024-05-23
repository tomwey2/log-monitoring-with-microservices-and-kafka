package de.tomwey2.poc.log.contextstream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
public class LogContextStream {

    @Value(value = "${spring.kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value(value = "${spring.kafka.application.id}")
    private String applicationId;
    @Value("${spring.kafka.input.topic1.name}")
    String inputTopic1Name;
    @Value("${spring.kafka.input.topic2.name}")
    String inputTopic2Name;
    @Value("${spring.kafka.output.topic.name}")
    String outputTopicName;

    public static void main(String[] args) {
        SpringApplication.run(LogContextStream.class, args);
    }

    @Bean
    public KStream<String, JSONObject> kStream(StreamsBuilder kStreamBuilder) {
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://my-schema-registry:8081");
        final Serde<JSONObject> valueJsonSerde = new JSONSerde();
        valueJsonSerde.configure(serdeConfig, false);
        KStream<String, JSONObject> inStream1 = kStreamBuilder
                .stream(inputTopic1Name, Consumed.with(Serdes.String(),valueJsonSerde));
        KStream<String, JSONObject> inStream2 = kStreamBuilder
                .stream(inputTopic2Name, Consumed.with(Serdes.String(),valueJsonSerde));

        KStream<String, JSONObject> outStream =
                inStream1.leftJoin(inStream2,
                        (value1, value2) -> mergeJson(value1, value2),
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(10))
                        );

        outStream.to(outputTopicName);
        outStream.print(Printed.toSysOut());
        return outStream;
    }

    private JSONObject mergeJson(JSONObject j1, JSONObject j2) {
        JSONObject j3 = new JSONObject();
        j3.put("o1", j1);
        j3.put("o2", j2);
        return j3;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JSONSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, EventTimeExtractor.class);

        return new KafkaStreamsConfiguration(props);
    }

}
