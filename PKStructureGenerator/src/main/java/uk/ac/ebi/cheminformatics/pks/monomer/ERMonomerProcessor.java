package uk.ac.ebi.cheminformatics.pks.monomer;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * ER domain signals that any double bond in backbone of the monomer to be added should be changed to a single bond.
 * This processor handles that.
 */
public class ERMonomerProcessor implements MonomerProcessor {

    @Override
    public void modify(PKMonomer monomer) {
        for (IBond bond : monomer.getMolecule().bonds()) {
            /**
             * TODO a better version of this would be to be limited between
             * C1 and C2, or them and the connecting atoms.
             */
            int carbonsInBond = 0;
            boolean anRGroupInBond = false;
            for (IAtom atomInBond : bond.atoms()) {
                if (atomInBond.getSymbol().equals("C"))
                    carbonsInBond++;
                if (atomInBond instanceof IPseudoAtom)
                    anRGroupInBond = true;
            }

            if (bond.getOrder().equals(IBond.Order.DOUBLE)
                    && (carbonsInBond == 2 || (carbonsInBond == 1 && anRGroupInBond))) {
                bond.setOrder(IBond.Order.SINGLE);
                // add 1 hydrogen to both atoms in the bond.
                for (IAtom atom : bond.atoms()) {
                    atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
                }
            }
        }
    }
}
