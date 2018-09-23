package uk.ac.ebi.cheminformatics.pks.parser;

import clustering.DBSCANClusterer;
import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.generator.DistanceMetricSequenceFeatures;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory.makeSequenceFeature;

public final class FeatureSelection {

    private static final Logger LOGGER = Logger.getLogger(FeatureSelection.class);

    public static Stream<SequenceFeature> bestMatchCascade(Stream<SequenceFeature> features, int cascadeHeight) {
        return bestMatchCascade(features, cascadeHeight, 5, 20);
    }

    public static Stream<SequenceFeature> bestMatchCascade(Stream<SequenceFeature> featureStream, int cascadeHeight, int minCluster, int maxDistance) {

        List<SequenceFeature> features = featureStream.collect(toList());

        if (features.isEmpty()) {
            return Stream.empty();
        }

        List<List<SequenceFeature>> clusteredByPosition = FeatureSelection.clusterByAlignment(features, minCluster, maxDistance);

        return clusteredByPosition.stream().flatMap(group -> {

            Stream<SequenceFeature> cascade = group.stream()
                    .sorted(comparingDouble((SequenceFeature feature) -> feature.getEValue().orElseThrow(() -> new IllegalStateException("EValue is required for clustering"))))
                    .limit(cascadeHeight);

            String debugGroup = group.stream()
                    .sorted(comparingDouble((SequenceFeature feature) -> feature.getEValue().orElseThrow(() -> new IllegalStateException("EValue is required for clustering"))))
                    .limit(5)
                    .map(seq -> seq.getName() + " " + seq.getLabel() + " " + seq.getRange() + " " + seq.getEValue().orElse(0.0))
                    .collect(Collectors.joining("\n"));

            LOGGER.info(debugGroup + "\n");

            List<SequenceFeature> collectedCascade = cascade.collect(toList());

            Map<Integer, Optional<Double>> indexToConfidentiality = getConfidentialityMapPerIndex(collectedCascade);

            String clusterId = UUID.randomUUID().toString();

            return mapWithIndex(collectedCascade.stream(),
                    (seq, index) -> {
                        Optional<Double> confidentialityLookup = indexToConfidentiality.getOrDefault((int) index, Optional.empty());
                        return makeSequenceFeature(seq.getOriginatingFeatureFileLine(), (int) index + 1, confidentialityLookup, Optional.of(clusterId));
                    });
        });
    }

    private static Map<Integer, Optional<Double>> getConfidentialityMapPerIndex(List<SequenceFeature> collect) {

        Map<Integer, Optional<Double>> ratios = new HashMap<>();

        if (2 > collect.size()) {
            return ratios;
        }

        for (int i = 0; i < collect.size() - 1; i++) {
            SequenceFeature current = collect.get(i);
            SequenceFeature next = collect.get(i + 1);

            Optional<Double> confidence =
                    next.getEValue().flatMap(nextEValue ->
                            current.getEValue().flatMap(currentEValue ->
                                    Optional.of(currentEValue / nextEValue)));
            ratios.put(i, confidence);
        }

        return ratios;
    }

    private static List<List<SequenceFeature>> clusterByAlignment(List<SequenceFeature> sequenceFeatures, int minCluster, int maxDistance) {
        try {

            DBSCANClusterer<SequenceFeature> clusterer = new DBSCANClusterer<>(sequenceFeatures, minCluster, maxDistance, new DistanceMetricSequenceFeatures());

            return clusterer.performClustering();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
