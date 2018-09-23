package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Iterables;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PKStructureImageGenerator {

    private StructureDiagramGenerator structureGenerator;
    private final Aromaticity daylight =
            new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.all(6)));

    private DepictionGenerator depictionGenerator = new DepictionGenerator();

    public PKStructureImageGenerator() {
        this.structureGenerator = new StructureDiagramGenerator();
    }

    public BufferedImage generateStructureImage(PKStructure pkMolecule, Dimension dimension) throws CDKException {
        calculateProperties(pkMolecule.getMolecule());
        IAtomContainer moleculeWithCoordinates = generateCoordinatesForMolecule(pkMolecule.getMolecule());

        // color low confident prediction differently
        List<IAtomContainer> nonConfidentialMonomerMolecules = pkMolecule.getLowConfidentialityMonomers().stream()
                .map(PKMonomer::getMolecule)
                .collect(toList());

        List<Iterable<IChemObject>> bondsAndAtoms = nonConfidentialMonomerMolecules.stream().map(molecule ->
                Iterables.concat(molecule.atoms(), molecule.bonds()))
                .collect(toList());

        return depictionGenerator
                .withSize(dimension.getWidth(), dimension.getHeight())
                .withHighlight(Iterables.concat(bondsAndAtoms), Color.LIGHT_GRAY)
                .depict(moleculeWithCoordinates)
                .toImg();
    }

    private IAtomContainer generateCoordinatesForMolecule(IAtomContainer molecule) throws CDKException {
        molecule.setStereoElements(new ArrayList<>());

        structureGenerator.setMolecule(molecule, false);

        // use false to avoid cloning, this modifies the original molecule.
        structureGenerator.generateCoordinates();

        IAtomContainer structure = structureGenerator.getMolecule();
        removeStereoChemistry(structure);

        return structure;
    }

    private void removeStereoChemistry(IAtomContainer structure) {
        for (IBond bond : structure.bonds()) {
            switch (bond.getStereo()) {
                case UP_OR_DOWN:
                case UP_OR_DOWN_INVERTED:
                case E_OR_Z:
                    bond.setStereo(IBond.Stereo.NONE);
                    break;
            }
        }
    }

    private void calculateProperties(IAtomContainer molecule) throws CDKException {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule);
        daylight.apply(molecule);
    }

}
