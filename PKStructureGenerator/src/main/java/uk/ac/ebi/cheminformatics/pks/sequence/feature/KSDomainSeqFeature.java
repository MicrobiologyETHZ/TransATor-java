package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessorFactory;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

public class KSDomainSeqFeature extends DomainSeqFeature {

    private final Optional<String> clusterId;

    public KSDomainSeqFeature(FeatureFileLine featureFileLine, int ranking, Optional<Double> confidentiality, Optional<String> clusterId) {
        super(featureFileLine, ranking, confidentiality);
        setUpMonomer(featureFileLine.getName());
        this.clusterId = clusterId;
    }

    public Double getEvalue() {
        return this.EValue.orElseThrow(() -> new IllegalStateException("KSDomainSeqFeature requires an evalue"));
    }

    private void setUpMonomer(String clade) {
        CladeAnnotation annotation = CladeAnnotationFactory.getInstance();
        this.monomer = new PKMonomer(clade, annotation);
        this.postProcessor = PostProcessorFactory.getPostProcessor(clade, annotation);
    }

    public Optional<String> getClusterId() {
        return clusterId;
    }
}
