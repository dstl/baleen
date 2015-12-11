//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.gazetteer.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CommonArrayFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Abstract class implementing the a gazetteer using the Aho-Corasick algorithm.
 * 
 * Reflection is used to try and identify entity properties and set them based on additional data fields in the gazetteer.
 * This means that this annotator can be used for any entity type, though there is a risk that a malformed gazetteer could corrupt the entities.
 * 
 * @baleen.javadoc
 */
public abstract class AbstractAhoCorasickAnnotator extends BaleenAnnotator{
	
	/**
	 * Should comparisons be done case sensitively?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_CASE_SENSITIVE = "caseSensitive";
	@ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "false")
	protected boolean caseSensitive;
	
	/**
	 * Should whitespace in document be preserved?
	 * 
	 * If set to false, the document text is normalized prior to comparison, so that any sequence of whitespace characters is translated to a single space character before matching against the gazetteer.
	 * The document text in the CAS is not modified, and any annotations created will cover the correct span (including any ignored whitespace) of surface text.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_EXACT_WHITESPACE = "exactWhitespace";
	@ConfigurationParameter(name = PARAM_EXACT_WHITESPACE, defaultValue = "true")
	protected boolean exactWhitespace;
	
	/**
	 * The type to use for extracted entities
	 * 
	 * @baleen.config Entity
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "Entity")
	protected String type;
	
	protected IGazetteer gazetteer;
	protected Class<?> entityType;
	protected Trie trie;
	
	private static final String ERROR_CANT_ASSIGN_ENTITY_PROPERTY = "Unable to assign property on entity - property will be skipped";
	
	/**
	 * Constructor
	 * 
	 * @param logger The Logger to use for errors, etc.
	 */
	public AbstractAhoCorasickAnnotator() {
	}
	
	/**
	 * Configure a gazetteer object and initialise it.
	 * Remember that the caseSensitive and type properties may also need to be passed to the gazetteer, dependent on the gazetteer.
	 * 
	 * @return A initialised gazetteer implementing IGazetteer
	 */
	public abstract IGazetteer configureGazetteer() throws BaleenException;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		try{
			gazetteer = configureGazetteer();
		}catch(BaleenException be){
			throw new ResourceInitializationException(be);
		}
		
		buildTrie();
		
