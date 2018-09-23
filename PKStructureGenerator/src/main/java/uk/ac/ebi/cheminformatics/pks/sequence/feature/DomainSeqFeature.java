package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

public class DomainSeqFeature extends AbstractSeqFeature {

    PostProcessor postProcessor;

    protected Optional<Double> EValue;

    private int ranking = 0;
    private Optional<Double> confidentiality = Optional.empty();

    protected Double getThreshold() {
        return Double.parseDouble("1E-10");
    }

    private FeatureFileLine featureFileLine;

    public DomainSeqFeature(FeatureFileLine featureFileLine) {
        super(featureFileLine.getStart(), featureFileLine.getStop(), featureFileLine.getName());
        this.EValue = parseEValue(featureFileLine.getEvalue());
        this.featureFileLine = featureFileLine;
    }

    public DomainSeqFeature(FeatureFileLine featureFileLine, int ranking, Optional<Double> confidentiality) {
        super(featureFileLine.getStart(), featureFileLine.getStop(), featureFileLine.getName());
        this.EValue = parseEValue(featureFileLine.getEvalue());
        this.featureFileLine = featureFileLine;
        this.ranking = ranking;
        this.confidentiality = confidentiality;
    }

    private Optional<Double> parseEValue(String string) {
        try {
            return Optional.of(Double.parseDouble(string));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public DomainSeqFeature(Integer start, Integer stop, String name, String label) {
        super(start, stop, name);
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
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }

    public boolean isSignificant() {
        return EValue.map(value -> value < getThreshold()).orElse(false);
    }

    @Override
    public Optional<Double> getScore() {
        return Optional.of(Double.parseDouble(this.featureFileLine.getScore()));
    }

    @Override
    public Optional<Double> getEValue() {
        return EValue;
    }

    @Override
    public Optional<Integer> getRanking() {
        return this.ranking == 0 ? Optional.empty() : Optional.of(this.ranking);
    }

    @Override
    public Optional<Double> getConfidentiality() {
        return this.confidentiality;
    }


    @Override
    public String getType() {
        return this.featureFileLine.getType();
    }

    // TODO: remove verification passes
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
        return featureFileLine;
    }

}
