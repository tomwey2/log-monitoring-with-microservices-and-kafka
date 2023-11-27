package de.tomwey2.poc.log.eventstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogEventFactoryTest {
    Configuration props = new Configuration();
    private LogEventFactory logEventFactory;

    @BeforeEach
    void setUp() {
        props.setTimestampRegex("(?<timestamp>\\d{4}-\\w{3}-\\d{1,2} \\d{2}:\\d{2}:\\d{2})");
        props.setContentRegex(".*sshd.*: (?<message>[^;]+)(.*rhost=(?<host>[^\\s]+))?(.*user=(?<user>[^$]+))?");
        props.setTimestampFormat("yyyy-MMM-dd HH:mm:ss");
        logEventFactory = new LogEventFactory(props);
    }

    @Test
    void toJsonTestOk() {
        JSONObject json = logEventFactory.toJson("2023-Jun-14 15:16:02 combo sshd(pam_unix)[19937]: authentication failure; logname= uid=0 euid=0 tty=NODEVssh ruser= rhost=218.188.2.4");
        System.out.println(json);

        JSONObject expectedJson = new JSONObject();
        expectedJson.put("timestamp", LocalDateTime.of(2023,6,14,15,16,2));
        expectedJson.put("message", "authentication failure");
        expectedJson.put("host", "218.188.2.4");
        expectedJson.put("user", JSONObject.NULL);
        ObjectMapper objectMapper = new ObjectMapper();
        assertTrue(json.similar(expectedJson));
    }

    @Test
    void toJsonTestFailed() {
        JSONObject json = logEventFactory.toJson("2023-Jun-14 15:16:02 combo su(pam_unix)[22644]: session closed for user news");
        System.out.println(json);
        assertNull(json);
    }
}