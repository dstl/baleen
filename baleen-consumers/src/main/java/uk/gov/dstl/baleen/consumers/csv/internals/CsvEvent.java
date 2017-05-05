//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv.internals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.csv.AbstractCsvConsumer;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Write events to CSV.
 * <p>
 * Format is:
 * <ul>
 * <li>source
 * <li>sentence
 * <li>type
 * <li>Words
 * <li>Entities
 * <li>Arguments...
 *
 */
public class CsvEvent extends AbstractCsvConsumer {

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		write("source", "sentence", "type", "words", "Entities then arguments...");
	}

	@Override
	protected void write(JCas jCas) {
		final String source = getDocumentAnnotation(jCas).getSourceUri();

		final Map<Event, Collection<Sentence>> coveringSentence = JCasUtil.indexCovering(jCas,
				Event.class,
				Sentence.class);

		JCasUtil.select(jCas, Event.class).stream()
				.map(e -> extracted(source, coveringSentence, e))
				.filter(s -> s.length > 0)
				.forEach(this::write);

	}

	private String[] extracted(final String source, final Map<Event, Collection<Sentence>> coveringSentence,
			Event e) {
		String sentence = "";
		final Collection<Sentence> sentences = coveringSentence.get(e);
		if (!sentences.isEmpty()) {
			sentence = sentences.iterator().next().getCoveredText();
		} else {
			// This shouldn't be empty, unless you have no sentence annotation
			return new String[0];
		}

		final List<String> list = new ArrayList<>();
		list.add(source);
		list.add(sentence);

		if (e.getEventType() != null) {
			list.add(Arrays.stream(UimaTypesUtils.toArray(e.getEventType()))
					.collect(Collectors.joining(",")));
		} else {
			list.add("");
		}

		if (e.getTokens() != null) {
			list.add(Arrays.stream(e.getTokens().toArray())
					.map(w -> ((WordToken) w).getCoveredText())
					.map(this::normalize)
					.collect(Collectors.joining(" ")));
		} else {
			list.add("");
		}

		if (e.getEntities() != null && e.getEntities().size() > 0) {
			Arrays.stream(e.getEntities().toArray())
					.forEach(x -> {
						final Entity t = (Entity) x;
						list.add(normalize(t.getCoveredText()));
					});
		}

		if (e.getArguments() != null && e.getArguments().size() > 0) {
			Arrays.stream(e.getArguments().toArray())
					.map(this::normalize)
					.forEach(list::add);
		}

		return list.toArray(new String[list.size()]);
	}

}