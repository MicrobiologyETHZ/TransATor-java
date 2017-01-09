package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NRPSMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Sequence feature for NRPS annotations.
 */
public class NRPSDomSeqFeature extends DomainSeqFeature implements SequenceFeature{

    String aminoAcid;

    public NRPSDomSeqFeature(FeatureFileLineParser parser) {
        super(parser);
        aminoAcid = parser.getName();
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NRPSMonomerProcessor(aminoAcid);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
