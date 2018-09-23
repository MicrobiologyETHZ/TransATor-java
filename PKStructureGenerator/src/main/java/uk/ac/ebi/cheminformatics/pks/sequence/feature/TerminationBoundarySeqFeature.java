package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import com.google.common.collect.Range;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessorFactory;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Collection;
import java.util.Optional;

public class TerminationBoundarySeqFeature implements SequenceFeature {

    private PKMonomer monomer;
    private PostProcessor postProcessor;
    private final String cladeId;

    public TerminationBoundarySeqFeature(String cladeId) {
        this.cladeId = cladeId;
        setupMonomer(cladeId);
    }

    private void setupMonomer(String cladeId) {
        CladeAnnotation annotation = CladeAnnotationFactory.getInstance();
        this.monomer = new PKMonomer(cladeId, annotation);
        this.postProcessor = PostProcessorFactory.getPostProcessor(cladeId, annotation);
    }

    @Override
    public PKMonomer getMonomer() {
        return monomer;
    }

    @Override
    public String getName() {
        return this.cladeId;
    }

    @Override
    public boolean hasPostProcessor() {
        return postProcessor != null;
    }

    @Override
    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    @Override
    public void setSubFeatures(Collection<SequenceFeature> subfeatures) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }

    @Override
    public boolean isSignificant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Range<Integer> getRange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Double> getScore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Double> getEValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Integer> getRanking() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Double> getConfidentiality() {
        return Optional.empty();
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Boolean> getVerificationPass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSubtype() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> getClusterId() {
        return Optional.empty();
    }

    @Override
    public FeatureFileLine getOriginatingFeatureFileLine() {
        throw new UnsupportedOperationException();
    }
}
