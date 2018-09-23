package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class CDomainSeqFeature extends DomainSeqFeature {
    public CDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    protected Double getThreshold() {
        return Double.parseDouble("1.80E-50");
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }
}
