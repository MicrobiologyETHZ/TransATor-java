package uk.ac.ebi.cheminformatics.pks.io;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Loader of Mol files, which can be for monomer, amino acids, stream based or file based.
 */
public interface StructureLoader {

    /**
     * Loads the structure defined for the loader.
     *
     * @return an {@link IAtomContainer} with the molecule.
     */
    IAtomContainer loadStructure() throws Exception;

}
