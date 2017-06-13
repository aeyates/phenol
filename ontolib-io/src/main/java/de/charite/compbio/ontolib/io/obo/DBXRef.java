package de.charite.compbio.ontolib.io.obo;

// TODO: Un-escape strings transparently

/**
 * Representation of the <code>dbXRef</code> from the OBO file.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBXRef {

  /** The dbXRef name. */
  private final String name;

  /** The dbXRef description, <code>null</code> if missing. */
  private final String description;

  /** The {@link TrailingModifier}, <code>null</code> if missing. */
  private final TrailingModifier trailingModifier;

  /**
   * Constructor.
   *
   * @param name The dbXRef name.
   * @param description The dbXRef description, <code>null</code> if missing.
   * @param trailingModifier The trailing modifier, <code>null</code> if missing.
   */
  public DBXRef(String name, String description, TrailingModifier trailingModifier) {
    this.name = name;
    this.description = description;
    this.trailingModifier = trailingModifier;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the trailingModifier
   */
  public TrailingModifier getTrailingModifier() {
    return trailingModifier;
  }

  @Override
  public String toString() {
    return "DBXRef [name=" + name + ", description=" + description + ", trailingModifier="
        + trailingModifier + "]";
  }

}
