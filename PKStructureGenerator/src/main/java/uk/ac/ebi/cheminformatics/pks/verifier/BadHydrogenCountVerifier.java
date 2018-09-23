package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IAtom;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

import java.util.HashSet;
import java.util.Set;

// TODO: Is this still required?
public class BadHydrogenCountVerifier implements Verifier {

    Set<IAtom> seen;

    public BadHydrogenCountVerifier() {
        this.seen = new HashSet<>();
    }

    @Override
    public boolean verify(PKStructure structure) {
        for (IAtom atom : structure.getMolecule().atoms()) {
            if (atom.getSymbol().equals("C") && !seen.contains(atom)) {
                if (atom.getBondOrderSum() != null && atom.getBondOrderSum() + atom.getImplicitHydrogenCount() > 4.0d) {
                    this.seen.add(atom);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String descriptionMessage() {
        return "Carbon Atoms with bad hydrogen count found";
    }
}
