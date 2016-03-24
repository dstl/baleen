package uk.gov.dstl.baleen.annotators.misc.helpers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Abstract class to provide common functionality for Keyword extraction annotators
 * 
 * @baleen.javadoc
 */
public abstract class AbstractKeywordsAnnotator extends BaleenAnnotator {
	/**
	 * Should the extracted keywords be annotated as Buzzwords within the document? 
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_ADD_BUZZWORDS = "addBuzzwords";
	@ConfigurationParameter(name = PARAM_ADD_BUZZWORDS, defaultValue="true")
	protected Boolean addBuzzwords;
	
	/**
	 * The maximum number of keywords to extract.
	 * 
	 * The number of keywords may be less than this.
	 * 
	 * If there are a number of keywords with the same score that would take the total
	 * number of keywords over the limit, then all are included.
	 * 
	 * @baleen.config 5
	 */
	public static final String PARAM_MAX_KEYWORDS = "maxKeywords";
	@ConfigurationParameter(name = PARAM_MAX_KEYWORDS, defaultValue="5")
	protected Integer maxKeywords;
	
	/**
	 * The stoplist to use. If the stoplist matches one of the enum's provided in
	 * {@link uk.gov.dstl.baleen.resources.SharedStopwordResource#StopwordList}, then
	 * that list will be loaded.
	 * 
	 * Otherwise, the string is taken to be a file path and that file is used.
	 * The format of the file is expected to be one stopword per line.
	 * 
	 * @baleen.config DEFAULT
	 */
	public static final String PARAM_STOPLIST = "stoplist";
	@ConfigurationParameter(name = PARAM_STOPLIST, defaultValue="DEFAULT")
	protected String stoplist;
	
	/**
	 * Connection to Stopwords Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
	 */
	public static final String KEY_STOPWORDS = "stopwords";
	@ExternalResource(key = KEY_STOPWORDS)
	protected SharedStopwordResource stopwordResource;
	
	protected Collection<String> stopwords;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		try{
			stopwords = stopwordResource.getStopwords(SharedStopwordResource.StopwordList.valueOf(stoplist));
		}catch(IllegalArgumentException iae){
			getMonitor().info("Value of {} does not match pre-defined list, assuming value is a file", PARAM_STOPLIST);
			getMonitor().debug("Unable to parse value of {} as StopwordList enum", PARAM_STOPLIST, iae);
			
			File f = new File(stoplist);
			
			try{
				stopwords = stopwordResource.getStopwords(f);
			}catch(IOException ioe){
				throw new ResourceInitializationException(
					new InvalidParameterException("Couldn't load stoplist", ioe)
				);
			}
		}catch(IOException ioe){
			getMonitor().warn("Unable to load Stopword list, resorting to default list", ioe);
			stopwords = stopwordResource.getStopwords();
		}
	}

	/**
	 * Add the supplied keywords to the CAS as Metadata and, if configured, Buzzwords
	 */
	protected void addKeywordsToJCas(JCas jCas, List<String> keywords){
		Metadata md = new Metadata(jCas);
		md.setKey("keywords");
		md.setValue(keywords.stream().collect(Collectors.joining(";")));
		addToJCasIndex(md);
		
		if(addBuzzwords){
			for(String keyword : keywords){
				Matcher m = Pattern.compile("\\b"+Pattern.quote(keyword)+"\\b", Pattern.CASE_INSENSITIVE).matcher(jCas.getDocumentText());
				while(m.find()){
					Buzzword bw = new Buzzword(jCas, m.start(), m.end());
					bw.setTags(UimaTypesUtils.toArray(jCas, Arrays.asList("keyword")));
					addToJCasIndex(bw);
				}
			}
		}
	}
	
	/**
	 * Add the supplied keywords to the CAS as Metadata and, if configured, Buzzwords.
	 * A list of additional buzzwords to be annotated can be provided, for example other variants
	 * of the main list of keywords (e.g. machines as well as machine)
	 */
	protected void addKeywordsToJCas(JCas jCas, List<String> keywords, List<String> additionalBuzzwords){
		Metadata md = new Metadata(jCas);
		md.setKey("keywords");
		md.setValue(keywords.stream().collect(Collectors.joining(";")));
		addToJCasIndex(md);
		
		if(addBuzzwords){
			Set<String> allKeywords = new HashSet<>(keywords);
			allKeywords.addAll(additionalBuzzwords);
			
			for(String keyword : allKeywords){
				Matcher m = Pattern.compile("\\b"+Pattern.quote(keyword)+"\\b", Pattern.CASE_INSENSITIVE).matcher(jCas.getDocumentText());
				while(m.find()){
					Buzzword bw = new Buzzword(jCas, m.start(), m.end());
					bw.setTags(UimaTypesUtils.toArray(jCas, Arrays.asList("keyword")));
					addToJCasIndex(bw);
				}
			}
		}
	}
	
	/**
	 * Build a regular expression that matches any of the current list of stopwords,
	 * along with any additional terms provided.
	 * 
	 * Any additional terms provided are not escaped, so you can provide your own regular
	 * expressions to include in the pattern.
	 */
	protected Pattern buildStopwordsPattern(String... additionalTerms){
		StringJoiner sj = new StringJoiner("|");
		for(String s : stopwords){
			sj.add(Pattern.quote(s));
		}
		
		if(additionalTerms != null){
			for(String s : additionalTerms){
				sj.add(s);
			}
		}
		
		return Pattern.compile("\\b("+sj.toString()+")\\b", Pattern.CASE_INSENSITIVE);
	}
}
