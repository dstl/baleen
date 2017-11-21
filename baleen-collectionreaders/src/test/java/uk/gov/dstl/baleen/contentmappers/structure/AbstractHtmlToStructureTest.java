//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers.structure;

import com.google.common.collect.ImmutableList;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.jsoup.Jsoup;
import uk.gov.dstl.baleen.contentextractors.helpers.DocumentToJCasConverter;
import uk.gov.dstl.baleen.contentmappers.StructuralAnnotations;
import uk.gov.dstl.baleen.core.utils.ReflectionUtils;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

import java.util.Set;

public class AbstractHtmlToStructureTest {

  private static final Set<Class<? extends Structure>> structuralClasses = ReflectionUtils.getSubTypes(Structure.class.getPackage().getName(), Structure.class);
  private static final DocumentToJCasConverter converter =
      new DocumentToJCasConverter(ImmutableList.of(new StructuralAnnotations()));


  public Node<Structure> createStructure(String html) throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    converter.apply(Jsoup.parse(html), jCas);
    return StructureHierarchy.build(jCas, structuralClasses).getRoot();
  }
  
}
