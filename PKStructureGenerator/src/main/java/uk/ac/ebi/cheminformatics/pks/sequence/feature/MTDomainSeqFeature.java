package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class MTDomainSeqFeature extends DomainSeqFeature {
    public MTDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    protected Double getThreshold() {
        return Double.parseDouble("2.80E-56");
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }
}
