package de.tomwey2.poc.log.eventstream;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LogEventFactory {

    private final Configuration props;

    public JSONObject toJson(final String message) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(props.getTimestampFormat(), Locale.ENGLISH);
        String regex = props.getTimestampRegex() + props.getContentRegex();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            return null;
        }

        // extract all group names (e.g. names within <> brakes) from the regex string
        List<String> groupNames = findAllGroupNamesInRegex(regex);

        // create JSON object with all group names and their matched values in message
        JSONObject json = new JSONObject();
        for (String group : groupNames) {
            String value = matcher.group(group);
            if (value == null) {
                json.put(group, JSONObject.NULL);
            } else {
                if (group.equals("timestamp")) {
                    json.put(group, LocalDateTime.parse(value, formatter));
                } else {
                    json.put(group, value);
                }
            }
        }

        return json;
    }

    private List<String> findAllGroupNamesInRegex(final String regex) {
        List<String> groupNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(regex);
        while (matcher.find()) {
            groupNames.add(matcher.group(1));
        }
        return groupNames;
    }

}
