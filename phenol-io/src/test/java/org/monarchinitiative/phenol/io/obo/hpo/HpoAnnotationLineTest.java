package org.monarchinitiative.phenol.io.obo.hpo;


import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HpoAnnotationLineTest {

  private final static String EMPTY_STRING="";

  private final static double EPSILON=0.00001;

  private HpoOntology ontology;

  @Before
  public void setUp() throws PhenolException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final HpOboParser parser = new HpOboParser(classLoader.getResourceAsStream("hp_head.obo"));
    ontology = parser.parse();
  }

  private HpoAnnotationLine makeLine(String items[]) throws PhenolException {
    String line = Arrays.stream(items).collect(Collectors.joining("\t"));
    return HpoAnnotationLine.constructFromString(line);
  }



  @Test
  public void test1() throws PhenolException{
    String items[]={"OMIM:269150",
      "SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME",
      "",
      "HP:0030736",
      "OMIM:269150",
      "TAS",
      "",
      "",
      "",
      "",
      "P",
      "HPO:skoehler[2017-07-13]"};
    List<String> publist = new ArrayList<>();
    publist.add("OMIM:269150");
    HpoAnnotationLine hpoAnnot = makeLine(items);
    assertEquals("OMIM:269150",hpoAnnot.getDiseaseId());
    assertEquals("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME", hpoAnnot.getDbObjectName());
    assertEquals("TAS",hpoAnnot.getEvidence());
    assertEquals(publist,hpoAnnot.getPublication());
    assertEquals("P",hpoAnnot.getAspect());
    assertEquals("HPO:skoehler[2017-07-13]", hpoAnnot.getBiocuration());
    assertFalse(hpoAnnot.isNOT());
    assertEquals(EMPTY_STRING,hpoAnnot.getFrequency());
    TermId diseaseId = hpoAnnot.getDiseaseTermId();
    TermId expected = TermId.constructWithPrefix("OMIM:269150");
    assertEquals(expected,diseaseId);

  }

  /**
   * The first field ("BadPrefix" instead of "OMIM") is invalid and should throw an exception.
   * @throws PhenolException expected to throw because prefix is malformed
   */
  @Test(expected = PhenolException.class)
  public void malformedDiseaseDatabasePrefix() throws PhenolException {
    String items[]={"BadPrefix",
      "269150",
      "SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME",
      "",
      "HP:0030736",
      "OMIM:269150",
      "TAS",
      "",
      "",
      "",
      "",
      "P",
      "2017-07-13",
      "HPO:skoehler"};
    HpoAnnotationLine hpoAnnot = makeLine(items);
    TermId diseaseId = hpoAnnot.getDiseaseTermId();
  }



  /**
   * Very frequent HP:0040281; Present in 80% to 99% of the cases.
   * The expected mean is (0.8+0.99)/2=0.895
   */
  @Test
  public void testGetFrequencyFromVeryFrequentTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040281",  //Frequency
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
      };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.895;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }




  /**
   * Very rare HP:0040284; Present in 1% to 4% of the cases.
   * The expected mean is (0.01+0.04)/2=0.025
   */
  @Test
  public void testGetFrequencyFromVeryRareTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040284",  //Frequency [VERY RARE]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.025;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Occasional HP:0040283; Present in 5% to 29% of the cases.
   * The expected mean is (0.05+0.29)/2=0.17
   */
  @Test
  public void testGetFrequencyFromOccassionalTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040283",  //Frequency [OCCASIONAL]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.17;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }


  /**
   * Frequent HP:0040282; Present in 30% to 79% of the cases.
   * The expected mean is (0.30+0.79)/2=0.545
   */
  @Test
  public void testGetFrequencyFromFrequentTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040282",  //Frequency [OCCASIONAL]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.545;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Frequent HP:0040280; Present in 100%  of the cases.
   * The expected mean is 1.0
   */
  @Test
  public void testGetFrequencyFromObligateTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040280",  //Frequency [OBLIGATE]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=1.0;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Excluded HP:0040285; Present in 0%  of the cases.
   * The expected mean is 0.0
   */
  @Test
  public void testGetFrequencyFromExcludedTerm() throws PhenolException{
    String items[]={
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040285",  //Frequency [EXCLUDED]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.constructWithPrefix("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.0;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }



}
