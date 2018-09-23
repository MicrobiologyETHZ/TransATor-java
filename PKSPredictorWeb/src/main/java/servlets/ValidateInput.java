package servlets;

import encrypt.Encrypter;
import io.TempDirMaker;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import runner.PKSPredictor;
import validator.SequenceValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@MultipartConfig
public class ValidateInput extends HttpServlet {

    public ValidateInput() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get the fasta file from the request, either from the test area or the file that can be uploaded. Check that the fasta is adequate.
        String seqInput = null;
        SequenceValidator validator = null;

        if (ServletFileUpload.isMultipartContent(request)) {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constraints
            File outputRepPath = TempDirMaker.createTempDir();
            factory.setRepository(outputRepPath);

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                // Parse the request
                List<FileItem> items = upload.parseRequest(request);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String name = item.getFieldName();
                        if (name.equalsIgnoreCase("sequenceInput")) {
                            seqInput = item.getString();
                        }
                    }
                }

                if (seqInput == null || seqInput.trim().length() == 0) {
                    for (FileItem item : items) {
                        if (!item.isFormField() && item.getFieldName().equalsIgnoreCase("fastaFile")) {
                            validator = new SequenceValidator(item.getInputStream(), outputRepPath.getCanonicalPath());
                        }
                    }
                } else {
                    validator = new SequenceValidator(seqInput, outputRepPath.getCanonicalPath());
                }

            } catch (FileUploadException e) {
                throw new RuntimeException("Problems with fasta upload file", e);
            }
        }


        if (validator == null || validator.fail()) {
            request.getSession().setAttribute("errorExp", validator.getError());
            response.sendRedirect("pages/Error.jsp");
        }

        // Provide the fasta file generated to the runner object and execute.
        PKSPredictor predictor = new PKSPredictor(validator.getFastaPath(), validator.getOutputPath());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(predictor);

        // TODO fire the execution of the image structure generator, which should wait for the results of the previous execution thread.
        List<String> identifiers = validator.getSequenceIdentifiers();
        request.getSession().setAttribute("identifers", identifiers);

        Encrypter encrypter = new Encrypter();
        String encrypted = encrypter.encrypt(validator.getOutputPath());
        System.out.println("Encrypted PKSWeb : " + encrypted);
        request.getSession().setAttribute("tmp", URLEncoder.encode(encrypted, "UTF-8"));
        response.sendRedirect("result.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
