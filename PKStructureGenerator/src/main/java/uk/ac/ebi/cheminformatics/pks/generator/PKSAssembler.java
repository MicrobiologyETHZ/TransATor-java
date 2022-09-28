package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.*;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.FinalSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.TerminationBoundarySeqFeature;
import uk.ac.ebi.cheminformatics.pks.verifier.*;

import java.util.*;

import static com.google.common.collect.ImmutableSet.of;

/**
 * Handles the assembly of the polyketide molecule, through the subsequent processing of the sequence features read.
 * <p>
 * TODO Deal gracefully with clades placed in the middle that can only be placed at the start because they lack an R1.
 */
public class PKSAssembler {

    private static final Logger LOGGER = LogManager.getLogger(PKSAssembler.class);

    private PKStructure structure;

    private List<SequenceFeature> toBePostProcessed;
    private List<SequenceFeature> subFeaturesForNextKS;

    // this is required for the termination rules
    private static final Set<String> terminationRelevantDomains = of(
            "KR", "DH", "ER", "MT", "OMT", "CR", "Oxy", "B");

    private static Map<Set<String>, String> terminationRules = new HashMap<>();

    static {
        ImmutableSet<String> betaHydroxy = of("KR");
        ImmutableSet<String> alphaBetaDoubleBonds = of("KR", "DH");
        ImmutableSet<String> reduced = of("KR", "DH", "ER");
        ImmutableSet<String> alphaMethylBetaHydroxy = of("KR", "MT");
        ImmutableSet<String> alphaMethylAlphaBetaDoubleBond = of("KR", "MT", "DH");
        ImmutableSet<String> betaMethoxy = of("KR", "OMT");
        ImmutableSet<String> betaOMethylAlphaMethyl = of("OMT", "MT");
        ImmutableSet<String> alphaMethyl = of("KR", "MT", "DH", "ER");
        ImmutableSet<String> exomethylene = of("CR", "CR");
        ImmutableSet<String> pyranFuranRing = of("KR", "DH", "PS");
        ImmutableSet<String> rearrangement = of("Oxy");
        ImmutableSet<String> vinolgousChainBranching = of("B");

        // TODO: clade_names to mol files? directly molfiles would be nice too...
        // TODO: where to read this from?
        terminationRules.put(betaHydroxy, "Clade_78");
        terminationRules.put(alphaBetaDoubleBonds, "Clade_65");
        terminationRules.put(reduced, "Clade_25");
        terminationRules.put(alphaMethylBetaHydroxy, "Clade_74");
        terminationRules.put(alphaMethylAlphaBetaDoubleBond, "Clade_83");
        terminationRules.put(betaMethoxy, "Clade_103");
        terminationRules.put(alphaMethyl, "Clade_52");
        terminationRules.put(exomethylene, "Clade_14");
        // TODO: post-processor needs to be added to this
        terminationRules.put(pyranFuranRing, "Clade_26");
        terminationRules.put(betaOMethylAlphaMethyl, "Clade_143");
        terminationRules.put(rearrangement, "Clade_35");
        terminationRules.put(vinolgousChainBranching, "Clade_12");
    }

    private Set<String> domainTypesSinceLastElongatingKs = new HashSet<>();

    private List<Verifier> verifiers;
    private CarbonHydrogenCountBalancer hydrogenCountBalancer;

    public PKSAssembler() {
        this.structure = new PKStructure();
        this.toBePostProcessed = new LinkedList<>();
        this.subFeaturesForNextKS = new LinkedList<>();
        this.verifiers = new LinkedList<>();
        this.verifiers.addAll(
                Arrays.asList(
                        new MissingBondOrderVerifier(), new SingleConnectedComponentVerifier(),
                        new StereoElementsVerifier()));
        this.hydrogenCountBalancer = new CarbonHydrogenCountBalancer();
    }


    /**
     * Given a sequenceFeature, it adds the next monomer to the PKS structure. The monomer is obtained from the sequence
     * feature. According to the sub-features found upstream, modifications can be exerted on the monomer.
     *
     * @param sequenceFeature
     */
    public void addMonomer(SequenceFeature sequenceFeature) {

        // TODO: implement cis-AT PKS functionality
        if (sequenceFeature.getName().equals("Clade_32")) {
            return;
        }

        if (!((sequenceFeature instanceof KSDomainSeqFeature))) {
            this.subFeaturesForNextKS.add(sequenceFeature);
            if (isTerminationRelevant(sequenceFeature)) {
                // TODO: we should only consider up until the TE domain
                this.domainTypesSinceLastElongatingKs.add(sequenceFeature.getSubtype());
            }
            return;
        }
        // From here, we are only looking at KS domains seq features.
        if (sequenceFeature.getMonomer().isNonElongating()) {
            return;
        }

        LOGGER.info("Adding monomer " + sequenceFeature.getName());
        if (sequenceFeature.getMonomer().getMolecule().getAtomCount() == 0) {
            // empty molecule for advancing only
            return;
        }

        processSubFeatures(sequenceFeature.getMonomer());

        if (structure.getMonomerCount() == 0) {
            // TODO: why 1d?
            structure.add(sequenceFeature.getMonomer(), sequenceFeature.getConfidentiality().orElse(1d));
        } else {
            if (sequenceFeature.getMonomer().isTerminationBoundary()) {
                terminateAtBoundary();
            }
            growByFeature(sequenceFeature);
        }

        runVerifiersForFeature(sequenceFeature);

        if (!sequenceFeature.getMonomer().isNonElongating()) {
            this.domainTypesSinceLastElongatingKs.clear();
        }
    }

