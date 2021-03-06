package org.monarchinitiative.phenol.io.obo.hpo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.*;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.monarchinitiative.phenol.formats.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT;
import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class parses the phenotype.hpoa file into a collection of HpoDisease objects.
 * If errors are enountered, the line is skipped and the error is added to the list
 * {@link #errors}. Client code can ask if the parsing was error-free with the method
 * {@link #validParse()} and can retrieve the errors (if any) with {@link #getErrors()}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:michael.gargano@jax.org">Michael Gargano</a>
 */
public class HpoDiseaseAnnotationParser {
  /** Path to the phenotype.hpoa annotation file. */
  private final String annotationFilePath;
  /** Reference to the HPO Ontology object. */
  private final HpoOntology ontology;
  /** Key: disease CURIE, e.g., OMIM:600100; Value: corresponding {@link HpoDisease} object. */
  private final Map<TermId, HpoDisease> diseaseMap;
  /** Key: HpoPhenotypeId; Value: corresponding {@link HpoDisease} object. */
  private ImmutableMultimap<TermId, TermId> phenotypeToDiseaseMap;
  /** List of errors encountered during parsing of the annotation file. */
  private List<String> errors;

  public HpoDiseaseAnnotationParser(String annotationFile, HpoOntology ontlgy) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontlgy;
    this.diseaseMap = new HashMap<>();
  }

  public HpoDiseaseAnnotationParser(File annotationFile, HpoOntology ontlgy){
    this.annotationFilePath = annotationFile.getAbsolutePath();
    this.ontology = ontlgy;
    this.diseaseMap = new HashMap<>();
  }
  /** @return true if the entire parse was error-free */
  public boolean validParse(){ return errors.isEmpty(); }
  /** @return a list of Strings each of which represents one parse error.*/
  public List<String> getErrors() { return errors; }

  public ImmutableMultimap<TermId, TermId> getTermToDiseaseMap(){
    return this.phenotypeToDiseaseMap;
  }

  /**
   * Parse the {@code phenotype.hpoa} file and return a map of diseases.
   * @return a map with key: disease id, e.g., OMIM:600123, and value the corresponding HpoDisease object.
   * */
  public Map<TermId, HpoDisease> parse() throws PhenolException {
    // First stage of parsing is to get the lines parsed and sorted according to disease.
    Map<TermId, List<HpoAnnotationLine>> disease2AnnotLineMap = new HashMap<>();
    Multimap<TermId, TermId> termToDisease = ArrayListMultimap.create();
    ImmutableList.Builder<String> errorbuilder=new ImmutableList.Builder<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(this.annotationFilePath));
      String line = br.readLine();
      while (line.startsWith("#")) {
        line=br.readLine();
      } // this skips the comments. The next line has the definition of the header
      if (!HpoAnnotationLine.isValidHeaderLine(line)) {
        br.close();
        throw new PhenolException(
          String.format(
            "Annotation file at %s has invalid header (%s)", annotationFilePath, line));
      }
      while ((line = br.readLine()) != null) {
        HpoAnnotationLine aline =  HpoAnnotationLine.constructFromString(line);
        if (! aline.hasValidNumberOfFields()) {
          errorbuilder.add(String.format("Invalid number of fields: %s",line));
          continue;
        }
        if(!termToDisease.containsEntry(aline.getPhenotypeId(),aline.getDiseaseTermId())){
          termToDisease.put(aline.getPhenotypeId(),aline.getDiseaseTermId());
        }

        TermId diseaseId = aline.getDiseaseTermId();
        disease2AnnotLineMap.putIfAbsent(diseaseId,new ArrayList<>());
        List<HpoAnnotationLine> annots=disease2AnnotLineMap.get(diseaseId);
        annots.add(aline);

      }
      ImmutableMultimap.Builder<TermId,TermId> builderTermToDisease = new ImmutableMultimap.Builder<>();
      builderTermToDisease.putAll(termToDisease);
      this.phenotypeToDiseaseMap = builderTermToDisease.build();
      br.close();
    } catch (IOException e) {
      throw new PhenolException(String.format("Could not read annotation file: %s",e.getMessage()));
    }
    // When we get down here, we have added all of the disease annotations to the disease2AnnotLineMap
    // Now we want to transform them into HpoDisease objects
    for (TermId diseaseId : disease2AnnotLineMap.keySet()) {
      List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
      final ImmutableList.Builder<HpoAnnotation> phenoListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> inheritanceListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> negativeTermListBuilder = ImmutableList.builder();
      String diseaseName = null;
      for (HpoAnnotationLine line : annots) {
        try {
          if (isInheritanceTerm(line.getPhenotypeId())) {
            inheritanceListBuilder.add(line.getPhenotypeId());
          } else if (line.isNOT()) {
            negativeTermListBuilder.add(line.getPhenotypeId());
          } else {
            HpoAnnotation tidm = HpoAnnotationLine.toHpoAnnotation(line,ontology);
            phenoListBuilder.add(tidm);
          }
          if (line.getDbObjectName() != null) diseaseName = line.getDbObjectName();
        } catch (Exception e) {
          errorbuilder.add(String.format("PHENOL ERROR] Line: %s--could not parse annotation: %s ",
            line.toString(), e.getMessage()));
        }
      }
      HpoDisease hpoDisease =
        new HpoDisease(
          diseaseName,
          diseaseId,
          phenoListBuilder.build(),
          inheritanceListBuilder.build(),
          negativeTermListBuilder.build());
      this.diseaseMap.put(hpoDisease.getDiseaseDatabaseId(), hpoDisease);
    }
    this.errors=errorbuilder.build();
    return diseaseMap;
  }

  /**
   * Check whether a term is a member of the inheritance subontology.
   * We check whether there is a path between the term and the root of the inheritance ontology.
   * We also check whether the term is itself the root of the inheritance ontology because
   * there cannot be a path between a term and itself.
   *
   * @param tid A term to be checked
   * @return true of tid is an inheritance term
   */
  private boolean isInheritanceTerm(TermId tid) {
    return tid.equals(INHERITANCE_ROOT) || existsPath(this.ontology,tid,INHERITANCE_ROOT);
  }





}
