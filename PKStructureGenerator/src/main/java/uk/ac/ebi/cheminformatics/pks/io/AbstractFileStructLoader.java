package uk.ac.ebi.cheminformatics.pks.io;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.FileReader;

public abstract class AbstractFileStructLoader {

    String path;
    String cladeName;
    String errorLabel = "Could not read molecule for PKS Clade monomer: ";

    public IAtomContainer loadStructure() throws Exception {
        MDLV2000Reader reader =
                new MDLV2000Reader(new FileReader(this.path));
        IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);
        return mol;
    }
}
