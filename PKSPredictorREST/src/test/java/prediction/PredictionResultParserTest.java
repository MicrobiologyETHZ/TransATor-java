package prediction;

import org.junit.Test;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 2/7/13
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class PredictionResultParserTest {
    @Test
    public void testGetPredictionContainer() throws Exception {
        // TODO change test to use file in package.
        String path = "/Users/pmoreno/Documents/Projects/PKS/pksPredictorRuns/ouput/posterEricCosmid_CladApril2013/";
        String seqID = "Cosmidsequence";
        PredictionResultParser parser = new PredictionResultParser(path,seqID);

        JacksonRepresentation representation = new JacksonRepresentation(parser.getPredictionContainer());
        System.out.println(representation.getText());
    }
}
