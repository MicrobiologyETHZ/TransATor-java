package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.verifier.CarbonHydrogenCountBalancer;

public class CyclizationPostProcessor implements PostProcessor {

    CarbonHydrogenCountBalancer balancer = new CarbonHydrogenCountBalancer();

    @Override
    public void process(PKStructure structure, PKMonomer monomer) {
        Integer currentIndex = structure.getMonomerIndex(monomer);
        PKMonomer upstream2 = structure.getMonomer(currentIndex - 2);
        // We need to check whether upstream2 monomer has a C=O that can attack
        // the current clade 3 C=C bond.
        IBond upstream2C_bond_O = getCBondToOxygen(upstream2);
        if (upstream2C_bond_O == null) {
            attemptToAddCOBondOnUpstream2C(upstream2, structure);
            upstream2C_bond_O = getCBondToOxygen(upstream2);
        }
        IBond currentC_dbond_C = getCDoubleBondC(monomer, structure);
        IAtom cInCurrentMonomerToAttack = monomer.getPosConnectionAtom();
        for (IAtom atomConToCinCurrentMonomer : structure.getMolecule().getConnectedAtomsList(cInCurrentMonomerToAttack)) {
            if (upstream2C_bond_O != null && currentC_dbond_C != null
                    && currentC_dbond_C.contains(atomConToCinCurrentMonomer)) {
                IAtom oxygenInDoubleBond2Upstream =
                        upstream2C_bond_O.getAtom(0).getSymbol().equals("O")
                                ? upstream2C_bond_O.getAtom(0) : upstream2C_bond_O.getAtom(1);
                upstream2C_bond_O.setOrder(IBond.Order.SINGLE);
                currentC_dbond_C.setOrder(IBond.Order.SINGLE);
                balancer.balanceImplicitHydrogens(structure.getMolecule(), cInCurrentMonomerToAttack);
                IBond upstreamOattackCurrentCbond = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
                upstreamOattackCurrentCbond.setOrder(IBond.Order.SINGLE);
                upstreamOattackCurrentCbond.setAtoms(new IAtom[]{oxygenInDoubleBond2Upstream, atomConToCinCurrentMonomer});
                structure.getMolecule().addBond(upstreamOattackCurrentCbond);
                // update implicit hydrogens for C and O than now form new bond C-O
                balancer.balanceImplicitHydrogens(structure.getMolecule(), atomConToCinCurrentMonomer);
                if (oxygenInDoubleBond2Upstream.getImplicitHydrogenCount() != null) {
                    oxygenInDoubleBond2Upstream.
                            setImplicitHydrogenCount(
                                    Math.max(oxygenInDoubleBond2Upstream.getImplicitHydrogenCount() - 1, 0));
                }
            }
        }
    }

    private void attemptToAddCOBondOnUpstream2C(PKMonomer upstream2, PKStructure structure) {
        IAtom cPosConnection = upstream2.getPosConnectionAtom();
        for (IAtom cConnectedToCposConnection : upstream2.getMolecule().getConnectedAtomsList(cPosConnection)) {
            if (!cConnectedToCposConnection.getSymbol().equals("C") || !upstream2.getMolecule().contains(cConnectedToCposConnection))
                continue;
            int hs = AtomContainerManipulator.countHydrogens(structure.getMolecule(), cConnectedToCposConnection);
            if (3 > hs && hs >= 1) {
                IBond coBond = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
                IAtom oxygen = SilentChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
                coBond.setAtom(cConnectedToCposConnection, 0);
                coBond.setAtom(oxygen, 1);
                coBond.setOrder(IBond.Order.SINGLE);
                structure.getMolecule().addBond(coBond);
                upstream2.getMolecule().addBond(coBond);
                structure.getMolecule().addAtom(oxygen);
                upstream2.getMolecule().addAtom(oxygen);
                cConnectedToCposConnection.setImplicitHydrogenCount(4 - cConnectedToCposConnection.getBondOrderSum().intValue());
                break;
            }
        }
    }

    private IBond getCBondToOxygen(PKMonomer monomer) {
        IBond bond = getS1BondToS2(monomer, "C", "O", IBond.Order.SINGLE);
        if (bond == null)
            bond = getS1BondToS2(monomer, "C", "O", IBond.Order.DOUBLE);
        return bond;
    }

    private IBond getCDoubleBondC(PKMonomer monomer, PKStructure structure) {
        IAtom cInMonomer = monomer.getPosConnectionAtom();
        for (IAtom cConToCInMonomer : structure.getMolecule().getConnectedAtomsList(cInMonomer)) {
            if (cConToCInMonomer.getSymbol().equals("C")) {
                for (IBond bond : structure.getMolecule().getConnectedBondsList(cConToCInMonomer)) {
                    for (IAtom atom : bond.atoms()) {
                        if (atom.equals(cConToCInMonomer))
                            continue;
                        if (atom.getSymbol().equals("C") && !atom.equals(cInMonomer))//bond.getOrder().equals(IBond.Order.DOUBLE))
                            return bond;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns a S1=S2 bond within a monomer.
     *
     * @param monomer
     * @return
     */
    private IBond getS1BondToS2(PKMonomer monomer, String symbol1, String symbol2, IBond.Order order) {
        IAtomContainer mon = monomer.getMolecule();
        for (IBond bond : mon.bonds()) {
            if (bond.getAtomCount() == 2 && bond.getOrder().equals(order)) {

                if ((bond.getAtom(0).getSymbol().equals(symbol1)
                        && bond.getAtom(1).getSymbol().equals(symbol2))
                        ||
                        (bond.getAtom(1).getSymbol().equals(symbol1)
                                && bond.getAtom(0).getSymbol().equals(symbol2))) {
                    return bond;
                }
            }
        }
        return null;
    }


}
