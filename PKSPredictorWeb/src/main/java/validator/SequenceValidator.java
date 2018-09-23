package validator;

import com.google.common.io.Files;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderFormat;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequenceValidator {
    private List<String> sequenceIdentifiers;

    private String outputPath;
    private String fastaPath;

    private Exception e;

    /**
     * Initializes the sequence validator with the given input stream to the file.
     *
     * @param fastaFileInputStream
     */
    public SequenceValidator(InputStream fastaFileInputStream) {
        this.sequenceIdentifiers = new ArrayList<>();
        processInput(fastaFileInputStream);
    }

    public SequenceValidator(InputStream fastaFileInputStream, String outputPath) {
        this.sequenceIdentifiers = new ArrayList<>();
        this.outputPath = outputPath;
        processInput(fastaFileInputStream);
    }

    public SequenceValidator(String sequenceLines, String outputPath) {
        this.sequenceIdentifiers = new ArrayList<>();
        this.outputPath = outputPath;
        processInput(new ByteArrayInputStream(sequenceLines.getBytes(Charset.forName("UTF-8"))));
    }

    private void processInput(InputStream input) {
        try {
            Map<String, ProteinSequence> data = FastaReaderHelper.readFastaProteinSequence(input);
            if (outputPath == null)
                outputPath = Files.createTempDir().getCanonicalPath();
            fastaPath = outputPath + File.separator + "query.faa";
            GenericFastaHeaderFormat<ProteinSequence, AminoAcidCompound> headerFormat
                    = new GenericFastaHeaderFormat<>();
            for (String protSeqIdent : data.keySet()) {
                String header = headerFormat.getHeader(data.get(protSeqIdent));
                sequenceIdentifiers.add(
                        header.indexOf(' ') >= 0 ?
                                header.substring(0, header.indexOf(' ')) :
                                header);
            }
            FastaWriterHelper.writeProteinSequence(new File(fastaPath), data.values());
            writeIdentifiersFile(sequenceIdentifiers);
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void writeIdentifiersFile(List<String> sequenceIdentifiers) {
        String pathIdents = outputPath + File.separator + "query.identifiers";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathIdents));
            for (String ident : sequenceIdentifiers) {
                writer.write(ident + "\n");
            }
            writer.close();
        } catch (IOException e1) {
            throw new RuntimeException("problems when writing identifiers", e1);
        }
    }

    private void cleanIdentifiers(List<String> sequenceIdentifiers) {
        for (String ident : sequenceIdentifiers) {
            String newIdent = ident.replaceAll("\\s.*$", "");
            sequenceIdentifiers.set(sequenceIdentifiers.indexOf(ident), newIdent);
        }
    }


    public String getFastaPath() {
        return fastaPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public List<String> getSequenceIdentifiers() {
        return sequenceIdentifiers;
    }

    public boolean fail() {
        return this.e != null;
    }

    public String getError() {
        return "";
    }
}
