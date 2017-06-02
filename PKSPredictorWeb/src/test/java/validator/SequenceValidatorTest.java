package validator;

import org.junit.Test;

import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class SequenceValidatorTest {
    @Test
    public void testGetSequenceIdentifiers() throws Exception {
        SequenceValidator validator =
                new SequenceValidator(
                        // TODO change to internal resource file.
                        new FileInputStream(
                "/Users/pmoreno/Documents/Projects/PKS/dataFromJoern/secondSetOfSeqsToRunOnApril2013/joinedSeqs.faa"));

        for (String ident : validator.getSequenceIdentifiers()) {
            System.out.println(ident);
        }
    }
}
