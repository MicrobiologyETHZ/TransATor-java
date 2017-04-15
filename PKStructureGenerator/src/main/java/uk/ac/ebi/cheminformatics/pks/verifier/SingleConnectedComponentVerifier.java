package uk.ac.ebi.cheminformatics.pks.verifier;

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 11/6/15
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class SingleConnectedComponentVerifier implements Verifier {

    private static final Logger LOGGER = Logger.getLogger(SingleConnectedComponentVerifier.class);

    @Override
    public boolean verify(PKStructure struc) {
        IAtomContainer mol = struc.getMolecule();
        for(IBond bond : mol.bonds()) {
            for(IAtom atomInBond : bond.atoms()) {
                if(!mol.contains(atomInBond)) {
                    LOGGER.error(
                            "Atom "+atomInBond.getSymbol()+" "+atomInBond.getID()+" not part of molecule. Bond "+mol.getBondNumber(bond));
                }
            }
        }
        if(!ConnectivityChecker.isConnected(mol)) {
            IAtomContainerSet mols = ConnectivityChecker.partitionIntoMolecules(mol);
                    LOGGER.error("Number of different components: "+mols.getAtomContainerCount());
            System.out.println("Number of different components: "+mols.getAtomContainerCount());
            return true;
        }
        return false;
    }

    @Override
    public String descriptionMessage() {
        return "Disconnected components found";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
