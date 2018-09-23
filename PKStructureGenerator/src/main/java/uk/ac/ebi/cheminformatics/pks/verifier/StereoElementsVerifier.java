package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

import java.util.LinkedList;
import java.util.List;

public class StereoElementsVerifier implements Verifier {

    private List<IStereoElement> stereoElementsToDel;
    private PKStructure structure;

    @Override
    public boolean verify(PKStructure structure) {
        IAtomContainer mol = structure.getMolecule();
        this.stereoElementsToDel = new LinkedList<>();
        this.structure = structure;
        for (IStereoElement element : mol.stereoElements()) {
            if (element instanceof IDoubleBondStereochemistry) {
                for (IBond bondInStereo : ((IDoubleBondStereochemistry) element).getBonds()) {
                    if (!structure.getMolecule().contains(bondInStereo)) {
                        stereoElementsToDel.add(element);
                    }
                }
            }
        }
        return stereoElementsToDel.size() > 0;
    }

    @Override
    public String descriptionMessage() {
        return "Unsynced stereo elements found";
    }
}
