package de.tomwey2.poc.log.eventstream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
@Slf4j
@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
public class LogEventStream {
    private final LogEventFactory logEventFactory;

    @Value(value = "${spring.kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value(value = "${spring.kafka.application.id}")
    private String applicationId;
    @Value("${spring.kafka.input.topic.name}")
    String inputTopicName;
    @Value("${spring.kafka.output.topic.name}")
    String outputTopicName;

    public static void main(String[] args) {
        SpringApplication.run(LogEventStream.class, args);
    }

    @Bean
    public KStream<String, JSONObject> kStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> inStream = kStreamBuilder
                .stream(inputTopicName, Consumed.with(Serdes.String(),Serdes.String()));

        KStream<String, JSONObject> outStream = inStream
                .mapValues(logEventFactory::toJson)
                .filter((k, v) -> Objects.nonNull(v))
                ;

        outStream.to(outputTopicName);
        outStream.print(Printed.toSysOut());
        return outStream;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, LogDataTimeExtractor.class);

        return new KafkaStreamsConfiguration(props);
    }

}
