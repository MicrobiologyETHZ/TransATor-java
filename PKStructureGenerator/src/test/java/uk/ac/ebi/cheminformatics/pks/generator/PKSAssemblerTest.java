package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.base.Joiner;
import org.junit.Test;
import org.openscience.cdk.io.MDLV2000Writer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 31/7/13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class PKSAssemblerTest {
    @Test
    public void testAddMonomer() throws Exception {
        FeatureFileLineParser parser = new FeatureFileLineParser(getCladeLine(100,1E-20,100.0f,1,1,"Clade_7",""));
        SequenceFeature clade7 = SequenceFeatureFactory.makeSequenceFeature(parser);
        FeatureFileLineParser parser2 = new FeatureFileLineParser(getCladeLine(200,1E-20,100.0f,1,1,"Clade_11",""));
        SequenceFeature clade11 = SequenceFeatureFactory.makeSequenceFeature(parser2);
        //FeatureFileLineParser parser3 = new FeatureFileLineParser(getCladeLine(300,1E-20,100.0f,1,1,"Clade_13",""));
        //SequenceFeature clade13 = SequenceFeatureFactory.makeSequenceFeature(parser3);
        FeatureFileLineParser parser3 = new FeatureFileLineParser(getCladeLine(300,1E-20,100.0f,1,1,"Clade_14",""));
        SequenceFeature clade14 = SequenceFeatureFactory.makeSequenceFeature(parser3);
        SequenceFeature clade7_2 = SequenceFeatureFactory.makeSequenceFeature(parser);

        PKSAssembler assembler = new PKSAssembler();
        assembler.addMonomer(clade7);
        assembler.addMonomer(clade11);
        assembler.addMonomer(clade14);
        assembler.addMonomer(clade7_2);

        SequenceFeature finalizer = new DomainSeqFeature(0,0,"finalExtension","0");
        assembler.addMonomer(finalizer);
        assembler.postProcess();

        PKStructure struc = assembler.getStructure();

        PKStructureImageGenerator imageGenerator = new PKStructureImageGenerator();
        BufferedImage image = imageGenerator.generateStructureImage(struc, new Dimension(900, 900));

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter("/tmp/pks.mol"));
        writer.write(struc.getMolecule());
        writer.close();

        File outputFile = new File("/tmp/pks.png");
        ImageIO.write(image, "png", outputFile);
    }

    @Test
    public void testGetStructure() throws Exception {

    }

    private String getCladeLine(Integer start, Double evalue, Float score, Integer ranking,
                                Integer stackNumber, String name, String label) {
        return Joiner.on("\t").join(start,start+100,evalue,score,ranking,stackNumber,"domain","KS",name,label,"yes");
    }
}
