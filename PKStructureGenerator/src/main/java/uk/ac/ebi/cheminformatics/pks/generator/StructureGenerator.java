package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Streams;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureSelection;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.cheminformatics.pks.parser.FeatureParser.parse;

@SuppressWarnings("Convert2MethodRef")
public class StructureGenerator {

    private PKStructure structure;

    private final List<SequenceFeature> sequenceFeatures;

    public StructureGenerator(Path featuresFile) {
        sequenceFeatures = parse(featuresFile);
    }

    public void run() {

        PKSAssembler assembler = new PKSAssembler();

        List<SequenceFeature> significantFeatures = sequenceFeatures.stream()
                // apply a cut-off to filter out false positives
                .filter(SequenceFeature::isSignificant)
                .collect(toList());

        // TODO: this code is currently duplicated
        Stream<SequenceFeature> nonKs = significantFeatures.stream().filter(seq -> !(seq instanceof KSDomainSeqFeature));

        List<SequenceFeature> nonKsAsList = nonKs.collect(toList());

        Stream<SequenceFeature> nonDomains = nonKsAsList.stream().filter(seq -> !(seq instanceof DomainSeqFeature));

        Stream<SequenceFeature> nonKsDomains = nonKsAsList.stream().filter(seq -> seq instanceof DomainSeqFeature);

        // thee parameter minCluster of 1 ensures that we get all domains, which do not overlap, back again
        Stream<SequenceFeature> bestNonKsDomains = FeatureSelection.bestMatchCascade(nonKsDomains, 1, 1, 20);

        Stream<SequenceFeature> ks = significantFeatures.stream().filter(KSDomainSeqFeature.class::isInstance);

        // in order for us to calculate the confidentiality we need to cascade at least 2 ks sequences
        Stream<SequenceFeature> bestKs = FeatureSelection.bestMatchCascade(ks, 2)
                .filter(kss -> kss.getRanking().isPresent() && kss.getRanking().get() == 1);

        List<SequenceFeature> selectedSequenceFeatures = Streams.concat(bestNonKsDomains, bestKs, nonDomains)
                .sorted(comparingInt(feature -> feature.getRange().lowerEndpoint()))
                .collect(toList());

        // TODO: filter out all with name "AllTransKs"
        selectedSequenceFeatures.forEach(feature -> {
            assembler.addMonomer(feature);
        });

        assembler.addFinalExtension();
        assembler.postProcess();

        this.structure = assembler.getStructure();
    }

    public PKStructure getStructure() {
        return this.structure;
    }
}
