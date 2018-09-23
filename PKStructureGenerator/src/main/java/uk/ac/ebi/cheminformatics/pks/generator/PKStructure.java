package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a growing PK chain.
 */
public class PKStructure {

    private IAtomContainer chain;
    private IAtom connectionAtom;
    private List<PKMonomer> monomers;

    private List<PKMonomer> lowConfidentialityMonomers;

    private final static double CONFIDENTIALITY_THRESHOLD = 1E-20;

    public PKStructure() {
        this.monomers = new ArrayList<>();
        this.lowConfidentialityMonomers = new ArrayList<>();

        this.chain = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
    }

    /**
     * Returns the atom of the growing PK chain where the next monomer should be connected to. This atom will be connected
     * to a generic atom R2 which should be stripped before adding a monomer.
     *
     * @return connection atom.
     */
    protected IAtom getConnectionAtom() {
        return connectionAtom;
    }

    /**
     * Adds the monomer to PKStructure. The necessary steps of deleting generic atoms and linking underlying molecules
     * are handled by the {@link PKSAssembler} class.
     *
     * @param monomer         to be added.
     * @param confidentiality
     */
    protected void add(PKMonomer monomer, double confidentiality) {
        this.connectionAtom = monomer.getPosConnectionAtom();
        monomers.add(monomer);
        this.chain.add(monomer.getMolecule());

        if (confidentiality > CONFIDENTIALITY_THRESHOLD) {
            lowConfidentialityMonomers.add(monomer);
        }
    }

    /**
     * Returns the growing chain in its current status, including generic atoms that have not been removed.
     *
     * @return current CDK object for the chain.
     */
    public IAtomContainer getMolecule() {
        return chain;
    }

    public int getMonomerCount() {
        return monomers.size();
    }

    public Integer getMonomerIndex(PKMonomer monomer) {
        return this.monomers.indexOf(monomer);
    }

    public PKMonomer getMonomer(int i) {
        return monomers.get(i);
    }

    public List<PKMonomer> getMonomers() {
        return monomers;
    }

    public List<PKMonomer> getLowConfidentialityMonomers() {
        return lowConfidentialityMonomers;
    }

    /**
     * Removes the last monomer available in the nacent polyketide chain. This is used by
     * the non-extending clades, where the previous added monomer is changed for the one of the
     * non-extending clade (simulating a modification of the polyketide) instead of simply adding
     * the new one (and not removing the previous one).
     */
    public void removeLastMonomer() {
        if (getMonomerCount() > 0) {
            // first we get the pointer to the last monomer
            PKMonomer previous = getMonomer(getMonomerCount() - 1);
            IBond connectionBondPrev = previous.getConnectionBond();
            IAtom toKeepPrev2Previous = null;
            for (IAtom atom : connectionBondPrev.atoms()) {
                // if the atom in the connection bond doesn't belong to the current
                // monomer, then it belong to the previous one to it, and as such
                // is the one that we want to keep.
                if (!previous.getMolecule().contains(atom)) {
                    toKeepPrev2Previous = atom;
                }
            }
            if (toKeepPrev2Previous != null) {
                chain.remove(previous.getMolecule());
                chain.removeBond(connectionBondPrev);
                //monomers.remove(previous); Check whether toKeepPrev2Previous is in chain
                //and whether it has any bonds to elements not in the chain.


                //if the chain has 2 or more monomers
                if (getMonomerCount() - 2 >= 0) {
                    PKMonomer toStay = getMonomer(getMonomerCount() - 2);
                    //This atom should be already on the chain, no need to add it again.
                    //chain.addAtom(toStay.getPosConnectionAtom());
                    // Now we need to add again the R2 group to the
                    for (IBond bondInToStay : toStay.getMolecule().getConnectedBondsList(toStay.getPosConnectionAtom())) {
                        if (!chain.contains(bondInToStay)) {
                            chain.addBond(bondInToStay);
                            for (IAtom inBond : bondInToStay.atoms()) {
                                if (!chain.contains(inBond))
                                    chain.addAtom(inBond);
                            }
                        }
                    }

                    monomers.remove(previous);
                    connectionAtom = toStay.getPosConnectionAtom();
                }

            }
        }
    }
}
