package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class DHDomainSeqFeature extends DomainSeqFeature {
    public DHDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    protected Double getThreshold() {
        return Double.parseDouble("1E-20");
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }
}
