package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class PatternSeqFeatureFactory {
    public static SequenceFeature makeSequenceFeature(FeatureFileLine featureFileLine) {
        return new ProteinPatternSeqFeature(featureFileLine);
    }
}
