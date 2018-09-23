package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

public final class FinalSeqFeature extends DomainSeqFeature {

    public FinalSeqFeature() {
        super(0, 0, "final", "final");
        CladeAnnotation annotation = CladeAnnotationFactory.getInstance();
        this.monomer = new PKMonomer("final", annotation);
    }

    @Override
    public boolean hasPostProcessor() {
        return false;
    }

    @Override
    public PostProcessor getPostProcessor() {
        return null;
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public Optional<Double> getScore() {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getEValue() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getRanking() {
        return Optional.empty();
    }

    @Override
    public String getType() {
        return "Finalizer";
    }

    @Override
    public Optional<Boolean> getVerificationPass() {
        return Optional.empty();
    }

    @Override
    public String getSubtype() {
        return "Finalizer";
    }

    @Override
    public String getLabel() {
        return "Finalizer";
    }

    @Override
    public FeatureFileLine getOriginatingFeatureFileLine() {
        return null;
    }
}
