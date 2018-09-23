package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IBond;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

public class MissingBondOrderVerifier implements Verifier {
    @Override
    public boolean verify(PKStructure structure) {
        for (IBond bond : structure.getMolecule().bonds()) {
            if (bond.getOrder() == null || bond.getOrder().equals(IBond.Order.UNSET))
                return true;
        }
        return false;
    }

    @Override
    public String descriptionMessage() {
        return "Bonds found with missing order";
    }
}
