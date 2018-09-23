package clustering;

/**
 * Interface for the implementation of distance clustering.metrics.
 *
 * @param <V> Value type to which distance metric is applied.
 * @author <a href="mailto:cf@christopherfrantz.org>Christopher Frantz</a>
 */
public interface DistanceMetric<V> {

    public double calculateDistance(V val1, V val2) throws DBSCANClusteringException;

}
