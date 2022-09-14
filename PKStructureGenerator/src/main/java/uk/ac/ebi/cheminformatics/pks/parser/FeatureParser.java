package uk.ac.ebi.cheminformatics.pks.parser;

import org.apache.logging.log4j.*;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Parses the .feature file produced by the Python implementation that marks domains and patterns.
 **/
public final class FeatureParser {

    private static final Logger LOGGER = LogManager.getLogger(FeatureParser.class);

    public static List<SequenceFeature> parse(Path featuresFile) {
        try (Stream<String> stream = Files.lines(featuresFile)) {
            return stream
                    .filter(line -> !line.startsWith("#"))
                    .map(FeatureFileLine::new)
                    .map(SequenceFeatureFactory::makeSequenceFeature)
                    .collect(toList());
        } catch (IOException e) {
            LOGGER.error("Problems reading in features file ", e);
            throw new RuntimeException(e);
        }
    }
}
