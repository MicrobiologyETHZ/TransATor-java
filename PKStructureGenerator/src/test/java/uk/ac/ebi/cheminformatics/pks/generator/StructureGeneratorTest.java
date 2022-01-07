package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.logging.log4j.*;
import org.junit.Test;
import org.openscience.cdk.io.MDLV2000Writer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;

public class StructureGeneratorTest {

    private static final Logger LOGGER = LogManager.getLogger(PKStructure.class);

    @Test
    public void testGetStructure() throws Exception {
        LOGGER.info("Starting cosmid sequence features test");
        runGenerator("Cosmidsequence.features", "/tmp/cosmid");
    }

    @Test
    public void testGetStructureConcat() throws Exception {
        LOGGER.info("Starting concat features test");
        String features = "concatenated.features";
        String molFileOut = "/tmp/concat";

        runGenerator(features, molFileOut);
    }

    @Test
    public void testGetStructureOnnamide() throws Exception {
        LOGGER.info("Starting onnamide sequence features test");
        // TODO shows additional hydrogens
        runGenerator("onnamid.features", "/tmp/onnamidePK");
    }

    @Test
    public void testGetStructureRhizopodin() throws Exception {
        LOGGER.info("Starting rhizopodin sequence features test");
        // TODO shows additional hydrogens
        runGenerator("rhizopodin.features", "/tmp/rhizopodinPK");
    }

    @Test
    public void testExtenderClade27ShortOnnamide() throws Exception {
        LOGGER.info("Starting extender features test");
        runGenerator("onnamidShortExtender27Test.features", "/tmp/onnamideExtenderShort");
    }

    @Test
    public void testGlyNRPS2OnnamideSorted() throws Exception {
        LOGGER.info("Starting NRPS2 Gly test onnamide test");
        runGenerator("onnamid_sorted.features", "/tmp/nrps2Gly_onnamideTest");
    }

    private void runGenerator(String features, String molFileOut) throws Exception {
        StructureGenerator generator = new StructureGenerator(Paths.get(StructureGeneratorTest.class.getResource(features).toURI()));

        generator.run();

        PKStructure struct = generator.getStructure();

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter(molFileOut + ".mol"));
        writer.write(struct.getMolecule());
        writer.close();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File(molFileOut + ".png");
        ImageIO.write(image, "png", outputFile);
    }
}
