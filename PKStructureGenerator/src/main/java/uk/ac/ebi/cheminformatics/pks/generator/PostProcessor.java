package uk.ac.ebi.cheminformatics.pks.generator;

import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

public interface PostProcessor {
    void process(PKStructure structure, PKMonomer monomer);
}
