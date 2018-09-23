package exec;

import runner.RunnerPreferenceField;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.util.prefs.Preferences;

public class PreferencePrinter {
    public static void main(String[] args) {
        Preferences pref = Preferences.userNodeForPackage(PKSPreferences.class);
        StringBuilder builder = new StringBuilder();
        for (RunnerPreferenceField field : RunnerPreferenceField.values()) {
            String label = field.toString();
            String value = pref.get(field.toString(), "");
            System.out.print(label + " : ");
            System.out.println(value);

            if (builder.length() > 0)
                builder.append(";");
            builder.append(label).append(":").append(value);
        }
        System.out.println("");
        System.out.println(builder.toString());
    }
}
