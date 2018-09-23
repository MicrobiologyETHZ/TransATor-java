package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

import static java.lang.Double.parseDouble;


public class ProteinPatternSeqFeature extends AbstractSeqFeature {


    private final FeatureFileLine featureFileLine;

    public ProteinPatternSeqFeature(FeatureFileLine featureFileLine) {
        super(featureFileLine.getStart(), featureFileLine.getStop(), featureFileLine.getName());
        this.monomer = new PKMonomer("");
        this.featureFileLine = featureFileLine;
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
        try {
            return Optional.of(parseDouble(this.featureFileLine.getScore()));
        } catch (NumberFormatException formatException) {
            return Optional.empty();
        }
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
    public Optional<Double> getConfidentiality() {
        return Optional.empty();
    }

    @Override
    public String getType() {
        return this.featureFileLine.getType();
    }

    @Override
    public Optional<Boolean> getVerificationPass() {

        if (this.featureFileLine.getVerificationPass().equals("N/A")) {
            return Optional.empty();
        }

        return Optional.of(this.featureFileLine.getVerificationPass().equals("True"));
    }

    @Override
    public String getSubtype() {
        return this.featureFileLine.getSubtype();
    }

    @Override
    public String getLabel() {
        return this.featureFileLine.getLabel();
    }

    @Override
    public FeatureFileLine getOriginatingFeatureFileLine() {
        return this.featureFileLine;
    }

}
