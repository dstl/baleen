//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import org.apache.uima.jcas.JCas;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;
import uk.gov.dstl.baleen.types.metadata.Metadata;

/**
 * Convert from meta tags into a Metadata annotation, retaining the key (name) and value (content or
 * charset) attributes.
 */
public class MetaTags implements ContentMapper {

	@Override
	public void map(JCas jCas, Element element, AnnotationCollector collector) {
		
		if("meta".equalsIgnoreCase(element.tagName())){
			Metadata md = new Metadata(jCas);

			String name = element.attr("name");
			md.setKey(name);

			String content = element.attr("content");
			String charset = element.attr("charset");
			if (!Strings.isNullOrEmpty(content)) {
				md.setValue(content);
			} else if (!Strings.isNullOrEmpty(charset)) {
				md.setValue(charset);
			}

			collector.add(md);
		}
	}
}