package uk.ac.ebi.cheminformatics.pks.verifier;

import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;


public interface Verifier {

    /**
     * Runs implemented verification, producing true if the error searched for
     * is present.
     *
     * @param structure
     * @return true if the feature is present.
     */
    boolean verify(PKStructure structure);

    String descriptionMessage();

}
