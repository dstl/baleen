package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNormalizeEntities;
import uk.gov.dstl.baleen.types.semantic.Entity;

import com.google.common.base.Strings;

/**
 * This cleaner removes whitespace in the values of entities matching the selected entity types.
 * <p>The entity types to process are specified in the pipeline configuration file using the
 * entityList configuration parameter.</p>
 * 
 * <p>The entityList allows specification of entity sub-type values or entity type values, with
 * sub-type taking precedence over type.</p>
 * 
 * <p>Removing whitespace is considered a normalisation operation so the normalised flag
 * will be set if any change is made to the entity value string</p>
 * 
 * <p>This cleaner is best used towards the end of the pipeline as it removes whitespace
 * which may be  being used as a delimiter by other cleaners e.g. the cleaner to normalise
 * dates will recognise and format "1 3 1995" but "131995" (the result after using this
 * cleaner) will be left unchanged by the date normalisation.</p>
 * @baleen.javadoc
 * 
 */
public class RemoveWhitespace extends AbstractNormalizeEntities {
	/**
	 * A list of the entity sub-types and type which are to have their whitespace removed.
	 * <p>The strings specified are compared against an entities sub-type value first and
	 * then the entities type value if no match is found. This allows fine control over
	 * selection of entities to process. e.g. specifying telephone will result in
	 * only CommsIdentifier entities with a sub-type of telephone being processed, those
	 * with a sub-type of ipv4address will be left untouched. However, specifying CommsIdentifier
	 * will result all entities of that type being processed whatever their sub-type is.</p> 
	 * <p>Default value is used to prevent execution in case a blank list is supplied.</p>
	 * @baleen.config entityList
	 */
	public static final String PARAM_ENTITY_LIST = "entityList";
	private static final String DEFAULT_VALUE = "NoEntities";
	@ConfigurationParameter(name = PARAM_ENTITY_LIST, defaultValue={DEFAULT_VALUE})
	String[] entityList;
	
	/**
	 * Convert entityList into a set, to speedup lookups
	 */
	Set<String> entityTypes;

	@Override
	public void doInitialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.doInitialize(aContext);

		if (!entityList[0].equals(DEFAULT_VALUE)) {
			entityTypes = new HashSet<String>();
			entityTypes.addAll(Arrays.asList(entityList));
		}
	}

	@Override
	protected boolean shouldNormalize(Entity e) {
		Boolean removeWhitespace = false;
		
		if (null != entityTypes) {
			String entityType = e.getSubType();
			if (Strings.isNullOrEmpty(entityType)) {
				entityType = e.getTypeName();
				entityType = entityType.substring(entityType.lastIndexOf('.') + 1);
			}

			if (entityTypes.contains(entityType)) {
				removeWhitespace = true;
			}
		}
		return removeWhitespace;
	}

	@Override
	protected String normalize(Entity e) {
		String normalized=e.getValue().replaceAll("[\\s]", "");
		return normalized;
	}

}
