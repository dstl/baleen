// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.factory.JCasFactory;

/**
 * Factory for creating an Odin Taxonomy. This takes an Apache UIMA TypeSystem and converts it to a
 * form used to produce an Odin Taxonomy in the {@link OdinConfigurationProcessor}.
 */
public class TaxonomyFactory {

  private final TypeSystem typeSystem;
  private final String[] includedTypeNames;

  /**
   * Constructor
   *
   * <p>Type system provided by {@link JCasFactory}
   *
   * @param includedTypeNames The (full) class names of the types to convert (eg
   *     Person.class.getName())
   * @throws UIMAException if type system can not be created
   */
  public TaxonomyFactory(String[] includedTypeNames) throws UIMAException {
    this(JCasFactory.createJCas().getTypeSystem(), includedTypeNames);
  }

  /**
   * Constructor
   *
   * @param typeSystem The TypeSystem to convert to a taxonomy
   * @param includedTypeNames The (full) class names of the types to convert (eg
   *     Person.class.getName())
   */
  public TaxonomyFactory(TypeSystem typeSystem, String[] includedTypeNames) {
    this.typeSystem = typeSystem;
    this.includedTypeNames = includedTypeNames;
  }

  /**
   * Create the Odin Taxonomy from the given TypeSystem and included Types
   *
   * @return the Odin Taxonomy
   */
  public Collection<Object> create() {

    Collection<Object> completeTaxonomyTree = new ArrayList<>();

    for (String typeName : includedTypeNames) {
      completeTaxonomyTree.add(createTaxonomyTreeForType(typeName));
    }

    return completeTaxonomyTree;
  }

  private Map<String, Collection<?>> createTaxonomyTreeForType(String typeName) {

    Map<String, Collection<?>> treeForType = new HashMap<>();

    Type type = getType(typeName);
    List<Type> subTypes = typeSystem.getDirectSubtypes(type);
    List<Object> typeValues = new ArrayList<>();

    treeForType.put(type.getShortName(), typeValues);

    for (Type subType : subTypes) {
      typeValues.add(createTaxonomyTreeForType(subType.getName()));
    }

    return treeForType;
  }

  private Type getType(String typeName) {
    Type type = typeSystem.getType(typeName);
    if (type != null) {
      return type;
    }
    Iterator<Type> iterator = typeSystem.getTypeIterator();
    while (iterator.hasNext()) {
      Type toCheck = iterator.next();
      if (typeName.equals(toCheck.getShortName())) {
        return toCheck;
      }
    }
    throw new IllegalArgumentException("Unknown type: " + typeName);
  }
}
