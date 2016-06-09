package uk.gov.dstl.baleen.annotators.misc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.ComparableEntitySpan;
import uk.gov.dstl.baleen.uima.utils.ComparableEntitySpanUtils;

/**
 * Creates entity annotations for each piece of text that is the same as the covered text.
 * <p>
 * This is useful when a model is used (rather than a regex) and it only finds a subset of the
 * mentions in a document.
 * <p>
 * If an annotation of the same type already exists on the covering text then another is not added.
 *
 * @baleen.javadoc
 */
public class MentionedAgain extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		final String text = jCas.getDocumentText();

		final Collection<Entity> list = JCasUtil.select(jCas, Entity.class);

		final Set<ComparableEntitySpan> spans = new HashSet<>(list.size());

		list.stream()
				.forEach(e -> {
					final Pattern pattern = Pattern.compile("\\b" + Pattern.quote(e.getCoveredText()) + "\\b");
					final Matcher matcher = pattern.matcher(text);
					while (matcher.find()) {
						if (!ComparableEntitySpanUtils.existingEntity(list, matcher.start(), matcher.end(), e.getClass())) {
							spans.add(new ComparableEntitySpan(e, matcher.start(), matcher.end()));
						}
					}
				});

		spans.stream().forEach(s -> {
			final Entity newEntity = ComparableEntitySpanUtils.copyEntity(jCas, s.getBegin(), s.getEnd(), s.getEntity());

			if (s.getEntity().getReferent() == null) {
				// Make them the same
				final ReferenceTarget rt = new ReferenceTarget(jCas);
				addToJCasIndex(rt);

				s.getEntity().setReferent(rt);
			}

			newEntity.setReferent(s.getEntity().getReferent());

			addToJCasIndex(newEntity);
		});
	}

}
