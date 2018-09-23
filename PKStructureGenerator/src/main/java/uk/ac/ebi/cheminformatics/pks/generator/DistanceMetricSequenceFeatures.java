package uk.ac.ebi.cheminformatics.pks.generator;

import clustering.DBSCANClusteringException;
import clustering.DistanceMetric;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import static java.lang.Math.abs;

public class DistanceMetricSequenceFeatures implements DistanceMetric<SequenceFeature> {

    @Override
    public double calculateDistance(SequenceFeature a, SequenceFeature b) throws DBSCANClusteringException {
        Integer aStart = a.getRange().lowerEndpoint();
        Integer aStop = a.getRange().upperEndpoint();

        Integer bStart = b.getRange().lowerEndpoint();
        Integer bStop = b.getRange().upperEndpoint();

        return abs(aStart - bStart) + abs(aStop - bStop);
    }
}
