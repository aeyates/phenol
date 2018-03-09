package org.monarchinitiative.phenol.ontology.algo;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableTerm;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableRelationship;

public class InformationContentComputationTest extends VegetableOntologyTestBase {

  InformationContentComputation<VegetableTerm, VegetableRelationship> computation;

  @Before
  public void setUp() {
    super.setUp();
    computation = new InformationContentComputation<>(ontology);
  }

  @Test
  public void test() {
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    assertEquals(7, informationContent.size());

    assertEquals(0.0, informationContent.get(idVegetable).doubleValue(), 0.001);
    assertEquals(0.0, informationContent.get(idRootVegetable).doubleValue(), 0.001);
    assertEquals(0.405, informationContent.get(idLeafVegetable).doubleValue(), 0.001);
    assertEquals(0.405, informationContent.get(idCarrot).doubleValue(), 0.001);
    assertEquals(0.405, informationContent.get(idBeet).doubleValue(), 0.001);
    assertEquals(0.405, informationContent.get(idPumpkin).doubleValue(), 0.001);
    assertEquals(1.099, informationContent.get(idBlueCarrot).doubleValue(), 0.01);
  }
}
