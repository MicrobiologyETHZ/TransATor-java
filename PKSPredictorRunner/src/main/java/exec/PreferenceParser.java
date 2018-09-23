package exec;

import runner.RunnerPreferenceField;

import java.util.HashMap;
import java.util.Map;

public class PreferenceParser {

    public Map<Enum, String> parse(String arguments) {
        String pairs[] = arguments.split(";");
        Map<Enum, String> map = new HashMap<>();
        for (String pair : pairs) {
            String[] token = pair.split(":");
            map.put(RunnerPreferenceField.valueOf(token[0]), token.length == 2 ? token[1] : "");
        }
        return map;
    }
}
