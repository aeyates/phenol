package org.monarchinitiative.phenol.io.obo;

import java.util.SortedMap;

import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.Relationship;

/**
 * Interface for constructing concrete {@link Term} and {@link Relationship} objects in
 * {@link OboImmutableOntologyLoader}.
 *
 * @param <T> The type to use for terms.
 * @param <R> The type to use for term relations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OboOntologyEntryFactory<T extends Term, R extends Relationship> {

  /**
   * Set map from Term Id string to {@link ImmutableTermId} into factory.
   *
   * @param termIds The term Id mapping to use.
   */
  public void setTermIds(SortedMap<String, ImmutableTermId> termIds);

  /**
   * Construct term from {@link Stanza}.
   *
   * @param stanza {@link Stanza} with information.
   * @return The constructed <code>T</code>.
   */
  public T constructTerm(Stanza stanza);

  /**
   * Construct term from {@link Stanza} and <code>is_a</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>is_a</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructrelationship(Stanza stanza, StanzaEntryIsA stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>disjoint_from</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>disjoint_from</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructrelationship(Stanza stanza, StanzaEntryDisjointFrom stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>union_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>union_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructrelationship(Stanza stanza, StanzaEntryUnionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>intersection_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>intersection_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructrelationship(Stanza stanza, StanzaEntryIntersectionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>relationship</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>relationship</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructrelationship(Stanza stanza, StanzaEntryRelationship stanzaEntry);

}
