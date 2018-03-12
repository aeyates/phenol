package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermPrefix;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.0.2 (2017-11-24)
 */
public class ImmutableTermIdWithMetadataTest {
  private static TermPrefix HP_PREFIX = null;
  /** If no frequency is provided, the parser uses the default (100%) */
  private static HpoFrequency defaultFrequency = null;

  @BeforeClass
  public static void setup() throws IOException {
    HP_PREFIX = new ImmutableTermPrefix("HP");
    String DEFAULT_FREQUENCY = "0040280";
    final TermId DEFAULT_FREQUENCY_ID = new ImmutableTermId(HP_PREFIX, DEFAULT_FREQUENCY);
    defaultFrequency = HpoFrequency.fromTermId(DEFAULT_FREQUENCY_ID);
  }



  /** Different onset means the terms are not equal */
  @Test
  public void testEqualityOfTerms2() {
    TermId oxycephalyId = new ImmutableTermId(HP_PREFIX, "0000263");
    HpoTermId oxycephaly1 =new ImmutableHpoTermId.Builder(oxycephalyId).onset(HpoOnset.ADULT_ONSET).build();
    HpoTermId oxycephaly2 =new ImmutableHpoTermId.Builder(oxycephalyId).build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }

  /** Different onset means the terms are not equal */
  @Test
  public void testEqualityOfTerms3() {
    TermId oxycephalyId = new ImmutableTermId(HP_PREFIX, "0000263");
    HpoTermId oxycephaly1 =new ImmutableHpoTermId.Builder(oxycephalyId).onset(HpoOnset.ADULT_ONSET).build();
    HpoTermId oxycephaly2 =new ImmutableHpoTermId.Builder(oxycephalyId).onset(HpoOnset.CHILDHOOD_ONSET).build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }

  @Test
  public void testEqualityOfTerms4() {
    TermId oxycephalyId = new ImmutableTermId(HP_PREFIX, "0000263");
    HpoTermId oxycephaly1 =new ImmutableHpoTermId.Builder(oxycephalyId).
      onset(HpoOnset.ADULT_ONSET).
      frequency(HpoFrequency.ALWAYS_PRESENT.mean()).
      build();

    HpoTermId oxycephaly2 =new ImmutableHpoTermId.Builder(oxycephalyId).
      onset(HpoOnset.ADULT_ONSET).frequency(HpoFrequency.
      OCCASIONAL.mean()).build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }
}