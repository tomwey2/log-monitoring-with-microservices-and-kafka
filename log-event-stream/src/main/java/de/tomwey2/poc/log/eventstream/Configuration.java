package de.tomwey2.poc.log.eventstream;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Configuration {
    @Value("${event.stream.timestamp.regex}")
    private String timestampRegex;
    @Value("${event.stream.content.regex}")
    private String contentRegex;
    @Value("${event.stream.timestamp.format}")
    private String timestampFormat;

}
