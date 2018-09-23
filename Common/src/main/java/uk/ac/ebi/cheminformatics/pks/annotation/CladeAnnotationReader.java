package uk.ac.ebi.cheminformatics.pks.annotation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Reads an annotation v2 file. The CladeAnnotation object should be obtained through the CladeAnnotationFactory object.
 */
class CladeAnnotationReader {

    private CladeAnnotation annotation;

    public CladeAnnotationReader(String pathToAnnotationFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathToAnnotationFile));
        String line;
        // ignore header
        reader.readLine();
        annotation = new CladeAnnotation();
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\t");
            String clade = tokens[0];
            annotation.setCladeDesc(clade, tokens[1]);
            annotation.setCladeDescTool(clade, tokens[2]);
            annotation.setCladeMolFileName(clade, tokens[3]);
            annotation.setCladePostProcessors(clade, Arrays.asList(tokens[4].split(";")));
            annotation.setCladeVerificationDomains(clade, Arrays.asList(tokens[5].split(";")));
            annotation.setCladeTerminationRule(clade, Arrays.asList(tokens[6].split(";")));
            annotation.setCladeNonElongating(clade, tokens[7].equalsIgnoreCase("yes"));
            annotation.setCladeVerificationMandatory(clade, tokens[8].equalsIgnoreCase("yes"));
            annotation.setCladeTerminationBoundary(clade, tokens[9].equalsIgnoreCase("yes"));
        }
        reader.close();
    }


    public CladeAnnotation getCladeAnnotation() {
        return annotation;
    }
}
