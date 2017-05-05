//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.TableBody;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.uima.utils.SelectorPart;
import uk.gov.dstl.baleen.uima.utils.SelectorPath;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * This class is used to search for {@link SelectorPath}s for the creation of
 * {@link TemplateRecord}s and {@link TemplateField}s. It records matches found
 * for repeating units, subsequent calls compensate the path for these repeated
 * structures. It is therefore important to process the document in order so
 * later items are correctly compensated for earlier repeating annotations. The
 * order property of the record definitions can be used for this.
 */
public class RecordStructureManager {

	/**
	 * A map to store alteration to the expected structure based on the
	 * repeating structural annotations
	 */
	private final Map<Structure, Map<Class<?>, Integer>> data = new HashMap<>();

	/** The structure hierarchy to process */
	private final ItemHierarchy<Structure> structureHierarchy;

	/**
	 * Constructor for the record structure manager
	 *
	 * @param structureHierarchy
	 *            the structure hierarchy
	 */
	public RecordStructureManager(ItemHierarchy<Structure> structureHierarchy) {
		this.structureHierarchy = structureHierarchy;
	}

	/**
	 * Get the structure annotation for the given path if found
	 *
	 * @see SelectorPath
	 * @param path
	 *            the selector path
	 * @return optional of the structure annotation
	 * @throws InvalidParameterException
	 */
	public Optional<Structure> select(String path) throws InvalidParameterException {
		return select(SelectorPath.parse(path));
	}

	/**
	 * Get the structure annotation for the given selector path if found
	 *
	 * @see SelectorPath
	 * @param selector
	 *            path the selector path
	 * @return optional of the structure annotation
	 */
	public Optional<Structure> select(SelectorPath selectorPath) {
		return select(structureHierarchy.getRoot(), selectorPath);
	}

