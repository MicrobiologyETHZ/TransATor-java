package uk.ac.ebi.cheminformatics.pks.monomer;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Attaches a methyl group to Carbon 2 of the monomer.
 * TODO Do we remove whatever is bound to this carbon atom that doesn't belong to the main chain?
 * Currently, yes.
 */
public class MTMonomerProcessor implements MonomerProcessor {
    @Override
    public void modify(PKMonomer monomer) {
        // We first locate the desired carbon, carbon 2, which is connected to the
        // post connection bond
        IAtom carbon2 = monomer.getPosConnectionAtom();
        // We check whether there is anything that we need to remove
        IAtom carbon1 = monomer.getPreConnectionAtom();
        for (IAtom conToCarbon2 : monomer.getMolecule().getConnectedAtomsList(carbon2)) {
            if (conToCarbon2 instanceof IPseudoAtom)
                continue;
            if (conToCarbon2.equals(carbon1))
                continue;
            // if we got here is because we have found something additional to either the R group
            // or carbon 1 (attached to carbon 2). This atom and whatever is connected through it
            // should be removed.
            IBond bond2rm = monomer.getMolecule().getBond(conToCarbon2, carbon2);
            monomer.getMolecule().removeBond(bond2rm);
            IAtomContainer partToRemoveFromMonomer = null;
            boolean foundBackbone = false;
            for (IAtomContainer part
                    : ConnectivityChecker.partitionIntoMolecules(monomer.getMolecule()).atomContainers()) {
                if (part.contains(carbon2) && part.contains(carbon1))
                    foundBackbone = true;
                else {
                    partToRemoveFromMonomer = part;
                }
            }
            // if the part identified to remove (a connective component) doesn't include
            // the carbon1 and carbon2, then we are happy to get rid of it.
            if (partToRemoveFromMonomer != null && foundBackbone) {
                monomer.getMolecule().remove(partToRemoveFromMonomer);
            } else {
                // otherwise, we put the bond removed back
                monomer.getMolecule().addBond(bond2rm);
            }
        }

        // Now we add the methyl to carbon 2:
        IAtom carbonForMethyl = SilentChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        carbonForMethyl.setImplicitHydrogenCount(3);
        IBond Carbon2ToMethylBond = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
        Carbon2ToMethylBond.setOrder(IBond.Order.SINGLE);
        Carbon2ToMethylBond.setAtom(carbon2, 0);
        Carbon2ToMethylBond.setAtom(carbonForMethyl, 1);
        monomer.getMolecule().addAtom(carbonForMethyl);
        monomer.getMolecule().addBond(Carbon2ToMethylBond);

    }
}
