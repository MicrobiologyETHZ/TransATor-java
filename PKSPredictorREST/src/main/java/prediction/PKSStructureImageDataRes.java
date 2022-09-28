package prediction;

import encrypt.Encrypter;
import org.apache.logging.log4j.*;
import org.openscience.cdk.exception.CDKException;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructureImageGenerator;
import uk.ac.ebi.cheminformatics.pks.generator.StructureGenerator;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileConcatenator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PKSStructureImageDataRes extends ServerResource {

    private static final Logger LOGGER = LogManager.getLogger(PKSStructureImageDataRes.class);

    @Get("png")
    public Representation represent() {
        String encryptedPath = getQuery().getValues("path");
        Encrypter encrypter = new Encrypter();
        System.out.println("Encrypted image data res : " + encryptedPath);
        String path = encrypter.decrypt(encryptedPath);
        if (!path.endsWith(File.separator))
            path += File.separator;

        FeatureFileConcatenator concatenator = new FeatureFileConcatenator(path);
        System.out.println("Concatenator ready");
        StructureGenerator generator = new StructureGenerator(concatenator.getConcatenatedPath());
        generator.run();
        PKStructure struct = generator.getStructure();
        System.out.println("PK structure generated");

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        try {
            BufferedImage image = imageGenerator.generateStructureImage(struct, new Dimension(900, 300));

            File outputFile = new File(path + "pksImage.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image generated");

            return new FileRepresentation(outputFile, MediaType.IMAGE_PNG);
        } catch (CDKException e) {
            LOGGER.error("Problems with CDK when generating the image", e);
        } catch (IOException e) {
            LOGGER.error("IO problems when generating the image", e);
        }
        return null;
    }
}
