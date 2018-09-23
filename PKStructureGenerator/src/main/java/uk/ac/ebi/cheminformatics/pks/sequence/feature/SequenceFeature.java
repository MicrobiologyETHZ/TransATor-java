package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import com.google.common.collect.Range;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Collection;
import java.util.Optional;

public interface SequenceFeature {

    /**
     * Returns the PKMonomer associated with this sequence feature.
     * This PKMonomer in turns contains the structure of the monomer
     * associated to this sequence feature.
     *
     * @return PKMonomer with structure associated to this sequence feature.
     */
    PKMonomer getMonomer();

    /**
     * The name of the sequence feature, which in the case of the clade would
     * correspond to the clade identifier.
     *
     * @return the name of the sequence feature.
     */
    String getName();

    /**
     * Whether the is a post processor assigned to this sequence feature.
     *
     * @return true if there is a post processor assigned.
     */
    boolean hasPostProcessor();

    /**
     * Obtains the post processor (if any) associated to this clade.
     *
     * @return
     */
    PostProcessor getPostProcessor();

    /**
     * Sets the sub feature list with the provided collection. This is used to add
     * as sub features the a domains/patterns found before the KS.
     *
     * @param subfeatures to set.
     */
    void setSubFeatures(Collection<SequenceFeature> subfeatures);

    /**
     * Returns the monomer processor for this sequence feature. Sequence features that do not
     * require any monomer processing should return {@link uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor}.
     *
     * @return the monomer processor for this sequence feature.
     */
    MonomerProcessor getMonomerProcessor();

    /**
     * If true, the seq has enough evidence (for example from a low e-score) to justify its inclusion in the final structure
     */
    boolean isSignificant();

    Range<Integer> getRange();

    Optional<Double> getScore();

    Optional<Double> getEValue();

    Optional<Integer> getRanking();

    Optional<Double> getConfidentiality();

    String getType();

    Optional<Boolean> getVerificationPass();

    String getSubtype();

    String getLabel();

    Optional<String> getClusterId();

    /**
     * Ugly hack to allow easy copying of SequenceFeatures
     * TODO: replace with normal constructor having all required fields
     */
    FeatureFileLine getOriginatingFeatureFileLine();
}
