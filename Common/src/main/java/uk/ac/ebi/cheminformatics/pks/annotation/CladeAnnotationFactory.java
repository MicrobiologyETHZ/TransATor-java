package uk.ac.ebi.cheminformatics.pks.annotation;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.Logger;
import runner.RunnerPreferenceField;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Created by pmoreno on 11/01/2017.
 */
public class CladeAnnotationFactory {

    private static CladeAnnotation cladeAnnotation;
    private static final Preferences prefs = Preferences.userNodeForPackage(PKSPreferences.class);
    private static final Logger LOGGER = LogManager.getLogger(CladeAnnotationFactory.class);

    public static CladeAnnotation getInstance() {
        if(cladeAnnotation == null) {
            CladeAnnotationReader reader = null;
            String annot_file_name = prefs.get(RunnerPreferenceField.HMMERModelPath.toString(), "")+".annot_v2";
            try {
                reader = new CladeAnnotationReader(annot_file_name);
            } catch (IOException e) {
                LOGGER.error("Could not read annotation file from "+annot_file_name, e);
            }
            cladeAnnotation = reader.getCladeAnnotation();
        }
        return cladeAnnotation;
    }
}
