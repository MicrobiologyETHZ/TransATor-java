package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

public class SingleConnectedComponentVerifier implements Verifier {
    @Override
    public boolean verify(PKStructure structure) {
        IAtomContainer mol = structure.getMolecule();
        return !ConnectivityChecker.isConnected(mol);
    }

    @Override
    public String descriptionMessage() {
        return "Disconnected components found";
    }
}
