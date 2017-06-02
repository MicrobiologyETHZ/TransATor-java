package prediction;

import com.google.common.base.Splitter;
import prediction.json.*;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/7/13
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class PredictionResultParser {
    private PredictionContainer predictionContainer;
    private Integer initialFeatureY = 60;
    private Integer initialPatternY = 30;
    private Integer featureHeight = 10;
    private Integer sequenceLenghtAA;
    private Integer seqXStartPixels = 20;
    private Integer seqXStopPixels = 680;
    private Integer patternRadius = 5;

    private Integer canvasSizePixels = 700;

    private String sequenceID;

    public PredictionResultParser(String path, String seqID) {
        sequenceID = seqID;
        predictionContainer = new PredictionContainer();
        // from the gbk we obtain the total length of the sequence
        parseGBK(path, seqID);
        Configuration config = getDefaultConfig();
        predictionContainer.setConfiguration(config);
        // from the .features we obtain the coordinates of the different clades and patterns, with their names and legends.
        List<FeaturesArray> features = getFeaturesArrays(path, seqID);
        predictionContainer.setFeaturesArray(features);

        predictionContainer.setLegend(getLegend());
        predictionContainer.setSegment(seqID);
    }

    private void parseGBK(String path, String seqID) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path+seqID+".gbk"));
            String line = reader.readLine();
            reader.close();
            String[] tokens = line.split("\\s+");
            try {
                sequenceLenghtAA = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private List<FeaturesArray> getFeaturesArrays(String path, String seqID) {
        // from the .features we obtain the coordinates of the different clades and patterns, with their names and legends.
        List<FeaturesArray> features = new LinkedList<FeaturesArray>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path+seqID+".features"));
            String line = reader.readLine();
            while( line!=null ) {
                FeaturesArray feature = getFeatureFromLine(line);
                features.add(feature);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return features;
    }

    private FeaturesArray getFeatureFromLine(String line) {
        FeatureFileLineParser lineParser = new FeatureFileLineParser(line);
        Integer start = lineParser.getStart();
        Integer stop = lineParser.getStop();
        String evalue = lineParser.getEvalue();
        String score = lineParser.getScore();
        String ranking = lineParser.getRanking();
        String stackNumber = lineParser.getStackNumber();
        String type = lineParser.getType();
        String subtype = lineParser.getSubtype();
        String name = lineParser.getName();
        String label = lineParser.getLabel();

        FeaturesArray feature = new FeaturesArray();

        feature.setFeatureStart(start);
        feature.setTypeCategory(type);
        feature.setFeatureEnd(stop);

        // Currently domain includes both Clades and NRPS2 results
        // we should be able to tell the difference.
        // TODO Currently we are doing this through the fact that
        // NRPS domains don't have a ranking
        boolean isClade = subtype.equals("KS");
        String evidence = "HMMER";
        if(subtype.equals("NRPS2")) {
            label = "Amino acid";
            evidence = "NRPS2 Predictor";
        }
        if(type.equalsIgnoreCase("domain")) {
            if(isClade) {
                Integer rankingInt = Integer.parseInt(ranking);
                feature.setY(initialFeatureY + rankingInt * featureHeight);
                // TODO change color based on verification value.
                feature.setStroke(getHexaColorFromRank(rankingInt));
                feature.setFill(getHexaColorFromRank(rankingInt));
            } else {
                feature.setY(initialFeatureY + featureHeight);
                feature.setStroke(getHexaColorFromSubtype(subtype));
                feature.setFill(getHexaColorFromSubtype(subtype));
            }
            feature.setX(calculateXPixel(start));
            feature.setType("rect");
            feature.setFeatureLabel("E-value : " + evalue + " Score : " + score);
            if(isClade) {
                feature.setFeatureId((name+"_"+stackNumber).replaceAll(" ","_"));
                feature.setFeatureTypeLabel(label);
                feature.setTypeLabel(label);
            } else {
                feature.setFeatureId(name+"_"+start);
                feature.setFeatureTypeLabel(label);
                feature.setTypeLabel(label);
            }
            feature.setTypeCode(name);
            feature.setHeight(featureHeight);
            feature.setWidth(calculateXPixel(stop) - calculateXPixel(start));
            feature.setStrokeWidth(1);
            feature.setEvidenceCode(name);
            feature.setEvidenceText(evidence);

        } else if(type.equalsIgnoreCase("pattern")) {
            // for pattern
            feature.setCx(calculateXPixel(start));
            feature.setCy(initialPatternY);
            feature.setFeatureId((name+"_"+start).replaceAll(" ","_"));
            feature.setFeatureLabel(name);
            feature.setFeatureTypeLabel("");
            feature.setType("diamond");
            feature.setTypeLabel("");
            feature.setTypeCode("");
            feature.setR(patternRadius);
            feature.setFill("#AA8CF0");
            feature.setStroke("#AA8CF0");
            feature.setStrokeWidth(1);
            feature.setEvidenceCode("");
            feature.setEvidenceText("EMBOSS fuzzpro");
        }
        feature.setFillOpacity(0.7);

        return feature;
    }

    private String getHexaColorFromRank(Integer rankingInt) {
        Integer red = 200 - (5 - rankingInt)*40;
        Integer green = 216;
        Integer black = 188;
        String hex = String.format("#%02x%02x%02x", red, green, black);
        return hex;
    }

    /**
     * Produces a hexadecimal colour based on the subtype string, for defined subtypes. Subtypes
     * should be defined by an enumeration probably. Colours could be as well defined in the domain
     * definition file externally.
     *
     * @param subtype
     * @return
     */
    private String getHexaColorFromSubtype(String subtype) {
        //http://colorbrewer2.org/?type=qualitative&scheme=Dark2&n=8
        String hexColour;
        switch (subtype) {
            case "PS":
                hexColour="#1b9e77";
                break;
            case "DH":
                hexColour="#d95f02";
                break;
            case "ACP":
                hexColour="#7570b3";
                break;
            case "AH":
                hexColour="#e7298a";
                break;
            case "AT_AH":
                hexColour="#66a61e";
                break;
            case "AT":
                hexColour="#e6ab02";
                break;
            case "KR":
                hexColour="#a6761d";
                break;
            default:
                hexColour="#666666";
                break;
        }

        return(hexColour);
    }

    private Integer calculateXPixel(Integer startInAA) {
        return seqXStartPixels + Math.round(startInAA * getPixelsPerAA());
    }

    private float getPixelsPerAA() {
        return ((seqXStopPixels - seqXStartPixels)*1f) / (sequenceLenghtAA*1f);
    }

    public PredictionContainer getPredictionContainer() {
        return predictionContainer;
    }


    public Configuration getDefaultConfig() {
        Configuration defConfiguration = new Configuration();
        defConfiguration.setAboveRuler(10);
        defConfiguration.setBelowRuler(30);
        defConfiguration.setGridLineHeight(12);
        defConfiguration.setHorizontalGrid(false);
        defConfiguration.setHorizontalGridNumLines(2);
        defConfiguration.setHorizontalGridNumLinesCentered(6);
        defConfiguration.setHorizontalGridNumLinesNonOverlapping(2);
        defConfiguration.setHorizontalGridNumLinesRows(8);
        defConfiguration.setLeftMargin(20);
        defConfiguration.setNonOverlapping(true);
        defConfiguration.setPixelsDivision(50);
        defConfiguration.setRequestedStart(1);
        defConfiguration.setRequestedStop(sequenceLenghtAA);
        defConfiguration.setRightMargin(20);
        defConfiguration.setRulerLength(660);
        // this should be related to the sequence lenght and the total lenght we are going to use for the image
        defConfiguration.setRulerY(20);
        defConfiguration.setSequenceLength(sequenceLenghtAA);
        defConfiguration.setSequenceLineY(54);
        defConfiguration.setSequenceLineYCentered(95);
        defConfiguration.setSequenceLineYNonOverlapping(54);
        defConfiguration.setSequenceLineYRows(260);
        defConfiguration.setSizeX(canvasSizePixels);
        defConfiguration.setSizeY(100);
        defConfiguration.setSizeYCentered(160);
        defConfiguration.setSizeYKey(210);
        defConfiguration.setSizeYNonOverlapping(54);
        defConfiguration.setSizeYRows(260);
        defConfiguration.setStyle("nonoverlapping");
        defConfiguration.setUnitSize(getPixelsPerAA());
        defConfiguration.setVerticalGrid(false);
        defConfiguration.setVerticalGridLineLength(66); // this does't make sense, too long
        defConfiguration.setVerticalGridLineLengthCentered(172);
        defConfiguration.setVerticalGridLineLengthNonOverlapping(66);
        defConfiguration.setVerticalGridLineLengthRows(284);

        return defConfiguration;
    }

    public Legend getLegend() {
        Legend legend = new Legend();
        legend.setSegment(getSegment());
        legend.setKey(getKeys());
        return legend;
    }

    public Segment getSegment() {
        Segment seg = new Segment();
        seg.setText(sequenceID);
        seg.setxPos(15);
        seg.setyPos(initialFeatureY + 5 * featureHeight + 30);
        seg.setyPosCentered(190);
        seg.setyPosNonOverlapping(106);
        seg.setyPosRows(290);
        return seg;
    }

    public List<Key> getKeys() {
        List<Key> keys = new ArrayList<Key>(predictionContainer.getFeaturesArray().size());

        /*
        Key ksDomainKey = getKSDomainKey();
        Label ksDomainLab = getKSDomainLab();
        ksDomainKey.setLabel(ksDomainLab);
        ksDomainKey.setShape();

        keys.add(ksDomainKey)
        for (FeaturesArray feature : predictionContainer.getFeaturesArray()) {
            Key key = new Key();
            Label lab = new Label();
            lab.setTotal("1");// ?
            lab.set
            key.setLabel();
        }
        */
        return keys;
    }
}
