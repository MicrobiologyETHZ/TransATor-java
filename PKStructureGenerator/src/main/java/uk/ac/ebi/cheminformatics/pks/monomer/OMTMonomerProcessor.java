package uk.ac.ebi.cheminformatics.pks.monomer;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * The presence of the OMT domain preceding the KS domain means that the monomer needs to have
 * a methyl group added to the the -OH present in the first carbon of the monomer.
 * <p>
 * TODO What happens if there is no -OH present? Currently, we will just skip it.
 * TODO What if there is a =O instead of an -OH? I presume that the same effect holds.
 */
public class OMTMonomerProcessor implements MonomerProcessor {
    @Override
    public void modify(PKMonomer monomer) {
        // First we locate the oxygen atom where we will attach the methyl group.
        // This atom is attached to carbon 1 of the monomer, the one which is bound to
        // the pre connection atom.
        IAtom carbon1 = monomer.getPreConnectionAtom();
        for (IAtom conToC1 : monomer.getMolecule().getConnectedAtomsList(carbon1)) {
            if (conToC1.getSymbol().equals("O")) {
                IAtom oxygenAtomInMonomer = conToC1;
                IAtom carbonForMethyl = SilentChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
                carbonForMethyl.setImplicitHydrogenCount(3);
                IBond Oxygen2MethylBond = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
                Oxygen2MethylBond.setOrder(IBond.Order.SINGLE);
                Oxygen2MethylBond.setAtom(oxygenAtomInMonomer, 0);
                Oxygen2MethylBond.setAtom(carbonForMethyl, 1);
                monomer.getMolecule().addAtom(carbonForMethyl);
                monomer.getMolecule().addBond(Oxygen2MethylBond);

                // Now lets fix the O atom based on the bond that is linking it to the C1
                IBond c1ToOxygen = monomer.getMolecule().getBond(oxygenAtomInMonomer, carbon1);
                if (c1ToOxygen.getOrder().equals(IBond.Order.DOUBLE)) {
                    // if the bond is double, we just switch it to single
                    c1ToOxygen.setOrder(IBond.Order.SINGLE);
                } else if (c1ToOxygen.getOrder().equals(IBond.Order.SINGLE)) {
                    // if the bond is single, we adjust the implicit hydrogen count, if applicable.
                    int implicitH = oxygenAtomInMonomer.getImplicitHydrogenCount();
                    if (implicitH >= 1)
                        oxygenAtomInMonomer.setImplicitHydrogenCount(implicitH - 1);
                }
            }
        }
    }
}