		try{
			entityType = TypeUtils.getType(type, JCasFactory.createJCas());
			if(entityType == null){
				getMonitor().warn("Type {} not found, Entity will be used instead", type);
				entityType = Entity.class;
			}
		}catch(UIMAException e){
			throw new ResourceInitializationException(e);
		}
	}
	
	/**
	 * Build the Trie and set the <em>trie</em> variable.
	 * This method can be overridden if you want to modify the gazetteer before parsing it.
	 */
	protected void buildTrie(){
		TrieBuilder builder = Trie.builder().onlyWholeWords();
		
		if(!caseSensitive)
			builder = builder.caseInsensitive();
		
		for(String s : gazetteer.getValues()){
			builder = builder.addKeyword(s);
		}
		
		trie = builder.build();
	}
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Map<String, List<Entity>> entities = exactWhitespace ? processExactWhitespace(jCas) : processNormalisedWhitespace(jCas);

		createReferenceTargets(jCas, entities.values());
		
	}
	
	private Map<String, List<Entity>> processExactWhitespace(JCas jCas){
		Map<String, List<Entity>> entities = new HashMap<>();

		Collection<Emit> emits = trie.parseText(jCas.getDocumentText());

		for(Emit emit : emits){
			try{
				String match = jCas.getDocumentText().substring(emit.getStart(), emit.getEnd() + 1);
				createEntityAndAliases(jCas, emit.getStart(), emit.getEnd() + 1, match, match, entities);
			}catch(BaleenException be){
				getMonitor().error("Unable to create entity of type {} for value '{}'", entityType.getName(), emit.getKeyword(), be);
				continue;
			}
		}
		
		return entities;
	}
	
	private Map<String, List<Entity>> processNormalisedWhitespace(JCas jCas){
		Map<String, List<Entity>> entities = new HashMap<>();

		TransformedString norm = normaliseString(jCas.getDocumentText());
		Collection<Emit> emits = trie.parseText(norm.getTransformedString());

		for(Emit emit : emits){
			try{
				Integer start = norm.getMapping().get(emit.getStart());
				Integer end = norm.getMapping().get(emit.getEnd() + 1);
				String match = norm.getOriginalString().substring(start, end);
				
				createEntityAndAliases(jCas, start, end, match, match, entities);
			}catch(BaleenException be){
				getMonitor().error("Unable to create entity of type {} for value '{}'", entityType.getName(), emit.getKeyword(), be);
				continue;
			}
		}
		
		return entities;
	}
	
	protected void createEntityAndAliases(JCas jCas, Integer start, Integer end, String value, String aliasKey, Map<String, List<Entity>> entities) throws BaleenException{
		Entity ent = createEntity(jCas, start, end, value, value);

		List<String> aliases = new ArrayList<>(Arrays.asList(gazetteer.getAliases(aliasKey)));
		aliases.add(aliasKey);
			
		String key = generateKey(aliases);
		
		List<Entity> groupEntities = entities.containsKey(key) ? entities.get(key) : new ArrayList<>();
		groupEntities.add(ent);
		entities.put(key, groupEntities);
	}
	
	/**
	 * Generate a key for an alias set by ordering and joining them
	 * 
	 * @param aliases
	 * @return
	 */
	protected String generateKey(List<String> aliases){
		List<String> correctCaseAliases;
		
		if(!caseSensitive){
			correctCaseAliases = aliases.stream().map(String::toLowerCase).collect(Collectors.toList());
		}else{
			correctCaseAliases = aliases;
		}
		
		Collections.sort(correctCaseAliases);
		
		return StringUtils.join(correctCaseAliases, "|");
	}
	
	/**
	 * Create a new entity of the configured type
	 * 
	 * @param jCas JCas object in which to create the entity
	 * @param begin The beginning of the entity in the text
	 * @param end The end of the entity in the text
	 * @param value The value of the entity
	 * @param gazetteerKey The key as it appears in the gazetteer
	 * @throws Exception
	 */
	protected Entity createEntity(JCas jCas, int begin, int end, String value, String gazetteerKey) throws BaleenException{
		Entity ent;
		try {
			ent = (Entity) entityType.getConstructor(JCas.class).newInstance(jCas);
		} catch (Exception e) {
			throw new BaleenException("Could not create new entity", e);
		}
		
		ent.setBegin(begin);
		ent.setEnd(end);
		ent.setValue(value);
		ent.setConfidence(1.0);
		
		Map<String, Object> additionalData = gazetteer.getAdditionalData(gazetteerKey);
		
		if(additionalData != null && !additionalData.isEmpty()){
			for(Method m : entityType.getMethods()){
				setProperty(ent, m, additionalData);
			}
		}
		
		addToJCasIndex(ent);
		
		return ent;
	}
	
	/**
	 * Create reference targets for entities with the same keys
	 * 
	 * @param jCas UIMA JCas Object
	 * @param entities A collection of lists of entities to coreference
	 */
	protected void createReferenceTargets(JCas jCas, Collection<List<Entity>> entities){
		for(List<Entity> group : entities){
			if(group.size() <= 1)
				continue;
			
			ReferenceTarget rt = new ReferenceTarget(jCas);
			rt.setBegin(0);
			rt.setEnd(jCas.getDocumentText().length());
			addToJCasIndex(rt);
			
			for(Entity e : group){
				e.setReferent(rt);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setProperty(Entity entity, Method method, Map<String, Object> additionalData){
		if(method.getName().startsWith("set") && method.getName().substring(3, 4).matches("[A-Z]") && method.getParameterCount() == 1){
			String property = method.getName().substring(3);
			property = property.substring(0, 1).toLowerCase() + property.substring(1);
			Object obj = additionalData.get(property);
			
			if(obj == null){
				return;
			}
			
			if(method.getParameterTypes()[0].isAssignableFrom(obj.getClass())){
				setPropertyObject(entity, method, obj);
			}else if(method.getParameterTypes()[0].isAssignableFrom(String.class)){
				getMonitor().debug("Converting gazetteer object of type {} to String", obj.getClass().getName());
				setPropertyString(entity, method, obj.toString());
			}else if(List.class.isAssignableFrom(obj.getClass()) && CommonArrayFS.class.isAssignableFrom(method.getParameterTypes()[0])){
				setPropertyArray(entity, method, (List<Object>) obj);
			}
		}
	}
	
	private void setPropertyObject(Entity entity, Method method, Object obj){
		try {
			method.invoke(entity, obj);
		} catch (Exception e) {
			getMonitor().error(ERROR_CANT_ASSIGN_ENTITY_PROPERTY, e);
		}
	}
	
	private void setPropertyString(Entity entity, Method method, String string){
		try {
			method.invoke(entity, string);
		} catch (Exception e) {
			getMonitor().error(ERROR_CANT_ASSIGN_ENTITY_PROPERTY, e);
		}
	}
	
	private void setPropertyArray(Entity entity, Method method, List<Object> obj){
		if(StringArray.class.isAssignableFrom(method.getParameterTypes()[0])){
			try{
				StringArray sa = listToStringArray(entity.getCAS().getJCas(), obj);
				method.invoke(entity, sa);
			}catch (Exception e){
				getMonitor().error(ERROR_CANT_ASSIGN_ENTITY_PROPERTY, e);
			}
		}else{
			getMonitor().error("Unsupported array type {} - property will be skipped", method.getParameterTypes()[0].getName());
		}
	}
	
	/**
	 * Replace repeated horizontal whitespace characters with a single space character,
	 * and return a TransformedString that maps between the original and normalised string
	 * 
	 * @param s The string to normalise
	 * @return A TransformedString mapping between the original and normalised text
	 */
	public static TransformedString normaliseString(String s){
		String remaining = s;
		StringBuilder builder = new StringBuilder();
		
		String previousChar = "";
		Map<Integer, Integer> indexMap = new HashMap<>();
	
		Integer index = 0;
		
		while(!remaining.isEmpty()){
			indexMap.put(builder.length(), index);
			index++;

			String character = remaining.substring(0, 1);
			remaining = remaining.substring(1);
			
			if(!(character.matches("\\h") && previousChar.matches("\\h"))){
				if(character.matches("\\h")){
					character = " ";
				}
				
				builder.append(character);
			}
			
			previousChar = character;
		}
		indexMap.put(builder.length(), index);
		
		return new TransformedString(s, builder.toString(), indexMap);
	}
	
	private StringArray listToStringArray(JCas jCas, List<Object> l){
		StringArray sa = new StringArray(jCas, l.size());
		
		int index = 0;
		for(Object o : l){
			sa.set(index, o.toString());
			index++;
		}

		return sa;
	}
	
	@Override
	public void doDestroy() {
		gazetteer.destroy();
		gazetteer = null;
		
		entityType = null;
		trie = null;
	}
}
