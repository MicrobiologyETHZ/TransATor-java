package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class ALDomainSeqFeature extends DomainSeqFeature {
    public ALDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    protected Double getThreshold() {
        return Double.parseDouble("5.40E-27");
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }
}
