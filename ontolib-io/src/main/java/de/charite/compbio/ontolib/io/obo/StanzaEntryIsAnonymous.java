package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>is_anonymous</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIsAnonymous extends StanzaEntry {

  /** Boolean value of the stanza entry. */
  private final boolean value;

  /**
   * Constructor.
   *
   * @param value The boolean value of the stanza.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIsAnonymous(boolean value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.IS_ANONYMOUS, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The entry's boolean value.
   */
  public boolean isAnonymous() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryName [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
