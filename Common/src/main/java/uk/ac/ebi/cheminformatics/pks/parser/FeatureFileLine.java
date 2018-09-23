package uk.ac.ebi.cheminformatics.pks.parser;

import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureFileLine {

    /**
     * The start of the feature in amino acid coordinates (referenced to the sequence).
     *
     * @return start in aa.
     */
    public Integer getStart() {
        return start;
    }

    /**
     * The stop of the feature in amino acid coordinates (references to the sequence).
     *
     * @return stop in aa.
     */
    public Integer getStop() {
        return stop;
    }

    /**
     * The e-value of the feature, if available. For features that do not have an evalue, the file
     * contains a "N/A" string.
     *
     * @return e-value as String or "N/A".
     */
    public String getEvalue() {
        return evalue;
    }

    public String getScore() {
        return score;
    }

    public String getRanking() {
        return ranking;
    }

    public String getStackNumber() {
        return stackNumber;
    }

    public String getType() {
        return type;
    }

    public String getVerificationPass() {
        return verification_pass;
    }

    /**
     * Obtains the subtype qualifier, which is used in particular by the
     * KS clade domain annotator to signal that this sequence feature is a
     * KS.
     *
     * @return the subtype string
     */
    public String getSubtype() {
        return subtype;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    private final Integer start;
    private final Integer stop;
    private final String evalue;
    private final String score;
    // TODO: these should not be read in here anymore
    private String ranking;
    private final String stackNumber;
    private final String type;
    private final String subtype;
    private final String name;
    private final String label;
    private final String verification_pass;

    private Pattern cladeNamePat = Pattern.compile("(Clade_\\d+)([A-Za-z])");

    /**
     * Parses a line of the .feature file provided by the python executable.
     *
     * @param line from .feature file.
     */
    public FeatureFileLine(String line) {
        Iterator<String> tokens = Splitter.on("\t").split(line).iterator();
        start = Integer.parseInt(tokens.next());
        stop = Integer.parseInt(tokens.next());
        evalue = tokens.next();
        score = tokens.next();
        ranking = tokens.next();
        stackNumber = tokens.next();
        type = tokens.next();
        subtype = tokens.next();
        name = processName(tokens.next());
        label = tokens.next();
        verification_pass = tokens.hasNext() ? tokens.next() : "N/A";
    }

    private String processName(String name) {
        // if name ends in character instead of a number, remove everything after the number.
        // TODO we might not need this anymore. Check.
        Matcher cladeNameMatcher = cladeNamePat.matcher(name);
        if (cladeNameMatcher.find()) {
            return cladeNameMatcher.group(1);
        }
        return name;
    }

    public void setRanking(long ranking) {
        this.ranking = Long.toString(ranking);
    }
}
