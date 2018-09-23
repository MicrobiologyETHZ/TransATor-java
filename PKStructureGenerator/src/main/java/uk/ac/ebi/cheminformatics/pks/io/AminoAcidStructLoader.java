package uk.ac.ebi.cheminformatics.pks.io;

import runner.RunnerPreferenceField;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.util.prefs.Preferences;

/**
 * Loads the mol file into an IAtomContainer for an amino acid from a file in the user's file system
 * (as opposed to an internal file in the jar).
 */
public class AminoAcidStructLoader extends AbstractFileStructLoader implements StructureLoader {

    Preferences prefs = Preferences.userNodeForPackage(PKSPreferences.class);

    /**
     * Inits the loader with the name of the amino acid, which needs to be exactly the name of the file
     * plus the mol file extension to load. The directory is defined by the
     * {@link RunnerPreferenceField.AminoAcidsMolsPath} field.
     *
     * @param aminoAcidName
     */
    public AminoAcidStructLoader(String aminoAcidName) {
        this.cladeName = aminoAcidName;
        this.errorLabel = "Could not read molecule for amino acid: ";
        this.path = prefs.get(RunnerPreferenceField.AminoAcidsMolsPath.toString(), "") + aminoAcidName + ".mol";
    }
}