    private void terminateAtBoundary() {
        Optional<SequenceFeature> boundarySeqFeature = findBoundarySeqFeature();
        boundarySeqFeature.ifPresent(feature -> growByFeature(feature));
        this.domainTypesSinceLastElongatingKs.clear();
    }

    private void growByFeature(SequenceFeature sequenceFeature) {

        growByMonomer(sequenceFeature.getMonomer(), sequenceFeature.getConfidentiality().orElse(1d));

        // here we do post processing specific to the particular clade just added
        if (sequenceFeature.hasPostProcessor()) {
            toBePostProcessed.add(sequenceFeature);
        }
    }

    private void growByMonomer(PKMonomer monomer, double confidentiality) {
        IAtom connectionAtomInChain = structure.getConnectionAtom();
        IBond connectionBondInMonomer = monomer.getConnectionBond();

        IAtomContainer structureMolecule = structure.getMolecule();

        removeGenericConnection(connectionAtomInChain, structureMolecule);

        IAtomContainer monomerMolecule = monomer.getMolecule();
        int indexToRemove = connectionBondInMonomer.getAtom(0) instanceof IPseudoAtom ? 0 : 1;

        monomerMolecule.removeAtomOnly(connectionBondInMonomer.getAtom(indexToRemove));

        connectionBondInMonomer.setAtom(connectionAtomInChain, indexToRemove);

        structure.add(monomer, confidentiality);

        // adjust implicit hydrogen atoms
        hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(0));
        hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(1));
    }

    private boolean isTerminationRelevant(SequenceFeature sequenceFeature) {
        return terminationRelevantDomains.contains(sequenceFeature.getSubtype());
    }

    private Optional<SequenceFeature> findBoundarySeqFeature() {

        // TODO: we need to somehow figure out if the last KS was non-elongating. In that case, do not add the beta-keto
        if (domainTypesSinceLastElongatingKs.size() == 0) {
            //  beta-keto
            return Optional.of(new TerminationBoundarySeqFeature("Clade_45"));
        }
        String cladeName = terminationRules.get(domainTypesSinceLastElongatingKs);

        if (cladeName == null) {
            return Optional.empty();
        }

        return Optional.of(new TerminationBoundarySeqFeature(cladeName));
    }


    /**
     * Removes the generic atom connected to the connectionAtomInChain, and the bond connecting them. Number of
     * hydrogens connected to the connectionAtomInChain is not modified. The order of the bond removed is obtained.
     *
     * @param connectionAtomInChain
     * @param structureMol
     * @return order of the bond removed.
     */
    private IBond removeGenericConnection(IAtom connectionAtomInChain, IAtomContainer structureMol) {
        IAtom toRemoveA = null;
        for (IBond connected : structureMol.getConnectedBondsList(connectionAtomInChain)) {
            for (IAtom atomCon : connected.atoms()) {
                if (atomCon.equals(connectionAtomInChain))
                    continue;
                if (atomCon instanceof IPseudoAtom && ((IPseudoAtom) atomCon).getLabel().equals("R2")) {
                    toRemoveA = atomCon;
                    break;
                }
            }
        }
        IBond bondToRemove = null;
        if (toRemoveA != null) {
            //order = structureMol.getBond(connectionAtomInChain,toRemoveA).getOrder().ordinal();
            bondToRemove = structureMol.getBond(connectionAtomInChain, toRemoveA);
            structureMol.removeBond(bondToRemove);
            structureMol.removeAtom(toRemoveA);
        }
        return bondToRemove;
    }

    private void runVerifiersForFeature(SequenceFeature feature) {
        runVerifiersForFeature(feature, "");
    }

    private void runVerifiersForFeature(SequenceFeature feature, String additionalMessage) {
        for (Verifier verifier : verifiers) {
            if (verifier.verify(structure)) {
                LOGGER.error(verifier.descriptionMessage() + " after " + feature.getName() + " " + additionalMessage);
            }
        }
    }


    /**
     * Deals with all the modifications that different domains upstream of the current KS
     * exert to the monomer added by this current KS.
     */
    private void processSubFeatures(PKMonomer monomer) {
        for (SequenceFeature feat : subFeaturesForNextKS) {
            MonomerProcessor processor = feat.getMonomerProcessor();
            processor.modify(monomer);
            runVerifiersForFeature(feat, "after processing sub-features.");
        }
        subFeaturesForNextKS.clear();
    }

    public void postProcess() {
        for (SequenceFeature toPP : this.toBePostProcessed) {
            PostProcessor proc = toPP.getPostProcessor();
            proc.process(structure, toPP.getMonomer());
            runVerifiersForFeature(toPP, "after post-processing");
        }
    }

    public void addFinalExtension() {

        // TODO: there is an edge case, when there is also a "NRPS" boundary at the end
        terminateAtBoundary();

        // TODO: how can we determine confidentiality here?
        growByMonomer(new FinalSeqFeature().getMonomer(), 1d);

    }

    public PKStructure getStructure() {
        return structure;
    }

}
