package prediction;

import encrypt.Encrypter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import prediction.json.PredictionContainer;

import java.io.File;


public class PKSPredictionDataResource extends ServerResource {

    private long timeout = 10 * 60 * 1000; // 10 minutes

    @Get("json")
    public Representation represent() {
        String encryptedPath = getQuery().getValues("path");
        Encrypter encrypter = new Encrypter();
        System.out.println("Encrypted : " + encryptedPath);
        String path = encrypter.decrypt(encryptedPath);
        if (!path.endsWith(File.separator))
            path += File.separator;
        String seqID = getQuery().getValues("seqId");
        System.out.println("Started represent: " + encryptedPath);

        File finished = new File(path + File.separator + seqID + ".finished");
        Long start = System.currentTimeMillis();
        // TODO: busy waiting might be the source of occasional 100% CPU when something goes wrong in the python part of the program
        while (true) {
            if (finished.exists())
                break;
            else if (System.currentTimeMillis() - start > this.timeout) {
                throw new RuntimeException("Timeout occurred");
            } else {
                try {
                    Thread.sleep(3000); // sleep 3 seconds and try again.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        PredictionResultParser parser = new PredictionResultParser(path, seqID);
        // Now we read the result files and produce the objects to be transformed into JSON.

        PredictionContainer container = parser.getPredictionContainer();

        JacksonRepresentation rep = new JacksonRepresentation(container);
        return rep;
    }
}
