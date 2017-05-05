//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.structure;

import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import org.reflections.Reflections;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.contentextractors.helpers.DocumentToJCasConverter;
import uk.gov.dstl.baleen.contentmappers.StructuralAnnotations;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

public class AbstractHtmlToStructureTest {


  private static final Set<Class<? extends Structure>> structuralClasses;
  private static final DocumentToJCasConverter converter =
      new DocumentToJCasConverter(ImmutableList.of(new StructuralAnnotations()));

  static {
    Reflections reflections = new Reflections(Structure.class.getPackage().getName());
    structuralClasses = reflections.getSubTypesOf(Structure.class);
  }

  public Node<Structure> createStructure(String html) throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    converter.apply(Jsoup.parse(html), jCas);
    return StructureHierarchy.build(jCas, structuralClasses).getRoot();
  }


}
