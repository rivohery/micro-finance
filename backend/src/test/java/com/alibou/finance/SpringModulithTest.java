package com.alibou.finance;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class SpringModulithTest {

    ApplicationModules modules = ApplicationModules.of(MicroFinanceApplication.class);

    @Test
    void verifiesModularStructure() {
        ApplicationModules.of(MicroFinanceApplication.class).verify();
    }

   // @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeModulesAsPlantUml()          // Diagramme global de tous les modules
                .writeIndividualModulesAsPlantUml(); // Diagramme par module (avec ses dépendances)
    }
}
