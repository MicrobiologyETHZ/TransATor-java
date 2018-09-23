package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NRPSMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

/**
 * Sequence feature for NRPS annotations.
 */
public class NRPSDomSeqFeature extends DomainSeqFeature {

    String aminoAcid;

    public NRPSDomSeqFeature(FeatureFileLine parser) {
        super(parser);
        aminoAcid = parser.getName();
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NRPSMonomerProcessor(aminoAcid);
    }

    @Override
    public boolean isSignificant() {
        // TODO: read about the score, and what makes it significant
        return true;
    }
}
