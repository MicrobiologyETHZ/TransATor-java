package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class OMTDomainSeqFeature extends DomainSeqFeature {
    public OMTDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        // TODO: For which clades do we still need this processor?
        // return new OMTMonomerProcessor()
        return new NoActionMonomerProcessor();
    }
}
