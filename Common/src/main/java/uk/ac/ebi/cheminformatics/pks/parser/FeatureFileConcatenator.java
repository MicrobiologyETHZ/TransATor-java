package uk.ac.ebi.cheminformatics.pks.parser;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FeatureFileConcatenator {

    private static final Logger LOGGER = Logger.getLogger(FeatureFileConcatenator.class);

    private Path concatenatedPath;

    public FeatureFileConcatenator(String path) {
        concatenatedPath = Paths.get(path + "concatenated.features");

        // Look for the original fasta or a file that holds the order of the identifiers.
        List<String> identifiers = getIdentifiers(path);
        // Read sequentially the features for each identifiers, waiting until the files are all ready.
        StringBuilder builder = new StringBuilder();
        for (String identifier : identifiers) {
            if (identifierFilesAreReady(path, identifier)) {
                builder.append(getFeatureFileContent(path, identifier));
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(concatenatedPath.toFile()));
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Problems writing ", e);
        }
    }

    private String getFeatureFileContent(String path, String identifier) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + identifier + ".features"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error("Problems reading feature files for concat : ", e);
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private boolean identifierFilesAreReady(String path, String identifier) {
        File identFin = new File(path + identifier + ".finished");
        try {
            while (true) {
                if (identFin.exists())
                    return true;
                else
                    Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Problems waiting for identifier files...", e);
            throw new RuntimeException(e);
        }
    }

    private List<String> getIdentifiers(String path) {
        List<String> identifiers = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "query.identifiers"));
            String line;
            while ((line = reader.readLine()) != null) {
                identifiers.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return identifiers;
    }

    public Path getConcatenatedPath() {
        return concatenatedPath;
    }
}
