package de.tomwey2.poc.log.producer.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.output.topic.name}")
    private String topic;

    public void uploadFile(final MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                uploadMessage(file.getOriginalFilename(), line);
            }
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        }
    }

    public void uploadMessage(final String id, final String message) {
        log.info("topic: {} message:{}", topic, message);
        kafkaTemplate.send(topic, id, message);
    }
}
