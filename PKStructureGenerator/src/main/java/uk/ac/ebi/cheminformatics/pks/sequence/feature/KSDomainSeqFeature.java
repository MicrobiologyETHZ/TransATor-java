package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 19/4/15
 * Time: 22:13
 * To change this template use File | Settings | File Templates.
 */
public class KSDomainSeqFeature extends DomainSeqFeature implements SequenceFeature {

    public KSDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
    }
}
