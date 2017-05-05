//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Url;

/**
 * Annotate URLs within a document using regular expressions

 * <p>The document content is run through a regular expression matcher looking for things that match the following URL regular expression:</p>
 * <pre>\\b((https?|ftp)://|www.)(([-a-z0-9]+)\\.)?([-a-z0-9\\.]+\\.[a-z0-9]+)(:([1-9][0-9]{1,5}))?(/([-a-z0-9+&@#/%=~_|$!:,.]*\\?[-a-z0-9+&@#/%=~_|$!:,.]*)|/([-a-z0-9+&@#/%=~_|$!:,.]*[-a-z0-9+&@#/%=~_|$!:,])|/)?</pre>
 * <p>Not all valid URLs as defined by RFC3986 will be captured by this regular expression and annotator.</p>
 * 
 * 
 */
public class UrlRegex extends AbstractRegexAnnotator<Url> {
	private static final String URL_PATTERN = "\\b((https?|ftp)://|www.)(([-a-z0-9]+)\\.)?([-a-z0-9\\.]+\\.[a-z0-9]+)(:([1-9][0-9]{1,5}))?(/([-a-z0-9+&@#/%=~_|$!:,.]*\\?[-a-z0-9+&@#/%=~_|$!:,.]*)|/([-a-z0-9+&@#/%=~_|$!:,.]*[-a-z0-9+&@#/%=~_|$!:,])|/)?";
	
	/** New instance.
	 * 
	 */
	public UrlRegex() {
		super(URL_PATTERN, false, 1.0f);
	}
	
	@Override
	protected Url create(JCas jCas, Matcher matcher) {
		return new Url(jCas);
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Url.class));
	}

}