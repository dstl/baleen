// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.json;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.templates.TemplateField;

/** Writes all TemplateFields to a JSON document. */
public class TemplateFieldJsonReportConsumer extends AbstractJsonConsumer<TemplateField> {

  @Override
  protected Iterable<TemplateField> selectAnnotations(JCas jCas) {
    return JCasUtil.select(jCas, TemplateField.class);
  }
}
