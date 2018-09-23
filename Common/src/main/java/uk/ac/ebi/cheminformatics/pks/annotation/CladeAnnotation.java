package uk.ac.ebi.cheminformatics.pks.annotation;

import runner.RunnerPreferenceField;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Holds the annotation data for the for the clades.
 */
public class CladeAnnotation {

    private final Preferences prefs = Preferences.userNodeForPackage(PKSPreferences.class);

    private Map<String, String> clade2molFileName;
    private Map<String, Boolean> cladeIsNonElongating;
    private Map<String, Boolean> cladeIsTerminationBoundary;

    private Map<String, String> clade2description;
    private Map<String, String> clade2descriptionForTool;
    private Map<String, List<String>> clade2PostProcessors;
    private Map<String, List<String>> clade2verficationDomains;
    private Map<String, List<String>> clade2domainsTerminationRule;
    private Map<String, Boolean> cladeIsVerificationMandatory;

    public CladeAnnotation() {
        clade2description = new HashMap<>();
        cladeIsNonElongating = new HashMap<>();
        cladeIsTerminationBoundary = new HashMap<>();
        clade2molFileName = new HashMap<>();
        clade2descriptionForTool = new HashMap<>();
        clade2PostProcessors = new HashMap<>();
        clade2verficationDomains = new HashMap<>();
        clade2domainsTerminationRule = new HashMap<>();
        cladeIsVerificationMandatory = new HashMap<>();
    }

    public String getMolFileName(String cladeName) {
        return getPref(RunnerPreferenceField.MonomerMolsPath) + File.separator + clade2molFileName.get(cladeName);
    }

    private String getPref(RunnerPreferenceField field) {
        return prefs.get(field.toString(), "");
    }

    /**
     * @param cladeName
     * @return True if the clade with cladeName is an elongating
     */
    public Boolean isNonElongating(String cladeName) {
        return cladeIsNonElongating.get(cladeName);
    }

    public boolean isTerminationBoundary(String cladeName) {
        return cladeIsTerminationBoundary.getOrDefault(cladeName, false);
    }

    public void setCladeDesc(String clade, String description) {
        this.clade2description.put(clade, description);
    }

    public void setCladeDescTool(String clade, String descriptionForTool) {
        this.clade2descriptionForTool.put(clade, descriptionForTool);
    }

    public void setCladeMolFileName(String clade, String molFileName) {
        this.clade2molFileName.put(clade, molFileName);
    }

    public void setCladePostProcessors(String clade, List<String> postProcessorsNames) {
        this.clade2PostProcessors.put(clade, postProcessorsNames);
    }

    public void setCladeVerificationDomains(String clade, List<String> verificationDomains) {
        this.clade2verficationDomains.put(clade, verificationDomains);
    }


    public void setCladeTerminationRule(String clade, List<String> domainsForRule) {
        this.clade2domainsTerminationRule.put(clade, domainsForRule);
    }

    public void setCladeTerminationBoundary(String clade, Boolean isTerminationBoundary) {
        this.cladeIsTerminationBoundary.put(clade, isTerminationBoundary);
    }

    public void setCladeNonElongating(String clade, Boolean nonElongating) {
        this.cladeIsNonElongating.put(clade, nonElongating);
    }

    public void setCladeVerificationMandatory(String clade, Boolean verificationMandatory) {
        this.cladeIsVerificationMandatory.put(clade, verificationMandatory);
    }

    public List<String> getCladePostProcessors(String clade) {
        return this.clade2PostProcessors.get(clade);
    }

    public boolean isVerificationMandatory(String clade) {
        return this.cladeIsVerificationMandatory.get(clade);
    }

}
