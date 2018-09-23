package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

public interface PatternSeqFeature extends SequenceFeature {

    /**
     * Sets the monomer that the implementing PatternSeqFeature will modify according to its rules. This will be the same
     * monomer later retrieved from this feature.
     *
     * @param monomer
     */
    void setPKMonomer(PKMonomer monomer);
}