	/**
	 * Internal recursive method to select the path. This compensates the path
	 * for recorded repeating structures.
	 *
	 * @param node
	 *            the current node
	 * @param selectorParts
	 *            the (remaining) path to select
	 * @return optional of the structure annotation
	 */
	private Optional<Structure> select(Node<Structure> node, SelectorPath selectorParts) {
		if (selectorParts.isEmpty()) {
			return Optional.ofNullable(node.getItem());
		}
		SelectorPart current = selectorParts.get(0);
		SelectorPath remaining = selectorParts.step();
		Map<Class<?>, Integer> map = data.get(node.getItem());
		Integer adjustment = Integer.valueOf(0);
		if (map != null) {
			adjustment = map.getOrDefault(current.getType(), Integer.valueOf(0));
		}
		Optional<Node<Structure>> found = node.getChildren().stream()
				.filter(c -> current.getType().equals(c.getItem().getClass()))
				.skip(Math.max(0, current.getIndex() - 1) + adjustment).findFirst();
		if (found.isPresent()) {
			return select(found.get(), remaining);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Create a definition of the possible repeating structures to search for
	 *
	 * @param recordDefinition
	 *            the record definition
	 * @return The repeat search object derived from the given record definition
	 * @throws InvalidParameterException
	 *             if any of the paths are not valid
	 */
	public RepeatSearch createRepeatSearch(TemplateRecordConfiguration recordDefinition)
			throws InvalidParameterException {
		List<SelectorPath> coveredRepeat = createRepeatUnit(recordDefinition.getCoveredPaths());
		SelectorPath minimalRepeat = SelectorPath.parse(recordDefinition.getMinimalRepeat());
		return new RepeatSearch(coveredRepeat, minimalRepeat);
	}

	/**
	 * Create the repeat unit
	 *
	 * @param preceedingParts
	 *            the preceding structure path
	 * @param paths
	 *            the path to generate the repeat
	 * @return a list of the repeating paths
	 * @throws InvalidParameterException
	 *             if one of the paths is invalid
	 */
	private List<SelectorPath> createRepeatUnit(List<String> paths) throws InvalidParameterException {
		List<SelectorPath> repeat = new ArrayList<>();

		for (String path : paths) {
			SelectorPath covered = SelectorPath.parse(path);
			repeat.add(covered);
		}
		return repeat;
	}

	/**
	 * Try to find the given repeating structure after the given preceding
	 * structure.
	 *
	 * @param preceding
	 *            the preceding structure
	 * @param repeatSearch
	 *            the repeat search
	 * @return optional of the found repeating structure
	 */
	private Optional<List<Structure>> getRepeat(Optional<Structure> preceding, RepeatSearch repeatSearch) {

		SelectorPath minimalRepeat = repeatSearch.getMinimalRepeat();
		Optional<List<Structure>> match = Optional.empty();

		if (minimalRepeat.getDepth() > 0) {
			match = checkNextForMatch(minimalRepeat, preceding);
		}

		if (!match.isPresent()) {
			match = searchParts(preceding, repeatSearch.getCoveredRepeat());
		}
		return match;

	}

	/**
	 * Try to find the given repeating structure after the given preceding
	 * structure.
	 *
	 * @param preceding
	 *            the preceding structure
	 * @param repeat
	 *            the repeat to search for
	 * @return optional of the found repeating structure
	 */
	private Optional<List<Structure>> searchParts(Optional<Structure> preceding, List<SelectorPath> repeat) {
		Optional<Structure> current = preceding;
		Iterator<SelectorPath> iterator = repeat.iterator();
		List<Structure> matched = new ArrayList<>();
		while (iterator.hasNext()) {
			SelectorPath covered = iterator.next();
			Optional<List<Structure>> match = checkNextForMatch(covered, current);
			if (match.isPresent()) {
				List<Structure> nextMatched = match.get();
				current = Optional.of(nextMatched.get(nextMatched.size() - 1));
				matched.addAll(nextMatched);
			} else {
				return Optional.empty();
			}
		}
		return Optional.of(matched);
	}

	/**
	 * Check the next structure to see if it matches the expected repeating
	 * part.
	 *
	 * @param path
	 *            the path to match with
	 * @param structure
	 *            the current structure
	 * @return optional of the matched repeating structures
	 */
	private Optional<List<Structure>> checkNextForMatch(SelectorPath path, Optional<Structure> structure) {
		if (path == null || path.isEmpty()) {
			return Optional.empty();
		}
		return checkNextForMatch(path, new ArrayList<>(), structure);
	}

	/**
	 * Check the next structure to see if it matches the expected repeating
	 * part.
	 *
	 * @param path
	 *            the path to match with
	 * @param matching
	 *            the current list of matching structures
	 * @param structure
	 *            the current structure
	 * @return optional of the matched repeating structures
	 */
	private Optional<List<Structure>> checkNextForMatch(SelectorPath path, List<Structure> matching,
			Optional<Structure> structure) {
		Optional<Structure> nextCheck;
		if (structure.isPresent()) {
			nextCheck = structureHierarchy.getNext(structure.get());
		} else {
			nextCheck = structureHierarchy.getRoot().getChildren().stream().map(Node::getItem).findFirst();
		}

		if (nextCheck.isPresent()) {
			Structure next = nextCheck.get();
			List<Structure> nextPath = structureHierarchy.getPath(next);
			Optional<Structure> match = match(nextPath, path);
			if (match.isPresent()) {
				matching.add(match.get());
				return Optional.of(matching);
			} else {
				if (isWhitespace(next)) {
					matching.add(next);
					return checkNextForMatch(path, matching, Optional.of(next));
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Check if the annotation contains anything
	 *
	 * @param structure
	 *            the structure
	 * @return true if empty or white space
	 */
	private boolean isWhitespace(Structure structure) {
		String coveredText = structure.getCoveredText();
		return StringUtils.isEmpty(coveredText) || StringUtils.isWhitespace(coveredText);
	}

	/**
	 * Check if the annotation contains anything
	 *
	 * @param structure
	 *            the structure
	 * @return true if not empty or white space
	 */
	private boolean isNotWhitespace(Structure structure) {
		return !isWhitespace(structure);
	}

	/**
	 * Try to match the given structure path to the selector path given. We
	 * first check the path matches the given list then if there are remaining
	 * elements of the path we check if the direct children match the path.
	 *
	 * @param structurePath
	 *            the starting structure path to match
	 * @param path
	 *            the path to match
	 * @return optional of the matching structure
	 */
	private Optional<Structure> match(List<Structure> structurePath, SelectorPath path) {
		Iterator<SelectorPart> iterator = path.getParts().iterator();
		for (Structure s : structurePath) {
			if (!iterator.next().getType().equals(s.getClass())) {
				return Optional.empty();
			}
		}
		Structure current = structurePath.get(structurePath.size() - 1);

		while (iterator.hasNext()) {
			List<Structure> children = structureHierarchy.getChildren(current);
			if (!children.isEmpty()) {
				current = children.get(0);
			} else {
				return Optional.empty();
			}
			if (!iterator.next().getType().equals(current.getClass())) {
				return Optional.empty();
			}
		}
		return Optional.of(current);
	}

	/**
	 * Check if the next structures are compatible with the given covered paths.
	 * <p>
	 * The returned structure may contain additional empty structures. This aims
	 * to make the record extraction more robust to additional whitespace
	 * particularly around repeating units.
	 *
	 * @param preceding
	 *            the optional preceding point in the structure
	 * @param repeatSearch
	 *            the repeat search
	 * @param isFirst
	 *            set true if this is the first call of a repeating record
	 * @return option of the final structure if we can repeat empty if not
	 */
	public Optional<Structure> repeatRecord(Optional<Structure> preceding, RepeatSearch repeatSearch, boolean isFirst) {
		Optional<List<Structure>> repeat = getRepeat(preceding, repeatSearch);
		if (repeat.isPresent()) {
			List<Structure> structures = repeat.get();
			// At least one match would be expected with out repetition
			if (!isFirst) {
				recordMatch(structures);
			}
			return Optional.of(structures.get(structures.size() - 1));
		} else {
			// At least one match would be expected with out repetition
			if (isFirst) {
				recordMissing(repeatSearch.getCoveredRepeat());
			}
			return Optional.empty();
		}
	}

	/**
	 * Check if the next structures are compatible with the given path.
	 * <p>
	 *
	 * @param preceding
	 *            the optional preceding point in the structure
	 * @param path
	 *            the repeating path
	 * @param end
	 *            the end of the record
	 * @return option of the field structure if we can repeat empty if not
	 */
	public Optional<Structure> repeatField(Optional<Structure> preceding, SelectorPath path, int end) {

		Optional<List<Structure>> repeat = checkNextForMatch(path, preceding);

		if (repeat.isPresent()) {
			List<Structure> structures = repeat.get();
			Structure lastStructure = structures.get(structures.size() - 1);
			if (lastStructure.getEnd() <= end) {
				recordMatch(structures);
				return structures.stream().filter(this::isNotWhitespace).findFirst();
			}
		}
		return Optional.empty();
	}

	/**
	 * Record the repeating match of the given structures. These will be used to
	 * compensate future calls to {@link #select(String)}.
	 *
	 * @param structures
	 *            the repeating structures
	 */
	private void recordMatch(List<Structure> structures) {
		for (Structure s : structures) {
			recordMatch(s);
		}
	}

	/**
	 * Record the repeating match of the given structure. These will be used to
	 * compensate future calls to {@link #select(String)}.
	 *
	 * @param structure
	 *            the repeating structure
	 */
	public void recordMatch(Structure record) {
		if (record instanceof TableBody) {
			Optional<Structure> table = structureHierarchy.getParent(record);
			if (table.isPresent()) {
				record = table.get();
			}
		}

		record(record);
	}

	/**
	 * Record the repeating match of the given structure. These will be used to
	 * compensate future calls to {@link #select(String)}.
	 *
	 * @param structure
	 *            the repeating structure
	 */
	private void record(Structure current) {
		Optional<Structure> parent = structureHierarchy.getParent(current);
		if (parent.isPresent()) {
			Map<Class<?>, Integer> map = data.get(parent.get());
			if (map == null) {
				map = new HashMap<>();
				data.put(parent.get(), map);
			}

			Integer integer = map.getOrDefault(current.getClass(), 0);
			map.put(current.getClass(), integer + 1);
		}
	}

	/**
	 * Record the missing first repeat of the given selector paths. These will
	 * be used to compensate future calls to {@link #select(String)}.
	 *
	 * @param paths
	 *            the repeating structure
	 */
	private void recordMissing(List<SelectorPath> paths) {
		for (SelectorPath s : paths) {
			recordMissing(s);
		}
	}

	/**
	 * Record the missing first repeat of the given selector paths. These will
	 * be used to compensate future calls to {@link #select(String)}.
	 *
	 * @param paths
	 *            the repeating structure
	 */
	public void recordMissing(SelectorPath path) {
		SelectorPath parentPath = path.toDepth(path.getDepth() - 1);
		Optional<Structure> parent = select(parentPath);
		if (parent.isPresent()) {
			Map<Class<?>, Integer> map = data.get(parent.get());
			if (map == null) {
				map = new HashMap<>();
				data.put(parent.get(), map);
			}
			Class<?> type = path.get(path.getDepth() - 1).getType();
			Integer integer = map.getOrDefault(type, 0);
			map.put(type, integer - 1);
		}
	}

}
