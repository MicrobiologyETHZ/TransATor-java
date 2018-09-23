package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

public class SequenceFeatureFactory {
    public static SequenceFeature makeSequenceFeature(FeatureFileLine parser) {
        return makeSequenceFeature(parser, 0, Optional.empty(), Optional.empty());
    }

    public static SequenceFeature makeSequenceFeature(FeatureFileLine parser, int ranking, Optional<Double> confidentiality, Optional<String> clusterId) {
        if (parser.getType().equalsIgnoreCase("domain")) {
            DomainSeqFeature nseqFeat;
            switch (parser.getSubtype()) {
                case "AL":
                    nseqFeat = new ALDomainSeqFeature(parser);
                    break;
                case "B":
                    nseqFeat = new BRDomainSeqFeature(parser);
                    break;
                case "C":
                    nseqFeat = new CDomainSeqFeature(parser);
                    break;
                case "Cyc":
                    nseqFeat = new CycDomainSeqFeature(parser);
                    break;
                case "DH":
                    nseqFeat = new DHDomainSeqFeature(parser);
                    break;
                case "PS":
                    nseqFeat = new PSDomainSeqFeature(parser);
                    break;
                case "ER":
                    nseqFeat = new ERDomainSeqFeature(parser);
                    break;
                case "MT":
                    nseqFeat = new MTDomainSeqFeature(parser);
                    break;
                case "KR":
                    nseqFeat = new KRDomainSeqFeature(parser);
                    break;
                case "KS":
                    nseqFeat = new KSDomainSeqFeature(parser, ranking, confidentiality, clusterId);
                    break;
                case "OMT":
                    nseqFeat = new OMTDomainSeqFeature(parser);
                    break;
                case "NRPS2":
                    nseqFeat = new NRPSDomSeqFeature(parser);
                    break;
                default:
                    nseqFeat = new DomainSeqFeature(parser, ranking, confidentiality);
                    break;
            }
            return nseqFeat;
        } else if (parser.getType().equalsIgnoreCase("pattern")) {
            return PatternSeqFeatureFactory.makeSequenceFeature(parser);
        }
        return null;
    }
}
