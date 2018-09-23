package uk.ac.ebi.cheminformatics.pks.generator;

import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;

public class PostProcessorFactory {

    /**
     * Use getPostProcessor with CladeAnnotation as argument instead.
     *
     * @param name
     * @return
     */
    @Deprecated
    public static PostProcessor getPostProcessor(String name) {
        if (name.equals("Clade_3")) {
            return new CyclizationPostProcessor();
        }
        return null;
    }

    /**
     * Returns appropiate post processor for the given clade. The annotation object can hold more than one postprocessor
     * currently, but this object only returns one. That should be changed in future (although we have no cases yet).
     *
     * @param clade      for which we need the postprocessor
     * @param annotation
     * @return
     */
    public static PostProcessor getPostProcessor(String clade, CladeAnnotation annotation) {
        if (annotation.getCladePostProcessors(clade).contains("Cyclization")) {
            return new CyclizationPostProcessor();
        }
        return null;
    }
}
