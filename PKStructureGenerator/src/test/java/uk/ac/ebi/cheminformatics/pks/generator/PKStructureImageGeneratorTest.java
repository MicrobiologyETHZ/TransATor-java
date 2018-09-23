package uk.ac.ebi.cheminformatics.pks.generator;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PKStructureImageGeneratorTest {

    private Path getFeaturesFile(String name) throws URISyntaxException {
        return Paths.get(PKStructureImageGeneratorTest.class.getResource(name).toURI());
    }


    @Test
    public void testGenerateStructureImage() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("Cosmidsequence.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksCosmidImage.png");
        ImageIO.write(image, "png", outputFile);

    }

    @Test
    public void testGenerateStructureImageOnnamide() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("onnamid.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksOnnamideImage.png");
        ImageIO.write(image, "png", outputFile);

    }

    @Test
    public void testGenerateStructureImageBacillaene() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("bacillaene.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksBacillaeneImage.png");
        ImageIO.write(image, "png", outputFile);

    }

    @Test
    public void testGenerateStructureImageOnnamideERTest() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("onnamide_ERsubFeatureTest.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksOnnamideWithERImage.png");
        ImageIO.write(image, "png", outputFile);
    }

    @Test
    public void testGenerateStructureImageOnnamideOMTTest() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("onnamide_OMTsubFeatureTest.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksOnnamideWithOMTImage.png");
        ImageIO.write(image, "png", outputFile);
    }

    @Test
    public void testGenerateStructureImageOnnamideMTTest() throws Exception {
        StructureGenerator generator = new StructureGenerator(getFeaturesFile("onnamide_MTsubFeatureTest.features"));
        generator.run();
        PKStructure struct = generator.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 900));

        File outputFile = new File("/tmp/pksOnnamideWithMTImage.png");
        ImageIO.write(image, "png", outputFile);
    }
}
